/**
 * 作成日: 2006/08/07
 */
package tottemsn.core;


/**
 * <p>メンバの状態や禁止・削除の有無を扱うクラスです。</p>
 */
public class Member implements Cloneable {
    private UserStatus status;
    private String address; // N
    private String name;	// F
    /* グループIDです。IDを指定します。
     * もしグループがない、もしくはどこのグループにも所属していない場合、
     * "0"が指定されます。
     * もしそのメンバが多重のグループに所属していた場合、
     * グループキーは以下のようになります。
     * 275f467b-309a-41c0-b442-5012fb018ae8,983b331b-c5db-4b9c-84e7-a8c9395c7467
     * つまり、[,]カンマで区切られて一つのトークンにまとめて送られてくるのです。
     */
    String groupeKey = "0"; 
    private boolean isBlock; // 禁止の有無
    private boolean isForward; // 削除の有無
    String psm = ""; // パーソナルメッセージです。(MSNP11以上)
    
    Member(String address, String name, int bitwise) {
        status = new UserStatus("FLN");
        this.address = address;
        this.name = name;
        isBlock = (bitwise & 0x0004) == 0x0004;
        isForward = (bitwise & 0x0001) == 0x0001;
    }
    
    /** このメンバーのアドレスを得ます */
    public String getAddress(){
        return address;
    }
    
    /** このメンバーの名前を変更します。 */
    void rename(String name) {
        this.name = name;
    }
    
    /**
     * このメンバの現在の名前を取得します
     * もし、名前がなかった場合、アドレスを返します
     * @return このメンバの名前
     */
    public String getName() {
        if(name.equals(""))
            return this.address;
        return this.name;
    }
    /**
     * 同一のメンバーかどうか判定します。
     * equalsメソッドのオーバーライドで判定方法は、アドレスの一致かどうかです。
     * @return 同一のメンバなら true を返す。
     */
    public boolean equals(Object obj) {
        if(obj instanceof Member) 
            return address.equals( ((Member)obj).address );
        else 
            return super.equals(obj);
    }
    
    public int hashCode() {
        return address.hashCode();
    }
    
    /**
     * メンバーの状態を取得します。
     */
    public UserStatus getStatus() {
        return status;
    }
    
    /**
     * Stringメソッドのオーバーライド
     */
    public String toString() {
       return "[status=" + status + ",address=" + address
       			+ ",name=" + name  + ",psm=" + psm +  ",groupeKey=" + groupeKey
       			+ ",block=" + isBlock + ",Forward=" + isForward + "]";
    }
    
    /**
     * このオブジェクトのコピーを生成します。
     */
    public Object clone() {
        try {
            Member mem = (Member)super.clone();
            mem.address = address;
            mem.name = name;
            mem.isBlock = isBlock;
            mem.isForward = isForward;
            mem.groupeKey = groupeKey;
            mem.psm = psm;
            mem.status = new UserStatus(status.getStatusCommand());
            return mem;
        } catch(CloneNotSupportedException e) {
            return null;
        }
    }
    
    /**
     * このメンバのパーソナルメッセージを返します。
     * @return パーソナルメッセージがない場合空文字
     */
    public String getPersonalMessage() {
        return psm;
    }
    
    /**
     * <p>引数で指定されたメンバでこのインスタンスを上書きします。</p>
     * メンバの全ての内容をリフレッシュしたいときに使います。
     * @param mem 変更したいメンバ
     */
    void setMember(Member mem) {
        this.address = mem.address;
        this.groupeKey = mem.groupeKey;
        this.isBlock = mem.isBlock;
        this.isForward = mem.isForward;
        this.psm = mem.psm;
        this.name = mem.name;
        this.status = mem.status;
    }
    
    /**
     * このメンバを禁止しているかどうか
     * @return このメンバを禁止していたらtrue
     */
    public boolean isBlock() {
        return isBlock;
    }
    
    /**
     * このメンバが削除されたメンバかどうか
     * @return このメンバが状態変化を通知されるべきメンバならtrue
     */
    public boolean isForward() {
       return isForward; 
    }
}