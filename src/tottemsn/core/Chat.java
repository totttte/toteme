/**
 * 作成日: 2006/08/10
 */
package tottemsn.core;

import java.awt.Color;
import java.awt.Font;
import java.util.Date;

/**
 * <p>１回分の発言のデータを保持します。</p>
 * 
 * 発言には、発言者が誰なのか、色、フォントデータ、メッセージ、発言時間などが格納されます。
 */
public class Chat {

    private Font font;
    private Color color;
    private String name;
    private String text;
    private Date date;
    private int type;
    
    public static final int INCOMING_MESSAGE = 1;
    public static final int OUTGOING_MESSAGE = 2;
    public static final int INFOMATION_MESSAGE = 3;

    /**
     * <p>INCOMING_MESSAGE形式(メッセージ受信)のコンストラクタです。</p>
     * このコンストラクタを外部パッケージから直接生成することはできません。
     * 外部パッケージからChatインスタンスを参照するには、{@link MessengerListener}の
     * 実装を通して行います。
     * なお、このインスタンスを作成したときが、getDate()メソッドで取得する時間となります。
     * @param msg メッセージを指定します
     */
    Chat(Messages msg) {
        this.font = msg.getFont();
        this.name = msg.getName();
        this.color = msg.getColor();
        this.text = msg.getPayload();
        this.date = new Date();
        this.type = INCOMING_MESSAGE;
    }
    
    /**
     * <p>このコンストラクタは、OUTGOING_MESSAGE時に使用します。つまり、
     * 自分が他のユーザにメッセージを送信したいときにこのコンストラクタを使います。</p>
     * なお、「ＸＸが退席しました」などの状態変化通知用メッセージはINFOMATION_MESSAGEを使用してください。
     * @param name 送信者の名前
     * @param text 送信者のテキスト
     * @param color 送信者の色
     * @param font 送信者のフォント
     */
    public Chat(String name, String text, Color color, Font font) {
        this.name = name;
        this.text = text;
        this.color = color;
        this.font = font;
        this.date = new Date();
        this.type = OUTGOING_MESSAGE;
    }
    
    /**
     * <p>INFOMATION_MESSEAGE用のコンストラクタです。</p>
     * なお、色は黒、nameは空文字列、フォントはサイズ11のDefault,PLAINTEXTです。
     * @param message 文字列
     */
    public Chat(String message) {
        this.text = message;
        this.name = "";
        this.color = Color.BLACK;
        this.date = new Date();
        this.font = new Font(null,Font.PLAIN,11);
        this.type = INFOMATION_MESSAGE;
    }
    
    /**
     * <p>発言した時間です。</p>
     * データを受け取ったまたは送信した時間が基準になります。
     */
    public Date getData() {
        return date;
    }

    /**	メッセージのフォント	 */
    public Font getFont() {
        return this.font;
    }
    
    /**	メッセージの色	 */
    public Color getColor() {
        return this.color;
    }
    
    /**
     * 発言者の名前を返します
     * @return 発言者の名前
     */
    public String getName() {
        return this.name;
    }
    
    /**	メッセージのテキスト	     */
    public String getText() {
        return this.text;
    }
    
    /**
     * このチャットのタイプです。タイプについてはフィールド参照。
     */
    public int getType() {
        return this.type;
    }
}
