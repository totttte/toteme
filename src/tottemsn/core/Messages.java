/**
 * �쐬��: 2006/08/09
 */
package tottemsn.core;

import java.awt.Color;
import java.awt.Font;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Hashtable;

/**
 * ���b�Z���W���[��MSG�R�}���h��͋y�э쐬�p�N���X�ł��B
 * MSG�R�}���h�̊T�v
 * <ul>
 * <li>Swichboard�ł̃`���b�g��M���B</li>
 * <pre>
 * MSG account name size(integer)\r\n
 * [ HeaderKey: HeaderValue\r\n ]*
 * \r\n
 * [messages]
 * </pre>
 * 
 * <li>Swichboard�ł̃`���b�g���M���B</li>
 * <pre>
 * MSG //TrID N size(integer)\r\n
 * [ HeaderKey: HeaderValue\r\n ]*
 * \r\n
 * [messages]
 * </pre>
 * 
 * <li>Notification�ł̃��[����M���B</li>
 * <pre>
 * MSG Hotmail Hotmail size(integer)\r\n
 * [ HeaderKey: HeaderValue\r\n ]*
 * \r\n
 * </pre>
 * </ul>
 * key: value��A�Ōオ�Q�s���s�ȂǁA��r�IHTML�w�b�_�ɋ߂����̂ƂȂ��Ă���B
 * 
 * �����Œ��ӂ��Ȃ��Ă͂����Ȃ��̂́AMSG�R�}���h�́A�P�̃p�P�b�g�Ɏ��܂�Ȃ��ꍇ�A
 * �f�Љ����đ����Ă���Ƃ������Ƃł���B���̂��ߕK���T�C�Y���擾���A���̃T�C�Y�ɖ����Ȃ�
 * �g�[�N���������Ă����ꍇ�A���̃g�[�N���ƘA�������邱�Ƃ��K�v�ł���B
 * �܂��A�����炩��MSG�R�}���h�𑗐M����Ƃ��ŁA���b�Z�[�W����͂���Ƃ��ɂ͍Ō��\r\n���܂܂Ȃ��悤��
 * ���Ȃ���΂Ȃ�Ȃ��B 
 */
public class Messages {
    
    private String message; // ���̃p�P�b�g�̃��b�Z�[�W�S��
    private Hashtable table; //�@�w�b�_�̃^�C�v�ƒl�̃n�b�V���e�[�u��
    private String payload; // �w�b�_���������c��̕���(�Ȃ��ꍇ�͒���0�̕�����)
    
    private static final String TYPE_IS_TEXT = "text/plain; charset=UTF-8";
    private static final String TYPE_IS_MAIL = "text/x-msmsgsinitialmdatanotification; charset=UTF-8";
    private static final String CONTENT_TYPE = "Content-Type";

    /**
     * �V���Ɏ�M�����p�P�b�g����A���b�Z�[�W���쐬���܂��B
     * @param message
     */
    Messages(String message) {
        this.message = message;
        // �s���Ƃɋ�؂��āA�w�b�_�̃^�C�v�ƒl�̃Z�b�g�̎擾
        String[] newlines = StringUtil.spiltNL(message);
        String[] headers = null;
        for(int i=0;;i++){
            if(i == newlines.length || newlines[i].equals("")) {
                headers = new String[i-1];
                System.arraycopy(newlines,1,headers,0,headers.length);
                break;
            }
        }
        // �e�[�u���쐬
        table = new Hashtable();
        for(int i=0; i<headers.length;i++){
            String key = headers[i].substring(0, headers[i].indexOf(':'));
            String value = headers[i].substring(headers[i].indexOf(':') + 2);
            table.put(key,value);
        }
        payload = ""; // payload�̎w��
        for(int i = table.size()+1; i < newlines.length; i++) 
            payload += newlines[i] + "\n";
        // �Ō�̂P�o�C�g�͉��s�Ȃ��Ȃ̂ŁA�P�����ǉ�
        if(payload.length() > 0)
            payload = payload.substring(0, payload.length() - 1); //�@�Ō�̉��s�̓J�b�g
        payload += message.charAt(message.length() - 1);
    }

    /**
     * ���[���A�h���X��Ԃ��܂��B
     * @return ���̃C���X�^���X�ɂ��镶����ɁA���[���A�h���X������ꍇ�̓��[���A�h���X�A
     * �Ȃ��ꍇ��null��Ԃ��܂��B
     */
    public String getMailAddress() {
        String mail = StringUtil.messeToken(message,1);
        if(mail.equals("Hotmail")) 
            return null;
        else return mail;
    }
    
