/**
 * �쐬��: 2006/08/07
 */
package tottemsn.core;

import java.awt.Color;
import java.awt.Font;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Vector;
/**
 * <p>MSN���b�Z���W���[�̑�����s���N���X�ł��B</p>
 * ���̃N���X�͊O���p�b�P�[�W���J�����N���X�ł��B���b�Z���W���[�Ɋւ��鑀��́A���̃N���X��ʂ��čs���܂��B
 */
public class Messenger {
        
    private Vector listeners;
    UserInfo user;
    MemberList memberlist;
    private Notification client;
    HotMail hotMail;
    
    /**
     * �V���Ƀ��b�Z���W���[�N���C���A���g���쐬���܂��B
     * @param address �A�J�E���g(���[���A�h���X)���w�肵�܂��B
     * @param password �p�X���[�h���w�肵�܂��B
     * @param status �T�C���C������Ƃ��̏�Ԃ��w�肵�܂��B
     */
    public Messenger(String account, String password, String status) {
        listeners = new Vector();
        user = new UserInfo(account , password, status);
    }
    
    /**
     * �V���Ƀ��b�Z���W���[�N���C���A���g���쐬���܂��B
     * �Ȃ��A���̃R���X�g���N�^���Ăяo���A�T�C���C�������Ƃ��͏�Ԃ�
     * �I�����C���ɂȂ�܂��B
     * @param address �A�J�E���g(���[���A�h���X)���w�肵�܂��B
     * @param password �p�X���[�h���w�肵�܂��B
     */
    public Messenger(String account, String password) {
        this(account, password, "NLN");
    }
    
    /**
     * <p>���b�Z���W���[�ɐڑ����܂��B</p>
     * ���̃��\�b�h���Ăяo���āA�\�P�b�g�ʐM�ɂ���ă��b�Z���W���[�T�[�o�Ƃ̑Θb��
     * �J�n���܂��B
     */
    public void connect() {
        client = new Notification(this);
        client.connect();
    }
    
    /**
     * ���̃��b�Z���W���[���q�����Ƃ��Ă��郆�[�U�̃f�[�^�ł��B
     */
    public UserInfo getUserInfo() {
        return user;
    }
    
    /**
     * <p>�V���Ƀ��b�Z���W���[���X�i�[��o�^���܂��B</p>
     * �o�^�����N���X�ɂ́A{@link MessengerListener}�̎����������\�b�h��
     * ���b�Z���W���[�֘A�̂��܂��܂Ȓʒm�����܂��B
     */
    public void addMessengerListener(MessengerListener l) {
        listeners.add(l);
    }
    
    /**
     * ���b�Z���W���[���X�i�[���폜���܂��B
     */
    public void removeMessengerListener(MessengerListener l) {
        listeners.remove(l);
    }
    
    /**
     * ���X�i�[�̐��𐔂��܂��B
     */
    public int getListenerCount() {
        return listeners.size();
    }
    
    /**
     * �w�肵�������o�����҂��A�V����SwitchBoard�Z�b�V�������m�����܂��B
     * @param member ���҂����������o
     */
    public Board invitingPrincipal(Member member) {
        Switchboard newSB = client.newFreeSB(member);
        Board board = newSB.getBoard();
        return board;
    }
    
    /**
     * <p>�w�肵�������o���A�w�肵��board�ɏ��҂��܂��B</p>
     * ���ɉ�b���Ă���E�B���h�E�ɐV���ɒN�������҂���Ƃ��Ɏg���Ă��������B
     * @param board �ǂ�board�ɌĂԂ�
     * @param member ���҂����������o
     */
    public void invitingPrincipal(Board board, Member member) {
        SendQueue que = client.getSendQueue(board);
        que.add("CAL //TrID " + member.getAddress());
    }
    
    /**
     *	�����̏�Ԃ�ύX����Ƃ������Ƃł��B
     *	@param status �ύX���������
     */
    public void changeStatus(UserStatus status) {
        String com = status.getStatusCommand();
        SendQueue que = client.getSendQueue();
        if(status.getStatusCommand().equals("FLN"))
            com = "HDN";
            que.add("CHG //TrID " + com + " 0");
    }
    
