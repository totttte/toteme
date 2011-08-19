/**
 * 作成日: 2006/08/07
 */
package tottemsn.core;

import java.net.Socket;
/**
 * クライアントの、データ送受信を担当するクラスはこれを継承します。
 * @see Reciever
 * @see Sender
 */
abstract class Client extends Thread {

    Messenger messe;
    TrID trID;
    Session session;
    Socket socket;
    
    Client(Messenger messe, TrID trID, Session session) {
        this.messe = messe;
        this.trID = trID;
        this.session = session;
        this.socket = session.socket;
        moveServer();
        // スレッド名を指定します。
        this.setName(this.getClass().getName() + this.getName());
    }
    
    /**
     *　サーバ移動のための実装
     */
    abstract void moveServer();
    
}