    /**
     * ���̃��b�Z�[�W�������������҂�Ԃ��܂�
     * @return ���̃C���X�^���X�ɂ��镶����ɁA���O������ꍇ�͖��O�A
     * �Ȃ��ꍇ��null��Ԃ��܂��B
     */
    public String getName() {
        String name = StringUtil.messeToken(message,2);
        if(name.equals("Hotmail")) 
            return null;
        else return name;        
    }
    
    /**
     * ����MSG�R�}���h����擾�����w�b�_��Hashtable�ɂ��ĕԂ��܂��B
     * �L�[�ɂ̓w�b�_�̃^�C�v�A�l�ɂ͂��̃L�[�̃p�����[�^�����ꂼ�ꕶ����œ���܂��B
     * ex)
     * Key="MIME-Version",value="1.0"
     * 
     * @return �w�b�_����擾����������̃n�b�V���e�[�u���B�w�b�_�ɉ����Ȃ��ꍇ����0�̋�̃e�[�u����Ԃ��܂��B
     */
    public Hashtable getHeader() {
        return table;
    }
    
    /**
     * payload(�܂�v���g�R���̃w�b�_������������)��Ԃ��܂�
     * @return payload��Ԃ��Bpayload���Ȃ��ꍇ�A����0�̕������Ԃ��B
     */
    public String getPayload() {
        return payload;
    }
    
    /**
     * </p>���̃��b�Z�[�W��Font���擾���܂��B�w�b�_����AFont�����擾���郁�\�b�h</p>
     * 
     * @return �w�b�_����ǂݍ��񂾏������Ƀt�H���g���쐬���܂��B
     * size�̓f�t�H���g�l�Ƃ���12���^�����܂��B
     */
    public Font getFont() {
        String format = table.get("X-MMS-IM-Format").toString();
        String ft = getIMFormatValue( StringUtil.messeToken(format,0) );
        return new Font(ft,Font.BOLD,12);
    }
    
    /**
     * <p>���̃��b�Z�[�W��Color���擾���܂��B</p>
     * �F���̎擾�ɂ��āBheader�t�@�C���ɂ���F����BGR(RGB�̋t��)��16�i��(00-FF)�ŁA
     * �Ȃ��ł��܂��B�܂�B��0�̂Ƃ���4���ɂȂ�AB��G��0�̂Ƃ���2���ARGB�S�Ă̒l��0(��)�̏ꍇ�́A
     * 0�ɂȂ�܂��B
     * @return �w�b�_����ǂݍ��񂾒l�����ɍ쐬����Color�C���X�^���X�ł��B
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
     * ����MSG�R�}���h�̒��g���Atext/plain�Ȃ̂��𒲂ׂ܂��B
     * @return text/plain�Ȃ�true
     */
    public boolean isTextMessage() {
        return table.get(CONTENT_TYPE).toString().equals(TYPE_IS_TEXT);
    }
    
    /**
     * ����MSG�R�}���h�̒��g���AHotmail�Ɋւ�����̂Ȃ̂��𒲂ׂ܂��B
     * @return text/x-msmsgsinitialmdatanotification�Ȃ�true
     */
    public boolean isMailData() {
        return table.get(CONTENT_TYPE).toString().equals(TYPE_IS_MAIL);
    }

   /**
    * MSG�R�}���h�ȊO������΁A��������o���܂��B
    * �Ȃ���΁Anull��Ԃ��܂��B
    * @param data MSG����n�܂�R�}���h
    * @param tokenc ���Ԗڂ̃g�[�N����������r�p�g�[�N����
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
    * key=value; �`������Avalue�����o�����\�b�h�ł��B
    * �Ȃ��AUTF-8�f�R�[�h�����܂��B
    * ex) 
    * "FN=%EF%BC%AD%EF%BC%B3%20%E3%82%B4%E3%82%B7%E3%83%83%E3%82%AF;"
    * �̖߂�l�́A"�l�r �S�V�b�N"�ɂȂ�܂��B
    * 
    * @param data ���ׂ����g�[�N��
    * @return UTF-8��URL�f�R�[�h���ꂽX-MMS-IM-Format�̒l
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
    * �V���ɁA���M�p���b�Z�[�W���쐬���܂��B
    * @param message ���M���������b�Z�[�W(payload����)
    * @param font ���M�������t�H���g(font��null�̏ꍇ�̓f�t�H���g��font���K������܂�)
    * @param color ���M�������F(color��null�̏ꍇ�͍�(0,0,0)���K������܂�)
    * @return MSG�R�}���h�`���ɂȂ������M�p���b�Z�[�W
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
           // 1646 = 1663 - 17 �ŏ��̍s�̃T�C�Y
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