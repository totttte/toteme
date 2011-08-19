/**
 * 作成日: 2006/08/07
 */
package tottemsn.core;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * メッセンジャーのメンバリストを格納します。
 */
public class MemberList {

    private Member[] list; // メンバをリスト形式で保存します
    private int listnum; // 現在のメンバ数
   /*	グループを表します。グループはString型のkeyと、
    *	それぞれグループの名前で表されています。
    */ 
    private Hashtable groupe;
    private int groupes;
    
    private Messenger messe;
    
    /**
     * 新たなメンバリストを作成します。
     * @param people メンバリストの人数を指定します。
     * @param groupes メンバリストのグループ数を指定します。
     */
    MemberList(int people, int groupes, Messenger messe) {
        list = new Member[people];
        groupe = new Hashtable();
        this.groupes = groupes;
        this.messe = messe;
    }
    
    /**
     * メンバを追加します。
     * リストコマンドのサンプルは以下の通り
     * <pre>
     * LST N=address@hotmail.com F=name C=key 11 1 [groupeKey]
     * </pre>
     * これからから、アカウント、名前、状態(この場合は11)、groupeKeyを取得し、
     * 新しいメンバを追加します。
     * @param command LSTコマンド(LSTから始まる１行)を指定します。
     */
    void addMember(String command){
        	int tokenn = Math.min( StringUtil.tokenCount(command)-2, 4);
            String N= StringUtil.messeToken(command,1).substring(2);
            String F= (tokenn>3)?StringUtil.messeToken(command,2).substring(2):"";
            int bitwise = Integer.parseInt(StringUtil.messeToken(command,tokenn));
            String groupe = StringUtil.messeToken(command,6);
            if(groupe==null) groupe = "0";
            Member mem = new Member(N,F,bitwise);
            mem.groupeKey = groupe;
            list[listnum++] = mem;
    }
    
    /**
     * リストが満杯になったかどうか
     * @return リストが満杯になったときtrue
     */
    boolean fullList() {
        return listnum == list.length;
    }

    /**
     * グループを追加します。
     * グループリストのサンプルは以下の通り
     * <pre>
     * LSG 友人 groupeKey
     * </pre>
     * @param command LSGコマンド(LSG から始まる1行)を指定します
     */
    void addGroupe(String command) {
        String key = StringUtil.messeToken(command,2);
        String value = StringUtil.messeToken(command,1);
        groupe.put(key,value);
    }
        
    /**
     * グループが満杯になったかどうか
     * @return グループが満杯になったときtrue
     */
    boolean fullGroupe() {
        return groupes == groupe.size();
    }
    
    /**
     * メンバの誰かの状態変化があったときこのメソッドを使用します。
     * 状態変化のサンプルは以下の通り
     * <pre>
     * NLN NLN address@hotmail.com name key key2
     * INL TrID NLN address name key key2
     * FLN address@hotmaill.com
     * </pre>
     * @param command INL,NLN,FLNコマンド
     * (ILN,NLN,FLNから始まる1行)を指定します
     * @return 状態変化が起きたメンバを返します。なお、
     * メンバはこのクラスが保持しているメンバリストのコピーであって、
     * 直接参照しているわけではありません。
     */
    Member changeStatus(String command) {
        int tokenn=1;
        if(command.startsWith("NLN"))
            tokenn = 2;
         else if(command.startsWith("ILN"))
            tokenn = 3;
        else if(command.startsWith("FLN"))
            tokenn = 1;
        String add = StringUtil.messeToken(command,tokenn);
        String status =  StringUtil.messeToken(command,tokenn-1);
        Member mem = equalsMember(add);
        mem.getStatus().setStatus(status);
        if(tokenn != 1) { // not FLN
            String name = StringUtil.messeToken(command,tokenn+1);
            if(! mem.getName().equals(name)) {
                mem.rename(name);
                messe.changedName(mem);
            }
        }
        return (Member)mem.clone();
    }
    
