/**
 * 作成日: 2006/08/29
 */
package tottemsn.core;

/**
 * パケットや、コマンドなどで、断片化をするStringを扱うEntityです。
 * パケット本体をmainにし、その他の別のコマンド・パケットをotherにします。
 */
class FragmentString {

    String main;
    String other;
    
    FragmentString(String main, String other) {
        this.main = main;
        this.other = other;
    }
}