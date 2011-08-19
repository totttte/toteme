/**
 * 作成日: 2006/08/09
 */
package tottemsn.core;

import java.awt.Color;
import java.awt.Font;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Hashtable;

/**
 * メッセンジャーのMSGコマンド解析及び作成用クラスです。
 * MSGコマンドの概要
 * <ul>
 * <li>Swichboardでのチャット受信時。</li>
 * <pre>
 * MSG account name size(integer)\r\n
 * [ HeaderKey: HeaderValue\r\n ]*
 * \r\n
 * [messages]
 * </pre>
 * 
 * <li>Swichboardでのチャット送信時。</li>
 * <pre>
 * MSG //TrID N size(integer)\r\n
 * [ HeaderKey: HeaderValue\r\n ]*
 * \r\n
 * [messages]
 * </pre>
 * 
 * <li>Notificationでのメール受信時。</li>
 * <pre>
 * MSG Hotmail Hotmail size(integer)\r\n
 * [ HeaderKey: HeaderValue\r\n ]*
 * \r\n
 * </pre>
 * </ul>
 * key: valueや、最後が２行改行など、比較的HTMLヘッダに近いものとなっている。
 * 
 * ここで注意しなくてはいけないのは、MSGコマンドは、１つのパケットに治まらない場合、
 * 断片化して送られてくるということである。このため必ずサイズを取得し、そのサイズに満たない
 * トークンが送られてきた場合、次のトークンと連結させることが必要である。
 * また、こちらからMSGコマンドを送信するときで、メッセージを入力するときには最後に\r\nを含まないように
 * しなければならない。 
 */
public class Messages {
    
    private String message; // このパケットのメッセージ全体
    private Hashtable table; //　ヘッダのタイプと値のハッシュテーブル
    private String payload; // ヘッダを除いた残りの部分(ない場合は長さ0の文字列)
    
    private static final String TYPE_IS_TEXT = "text/plain; charset=UTF-8";
    private static final String TYPE_IS_MAIL = "text/x-msmsgsinitialmdatanotification; charset=UTF-8";
    private static final String CONTENT_TYPE = "Content-Type";

    /**
     * 新たに受信したパケットから、メッセージを作成します。
     * @param message
     */
    Messages(String message) {
        this.message = message;
        // 行ごとに区切って、ヘッダのタイプと値のセットの取得
        String[] newlines = StringUtil.spiltNL(message);
        String[] headers = null;
        for(int i=0;;i++){
            if(i == newlines.length || newlines[i].equals("")) {
                headers = new String[i-1];
                System.arraycopy(newlines,1,headers,0,headers.length);
                break;
            }
        }
        // テーブル作成
        table = new Hashtable();
        for(int i=0; i<headers.length;i++){
            String key = headers[i].substring(0, headers[i].indexOf(':'));
            String value = headers[i].substring(headers[i].indexOf(':') + 2);
            table.put(key,value);
        }
        payload = ""; // payloadの指定
        for(int i = table.size()+1; i < newlines.length; i++) 
            payload += newlines[i] + "\n";
        // 最後の１バイトは改行なしなので、１文字追加
        if(payload.length() > 0)
            payload = payload.substring(0, payload.length() - 1); //　最後の改行はカット
        payload += message.charAt(message.length() - 1);
    }

    /**
     * メールアドレスを返します。
     * @return このインスタンスにある文字列に、メールアドレスがある場合はメールアドレス、
     * ない場合はnullを返します。
     */
    public String getMailAddress() {
        String mail = StringUtil.messeToken(message,1);
        if(mail.equals("Hotmail")) 
            return null;
        else return mail;
    }
    
    /**
     * このメッセージを書いた発言者を返します
     * @return このインスタンスにある文字列に、名前がある場合は名前、
     * ない場合はnullを返します。
     */
    public String getName() {
        String name = StringUtil.messeToken(message,2);
        if(name.equals("Hotmail")) 
            return null;
        else return name;        
    }
    
    /**
     * このMSGコマンドから取得したヘッダをHashtableにして返します。
     * キーにはヘッダのタイプ、値にはそのキーのパラメータがそれぞれ文字列で入ります。
     * ex)
     * Key="MIME-Version",value="1.0"
     * 
     * @return ヘッダから取得した文字列のハッシュテーブル。ヘッダに何もない場合長さ0の空のテーブルを返します。
     */
    public Hashtable getHeader() {
        return table;
    }
    
    /**
     * payload(つまりプロトコルのヘッダを除いた部分)を返します
     * @return payloadを返す。payloadがない場合、長さ0の文字列を返す。
     */
    public String getPayload() {
        return payload;
    }
    
    /**
     * </p>このメッセージのFontを取得します。ヘッダから、Font情報を取得するメソッド</p>
     * 
     * @return ヘッダから読み込んだ情報を元にフォントを作成します。
     * sizeはデフォルト値として12が与えられます。
     */
    public Font getFont() {
        String format = table.get("X-MMS-IM-Format").toString();
        String ft = getIMFormatValue( StringUtil.messeToken(format,0) );
        return new Font(ft,Font.BOLD,12);
    }
    