    /**
     * メンバリストを取得します。なお、内部データのコピーであってMemberオブジェクト
     * の内部データの配列への参照を保持しているわけではありません。
     * なお、このメソッドを使うと、オフラインユーザが配列の後方に並ぶようになっています。
     * @return コピーされたメンバリスト
     */
    public Member[] getList() {
        Member[] newList = new Member[list.length];
        /* オフラインユーザは後ろから埋めていく
         * オンラインユーザは前からつめていく 	*/
        int j = newList.length - 1;
        int i = 0;
        for(int k=0; k < newList.length;k++) 
            if(list[k].getStatus().getStatusCommand().equals("FLN"))
                 newList[j--] = (Member)list[k].clone();
             else 
                 newList[i++] = (Member)list[k].clone();
        return newList;
    }
    
    /**
     * <p>引数で指定したグループに所属しているメンバリストを返します。</p>
     * なお、このメソッドを使うと、オフラインユーザが配列の後方に並ぶようになっています。
     * @see #getList()
     * @param groupeName グループ名を指定します。なお、nullや空文字列を指定すると、グループなしの人
     * を返します。
     * @return グループの配列
     */
    public Member[] getList(String groupeName) {
        // 名前からグループキーを受け取る
        String key = "0";
        if(groupeName == null || groupeName.equals("") || !groupe.containsValue(groupeName)) {
            key = "0";
        } else {
            Enumeration e = groupe.keys();
            for(int i=0; i < groupe.size(); i++) {
                Object keye = e.nextElement();
                if(groupe.get(keye).equals(groupeName)) {
                    key = keye.toString();
                    break;
                }
            }
        }
        Vector dest = new Vector(); // 配列の中身はMember
        for(int i = 0; i < list.length; i++) {
            if(isGroupe(list[i],key)) 
                if(list[i].getStatus().getStatusCommand().equals("FLN"))
                    dest.add(dest.size(),list[i]);
                else 
                    dest.add(0,list[i]);
        }
        Member[] destArray = new Member[dest.size()]; 
        for(int i = 0; i < dest.size(); i++) 
            destArray[i] = (Member)dest.get(i);
        return destArray;
    }
    
    /**
     * <p>引数で指定したMemberが、引数で指定したKeyのメンバかどうか調べます。</p>
     * このメソッドを使って呼び出した場合、多重グループ登録されてるメンバにも対応させています。
     * @param mem メンバ
     * @param key キー
     * @return　引数で指定したグループに所属していた場合true
     */
    private static boolean isGroupe(Member mem,String key) {
        String memKey = mem.groupeKey;
        if(key.equals(memKey))
            return true;
        else if(memKey.indexOf(',')==-1)
            return false;
        else {
            int index = 0;
            int oldIndex = 0;
            while((index = memKey.indexOf(',',index+1))!=-1)
                if(memKey.substring(oldIndex,index).equals(key))
                    return true;
                else 
                    oldIndex = index + 1;
            return memKey.substring(oldIndex).equals(key);
        }
    }
    
    /**
     * グループ一覧を取得します
     * "グループなし"のグループも入れます。
     * @return グループ一覧(グループ名)をStringの配列形式で返す。
     */
    public String[] getGroupes() {
        String[] strings = new String[groupe.size() + 1];
        Enumeration e = groupe.keys();
        for(int i=0; i < strings.length - 1;i++)
            strings[i] = (String)groupe.get( e.nextElement() );
        strings[strings.length - 1] = "グループなし";
        return strings;
    }
    
    /**
     * toString()メソッドのオーバライドです。
     * [メンバを全員表示し、その後グループリストを表示します。
     */
    public String toString() {
        String buf = "[list:";
        for(int i=0; i< list.length;i++)
            buf += list[i] + ",";
        buf += "][groupe:";
        String[] gr = getGroupes();
        for(int i=0; i < gr.length;i++)
            buf += gr[i] + ",";
        return buf + "]";
    }
    
    /**
     * 引数で指定したアドレスが一致するメンバを探し、取得します。
     * @param address メールアドレス
     * @return アドレスが一致したメンバ。メンバがいない場合nullを返します。
     */
    public Member equalsMember(String address) {
        for(int i=0; i< listnum && list[i]!=null ; i++)
            if(list[i].getAddress().equals(address))
                return list[i];
        return null;
    }
    
    /**
     * 引数で指定したメンバの状態を変更します。
     * @param member 状態を変更したいメンバ
     */
    public void chengeMember(Member member) {
        Member old  = equalsMember(member.getAddress());
        old.setMember(member);
    }
}