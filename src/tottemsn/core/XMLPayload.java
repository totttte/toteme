/**
 * 作成日: 2006/08/24
 */
package tottemsn.core;

import java.io.UnsupportedEncodingException;


/**
 * <p>MSNP11から実装され始めたXMLで書かれたPayload部分を含むコマンドに関する処理を行います。</p>
 * <ul>
 * 主に以下のコマンドに関する処理を扱います。
 *  <li>UUX・・・自分がパーソナルメッセージを設定するためのコマンド</li>
 *  <li>UBX・・・フレンドリストにいる誰かがパーソナルメッセージを設定したときのためのコマンド</li>
 *  <li>GCF・・・ウィンク、カメラ、背景の共有などを扱うコマンド。</li>
 * </ul>
 * <p>これらのコマンドには以下のような形式になっています。</p>
 * UUXおよびGCF
 * <pre>
 * UUX //TrID //payloadsize(integer)\r\n
 * //XMLDocument
 * </pre>
 * UBX
 * <pre>
 * UBX account //payloadsize(integer)\r\n
 * //XMLDocument
 * </pre>
 * <p>
 * Payloadを付加しているコマンドには他にも、MSGコマンドがありますが、そちらはHTMLのヘッダメソッドに
 * 近い形式で書かれているので、クラス分割しました。
 * </p>
 * <i>このクラスのインスタンスは、頻度に呼び出され生成される可能性が高く、それぞれのXMLパーサの処理は、
 * 比較的簡単なものが多いので、javax.xml.*パッケージのパーサは使っていません。
 * </i>
 * @see tottemsn.core.StringUtil#isfragmentation(String)
 */
class XMLPayload {

    private String address;
    private String psm;
    
    /**
     * <p>受信したパケットからUBXコマンドの解析用インスタンスを生成します。</p>
     * <i>なお、他の複数のコマンドと同時に受信したときのために、あらかじめ
     * {@link StringUtil#getMSG(String, int)}でこのコマンドのみを取り出しておく必要があります。
     * また、このコマンドが断片化していないことが必要条件です。</i>
     * @param ubx ubxコマンドとUBXコマンドに続くpayload。
     */
    XMLPayload(String ubx) {
        this.address = StringUtil.messeToken(ubx,1);
        if(ubx.indexOf('\n') >= ubx.length() - 1)
            this.psm = "";
        else {
            String xml = ubx.substring(ubx.indexOf('\n')+1);
            this.psm = getAttribute(xml,"PSM");
        }
    }
    
    String getAddress() {
        return address;
    }
    
    String getPersonalMessage() {
        return psm;
    }
    
    public String toString() {
        return  "[account=" + address + ",psm=" + psm + "]";
    }
    
    /**
     * UUXコマンド形式の文字列を作ります。
     * @param psm personal Messageを指定します。
     * @return xml形式になったUUXコマンド
     */
    static String makeUUXCommand(String psm) {
        final String header = "<Data><PSM>";
        final String footer = "</PSM><CurrentMedia></CurrentMedia></Data>";
        String payload = header + psm + footer;
        int length = 0;
        try {
            length = payload.getBytes("UTF-8").length;
        } catch(UnsupportedEncodingException e) {
        }
        return "UUX //TrID " + length + "\r\n" + payload;
    }
    
    /** <p>XMLの要素名から、その要素を取り出します。</p>
     * 例）
     * <pre>
     * String src = "&lt;data&gt;&lt;psm&gt;test&lt;/psm&gt;&lt;/data&gt;";
     * String name = "psm";
     * System.out.println(  getAttribute(src,name) );
     * >test
     * </pre>
     * @param src XMLドキュメント
     * @param name 調べたい要素
     */
    private static String getAttribute(String src,String name) {
        int begin = src.indexOf(name) + name.length() + 1;
        int end = src.indexOf(name,begin) - 2;
        return src.substring(begin,end);
    }
}