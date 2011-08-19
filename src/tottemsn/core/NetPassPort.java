/**
 * �쐬��: 2006/06/14
 */
package tottemsn.core;

import java.io.UnsupportedEncodingException;
import java.net.*;

/**
 * <p>.netPassPort�ŃT�C���C�����邽�߂̃N���X�ł��B</p>
 * ��̓I�ȗ�͈ȉ��̒ʂ�
 * <pre>
 * NetPassPort pass = new NetPassPort(user, key);
 * String test;
 * if( pass.tryAuthorization() ) {
 * 	test = pass.getKey();
 * } else {
 * 	System.out.println("�F�؃G���[�ł����B:" + pass.getErrorMSG().toString());
 * }
 * </pre>
 * �Ȃ��ANotification��SwitchBoard�ƈႢ���̃Z�b�V�����͎g�p�p�x�����Ȃ�����
 * �V���O���X���b�h�œ��삵�܂��B
 */
public class NetPassPort {

    private UserInfo user;
    private Exception errorMSG = null;
    private String key;
    private String challengeKey;
    
    /**	.net Pass Port�Ɍq���Ƃ��́A����URL�փA�N�Z�X���܂��B	*/
    public final String NEXUS_URL = "https://login.live.com/login2.srf"; 
    
    /**
     * @param user ���O�C�����������[�U���w�肵�܂��B
     * @param challengeKey TWN S�Ń��b�Z�T�[�o���瑗���Ă����L�[���w�肵�܂��B
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
     * .NetPassPort�ɐڑ������݂܂��B
     * @return �ڑ��ɐ�������΁Atrue�A�Ȃ�炩�̌����Őڑ��Ɏ��s�����false��Ԃ��܂��B
     * ���������ꍇ�̌��́A{@link #getKey()}�ɂ���ē��邱�Ƃ��ł��܂��B
     * �܂��A���s������O�n���h����{@link #getErrorMSG()}���Ăяo���ē��邱�Ƃ��ł��܂��B
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
            errorMSG = new MessengerException("�p�X���[�h���A�J�E���g�����Ⴂ�܂��B");
            return false;
        }
        return true;
    }
    
    /**
     * �ڑ��G���[�ɂȂ����Ƃ��A���̗�O�n���h����Ԃ��܂��B
     * �ڑ��ɐ��������Ƃ����O�n���h���������o����Ă��Ȃ��ꍇ�́Anull��Ԃ��܂��B
     * @return �ڑ��G���[�ɂȂ�������
     */
    public Exception getErrorMSG() {
        return errorMSG;
    }
    
    /**
     * �F�؂ɐ������Ă���ꍇ�A�����Ŏ擾����key��Ԃ��܂��B
     * @return USR�R�}���h�Ɏg�����߂̃L�[
     */
    String getKey() {
        return key;
    }
}