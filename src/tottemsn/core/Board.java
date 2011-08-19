/**
 * �쐬��: 2006/08/10
 */
package tottemsn.core;

import java.util.Date;
import java.util.Vector;
/**
 * <p>���b�Z���W���[�̉�b���O��\������Ƃ��Ɏg���܂��B</p>
 * �Ⴆ�΁A���̃Z�b�V�����ł̉�b���O��A���݂̎Q���҈ꗗ�Ȃǂł��B
 * 
 * <p>
 * �@SwitchBoard��Board�́A�K�������΂ɂȂ�Ȃ����Ƃɒ��ӂ��Ă��������B<br>
 * SwitchBoard�́A��b���ł����肪���b�Z���W���[�̃`���b�g�E�B���h�E�����΁A
 * �Z�b�V�������I�����܂��B�܂��A���̎��ԉ�b���Ȃ��ƃZ�b�V�������I�����܂��B<br>
 * �@�������A��ʂ̃��[�U�����猩��͈͂ł́ASwitchBoard�Z�b�V�����̏I���A�Đڑ��̗L����
 * �ʒm������K�v�͂���܂���B�܂��A�Z�b�V�������I����������Ƃ����Ă��̂��тɁA�E�B���h�E��
 * �J���Ȃ����̂ł̓��[�U�ɂƂ��ĕ��S�ɂȂ�܂��B
 * </p>
 * <p>
 * ���̂��߁A�`���b�g�E�B���h�E�̉�b���O�\���̈�̂��߂̃N���X���K�v�ɂȂ��Ă��܂����B
 * SwitchBoard�Z�b�V�����ƃ��[�U�����猩��`���b�g�E�B���h�E�Ƃ̊Ԃɂ���Board�N���X�𒇉��
 * ���Ƃɂ���āASwitchBoard�̐ؒf��Đڑ����u���b�N�{�b�N�X�����܂��B
 * </p>
 * 
 */
public class Board {
    private Vector members; //�Q����(�C���X�^���X��Member�N���X)
    private MemberList list; // ���[�U���m���Ă��郁���o�̏ꍇ�̏Ƃ炵���킹�p
    /*  Chat�܂���String�̃C���X�^���X������
     *  ������́A(XX���ސȂ��܂����Ȃǂ�)�C���t�H���[�V�����ł��B
     */
    Vector logs;
    /*	��ԍŌ�܂Ŏc���Ă����l�̃A�h���X�ł��B
     * ����́A�Z�b�V�����ĊJ���Ɋm�F���邽�߂̂��̂ł��B
     */
    private String lastAddress = "";
    /*	����Board���������ꂽ���Ԃ�ێ����܂��B��ӂ�board��F�����邽�߂Ɏg���܂��B */
    long startTime;
    
    /**
     * �O���p�b�P�[�W����ABoard�C���X�^���X�ւ̃A�N�Z�X�́A
     * {@link MessengerListener}�����������Ƃ��̈�������s���Ă��������B
     * @param list Messenger�N���X��������p���������o���X�g�������܂��B
     */
    Board(MemberList list) {
        this.list = list;
        this.members = new Vector();
        logs = new Vector();
        startTime = new Date().getTime();
    }
    
    /**
     * �Q���҂�ǉ����܂��B
     */
    Member addParticipant(Member member) {
        if(!members.contains(member))
            members.add( member);
        lastAddress = member.getAddress();
        return member;
    }
    
    /**
     * <p>���̃E�B���h�E���������ꂽ���Ԃ�Ԃ��܂�</p>
     * ��ӂ�Board��F�����邽�߂Ɏg���܂��B
     */
    public long getTime() {
        return this.startTime;
    }
    
    /**
     * �Q���҂���l�ސȂ��܂�
     */
    Member removeParticipant(String address) {
        Member mem = list.equalsMember(address);
        if(mem == null)
            mem = new Member(address,"",1);
        members.remove(mem);
        if(members.size()==0)
            lastAddress = address;
        return mem;
    }
    
    /**
     * �����Ŏw�肵�������o������Board�ɂ��邩
     * @return ����Board�̎Q���҂Ɉ����Ŏw�肵�������o�������true
     */
    boolean contains(Member mem) {
        return members.contains(mem);
    }
    
    /**
     * ���ݎQ�����Ă��郁���o�̔z���Ԃ��܂��B
     * @return ���ݎQ�����Ă��郁���o�̔z��
     */
    public Member[] getMember() {
        Member[] mem = new Member[members.size()];
        members.copyInto(mem);
        return mem;
    }
    
    /**
     * �Q���҂�Ԃ��܂�
     * @return �������܂߂Ȃ��Q���Ґ�
     */
    public int getNumber() {
       return members.size(); 
    }
    
    /**
     * ����Board�C���X�^���X�Ɏc���Ă��鍡�܂ł̔�������
     * �Ԃ��܂��B
     * @return ���܂ł�Log�S��
     */
    public String getLog() {
        String texts = "";
        for(int i = 0; i < logs.size() ; i++) 
            texts += ((Chat)logs.get(i)).getText();
        return texts;
    }
    
    /**
     * ����Board���̔��胁�\�b�h
     * @return ���������o�ō\�������Board�Ȃ�Atrue��Ԃ��܂��B
     * �܂�Amembers�̕��я����قȂ��Ă��Ă��A����Board�Ƃ݂Ȃ���܂��B
     * �������ALastMember�͓����łȂ���΂����܂���B
     */
    public boolean equals(Object obj) {
        if(obj instanceof Board) {
            Board argBoard = (Board)obj;
            Vector comp = argBoard.members;
            if(members.size() != comp.size())
                return false;
            for(int i = 0; i < comp.size(); i++)
                if(! members.contains( comp.get(i) ) )
                    return false;
            return	this.lastAddress.equals(argBoard.lastAddress);
        } else
            return super.equals(obj);
    }
    
    /**
     * ���������o�i���я��͊֘A���Ȃ��j�A�Ō�̐l�������A�h���X�ɂȂ�悤�ȁA
     * hashcode�l��Ԃ��B
     */
    public int hashCode() {
        int code = lastAddress.hashCode();
        int start = code;
        for(int i = 0; i < members.size(); i++) 
            if(members.get(i).hashCode() != start)
                code += members.get(i).hashCode();
        return (int)(code %= Integer.MAX_VALUE);
    }
    
    public String getLastAddress() {
        return lastAddress;
    }

    Chat addLog(Messages msg) {
        Chat chat = new Chat(msg);
        logs.add(chat);
        return chat;
    }    
}