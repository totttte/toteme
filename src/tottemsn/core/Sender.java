/**
 * 作成日: 2006/08/09
 */
package tottemsn.core;
import java.io.DataOutputStream;
import java.io.IOException;
/**
 * <p>データ送信を担当するクラスです。</p>
 * 受信を担当するRecieverクラスとは別スレッドで動きます。
 * <p>
 * このクラスは常に{@link tottemsn.core.SendQueue}を監視し、キューが空になるまでデータを送信し続け、
 * キューが空になった場合は、新たなキューが入るまで待機します。
 * どのような方法で、SendQueueに残っているデータを送るかは、parserメソッドを継承して決めます。
 * </p>
 * @see tottemsn.core.NotificationReciever
 * @see tottemsn.core.SwitchboardReciever
 */
abstract class Sender extends Client {

    private DataOutputStream out;
    SendQueue que;
    
    private boolean lock = false;

    Sender(Messenger messe, TrID trID,Session session) {
        super(messe, trID, session);
        que = new SendQueue(64);
    }

    /**
     * サーバが移動したとき、または新たにソケットを接続するときにこれを呼び出します。
     */
    void moveServer() {
        try {
            socket = session.socket;
            out = new DataOutputStream(socket.getOutputStream());
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * <p>キューが空になるまで、ソケット通信先にキューの中身を送信し、空になったら待機します。</p>
     * Thread#run() のオーバーライドです。
     * また、このメソッドに割り込みがかけられた場合、メインループを抜け出しメソッドを出ます。
     * @see SwitchboardReciever#doOut(String)
     */
    public void run() {
        try {
            while(true) {
                while(que.isEmpty() || lock) 
                    synchronized(que){
                        que.wait();
                    }
                String data = parser(que.poll());
                if(data == null) continue;
                else sendCommand(data);
            }
        }catch(InterruptedException e) {
        }
    }
    
    /**
     * 現在接続しているソケット先にデータを送ります。
     * なお、メッセンジャーでは、QRYコマンド以外最後に改行コードを入れることになっているので、
     * それも実装します。
     * @param data 送信したいデータ
     */
    private void sendCommand(String data) {
        // 終端に改行コード(CRLR)がない場合は、挿入する。
        if( !data.endsWith("\r\n") && !data.startsWith("QRY") && !data.startsWith("MSG")
                && !data.startsWith("UUX")) {
            data += "\r\n";
        }
        if(! socket.isConnected())
            throw new MessengerException("サーバから切断されました。");
        // UTF-8のバイト配列に変え、ソケット先に送信します。
        try {               
            out.write( data.getBytes("UTF-8") );
            out.flush(); // かならずflushする。
        } catch(IOException e) {
            if(session instanceof Switchboard) {
                this.interrupt();
                ((Switchboard)session).reciever.interrupt();
            } else throw new RuntimeException(e);
        }
        if(session instanceof Switchboard)
            StringUtil.println("[C -> SB]" + data);
        else 
            StringUtil.println("[C -> NC]" + data);
        trID.addTrID();
    }
    
    /**
     * このメソッドは、このクラスを継承するNotificationSenderやSwitchboardSenderで、
     * 実装されます。
     * send2.txt用独自パーサです。
     * //で始まるコマンド類を、変換します。
     * また、//Waitなど、String文字を出力しない文字列の場合は、nullを返します。
     * @param data 変換したい文字列
     * @return 変換後の文字列
     */
    protected abstract String parser(String data);
    
    /**
     *	SendQueueの参照を得ます
     */
    SendQueue getSendQueue() {
        return que;
    }
    
    /**
     *　このメソッドを呼び出すと、メインループでwaitメソッドを抜け出したとき
     *	直後のwhile文によって、ふたたびwaitします。
     *	このメソッドを呼び出した後は、必ずunlock()をしなければなりません。
     */
    void lock() {
        lock = true;
    }
    
    /**
     * ロックを解除します。
     * @see #lock()
     */
    void unlock() {
        lock = false;
    }
}
