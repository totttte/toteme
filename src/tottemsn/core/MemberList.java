/**
 * �쐬��: 2006/08/07
 */
package tottemsn.core;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * ���b�Z���W���[�̃����o���X�g���i�[���܂��B
 */
public class MemberList {

    private Member[] list; // �����o�����X�g�`���ŕۑ����܂�
    private int listnum; // ���݂̃����o��
   /*	�O���[�v��\���܂��B�O���[�v��String�^��key�ƁA
    *	���ꂼ��O���[�v�̖��O�ŕ\����Ă��܂��B
    */ 
    private Hashtable groupe;
    private int groupes;
    
    private Messenger messe;
    
    /**
     * �V���ȃ����o���X�g���쐬���܂��B
     * @param people �����o���X�g�̐l�����w�肵�܂��B
     * @param groupes �����o���X�g�̃O���[�v�����w�肵�܂��B
     */
    MemberList(int people, int groupes, Messenger messe) {
        list = new Member[people];
        groupe = new Hashtable();
        this.groupes = groupes;
        this.messe = messe;
    }
    
    /**
     * �����o��ǉ����܂��B
     * ���X�g�R�}���h�̃T���v���͈ȉ��̒ʂ�
     * <pre>
     * LST N=address@hotmail.com F=name C=key 11 1 [groupeKey]
     * </pre>
     * ���ꂩ�炩��A�A�J�E���g�A���O�A���(���̏ꍇ��11)�AgroupeKey���擾���A
     * �V���������o��ǉ����܂��B
     * @param command LST�R�}���h(LST����n�܂�P�s)���w�肵�܂��B
     */
    void addMember(String command){
        	int tokenn = Math.min( StringUtil.tokenCount(command)-2, 4);
            String N= StringUtil.messeToken(command,1).substring(2);
            String F= (tokenn>3)?StringUtil.messeToken(command,2).substring(2):"";
            int bitwise = Integer.parseInt(StringUtil.messeToken(command,tokenn));
            String groupe = StringUtil.messeToken(command,6);
            if(groupe==null) groupe = "0";
            Member mem = new Member(N,F,bitwise);
            mem.groupeKey = groupe;
            list[listnum++] = mem;
    }
    
    /**
     * ���X�g�����t�ɂȂ������ǂ���
     * @return ���X�g�����t�ɂȂ����Ƃ�true
     */
    boolean fullList() {
        return listnum == list.length;
    }

    /**
     * �O���[�v��ǉ����܂��B
     * �O���[�v���X�g�̃T���v���͈ȉ��̒ʂ�
     * <pre>
     * LSG �F�l groupeKey
     * </pre>
     * @param command LSG�R�}���h(LSG ����n�܂�1�s)���w�肵�܂�
     */
    void addGroupe(String command) {
        String key = StringUtil.messeToken(command,2);
        String value = StringUtil.messeToken(command,1);
        groupe.put(key,value);
    }
        
    /**
     * �O���[�v�����t�ɂȂ������ǂ���
     * @return �O���[�v�����t�ɂȂ����Ƃ�true
     */
    boolean fullGroupe() {
        return groupes == groupe.size();
    }
    
    /**
     * �����o�̒N���̏�ԕω����������Ƃ����̃��\�b�h���g�p���܂��B
     * ��ԕω��̃T���v���͈ȉ��̒ʂ�
     * <pre>
     * NLN NLN address@hotmail.com name key key2
     * INL TrID NLN address name key key2
     * FLN address@hotmaill.com
     * </pre>
     * @param command INL,NLN,FLN�R�}���h
     * (ILN,NLN,FLN����n�܂�1�s)���w�肵�܂�
     * @return ��ԕω����N���������o��Ԃ��܂��B�Ȃ��A
     * �����o�͂��̃N���X���ێ����Ă��郁���o���X�g�̃R�s�[�ł����āA
     * ���ڎQ�Ƃ��Ă���킯�ł͂���܂���B
     */
    Member changeStatus(String command) {
        int tokenn=1;
        if(command.startsWith("NLN"))
            tokenn = 2;
         else if(command.startsWith("ILN"))
            tokenn = 3;
        else if(command.startsWith("FLN"))
            tokenn = 1;
        String add = StringUtil.messeToken(command,tokenn);
        String status =  StringUtil.messeToken(command,tokenn-1);
        Member mem = equalsMember(add);
        mem.getStatus().setStatus(status);
        if(tokenn != 1) { // not FLN
            String name = StringUtil.messeToken(command,tokenn+1);
            if(! mem.getName().equals(name)) {
                mem.rename(name);
                messe.changedName(mem);
            }
        }
        return (Member)mem.clone();
    }
    
    /**
     * �����o���X�g���擾���܂��B�Ȃ��A�����f�[�^�̃R�s�[�ł�����Member�I�u�W�F�N�g
     * �̓����f�[�^�̔z��ւ̎Q�Ƃ�ێ����Ă���킯�ł͂���܂���B
     * �Ȃ��A���̃��\�b�h���g���ƁA�I�t���C�����[�U���z��̌���ɕ��Ԃ悤�ɂȂ��Ă��܂��B
     * @return �R�s�[���ꂽ�����o���X�g
     */
    public Member[] getList() {
        Member[] newList = new Member[list.length];
        /* �I�t���C�����[�U�͌�납�疄�߂Ă���
         * �I�����C�����[�U�͑O����߂Ă��� 	*/
        int j = newList.length - 1;
        int i = 0;
        for(int k=0; k < newList.length;k++) 
            if(list[k].getStatus().getStatusCommand().equals("FLN"))
                 newList[j--] = (Member)list[k].clone();
             else 
                 newList[i++] = (Member)list[k].clone();
        return newList;
    }
    
    /**
     * <p>�����Ŏw�肵���O���[�v�ɏ������Ă��郁���o���X�g��Ԃ��܂��B</p>
     * �Ȃ��A���̃��\�b�h���g���ƁA�I�t���C�����[�U���z��̌���ɕ��Ԃ悤�ɂȂ��Ă��܂��B
     * @see #getList()
     * @param groupeName �O���[�v�����w�肵�܂��B�Ȃ��Anull��󕶎�����w�肷��ƁA�O���[�v�Ȃ��̐l
     * ��Ԃ��܂��B
     * @return �O���[�v�̔z��
     */
    public Member[] getList(String groupeName) {
        // ���O����O���[�v�L�[���󂯎��
        String key = "0";
        if(groupeName == null || groupeName.equals("") || !groupe.containsValue(groupeName)) {
            key = "0";
        } else {
            Enumeration e = groupe.keys();
            for(int i=0; i < groupe.size(); i++) {
                Object keye = e.nextElement();
                if(groupe.get(keye).equals(groupeName)) {
                    key = keye.toString();
                    break;
                }
            }
        }
        Vector dest = new Vector(); // �z��̒��g��Member
        for(int i = 0; i < list.length; i++) {
            if(isGroupe(list[i],key)) 
                if(list[i].getStatus().getStatusCommand().equals("FLN"))
                    dest.add(dest.size(),list[i]);
                else 
                    dest.add(0,list[i]);
        }
        Member[] destArray = new Member[dest.size()]; 
        for(int i = 0; i < dest.size(); i++) 
            destArray[i] = (Member)dest.get(i);
        return destArray;
    }
    
    /**
     * <p>�����Ŏw�肵��Member���A�����Ŏw�肵��Key�̃����o���ǂ������ׂ܂��B</p>
     * ���̃��\�b�h���g���ČĂяo�����ꍇ�A���d�O���[�v�o�^����Ă郁���o�ɂ��Ή������Ă��܂��B
     * @param mem �����o
     * @param key �L�[
     * @return�@�����Ŏw�肵���O���[�v�ɏ������Ă����ꍇtrue
     */
    private static boolean isGroupe(Member mem,String key) {
        String memKey = mem.groupeKey;
        if(key.equals(memKey))
            return true;
        else if(memKey.indexOf(',')==-1)
            return false;
        else {
            int index = 0;
            int oldIndex = 0;
            while((index = memKey.indexOf(',',index+1))!=-1)
                if(memKey.substring(oldIndex,index).equals(key))
                    return true;
                else 
                    oldIndex = index + 1;
            return memKey.substring(oldIndex).equals(key);
        }
    }
    
    /**
     * �O���[�v�ꗗ���擾���܂�
     * "�O���[�v�Ȃ�"�̃O���[�v������܂��B
     * @return �O���[�v�ꗗ(�O���[�v��)��String�̔z��`���ŕԂ��B
     */
    public String[] getGroupes() {
        String[] strings = new String[groupe.size() + 1];
        Enumeration e = groupe.keys();
        for(int i=0; i < strings.length - 1;i++)
            strings[i] = (String)groupe.get( e.nextElement() );
        strings[strings.length - 1] = "�O���[�v�Ȃ�";
        return strings;
    }
    
    /**
     * toString()���\�b�h�̃I�[�o���C�h�ł��B
     * [�����o��S���\�����A���̌�O���[�v���X�g��\�����܂��B
     */
    public String toString() {
        String buf = "[list:";
        for(int i=0; i< list.length;i++)
            buf += list[i] + ",";
        buf += "][groupe:";
        String[] gr = getGroupes();
        for(int i=0; i < gr.length;i++)
            buf += gr[i] + ",";
        return buf + "]";
    }
    
    /**
     * �����Ŏw�肵���A�h���X����v���郁���o��T���A�擾���܂��B
     * @param address ���[���A�h���X
     * @return �A�h���X����v���������o�B�����o�����Ȃ��ꍇnull��Ԃ��܂��B
     */
    public Member equalsMember(String address) {
        for(int i=0; i< listnum && list[i]!=null ; i++)
            if(list[i].getAddress().equals(address))
                return list[i];
        return null;
    }
    
    /**
     * �����Ŏw�肵�������o�̏�Ԃ�ύX���܂��B
     * @param member ��Ԃ�ύX�����������o
     */
    public void chengeMember(Member member) {
        Member old  = equalsMember(member.getAddress());
        old.setMember(member);
    }
}