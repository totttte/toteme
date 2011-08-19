/**
 * 作成日: 2006/08/09
 */
package tottemsn.core;

import java.io.IOException;
import java.net.Socket;

/**
 * <p>Switchboard Serverとのやりとりを担当するクラスです。</p>
 * このクラスで、SwitchboardSenderとSwitchboardRecieverを管理します。
 * <p>
 * このクラスが担当するのは、あくまでもソケット通信によるSwitchBoardサーバとのやりとりのみです。
 * チャットログや、このセッションに接続しているメンバなどの情報は、Boardが担当します。
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
    private boolean finish = false; // 初期化作業終了およびUSRコマンド送信の有無
    
    Switchboard(Messenger messe, int key, String key2, Notification noti) {
        this.messe = messe;
        this.key = key;
        this.key2 =  key2;
        this.noti = noti;
        trID = new TrID();
        board = new Board(messe.memberlist);
    }
    
    /* セッション接続	 */
    void connect(String address, int port) {
        try {
            socket = new Socket(address,port);
            StringUtil.println("[Info:SBに接続]"+socket.getInetAddress());
        } catch (IOException e) {
            throw new MessengerException("ネットに繋がっていない可能性があります。");
        }
        sender = new SwitchboardSender(messe,trID,this);
        reciever = new SwitchboardReciever(messe,trID,this,sender);
        sender.start();
        reciever.start();
        reciever.setBoard(board);
    }
    
    /**	<I>受信担当のSwitchBoardRecieverへのboardの参照もこのメソッドを通して行います</I>
     * コンストラクタで参照を渡さないのには、以下の理由があります。
     * <ul><li>SwitchBoardのセッションを確立した時点では、セッションのリストを取得していない。</li></ul>
     * {@link #connect(String, int)}を行なった後、このメソッドを呼び出してください。
     */
    void setBoard(Board board) {
        this.board = board;
        reciever.setBoard(board);
    }
    
    Board getBoard() {
        return board;
    }
    
    /**	ソケットを閉じます。 */
    void close() {
        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    /*　パケット送信用キューの参照を渡します。     */
    SendQueue getQueue() {
        return sender.que;
    }
    
    /**	SwitchBoardの最初のメッセージを受信したかどうか	*/
    boolean finishedInit() {
        return finish;
    }
    
    /**	SwitchBoardの初期化および、最初のUSRコマンドを送信したとき、このメソッドを１度だけ呼び出します
     */
    void setFinished() {
        if(finish)
            throw new MessengerException("プログラムのエラーです。\n" +
            		"1度しか呼び出さないはずのメソッドを２度呼び出しています。");
        finish = true;
    }
}
