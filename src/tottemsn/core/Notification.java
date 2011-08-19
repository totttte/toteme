/**
 * 作成日: 2006/08/07
 */
package tottemsn.core;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.Vector;

/**
 * メッセンジャーのNotification serverとのやりとりについてについて、実装します。
 */
class Notification extends Session {
    
    /**	接続しようとしているメッセンジャーサーバアドレスです。 */
    public static final String SERVER_ADDRESS = "messenger.hotmail.com";
    /**	接続しようとしているメッセンジャーサーバポートです。	*/
    public static final int SERVER_PORT = 1863;
    
    private Messenger messe;
    private TrID trID;
    // スイッチボードセッションを管理します。
    private Vector switchboards;
    /*	セッションを閉じたボードを管理します。セッションは閉じてもボードは保持します。
     *	一度終了したセッションでも、同じメンバ構成のセッションの場合、昔のボードを引き継ぎます。
     *
     *	つまり、具体的な例として、参加者が１人のウィンドウで相手が一度ウィンドウを閉じるなどして
     *	セッションが終了した後、またウィンドウを開きセッションを開始しても新たなBoardインスタンスを生成
     *	しないということです。
     *	テーブルのkeyには、そのセッションに最後までいた人のアドレス、
     *	valueにはboardインスタンスがそれぞれ格納されています。
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
            throw new MessengerException("メッセンジャーのサーバに接続できません。");
        } catch (IOException e) {
            throw new MessengerException("ネットに繋がっていない可能性があります。");
        }
        Sender sender = new NotificationSender(messe,trID,this);
        que = sender.getSendQueue();
        Reciever reciever = new NotificationReciever(messe, trID,this,sender);
        sender.start();
        reciever.start();
    }
    
    /**
     *　switchboardのセッションを追加します。
     */
    void addSession(int key, String key2, String ipaddress, int port) {
        Switchboard sb = new Switchboard(messe,key,key2,this);
        sb.connect(ipaddress, port);
        switchboards.add(sb);
    }
    
    /**
     * セッションを閉じます。
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
     * 現在接続しているswitchboard-sessionの数を返します。
     */
    int switchBoardCount() {
        return switchboards.size();
    }
    
    /**
     * 閉じたボードの数を返します。
     */
    int closedBoardCount() {
        return oldBoard.size();
    }
    
    /**
     * NotificationのSendQueueを返します
     */
    SendQueue getSendQueue() {
        return que;
    }
    
    /**
     * <p>指定されたboardがあるSwichBoardセッションのSendQueueを返します。</p>
     * もしも、そのboardがSwitchBoardから切断されたBoardだったら、再接続を試みます。
     */
    SendQueue getSendQueue(Board board) {
        // 現在Switchboardに接続しているものを先に調べる
        for(int i =0; i < switchboards.size(); i++)
            if(((Switchboard)switchboards.get(i)).getBoard().equals(board))
                return ((Switchboard)switchboards.get(i)).getQueue();
        // もしも、接続が切れたBoardだったら。
        if(oldBoard.containsKey(board.getLastAddress()))
            return newFreeSB(messe.memberlist.equalsMember( board.getLastAddress() )).getQueue();
        throw new RuntimeException("会話を開始できませんでした。");
    }
    
    // そのBoardがSwitchBoardに接続されているか
    boolean isConnectSB(Board board) {
        // 現在Switchboardに接続しているものを先に調べる
        for(int i =0; i < switchboards.size(); i++)
            if(((Switchboard)switchboards.get(i)).getBoard().equals(board))
                return true;
        return false;
    }
    
    /**
     * <p>新たに、引数で指定した参加者と自分の計２人がいるSwitchBoardサーバの確立を要求し、
     * 新たなSwitchBoardセッションが確立できたら、引数で指定した参加者を招待します。</p>
     * 
     * このメソッドでは、Boardの割り当てを行います。
     * メッセンジャーにおいて、誰かに話しかけるときの順序は、まずSwitchBoardセッションを
     * 作成し、それから誰かを招待するという形である。従って、先に参加者が自分のみのフリーの
     * SwitchBoardセッションを作るべきである。
     *
     * <I>このメソッドを呼び出したスレッドはSwitchBoardが初期化されるまで
     * 一定時間スリープするので注意が必要です。実測では、およそ300ミリ秒程度かかっています。</I>
     */
    Switchboard newFreeSB(Member mem) {
        que.add("XFR //TrID SB");
        que.add("//Wait");
        // 誰もいないSwitchBoardセッションができるまで待機
        try {
            while( !(switchboards.size() != 0 && 
                     ((Switchboard)switchboards.lastElement()).getBoard().getNumber() == 0
                     && ((Switchboard)switchboards.lastElement()).finishedInit()))
                Thread.sleep(20);
        } catch (InterruptedException e) {
        }
        // Boardの割り当て
        Switchboard freesb = (Switchboard)switchboards.lastElement();
        if(oldBoard.containsKey(mem.getAddress())) { // 既に同じメンバのBoardがあったら、
            freesb.setBoard((Board)oldBoard.get(mem.getAddress()));
            oldBoard.remove(mem.getAddress());
        }
        freesb.getQueue().add("CAL //TrID " + mem.getAddress());
        freesb.getQueue().add("//Lock"); // ロックをかけないと、誰も招待しないで会話をしてしまう
        return freesb;
    }
}