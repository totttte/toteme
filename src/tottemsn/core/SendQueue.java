/**
 * 作成日: 2006/06/16
 */
package tottemsn.core;

/**
 * <P>このクラスは、データ送信用の待ち行列のバッファを表します。</p>
 * <p>Recieverやユーザからの入力によって、待ち行列にバッファが追加されていき、
 *	Senderはこれを取り出して、サーバに送信していきます。</p>
 * <p>また、このクラスは複数のスレッドがアクセスするため、同期化を取ります。</p>
 *
 * <ol>全体として以下のような動作の流れになります。
 * <li>バッファは空の状態で生成される。</li>
 * <li>バッファが空の状態のとき、Senderは待機する。</li>
 * <li>Recieverやその他クラスによってバッファにデータが追加される。</li>
 * <li>データが追加されたとき、senderはnotifyAll()が呼び出され、動き出す。</li>
 * <li>Senderはバッファに入っているデータを手前から順に取り出し、それをサーバに送信する。</li>
 * <li>これをバッファが空になるまで続ける。2.に行き繰り返す。</li></ol>
 */
class SendQueue {

    /*	コレクションフレームワークでは、JDK1.5から、キューが実装されているが、
     * 	1.5はまだ新しいので、独自にキューを作る。								*/
    private String[] s; // キューの対象となるString
    private int pointer = 0; // 現在のキューの先頭を指している
    private int queue = 0; //　現在のキューの個数を表している
    
    /**
     * Stringの環状待ち行列を作ります
     * @param size 待ち行列のサイズを決定します。ここで指定したサイズ以上のキューが入ったままの
     * 状態の時、RuntimeExcetionが発生するので、サイズは大きめにとっておく必要があります。
     */
    SendQueue(int size) {
        s = new String[size];
    }
    
    /**
     * バッファの最後尾に文字列を追加します。
     * @throws 既にバッファが満杯の場合、RuntimeExceptionを発生させます。
     * @param o 追加したい文字列
     */
    synchronized void add(String o) {
        int last = (pointer + queue) % s.length;
        if(pointer - last == 1)
            throw new RuntimeException("Buffer over flow.");
        s[last] = o;
        queue++;
        notifyAll(); //　空になって待機している可能性があるので再開させる。
    }
    
    /**
     * 複数行いっぺんにキューを入れたいときはこちら
     */
    void add(String[] o) {
        for(int i = 0; i < o.length; i++) 
            add(o[i]);
    }
    
    /**
     * 文字列を一つ取得し、削除します。
     * @return 文字列があれば先頭から一つ取り出し、空の場合は、nullを返します。
     */
    synchronized String poll() {
        if(isEmpty()) return null;
        queue--;
        return s[pointer++];
    }
    
    /**		バッファが空かどうか  */
    synchronized boolean isEmpty() {
        return queue == 0;
    }    
}
