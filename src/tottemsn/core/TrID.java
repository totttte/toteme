/**
 * 作成日: 2006/06/15
 */
package tottemsn.core;

/**
 * <p>トランザクションIDを管理します。</p>
 * SwitchBoardサーバおよびNotificationサーバとの同期的なやりとりには、トランザクションIDが使われます。
 * 
 * <p>トランザクションIDは、主にクライアントがなんらかのメッセージをサーバに送信したいときに、
 * 設定するもので、整数値をコマンドの２番目のトークンに指定します。基本的に、値は0からインクリメント
 * していくソフトが多いですが、番号の順序には関連性がないようです。
 * トランザクションをつけたメッセージへの応答は、同じ番号返ってきます。</p>
 * <p>
 * MSNプロトコルのコマンドの中には、返答を待たずに次のコマンドを送信するとエラーを返され、
 * セッションを強制終了されるものもあります。そのため、{@link Reciever}が受信するまで
 * {@link SendQueue}にあるコマンドを送らず待機しなければいけません。<br>
 * 　つまり送信したTrIDの番号が一致するまで待機すればよいのです。
 * </p>
 */
class TrID {

    private int trID = 0; // トランザクションID
    private boolean synFlag = false; // 同期がとれたときtrue
    
    /**
     * 引数のIDと同じかどうか判定します。
     * @param id
     * @return IDが同じならtrue
     */
    synchronized boolean isSame(int id) {
        synFlag = (trID == id);
        if(synFlag) notifyAll();
        return synFlag;
    }
    
    synchronized void addTrID() {
        trID++;
    }
    
    synchronized int getID() {
        return trID;
    }
    
    /**
     * TrIDの同期が取れるまでwaitし続けます。
     */
    synchronized void waitForSame() {
        try {
            while(! synFlag) 
                wait();
            synFlag = false;
        } catch(InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     *	<p>文字列からTrIDを得るためのメソッドです。</p>
     *  "command //TrID anything"の順にならんでいるので、スペースを探し、
     * TrIDを得ます。
     * また、第二引数がトランザクションIDではない、つまり整数値以外の場合、
     * -1を返します。
     */
    static int getTrID(String data) {
        int offset = data.indexOf(' ') + 1;
        int end = data.indexOf(' ', offset);
        end = (end == -1) ? data.length() - 2: end;
        int trid;
        try {
            trid =  Integer.parseInt( data.substring(offset, end) );
        }	catch(Exception e) {
            trid = -1;
        }
        return trid;
    }
}