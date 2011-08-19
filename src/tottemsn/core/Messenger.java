/**
 * 作成日: 2006/08/07
 */
package tottemsn.core;

import java.awt.Color;
import java.awt.Font;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Vector;
/**
 * <p>MSNメッセンジャーの操作を行うクラスです。</p>
 * このクラスは外部パッケージ公開向けクラスです。メッセンジャーに関する操作は、このクラスを通して行います。
 */
public class Messenger {
        
    private Vector listeners;
    UserInfo user;
    MemberList memberlist;
    private Notification client;
    HotMail hotMail;
    
    /**
     * 新たにメッセンジャークラインアントを作成します。
     * @param address アカウント(メールアドレス)を指定します。
     * @param password パスワードを指定します。
     * @param status サインインするときの状態を指定します。
     */
    public Messenger(String account, String password, String status) {
        listeners = new Vector();
        user = new UserInfo(account , password, status);
    }
    
    /**
     * 新たにメッセンジャークラインアントを作成します。
     * なお、このコンストラクタを呼び出し、サインインしたときは状態は
     * オンラインになります。
     * @param address アカウント(メールアドレス)を指定します。
     * @param password パスワードを指定します。
     */
    public Messenger(String account, String password) {
        this(account, password, "NLN");
    }
    
    /**
     * <p>メッセンジャーに接続します。</p>
     * このメソッドを呼び出して、ソケット通信によってメッセンジャーサーバとの対話を
     * 開始します。
     */
    public void connect() {
        client = new Notification(this);
        client.connect();
    }
    
    /**
     * このメッセンジャーを繋ごうとしているユーザのデータです。
     */
    public UserInfo getUserInfo() {
        return user;
    }
    
    /**
     * <p>新たにメッセンジャーリスナーを登録します。</p>
     * 登録したクラスには、{@link MessengerListener}の実装したメソッドに
     * メッセンジャー関連のさまざまな通知をします。
     */
    public void addMessengerListener(MessengerListener l) {
        listeners.add(l);
    }
    
    /**
     * メッセンジャーリスナーを削除します。
     */
    public void removeMessengerListener(MessengerListener l) {
        listeners.remove(l);
    }
    
    /**
     * リスナーの数を数えます。
     */
    public int getListenerCount() {
        return listeners.size();
    }
    
    /**
     * 指定したメンバを招待し、新たにSwitchBoardセッションを確立します。
     * @param member 招待したいメンバ
     */
    public Board invitingPrincipal(Member member) {
        Switchboard newSB = client.newFreeSB(member);
        Board board = newSB.getBoard();
        return board;
    }
    
    /**
     * <p>指定したメンバを、指定したboardに招待します。</p>
     * 既に会話しているウィンドウに新たに誰かを招待するときに使ってください。
     * @param board どのboardに呼ぶか
     * @param member 招待したいメンバ
     */
    public void invitingPrincipal(Board board, Member member) {
        SendQueue que = client.getSendQueue(board);
        que.add("CAL //TrID " + member.getAddress());
    }
    
    /**
     *	自分の状態を変更するということです。
     *	@param status 変更したい状態
     */
    public void changeStatus(UserStatus status) {
        String com = status.getStatusCommand();
        SendQueue que = client.getSendQueue();
        if(status.getStatusCommand().equals("FLN"))
            com = "HDN";
            que.add("CHG //TrID " + com + " 0");
    }
    
    /**
     *	自分の名前を変更するということです。
     *	@param name 変更したい名前
     */
    public void changeName(String name) {
        try {
            if(name == null || name.equals("")) 
                name = user.getAccount();
            name = URLEncoder.encode(name, "UTF-8");
            // URLEncodeと違うところは　+ を %20に変えないといけないところ。
            name = StringUtil.replaceAll(name , "+" , "%20");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        SendQueue que = client.getSendQueue();
        que.add("PRP //TrID MFN " + name);
    }
    
    /**
     * 自分のパーソナルメッセージを変更します。
     * @param msg パーソナルメッセージ
     */
    public void changePersonalMessage(String msg) {
        SendQueue que = client.getSendQueue();
        que.add(XMLPayload.makeUUXCommand(msg));
    }
    
    /**
     * <p>メッセージを送信します。</p>
     * なお、MSNメッセンジャーにおいてメッセージのサイズの上限が決められています。
     * それを超えて送信しようとするとサーバから切断されるため、本メッセンジャーでは、
     * 自動的に分割してメッセージを送信するようにします。
     * @param board どのセッションに送信したいか指定します
     * @param message 送信したいテキスト
     * @param font 送信したいテキストのフォント
     * @param color 送信したいテキストの色
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
     * 自分が会話から退席します
     * @param board どのセッションから退席したいか
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
     * サインアウトします
     */
    public void singout() {
        client.getSendQueue().add("OUT");
    }
    
    public HotMail getHotMail() {
        return hotMail;
    }
    
    
    //以下リスナー

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