    /**
     * <p>このメッセージのColorを取得します。</p>
     * 色情報の取得について。headerファイルにある色情報はBGR(RGBの逆順)で16進数(00-FF)で、
     * ならんでいます。またBが0のときは4桁になり、BとGが0のときは2桁、RGB全ての値が0(黒)の場合は、
     * 0になります。
     * @return ヘッダから読み込んだ値を元に作成したColorインスタンスです。
     */
    public Color getColor() {
        String format = table.get("X-MMS-IM-Format").toString();
        int co = Integer.parseInt(   getIMFormatValue( StringUtil.messeToken(format,2) ), 16   );
        int r = co & 0x000000ff;
        int g = (co & 0x0000ff00) >> 8;
        int b = (co & 0x00ff0000) >> 16;
        return new Color(r,g,b);
    }
    
    /**
     * このMSGコマンドの中身が、text/plainなのかを調べます。
     * @return text/plainならtrue
     */
    public boolean isTextMessage() {
        return table.get(CONTENT_TYPE).toString().equals(TYPE_IS_TEXT);
    }
    
    /**
     * このMSGコマンドの中身が、Hotmailに関するものなのかを調べます。
     * @return text/x-msmsgsinitialmdatanotificationならtrue
     */
    public boolean isMailData() {
        return table.get(CONTENT_TYPE).toString().equals(TYPE_IS_MAIL);
    }

   /**
    * MSGコマンド以外があれば、それを取り出します。
    * なければ、nullを返します。
    * @param data MSGから始まるコマンド
    * @param tokenc 何番目のトークンが長さ比較用トークンか
    */
   static String getOthers(String data, int tokenc) {
       int size = Integer.parseInt(StringUtil.messeToken(data,tokenc));
       if(size < StringUtil.getUTF8Length(data)) {
           byte[] tmp;
           try {
               tmp = data.substring(data.indexOf('\n')).getBytes("UTF-8");
               return new String(tmp, size + 1, tmp.length - (size + 1), "UTF-8");
           }catch(UnsupportedEncodingException e) {
               throw new RuntimeException(e);
           }
       } else 
           return null;
   }
   
   /**
    * key=value; 形式から、valueを取り出すメソッドです。
    * なお、UTF-8デコードもやります。
    * ex) 
    * "FN=%EF%BC%AD%EF%BC%B3%20%E3%82%B4%E3%82%B7%E3%83%83%E3%82%AF;"
    * の戻り値は、"ＭＳ ゴシック"になります。
    * 
    * @param data 調べたいトークン
    * @return UTF-8でURLデコードされたX-MMS-IM-Formatの値
    */
   static String getIMFormatValue(String data) {
       try {
           int end = (data.charAt(data.length()-1)==';')?
                   data.length() - 1 : data.length();
           data = data.substring(data.indexOf('=')+1 ,end);
           data = URLDecoder.decode( data, "UTF-8") ;
       } catch (UnsupportedEncodingException e) {
           throw new RuntimeException(e);
       }
       return data;
   }
   
   /**
    * 新たに、送信用メッセージを作成します。
    * @param message 送信したいメッセージ(payload部分)
    * @param font 送信したいフォント(fontがnullの場合はデフォルトのfontが適応されます)
    * @param color 送信したい色(colorがnullの場合は黒(0,0,0)が適応されます)
    * @return MSGコマンド形式になった送信用メッセージ
    */
   static FragmentString outGoingMessage(String message, Font font, Color color) {
       String header = "MSG //TrID A "; 
       String template = "MIME-Version: 1.0\r\n"
           + "Content-Type: text/plain; charset=UTF-8\r\n"
           + "X-MMS-IM-Format: FN=";
       if(font == null) 
           font = new Font(null, Font.PLAIN, 12);
       if(color == null) 
           color = Color.BLACK;
       try {
           String FN = URLEncoder.encode( font.getFamily() , "UTF-8") + "; ";
           String EF = "EF=" + ((font.isItalic())?"I":"") + ((font.isBold())? "B":"") + "; ";
           int g = color.getGreen();
           int b = color.getBlue();
           int r = color.getRed();
           String CO =  "CO=" + ((b==0)?"":Integer.toHexString(b))
           + ((g==0)?"00":Integer.toHexString(g)) 
           + ((r==0)?"00":Integer.toHexString(r)) + "; ";
           String footer =  "CS=0; PF=22\r\n\r\n";
           String sum = template + FN + EF + CO + footer + message;
           // 1646 = 1663 - 17 最初の行のサイズ
           final int MAX_SIZE = 1646;
           int length = Math.min(sum.getBytes("UTF-8").length, MAX_SIZE);
           String frag = "";
           if(sum.getBytes("UTF-8").length > MAX_SIZE) {
               byte tmp[] = new byte[MAX_SIZE];
               System.arraycopy(sum.getBytes("UTF-8"),0,tmp,0,tmp.length);
               byte fragb[] = new byte[sum.getBytes("UTF-8").length - tmp.length];
               System.arraycopy(sum.getBytes("UTF-8"),tmp.length,fragb,0,fragb.length);
               sum = new String(tmp,"UTF-8");
               frag = new String(fragb,"UTF-8");
           }
           return new FragmentString(header + length + "\r\n"+ sum, frag);
       } catch (UnsupportedEncodingException e) {
           throw new RuntimeException(e);
       }
   }
}