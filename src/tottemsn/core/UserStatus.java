/**
 * 作成日: 2006/06/23
 */
package tottemsn.core;
import java.io.FileInputStream;
import java.util.LinkedHashMap;
import java.util.Properties;
/**
 * ユーザーのステータスです。
 */
public class UserStatus {

    private String status;
    private static Properties ini;
    
    /*	staticイニシャライザによって、まずsetting.iniファイルを探します。 */
    static {
        ini = new Properties();
        try {
            ini.load(new FileInputStream("data/setting.ini"));
        } catch(Exception e) {
            throw new MessengerException("設定ファイル<setting.ini>ファイルが見つかりません。");
        }
    }
    
    /**
     * 新たなユーザーステータスを設定します。
     * 引数については {@link #setStatus(String)}を参照してください。
     */
    public UserStatus(String token) {
        setStatus(token);
    }
    
    /**
     * ステータスを指定します。
     * ex)UserStatus#setStatus("NLN");など
     */
    void setStatus(String token) {
        status = token;
    }
    
    /**
     *	<p>ステータスを返します。</p>
     *	ex) <br>
     *	>System.out.println( UserStatus#getStatus() );<br>
     *	退席中<br>
     */
    public String getStatus() {
            return ini.getProperty(status);
    }
    
    /**
     * <p>現在のステータスをコマンド形式で返します。</p>
     *  ex) <br>
     *	>System.out.println( UserStatus#getStatusCommand() );<br>
     *	NLN<br>
     */
    public String getStatusCommand() {
        return status;
    }
    
    /**
     * toString()のオーバーライド
     */
    public String toString() {
        return getStatus();
    }
    
    /**
     * <p>状態変化通知用メッセージと、通知コマンドが対になったLinkedHashMapを返します。</p>
     * <p>なお、このMapは新たにclone()メソッドを用いて呼び出されたテーブルから作成されているため、
     * 状態変化用通知メッセージの名前や数を変えることはできません。</p>
     * <p>なお、並び順は、一般的なメッセンジャーの並び方と同じです。但し最後にFLN(他ユーザのオフライン)
     * が入ります。</p>
     */
    public static LinkedHashMap tables() {
        LinkedHashMap map = new LinkedHashMap(9);
        map.put("NLN", ini.getProperty("NLN"));
        map.put("BSY", ini.getProperty("BSY"));
        map.put("IDL", ini.getProperty("IDL"));
        map.put("BRB", ini.getProperty("BRB"));
        map.put("AWY", ini.getProperty("AWY"));
        map.put("PHN", ini.getProperty("PHN"));
        map.put("LUN", ini.getProperty("LUN"));
        map.put("HDN", ini.getProperty("HDN"));
        map.put("FLN", ini.getProperty("FLN"));
        return map;
    }
}
