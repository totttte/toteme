/**
 * 作成日: 2006/09/02
 */
package tottemsn.core;

/**
 * ホットメールのEntityです。
 */
public class HotMail {

    private int inbox;
    private int inboxUnread;
    private int other;
    private int otherUnread;
    
    /**
     * [MSG Hotmail Hotmail size]コマンドのpayload部を指定します。
     * @see Messages
     */
    HotMail(String payload) {
        inbox = Integer.parseInt(payload.substring(payload.indexOf("<I>") + 3, payload.indexOf("</I>")));
        inboxUnread =
            Integer.parseInt(payload.substring(payload.indexOf("<IU>") + 4, payload.indexOf("</IU>")));
        other = Integer.parseInt(payload.substring(payload.indexOf("<O>") + 3, payload.indexOf("</O>")));
        otherUnread = 
            Integer.parseInt(payload.substring(payload.indexOf("<OU>") + 4, payload.indexOf("</OU>")));
    }
    
    /**
     * 受信フォルダのメール受信件数
     */
    public int getInbbox() {
        return inbox;
    }
    /**
     * 受信フォルダ未読件数
     */
    public int getInboxUnread() {
        return inboxUnread;
    }
    /**
     * Otherとは、inbox以外のフォルダです。
     */
    public int getOther() {
        return other;
    }
    public int getOtherUnread() {
        return otherUnread;
    }
}