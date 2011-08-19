/**
 * 作成日: 2006/06/14
 */
package tottemsn.core;
/**
 * サインインした人のユーザデータを保持するクラスです。
 */
public class UserInfo {

    private String account;
    private String password;
    private String name;
    private UserStatus status;
    String challengeKey; //　サインイン時に用いる、認証用キーです。TWNサーバから受信します。
    
    UserInfo(String account, String password , String status) {
        this.account = account;
        this.password = password;
        this.status = new UserStatus(status);
    }
    
    public String getAccount() {
        return account;
    }
    
    String getPassWord() {
        return password;
    }
    
    public String getDefaultName() {
        return name;
    }
    
    void reName(String name) {
        this.name = name;
    }
    
    public UserStatus getUserStatus() {
        return status;
    }
}