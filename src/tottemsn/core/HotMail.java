/**
 * �쐬��: 2006/09/02
 */
package tottemsn.core;

/**
 * �z�b�g���[����Entity�ł��B
 */
public class HotMail {

    private int inbox;
    private int inboxUnread;
    private int other;
    private int otherUnread;
    
    /**
     * [MSG Hotmail Hotmail size]�R�}���h��payload�����w�肵�܂��B
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
     * ��M�t�H���_�̃��[����M����
     */
    public int getInbbox() {
        return inbox;
    }
    /**
     * ��M�t�H���_���ǌ���
     */
    public int getInboxUnread() {
        return inboxUnread;
    }
    /**
     * Other�Ƃ́Ainbox�ȊO�̃t�H���_�ł��B
     */
    public int getOther() {
        return other;
    }
    public int getOtherUnread() {
        return otherUnread;
    }
}