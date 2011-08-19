/**
 * �쐬��: 2006/06/14
 */
package tottemsn.core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>�����񑀍�̂��߂̃��[�e�B���e�B�ł��B</p>
 * ���̃N���X�̃��\�b�h�̓C���X�^���X�𐶐����邱�ƂȂ��N���X���\�b�h�ŌĂяo���܂��B
 */
public class StringUtil {
    
    /**	�f�o�b�O���[�h�̎��́Atrue	 */
    public static boolean debuc = true;
    /**	�p�P�b�g���O�̏o�͐�				*/
    public final static PrintStream log = System.out;

    /**
     * [ ver=value, ]�p�^�[������Aver���w�肷���value��Ԃ��܂��B
     * ���̍ہA''�ň͂܂ꂽ�����ɕԂ��ẮA�Ԃ�,���������Ƃ��Ă������Ƃ��܂��B
     * �܂��A������̍Ō�܂ł������ꍇ�́A�Ō�܂ł�value�Ƃ��܂��B
     * �ȉ��ɗ�������܂��B
     * concater("type=test,name=tote,", "name") �̖߂�l��tote
     * concater("type=test,name='tote,totte',", "name") �̖߂�l��tote,totte
     */
    public static String concater(String src, String key) {
        int begin = src.indexOf(key) + key.length() + 1;
        int end;
        if(src.charAt(begin) == '\'') { 
            begin++; // ' �̕����������ǂݐi�߂�
            end = src.indexOf('\'', begin + 1);
        } else 
            end = src.indexOf(',', begin); 
        return  src.substring(begin, (end == -1) ? src.length() : end);
    }
    
    /**
     * $�L���Ή�������u���������\�b�h
     * �Ƃɂ����A�u�������邾���ł��B
     * String#replaceAll()�n�ƈႤ�Ƃ���́A���K�\�����g���Ēu�������邩�A�����łȂ����ł��B
     * @param src �ǂݍ��ޕ�����
     * @param before �}�b�`���镶����
     * @param after �}�b�`�����������u��������
     * @return �u��������̕�����
     */
    public static String replaceAll(String src, String before ,String after) {
        int pos = 0;
        int srcPos = 0;
        String dest = "";
        for(int i = 0; i < src.length(); i++) {
            if(src.charAt(i) == before.charAt(pos)) pos++;
            else  pos = 0;
            if(pos == before.length()) { // match
                dest += src.substring(srcPos, i - pos + 1) + after;
                srcPos = i + 1;
                pos = 0;
            }
        }
        dest += src.substring(srcPos);
        return dest;
    }
    
    /**
     * �T�C���C����p�t�@�C����ǂݍ���ŁA1�s���z��ɂ��ĕԂ��܂��B
     * @param fileName �ǂݍ��ރt�@�C��
     * @return �z�񉻂��ꂽ������
     */
    public static String[] readSignInFile(String fileName) {
        try {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        Vector v = new Vector();
        String l = "";
        while((l = br.readLine()) != null) {
            if(l.startsWith("#")) continue; // #�̓R�����g�s�Ƃ���B
            v.add(l);
        }
        String[] line = new String[v.size()];
        for(int i = 0; i < v.size(); i++) 
            line[i] = (String)v.get(i);   
        v = null;
        br.close();
        return line;
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * �����Ŏw�肵��MD5�_�C�W�F�X�g�̃L�[��Ԃ��܂��B
     * @return 16�i[0-9,A-F]��32���̃R�[�h��Ԃ��܂��B
     */
    public static String getMD5Key(String pass) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] keyB = md5.digest(pass.getBytes());
            StringBuffer key = new StringBuffer();
            for(int i=0; i < keyB.length ; i++) {
                int integer = keyB[i];
                if(integer < 0) integer += 256;
                if(integer < 0x0f)	key.append('0');
                key.append(Integer.toString(integer,16));
            }
            return key.toString().toLowerCase();
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * ���s���Ƃɔz�񉻂��܂��B
     * "\r\n"��؂�
     * @param src ���s��؂肵����������
     */
    public static String[] spiltNL(String src) {
        StringTokenizer token = new StringTokenizer(src, "\n");
        String[] dest = new String[token.countTokens()];
        int i = 0;
        for(dest[0] = token.nextToken(); token.hasMoreTokens(); dest[++i] = token.nextToken())
            if(dest[i].charAt(dest[i].length() - 1) == '\r')
                dest[i] = dest[i].substring(0, dest[i].length() - 1); // "\r"��؂�
            else continue;
        dest[i] = dest[i].substring(0, dest[i].length() - 1); // �Ō�͂����ꕶ�����
        return dest;
    } 
    
    /**
     * <p>�R�}���h���Ƃɋ�؂��Ĕz�񉻂��܂��B</p>
     * <p>�R�}���h�ɂ́A1�s�ŏI���R�}���h������΁AMSG�R�}���h�̂悤�ɉ��s���ɓn���đ���
     * �R�}���h������܂��B������String�̔z��ɒu�������܂��B</p>
     * <ul>���ݑΉ����Ă��镡���s�R�}���h<li>MSG</li><li>UBX</li></ul>
     * @param src ��؂肽���f�[�^
     * @return ��؂��Ĕz�񉻂��ꂽ����
     */
    public static String[] spiltCommand(String src) {
        Vector dest = new Vector();
        int end = 0;
        for(int i =0 ; i < src.length();i = end+2) {
            end = ((end=src.indexOf('\r',i))==-1)?src.length():end;
            dest.add(src.substring(i, end));
            String last = (String)dest.lastElement();
            if(last.startsWith("MSG ")) {
                dest.remove(dest.size() - 1);
                dest.add(getMSG(src.substring(i),3));
                String other = Messages.getOthers(src.substring(i),3);
                if(other == null)
                    break;
                String[] others = spiltCommand( other );
                for(int j = 0; j < others.length; j++) 
                    dest.add(others[j]);
                break;
            } else if(last.startsWith("UBX ")) {
                dest.remove(dest.size() - 1);
                dest.add(getMSG(src.substring(i),2));
                end += dest.lastElement().toString().length() - last.length() - 2;
            }
        }
        String[] out = new String[dest.size()];
        for(int i=0;i < out.length;i++)
            out[i] = dest.get(i).toString();
        return out;
    }
    
    /**
     * �����Ŏw�肵���ʒu�̃g�[�N����Ԃ��AURL�f�R�[�h���܂��B
     * ���b�Z���W���[���L�́A���p�X�y�[�X��؂���g�[�N���Ƃ��܂��B
     * <pre>ex) System.out.println( getMesseToken("XFA 2 test hoge",2) );
     * >test</pre>
     * @param src ��؂肽���g�[�N��
     * @param n ���Ԗڂ̃g�[�N�������o���������w�肵�܂��B�ŏ��̃g�[�N����0����n�܂�܂��B
     * @return ���o������̃g�[�N����Ԃ��܂��B�����An�ԖڂɃg�[�N�����Ȃ��ꍇ�Anull
     */
    public static String messeToken(String src, int n) {
        StringTokenizer token = new StringTokenizer(src, " ");
        if(n >= token.countTokens())
            return null;
        for(int i = 0; i < n; i++)
            token.nextToken();
        String ans = token.nextToken();
        if(ans.indexOf("\r\n")!=-1) ans = ans.substring(0,ans.indexOf("\r\n"));
        try {
            ans = URLDecoder.decode(ans,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return ans;
    }
    
    public static int tokenCount(String src){
        return new StringTokenizer(src, " ").countTokens();
    }
    
    /**
     * <p>���̃R�}���h���f�Љ����Ă��邩�𒲂ׂ܂��B</p>
     * @param data �ǂݍ��񂾃p�P�b�g���w�肵�܂��B
     * @return �f�Љ����Ă�����true
     */
    static boolean isfragmentation(String data) {
        int tokennumber = 0;
        if(data.startsWith("MSG "))
            tokennumber = 3;
        else if(data.startsWith("UBX "))
            tokennumber = 2;
        else if(data.indexOf('\n', data.indexOf('\n')+1) != -1)
            return isfragmentation(data.substring(data.indexOf('\n')+1));
        else return ! data.endsWith("\r\n");
        int size = Integer.parseInt(StringUtil.messeToken(data,tokennumber));
        /* ������<size�ƂȂ��Ă����ꍇ�A�f�Љ����Ă���Ƃ������Ƃł���B
         * ������==size�́AMSG�R�}���h�P�����������Ă����Ƃ������Ƃł���B
         * MSG�R�}���h�̌�ɕʂ̃R�}���h�������ꍇ�́A ������>size�@�ƂȂ�ꍇ������B 
         */
        if(getUTF8Length(data) > size) 
            return isfragmentation( Messages.getOthers(data,tokennumber) );
        return getUTF8Length(data) < size;
    }

    /**
     * <p>payload�����R�}���h�ɂ����āA�Q�s�ڈȍ~�̕�����̒�����byte�P�ʂŎ擾���܂��B</p>
     * ��M�����p�P�b�g��UTF-8�G���R�[�h�ł̕�����̒������擾���܂��B
     * @param data ������̒���
     */
   static int getUTF8Length(String data) {
       try {
           return data.substring(data.indexOf('\n')+1).getBytes("UTF-8").length;
       } catch (UnsupportedEncodingException e) {
           return -1; //�@�܂��Ȃ��Ƃ͎v�����ǁB
       }
   }
   
   /**
    * <p>�����̃R�}���h���Z�b�g�ɂȂ��������񂩂�ŏ���MSG�R�}���h,UBX�R�}���h��Ԃ��܂��B</p>
    * 
    * ����) �R�}���h���f�Љ����Ă����ꍇ�A@see StringIndexOutOfBoundsException ��
    * �������鋰�ꂪ����܂��B@see #isfragmentation(String) �Ń`�F�b�N���Ă���g���悤�ɂ��Ă��������B
    * @param data
    * @param tokenpoint ���Ԗڂ̃g�[�N�����T�C�Y�̕]���Ɏg����
    */
   static String getMSG(String data,int tokenpoint) {
       int size = Integer.parseInt(StringUtil.messeToken(data,tokenpoint));
       String before = data.substring(0,data.indexOf('\n'));
       byte[] tmp;
       try {
       tmp = data.substring(data.indexOf('\n') + 1).getBytes("UTF-8");
       String after = new String(tmp, 0, size, "UTF-8");
       return before + '\n' + after;
       } catch(Exception e) {
           throw new RuntimeException(e);
       }
   }

    /**	�f�o�b�O���O�o�͗p���\�b�h�ł��B */
    public static void println(String string) {
        if(debuc) log.println(string);
    }
    
    /** �w�肵�������񂩂�URL���𔲂��o���܂��B�������ŏ��̂P�����B*/
    public static String getURL(String data) {
        Pattern pattern = Pattern.compile("^http(s?)://[\\w\\.\\-/:&?,=#]+",Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(data.substring(data.indexOf("htt")));
        if(matcher.find())	return matcher.group();
        else 				return null;
    }
}