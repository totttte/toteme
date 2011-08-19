/**
 * �쐬��: 2006/06/23
 */
package tottemsn.core;
import java.io.FileInputStream;
import java.util.LinkedHashMap;
import java.util.Properties;
/**
 * ���[�U�[�̃X�e�[�^�X�ł��B
 */
public class UserStatus {

    private String status;
    private static Properties ini;
    
    /*	static�C�j�V�����C�U�ɂ���āA�܂�setting.ini�t�@�C����T���܂��B */
    static {
        ini = new Properties();
        try {
            ini.load(new FileInputStream("data/setting.ini"));
        } catch(Exception e) {
            throw new MessengerException("�ݒ�t�@�C��<setting.ini>�t�@�C����������܂���B");
        }
    }
    
    /**
     * �V���ȃ��[�U�[�X�e�[�^�X��ݒ肵�܂��B
     * �����ɂ��Ă� {@link #setStatus(String)}���Q�Ƃ��Ă��������B
     */
    public UserStatus(String token) {
        setStatus(token);
    }
    
    /**
     * �X�e�[�^�X���w�肵�܂��B
     * ex)UserStatus#setStatus("NLN");�Ȃ�
     */
    void setStatus(String token) {
        status = token;
    }
    
    /**
     *	<p>�X�e�[�^�X��Ԃ��܂��B</p>
     *	ex) <br>
     *	>System.out.println( UserStatus#getStatus() );<br>
     *	�ސȒ�<br>
     */
    public String getStatus() {
            return ini.getProperty(status);
    }
    
    /**
     * <p>���݂̃X�e�[�^�X���R�}���h�`���ŕԂ��܂��B</p>
     *  ex) <br>
     *	>System.out.println( UserStatus#getStatusCommand() );<br>
     *	NLN<br>
     */
    public String getStatusCommand() {
        return status;
    }
    
    /**
     * toString()�̃I�[�o�[���C�h
     */
    public String toString() {
        return getStatus();
    }
    
    /**
     * <p>��ԕω��ʒm�p���b�Z�[�W�ƁA�ʒm�R�}���h���΂ɂȂ���LinkedHashMap��Ԃ��܂��B</p>
     * <p>�Ȃ��A����Map�͐V����clone()���\�b�h��p���ČĂяo���ꂽ�e�[�u������쐬����Ă��邽�߁A
     * ��ԕω��p�ʒm���b�Z�[�W�̖��O�␔��ς��邱�Ƃ͂ł��܂���B</p>
     * <p>�Ȃ��A���я��́A��ʓI�ȃ��b�Z���W���[�̕��ѕ��Ɠ����ł��B�A���Ō��FLN(�����[�U�̃I�t���C��)
     * ������܂��B</p>
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