    /**
     *	�����̖��O��ύX����Ƃ������Ƃł��B
     *	@param name �ύX���������O
     */
    public void changeName(String name) {
        try {
            if(name == null || name.equals("")) 
                name = user.getAccount();
            name = URLEncoder.encode(name, "UTF-8");
            // URLEncode�ƈႤ�Ƃ���́@+ �� %20�ɕς��Ȃ��Ƃ����Ȃ��Ƃ���B
            name = StringUtil.replaceAll(name , "+" , "%20");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        SendQueue que = client.getSendQueue();
        que.add("PRP //TrID MFN " + name);
    }
    
    /**
     * �����̃p�[�\�i�����b�Z�[�W��ύX���܂��B
     * @param msg �p�[�\�i�����b�Z�[�W
     */
    public void changePersonalMessage(String msg) {
        SendQueue que = client.getSendQueue();
        que.add(XMLPayload.makeUUXCommand(msg));
    }
    
    /**
     * <p>���b�Z�[�W�𑗐M���܂��B</p>
     * �Ȃ��AMSN���b�Z���W���[�ɂ����ă��b�Z�[�W�̃T�C�Y�̏�������߂��Ă��܂��B
     * ����𒴂��đ��M���悤�Ƃ���ƃT�[�o����ؒf����邽�߁A�{���b�Z���W���[�ł́A
     * �����I�ɕ������ă��b�Z�[�W�𑗐M����悤�ɂ��܂��B
     * @param board �ǂ̃Z�b�V�����ɑ��M���������w�肵�܂�
     * @param message ���M�������e�L�X�g
     * @param font ���M�������e�L�X�g�̃t�H���g
     * @param color ���M�������e�L�X�g�̐F
     */
    public void sendMessage(Board board, String message, Font font, Color color) {
        message = StringUtil.replaceAll(message,"//","/-");
        message = StringUtil.replaceAll(message,"\n","\r");
        sendMessage0(board,message,font,color);
    }
    
    private void sendMessage0(Board board,String message,Font font, Color color) {
        SendQueue que = client.getSendQueue(board);
        FragmentString msg = Messages.outGoingMessage(message,font,color);
        que.add(msg.main);
        if(msg.other.equals(""))
            return;
        sendMessage(board,msg.other,font,color);        
    }
    
    /**
     * ��������b����ސȂ��܂�
     * @param board �ǂ̃Z�b�V��������ސȂ�������
     */
    public void bye(Board board) {
        if(! client.isConnectSB(board))
            return;
        SendQueue que = client.getSendQueue(board);
        que.add("//Unlock");
        que.add("OUT");
        que.add("//Closed");
    }
    
    /**
     * �T�C���A�E�g���܂�
     */
    public void singout() {
        client.getSendQueue().add("OUT");
    }
    
    public HotMail getHotMail() {
        return hotMail;
    }
    
    
    //�ȉ����X�i�[

    void getList(MemberList list) {
        for(int i = 0; i < listeners.size(); i++) 
            ((MessengerListener)listeners.get(i)).getList(list);
    }

    void changedStatus(Member member) {
        for(int i = 0; i < listeners.size(); i++)
            ((MessengerListener)listeners.get(i)).changedStatus(member);
    }

    void changedName(Member member) {
        for(int i = 0; i < listeners.size(); i++)
            ((MessengerListener)listeners.get(i)).changedName(member);
    }

    void getGroupe(String[] s) {
        for(int i = 0; i < listeners.size(); i++)
            ((MessengerListener)listeners.get(i)).getGroupe(s);
    }

    void finshSignIn(boolean isSuccess, Exception e) {
        for(int i = 0; i < listeners.size(); i++)
            ((MessengerListener)listeners.get(i)).finshSignIn(isSuccess, e);
    }

    void addParticipant(Board board,Member member) {
        for(int i = 0; i < listeners.size(); i++)
            ((MessengerListener)listeners.get(i)).addParticipant(board,member);
    }

    void removeParticipant(Board board,Member member) {
        for(int i = 0; i < listeners.size(); i++)
            ((MessengerListener)listeners.get(i)).removeParticipant(board,member);
    }

    void getChat(Chat chat, Board board) {
        for(int i = 0; i < listeners.size(); i++)
            ((MessengerListener)listeners.get(i)).getChat(chat,board);
    }
    
    void update(int type) {
        for(int i =0; i < listeners.size(); i++)
            ((MessengerListener)listeners.get(i)).update(type);
    }
    
    void errorMSG(MessengerException e) {
        for(int i = 0; i < listeners.size(); i++)
            ((MessengerListener)listeners.get(i)).errorMSG(e);
    }
    
    void finishSendMsg(Board board,boolean isSucess) {
        for(int i = 0; i < listeners.size(); i++)
            ((MessengerListener)listeners.get(i)).finishSendMsg(board,isSucess);
    }
}