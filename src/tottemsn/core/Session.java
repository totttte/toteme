/**
 * 作成日: 2006/08/09
 */
package tottemsn.core;

import java.net.Socket;

/**		セッションクラス。SwitchBoardおよびNotificationとのソケット通信の基底です。 */
class Session {
    Socket socket;
    protected Messenger messe;
    protected TrID trID;
}