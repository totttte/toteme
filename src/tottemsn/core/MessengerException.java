/**
 * 作成日: 2006/08/07
 */
package tottemsn.core;

/**
 * <p>メッセンジャーがらみの例外は、この例外を発生させます。</p>
 * 
 * 主に、サーバとのやりとりで、なんらかのコマンド送信ミスがあった場合や、
 * 認証に失敗した場合などがあげられます。
 */
public class MessengerException extends RuntimeException {
    
    private int errorCode = 0;
    private Board board; // SwitchBoardサーバでおきたエラーの場合のみ。
    
    public MessengerException(Throwable e) {
        super(e);
    }
    
    public MessengerException(String s) {
        super(s);
    }
    
    public MessengerException(int errorCode) {
        super(Integer.toString(errorCode));
        this.errorCode = errorCode;
    }
    
    public MessengerException(Board board, Exception e) {
        super(e);
        this.board = board;
    }
    
    /**
     * メッセンジャーサーバから送られてきたエラーコードを得ます。
     * @return コード番号(9xx)
     */
    public int getErrorCode() {
        return errorCode;
    }
    
    public Board getBoard() {
        return this.board;
    }
    
    public String getMessage() {
        String message = super.getMessage();
        if((errorCode >= 600 && errorCode < 700) || errorCode == 910) {
            message = "メッセンジャーのサーバがダウンしているか重くなっています。\n" +
            		"エラー番号:\t" + errorCode;
        }
        return message;
    }
}
