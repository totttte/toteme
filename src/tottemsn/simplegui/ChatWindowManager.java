/**
 * �쐬��: 2006/09/03
 */
package tottemsn.simplegui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.JFrame;

import tottemsn.core.Board;
import tottemsn.core.Member;


//TODO �J���ĊJ�����Ƃ��A�܂����ꂩ��A���������B hashtable���珑������
/**
 * ���t�@�N�^�����O�ŏd�v�Ώ�
 * �`���b�g�E�B���h�E���Ǘ�����N���X�ł��B
 * @see Board
 */
public class ChatWindowManager {
    
    Member[] list;
    BasedMainWindow owner;
    // 1��1�̉�b�E�B���h�E���J���Ă��邩�ǂ����@�z��̕��я��́Alist�ɓ���
    boolean[] isInvite;
    
    private Hashtable manager; // �S�Ẵ`���b�g�E�B���h�E��ێ� key:board value:basedchatwindow
    
    ChatWindowManager(BasedMainWindow owner) {
        if(owner == null) //�@�X�L���ݒ�̂��ߐ�ɃN���X�������ꍇ�����邽�߁B
            return;
        setInit(owner);
    }
    
    void setInit(BasedMainWindow owner) {
        this.owner = owner;
        this.list = owner.list.getList();
        this.isInvite = new boolean[list.length];
        manager = new Hashtable();        
    }

   //TODO ���t�@�N�^�����O�Ώۂ����ύX���͐T�d�ɁE�E�E
    /*	�V���ɁA�E�B���h�E�𐶐����܂��B
     * 
     */
    void createNewWindow(final Board board, boolean isVisible, int frameType, Member mem) {
        if(isTalking(mem)) { // �R�l�N�V�������Q����Ƃ�������
        /* �������ɁA���݂����b�������悤�Ƃ����Ƃ��A���������o�̃R�l�N�V�������Q���݂���ꍇ
         * ������܂��B���̂Ƃ��́A��board��V����board�ŏ㏑�������A�V�����E�B���h�E�͊J���Ȃ�
         * �悤�ɂ��܂� */
            Enumeration e = manager.keys();
            Board old;
            while(e.hasMoreElements()) {
                if((old = (Board)e.nextElement()).getNumber() == 1) {
                    if(old.getLastAddress().equals(mem.getAddress())) {
                        //  manager.get(old) ���Ȃ���null�ɂȂ��Ă��܂��ׁA�Ƃ肠�������`�T��
                        Object[] values = manager.values().toArray();
                        BasedChatWindow oldwindow = null;
                        for(int i = 0; i < values.length; i++) {
                            if(((BasedChatWindow)values[i]).
                                    board.getLastAddress().equals(old.getLastAddress())) {
                                oldwindow = ((BasedChatWindow)values[i]);
                                break;
                            }
                        }
                        if(oldwindow != null) {
                            manager.remove(old);
                            manager.put(board,oldwindow);
                            oldwindow.board = board;
                            owner.messe.bye(old); // SwitchBoard�Z�b�V��������A�P�ɂ���
                            return;
                        }
                    }
                }
            }
        }  // end if(isTalking(mem))
        
        final BasedChatWindow chatwindow = new BasedChatWindow(board,owner);
        chatwindow.addShallMember(mem);
        JFrame frame = setSkin(chatwindow);
        chatwindow.setFrame(frame);
        manager.put(board,chatwindow);
        frame.setExtendedState(frameType);
        isInvite[getMemberNumber(mem)] = true;
        frame.addWindowListener( new WindowAdapter(){
            public void windowClosed( WindowEvent we ){
                close();
            }
            
            public void windowClosing( WindowEvent we) {
                close();
            }
            
            public void close() {
                if(board.getNumber()==1) {
                    int memberNumber = getMemberNumber(board.getMember()[0]);
                    if(memberNumber != -1) // ���o�^�҂݂̂̏ꍇ
                        isInvite[memberNumber] = false;
                } else if(board.getNumber() == 0) { // ���҂���O�ɕ���ꂽ��
                    isInvite[getMemberNumber(
                           owner.list.equalsMember(chatwindow.infomationBar.getName()))]
                           = false;
                }
                owner.messe.bye(board);
                manager.remove(board);                
            }
        });
        frame.setVisible(isVisible);

    }
    
    /**
     * @param member �������ɂ́A�V���ɂ��ꂩ�珵�҂��������o���w�肵�܂��B���Ҏ��ȊO��null
     */
    BasedChatWindow getMangaer(Board board, Member member) {
        // �܂�board����ӂɔF������ boar�������Ԃ𒲂ׂ܂�
        Object[] windowob = manager.values().toArray();
        for(int i = 0; i < windowob.length; i++){
            BasedChatWindow old = (BasedChatWindow)windowob[i];
            if(old.board.getTime() == board.getTime())
                return old;
        }

        // ���Ƀ}�l�[�W���ɓo�^����Ă���E�B���h�E�𒲂ׂ܂�
        BasedChatWindow window =  (BasedChatWindow)manager.get(board);
        if(window != null)
            return window;
        // �P�΂P�̃����o�𒲂ׂ܂�
        if(board.getNumber() == 1 && isTalking(board.getMember()[0])){
            Object[] o = manager.values().toArray();
            for(int i = 0; i < o.length;i++)
                if(((BasedChatWindow)o[i]).board.getNumber() == 1)
                    if(((BasedChatWindow)o[i]).board.getMember()[0].equals(board.getMember()[0]))
                        return (BasedChatWindow)o[i];
        }
        // addParticipant���́@�܂������o�����Ȃ����𒲂ׂ܂�
        Member mem = owner.list.equalsMember(board.getLastAddress());
        if(board.getNumber() == 0) {
            Object[] o = manager.values().toArray();
            for(int i = 0; i < o.length;i++) {
                if(((BasedChatWindow)o[i]).board.getNumber() == 0) {
                    String a = ((BasedChatWindow)o[i]).board.getLastAddress();
                    String b = member.getAddress();
                    if(a.equals(b))
                        return (BasedChatWindow)o[i];
                }
            }            
        }
        return null;
    }
    
    //�@���̃����o�Ƃ̃`���b�g�E�B���h�E���J���Ă��邩�ǂ���
    boolean isTalking(Member mem) {
        for(int i = 0; i< list.length; i++)
            if(list[i].equals(mem)) 
                return isInvite[i];
        return false;
    }
    
    // �t�B�[���h�̃����o�̉��Ԗڂ�
    int getMemberNumber(Member mem) {
        for(int i = 0; i < list.length; i++)
            if(list[i].equals(mem))
                return i;
        return -1;
    }
    
    /**
     * ���̃��\�b�h���I�[�o�[���C�h���āA�X�L����ύX�����܂��B
     */
    public JFrame setSkin(BasedChatWindow window) {
        return new JFrame();
    }
}