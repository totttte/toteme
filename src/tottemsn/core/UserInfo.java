/**
 * �쐬��: 2006/06/14
 */
package tottemsn.core;
/**
 * �T�C���C�������l�̃��[�U�f�[�^��ێ�����N���X�ł��B
 */
public class UserInfo {

    private String account;
    private String password;
    private String name;
    private UserStatus status;
    String challengeKey; //�@�T�C���C�����ɗp����A�F�ؗp�L�[�ł��BTWN�T�[�o�����M���܂��B
    
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