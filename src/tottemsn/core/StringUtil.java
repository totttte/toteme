/**
 * 作成日: 2006/06/14
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
 * <p>文字列操作のためのユーティリティです。</p>
 * このクラスのメソッドはインスタンスを生成することなくクラスメソッドで呼び出します。
 */
public class StringUtil {
    
    /**	デバッグモードの時は、true	 */
    public static boolean debuc = true;
    /**	パケットログの出力先				*/
    public final static PrintStream log = System.out;

    /**
     * [ ver=value, ]パターンから、verを指定するとvalueを返します。
     * その際、''で囲まれた部分に返しては、間に,があったとしても無効とします。
     * また、文字列の最後までいった場合は、最後までをvalueとします。
     * 以下に例を示します。
     * concater("type=test,name=tote,", "name") の戻り値はtote
     * concater("type=test,name='tote,totte',", "name") の戻り値はtote,totte
     */
    public static String concater(String src, String key) {
        int begin = src.indexOf(key) + key.length() + 1;
        int end;
        if(src.charAt(begin) == '\'') { 
            begin++; // ' の文字分だけ読み進める
            end = src.indexOf('\'', begin + 1);
        } else 
            end = src.indexOf(',', begin); 
        return  src.substring(begin, (end == -1) ? src.length() : end);
    }
    
    /**
     * $記号対応文字列置き換えメソッド
     * とにかく、置き換えるだけです。
     * String#replaceAll()系と違うところは、正規表現を使って置き換えるか、そうでないかです。
     * @param src 読み込む文字列
     * @param before マッチする文字列
     * @param after マッチした文字列を置き換える
     * @return 置き換え後の文字列
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
     * サインイン専用ファイルを読み込んで、1行ずつ配列にして返します。
     * @param fileName 読み込むファイル
     * @return 配列化された文字列
     */
    public static String[] readSignInFile(String fileName) {
        try {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        Vector v = new Vector();
        String l = "";
        while((l = br.readLine()) != null) {
            if(l.startsWith("#")) continue; // #はコメント行とする。
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
     * 引数で指定したMD5ダイジェストのキーを返します。
     * @return 16進[0-9,A-F]の32桁のコードを返します。
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
     * 改行ごとに配列化します。
     * "\r\n"区切り
     * @param src 改行区切りしたい文字列
     */
    public static String[] spiltNL(String src) {
        StringTokenizer token = new StringTokenizer(src, "\n");
        String[] dest = new String[token.countTokens()];
        int i = 0;
        for(dest[0] = token.nextToken(); token.hasMoreTokens(); dest[++i] = token.nextToken())
            if(dest[i].charAt(dest[i].length() - 1) == '\r')
                dest[i] = dest[i].substring(0, dest[i].length() - 1); // "\r"を切る
            else continue;
        dest[i] = dest[i].substring(0, dest[i].length() - 1); // 最後はもう一文字削る
        return dest;
    } 
    
    /**
     * <p>コマンドごとに区切って配列化します。</p>
     * <p>コマンドには、1行で終わるコマンドもあれば、MSGコマンドのように何行かに渡って続く
     * コマンドもあります。それらをStringの配列に置き換えます。</p>
     * <ul>現在対応している複数行コマンド<li>MSG</li><li>UBX</li></ul>
     * @param src 区切りたいデータ
     * @return 区切られて配列化されたもの
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
     * 引数で指定した位置のトークンを返し、URLデコードします。
     * メッセンジャー特有の、半角スペース区切りをトークンとします。
     * <pre>ex) System.out.println( getMesseToken("XFA 2 test hoge",2) );
     * >test</pre>
     * @param src 区切りたいトークン
     * @param n 何番目のトークンを取り出したいか指定します。最初のトークンは0から始まります。
     * @return 取り出した後のトークンを返します。もし、n番目にトークンがない場合、null
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
     * <p>このコマンドが断片化しているかを調べます。</p>
     * @param data 読み込んだパケットを指定します。
     * @return 断片化していたらtrue
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
        /* 文字列<sizeとなっていた場合、断片化しているということである。
         * 文字列==sizeは、MSGコマンド１つだけが送られてきたということである。
         * MSGコマンドの後に別のコマンドがきた場合は、 文字列>size　となる場合がある。 
         */
        if(getUTF8Length(data) > size) 
            return isfragmentation( Messages.getOthers(data,tokennumber) );
        return getUTF8Length(data) < size;
    }

    /**
     * <p>payloadがつくコマンドにおいて、２行目以降の文字列の長さをbyte単位で取得します。</p>
     * 受信したパケットのUTF-8エンコードでの文字列の長さを取得します。
     * @param data 文字列の長さ
     */
   static int getUTF8Length(String data) {
       try {
           return data.substring(data.indexOf('\n')+1).getBytes("UTF-8").length;
       } catch (UnsupportedEncodingException e) {
           return -1; //　まぁないとは思うけど。
       }
   }
   
   /**
    * <p>複数のコマンドがセットになった文字列から最初のMSGコマンド,UBXコマンドを返します。</p>
    * 
    * 注意) コマンドが断片化していた場合、@see StringIndexOutOfBoundsException が
    * 発生する恐れがあります。@see #isfragmentation(String) でチェックしてから使うようにしてください。
    * @param data
    * @param tokenpoint 何番目のトークンをサイズの評価に使うか
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

    /**	デバッグログ出力用メソッドです。 */
    public static void println(String string) {
        if(debuc) log.println(string);
    }
    
    /** 指定した文字列からURL部を抜き出します。ただし最初の１つだけ。*/
    public static String getURL(String data) {
        Pattern pattern = Pattern.compile("^http(s?)://[\\w\\.\\-/:&?,=#]+",Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(data.substring(data.indexOf("htt")));
        if(matcher.find())	return matcher.group();
        else 				return null;
    }
}