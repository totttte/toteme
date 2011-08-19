/**
 * �쐬��: 2006/08/09
 */
package tottemsn.core;

import java.io.IOException;
import java.net.Socket;

/**
 * <p>Switchboard Server�Ƃ̂��Ƃ��S������N���X�ł��B</p>
 * ���̃N���X�ŁASwitchboardSender��SwitchboardReciever���Ǘ����܂��B
 * <p>
 * ���̃N���X���S������̂́A�����܂ł��\�P�b�g�ʐM�ɂ��SwitchBoard�T�[�o�Ƃ̂��Ƃ�݂̂ł��B
 * �`���b�g���O��A���̃Z�b�V�����ɐڑ����Ă��郁���o�Ȃǂ̏��́ABoard���S�����܂��B
 * </p>
 * @see tottemsn.core.Board
 */
class Switchboard extends Session {

    int key;
    String key2;
    private Board board;
    Notification noti;
    private SwitchboardSender sender;
    SwitchboardReciever reciever;
    private boolean finish = false; // ��������ƏI�������USR�R�}���h���M�̗L��
    
    Switchboard(Messenger messe, int key, String key2, Notification noti) {
        this.messe = messe;
        this.key = key;
        this.key2 =  key2;
        this.noti = noti;
        trID = new TrID();
        board = new Board(messe.memberlist);
    }
    
    /* �Z�b�V�����ڑ�	 */
    void connect(String address, int port) {
        try {
            socket = new Socket(address,port);
            StringUtil.println("[Info:SB�ɐڑ�]"+socket.getInetAddress());
        } catch (IOException e) {
            throw new MessengerException("�l�b�g�Ɍq�����Ă��Ȃ��\��������܂��B");
        }
        sender = new SwitchboardSender(messe,trID,this);
        reciever = new SwitchboardReciever(messe,trID,this,sender);
        sender.start();
        reciever.start();
        reciever.setBoard(board);
    }
    
    /**	<I>��M�S����SwitchBoardReciever�ւ�board�̎Q�Ƃ����̃��\�b�h��ʂ��čs���܂�</I>
     * �R���X�g���N�^�ŎQ�Ƃ�n���Ȃ��̂ɂ́A�ȉ��̗��R������܂��B
     * <ul><li>SwitchBoard�̃Z�b�V�������m���������_�ł́A�Z�b�V�����̃��X�g���擾���Ă��Ȃ��B</li></ul>
     * {@link #connect(String, int)}���s�Ȃ�����A���̃��\�b�h���Ăяo���Ă��������B
     */
    void setBoard(Board board) {
        this.board = board;
        reciever.setBoard(board);
    }
    
    Board getBoard() {
        return board;
    }
    
    /**	�\�P�b�g����܂��B */
    void close() {
        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    /*�@�p�P�b�g���M�p�L���[�̎Q�Ƃ�n���܂��B     */
    SendQueue getQueue() {
        return sender.que;
    }
    
    /**	SwitchBoard�̍ŏ��̃��b�Z�[�W����M�������ǂ���	*/
    boolean finishedInit() {
        return finish;
    }
    
    /**	SwitchBoard�̏���������сA�ŏ���USR�R�}���h�𑗐M�����Ƃ��A���̃��\�b�h���P�x�����Ăяo���܂�
     */
    void setFinished() {
        if(finish)
            throw new MessengerException("�v���O�����̃G���[�ł��B\n" +
            		"1�x�����Ăяo���Ȃ��͂��̃��\�b�h���Q�x�Ăяo���Ă��܂��B");
        finish = true;
    }
}
