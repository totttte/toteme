/**
 * 作成日: 2006/06/14
 */
package tottemsn.core;

import java.io.UnsupportedEncodingException;
import java.net.*;

/**
 * <p>.netPassPortでサインインするためのクラスです。</p>
 * 具体的な例は以下の通り
 * <pre>
 * NetPassPort pass = new NetPassPort(user, key);
 * String test;
 * if( pass.tryAuthorization() ) {
 * 	test = pass.getKey();
 * } else {
 * 	System.out.println("認証エラーでした。:" + pass.getErrorMSG().toString());
 * }
 * </pre>
 * なお、NotificationやSwitchBoardと違いこのセッションは使用頻度が少ないため
 * シングルスレッドで動作します。
 */
public class NetPassPort {

    private UserInfo user;
    private Exception errorMSG = null;
    private String key;
    private String challengeKey;
    
    /**	.net Pass Portに繋ぐときは、このURLへアクセスします。	*/
    public final String NEXUS_URL = "https://login.live.com/login2.srf"; 
    
    /**
     * @param user ログインしたいユーザを指定します。
     * @param challengeKey TWN Sでメッセサーバから送られてきたキーを指定します。
     */
    public NetPassPort(UserInfo user, String challengeKey) {
        this.user = user;
        try {
            this.challengeKey = 
                "Passport1.4 OrgVerb=GET,OrgURL=http%3A%2F%2Fmessenger%2Emsn%2Ecom,sign-in="
                 + URLEncoder.encode(user.getAccount() , "UTF-8") + ",pwd=" +
                 URLEncoder.encode(user.getPassWord() , "UTF-8") + "," +challengeKey;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * .NetPassPortに接続を試みます。
     * @return 接続に成功すれば、true、なんらかの原因で接続に失敗すればfalseを返します。
     * 成功した場合の鍵は、{@link #getKey()}によって得ることができます。
     * また、失敗した例外ハンドルは{@link #getErrorMSG()}を呼び出して得ることができます。
     */
    public boolean tryAuthorization() {
        try {
            URL nexus = new URL(NEXUS_URL);
            HttpURLConnection con = (HttpURLConnection) nexus.openConnection();
            con.setRequestMethod("GET");
            con.addRequestProperty("Authorization", challengeKey);
            con.getInputStream();
            key = StringUtil.concater(con.getHeaderField("Authentication-Info"),"from-PP");
        } catch(Exception e) {
            errorMSG = new MessengerException("パスワードかアカウント名が違います。");
            return false;
        }
        return true;
    }
    
    /**
     * 接続エラーになったとき、その例外ハンドルを返します。
     * 接続に成功したときや例外ハンドルが投げ出されていない場合は、nullを返します。
     * @return 接続エラーになった原因
     */
    public Exception getErrorMSG() {
        return errorMSG;
    }
    
    /**
     * 認証に成功している場合、そこで取得したkeyを返します。
     * @return USRコマンドに使うためのキー
     */
    String getKey() {
        return key;
    }
}