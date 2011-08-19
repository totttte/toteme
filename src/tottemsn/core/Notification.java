/**
 * �쐬��: 2006/08/07
 */
package tottemsn.core;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.Vector;

/**
 * ���b�Z���W���[��Notification server�Ƃ̂��Ƃ�ɂ��Ăɂ��āA�������܂��B
 */
class Notification extends Session {
    
    /**	�ڑ����悤�Ƃ��Ă��郁�b�Z���W���[�T�[�o�A�h���X�ł��B */
    public static final String SERVER_ADDRESS = "messenger.hotmail.com";
    /**	�ڑ����悤�Ƃ��Ă��郁�b�Z���W���[�T�[�o�|�[�g�ł��B	*/
    public static final int SERVER_PORT = 1863;
    
    private Messenger messe;
    private TrID trID;
    // �X�C�b�`�{�[�h�Z�b�V�������Ǘ����܂��B
    private Vector switchboards;
    /*	�Z�b�V����������{�[�h���Ǘ����܂��B�Z�b�V�����͕��Ă��{�[�h�͕ێ����܂��B
     *	��x�I�������Z�b�V�����ł��A���������o�\���̃Z�b�V�����̏ꍇ�A�̂̃{�[�h�������p���܂��B
     *
     *	�܂�A��̓I�ȗ�Ƃ��āA�Q���҂��P�l�̃E�B���h�E�ő��肪��x�E�B���h�E�����Ȃǂ���
     *	�Z�b�V�������I��������A�܂��E�B���h�E���J���Z�b�V�������J�n���Ă��V����Board�C���X�^���X�𐶐�
     *	���Ȃ��Ƃ������Ƃł��B
     *	�e�[�u����key�ɂ́A���̃Z�b�V�����ɍŌ�܂ł����l�̃A�h���X�A
     *	value�ɂ�board�C���X�^���X�����ꂼ��i�[����Ă��܂��B
     */
    private Hashtable oldBoard;
    
    private SendQueue que;
    Notification(Messenger messe) {
        this.messe = messe;
        trID = new TrID();
        switchboards = new Vector();
        oldBoard = new Hashtable();
    }
    
    void connect() {
        try {
            socket = new Socket(SERVER_ADDRESS , SERVER_PORT);
        } catch (UnknownHostException e) {
            throw new MessengerException("���b�Z���W���[�̃T�[�o�ɐڑ��ł��܂���B");
        } catch (IOException e) {
            throw new MessengerException("�l�b�g�Ɍq�����Ă��Ȃ��\��������܂��B");
        }
        Sender sender = new NotificationSender(messe,trID,this);
        que = sender.getSendQueue();
        Reciever reciever = new NotificationReciever(messe, trID,this,sender);
        sender.start();
        reciever.start();
    }
    
    /**
     *�@switchboard�̃Z�b�V������ǉ����܂��B
     */
    void addSession(int key, String key2, String ipaddress, int port) {
        Switchboard sb = new Switchboard(messe,key,key2,this);
        sb.connect(ipaddress, port);
        switchboards.add(sb);
    }
    
    /**
     * �Z�b�V��������܂��B
     * @param sb
     */
    void removeSession(Switchboard sb) {
        Board board = sb.getBoard();
        if(board.getNumber() == 1)
            board.removeParticipant(board.getMember()[0].getAddress());
        if(!oldBoard.containsValue(sb.getBoard()))
            oldBoard.put(sb.getBoard().getLastAddress() ,sb.getBoard());
        sb.close();
        switchboards.remove(sb);
        sb = null;
    }
    
    /**
     * ���ݐڑ����Ă���switchboard-session�̐���Ԃ��܂��B
     */
    int switchBoardCount() {
        return switchboards.size();
    }
    
    /**
     * �����{�[�h�̐���Ԃ��܂��B
     */
    int closedBoardCount() {
        return oldBoard.size();
    }
    
    /**
     * Notification��SendQueue��Ԃ��܂�
     */
    SendQueue getSendQueue() {
        return que;
    }
    
    /**
     * <p>�w�肳�ꂽboard������SwichBoard�Z�b�V������SendQueue��Ԃ��܂��B</p>
     * �������A����board��SwitchBoard����ؒf���ꂽBoard��������A�Đڑ������݂܂��B
     */
    SendQueue getSendQueue(Board board) {
        // ����Switchboard�ɐڑ����Ă�����̂��ɒ��ׂ�
        for(int i =0; i < switchboards.size(); i++)
            if(((Switchboard)switchboards.get(i)).getBoard().equals(board))
                return ((Switchboard)switchboards.get(i)).getQueue();
        // �������A�ڑ����؂ꂽBoard��������B
        if(oldBoard.containsKey(board.getLastAddress()))
            return newFreeSB(messe.memberlist.equalsMember( board.getLastAddress() )).getQueue();
        throw new RuntimeException("��b���J�n�ł��܂���ł����B");
    }
    
    // ����Board��SwitchBoard�ɐڑ�����Ă��邩
    boolean isConnectSB(Board board) {
        // ����Switchboard�ɐڑ����Ă�����̂��ɒ��ׂ�
        for(int i =0; i < switchboards.size(); i++)
            if(((Switchboard)switchboards.get(i)).getBoard().equals(board))
                return true;
        return false;
    }
    
    /**
     * <p>�V���ɁA�����Ŏw�肵���Q���҂Ǝ����̌v�Q�l������SwitchBoard�T�[�o�̊m����v�����A
     * �V����SwitchBoard�Z�b�V�������m���ł�����A�����Ŏw�肵���Q���҂����҂��܂��B</p>
     * 
     * ���̃��\�b�h�ł́ABoard�̊��蓖�Ă��s���܂��B
     * ���b�Z���W���[�ɂ����āA�N���ɘb��������Ƃ��̏����́A�܂�SwitchBoard�Z�b�V������
     * �쐬���A���ꂩ��N�������҂���Ƃ����`�ł���B�]���āA��ɎQ���҂������݂̂̃t���[��
     * SwitchBoard�Z�b�V���������ׂ��ł���B
     *
     * <I>���̃��\�b�h���Ăяo�����X���b�h��SwitchBoard�������������܂�
     * ��莞�ԃX���[�v����̂Œ��ӂ��K�v�ł��B�����ł́A���悻300�~���b���x�������Ă��܂��B</I>
     */
    Switchboard newFreeSB(Member mem) {
        que.add("XFR //TrID SB");
        que.add("//Wait");
        // �N�����Ȃ�SwitchBoard�Z�b�V�������ł���܂őҋ@
        try {
            while( !(switchboards.size() != 0 && 
                     ((Switchboard)switchboards.lastElement()).getBoard().getNumber() == 0
                     && ((Switchboard)switchboards.lastElement()).finishedInit()))
                Thread.sleep(20);
        } catch (InterruptedException e) {
        }
        // Board�̊��蓖��
        Switchboard freesb = (Switchboard)switchboards.lastElement();
        if(oldBoard.containsKey(mem.getAddress())) { // ���ɓ��������o��Board����������A
            freesb.setBoard((Board)oldBoard.get(mem.getAddress()));
            oldBoard.remove(mem.getAddress());
        }
        freesb.getQueue().add("CAL //TrID " + mem.getAddress());
        freesb.getQueue().add("//Lock"); // ���b�N�������Ȃ��ƁA�N�����҂��Ȃ��ŉ�b�����Ă��܂�
        return freesb;
    }
}