/**
 * 作成日: 2006/08/10
 */
package tottemsn.core;

import java.util.Date;
import java.util.Vector;
/**
 * <p>メッセンジャーの会話ログを表現するときに使います。</p>
 * 例えば、このセッションでの会話ログや、現在の参加者一覧などです。
 * 
 * <p>
 * 　SwitchBoardとBoardは、必ずしも対にならないことに注意してください。<br>
 * SwitchBoardは、会話中でも相手がメッセンジャーのチャットウィンドウを閉じれば、
 * セッションが終了します。また、一定の時間会話がないとセッションが終了します。<br>
 * 　しかし、一般のユーザ側から見る範囲では、SwitchBoardセッションの終了、再接続の有無の
 * 通知をする必要はありません。また、セッションが終了したからといってそのたびに、ウィンドウを
 * 開きなおすのではユーザにとって負担になります。
 * </p>
 * <p>
 * このため、チャットウィンドウの会話ログ表示領域のためのクラスが必要になってきました。
 * SwitchBoardセッションとユーザ側から見るチャットウィンドウとの間にこのBoardクラスを仲介する
 * ことによって、SwitchBoardの切断や再接続をブラックボックス化します。
 * </p>
 * 
 */
public class Board {
    private Vector members; //参加者(インスタンスはMemberクラス)
    private MemberList list; // ユーザが知っているメンバの場合の照らし合わせ用
    /*  ChatまたはStringのインスタンスを入れる
     *  文字列は、(XXが退席しましたなどの)インフォメーションです。
     */
    Vector logs;
    /*	一番最後まで残っていた人のアドレスです。
     * これは、セッション再開時に確認するためのものです。
     */
    private String lastAddress = "";
    /*	このBoardが生成された時間を保持します。一意のboardを認識するために使います。 */
    long startTime;
    
    /**
     * 外部パッケージから、Boardインスタンスへのアクセスは、
     * {@link MessengerListener}を実装したときの引数から行ってください。
     * @param list Messengerクラスから引き継いだメンバリストを代入します。
     */
    Board(MemberList list) {
        this.list = list;
        this.members = new Vector();
        logs = new Vector();
        startTime = new Date().getTime();
    }
    
    /**
     * 参加者を追加します。
     */
    Member addParticipant(Member member) {
        if(!members.contains(member))
            members.add( member);
        lastAddress = member.getAddress();
        return member;
    }
    
    /**
     * <p>このウィンドウが生成された時間を返します</p>
     * 一意のBoardを認識するために使います。
     */
    public long getTime() {
        return this.startTime;
    }
    
    /**
     * 参加者が一人退席します
     */
    Member removeParticipant(String address) {
        Member mem = list.equalsMember(address);
        if(mem == null)
            mem = new Member(address,"",1);
        members.remove(mem);
        if(members.size()==0)
            lastAddress = address;
        return mem;
    }
    
    /**
     * 引数で指定したメンバがこのBoardにいるか
     * @return このBoardの参加者に引数で指定したメンバがいればtrue
     */
    boolean contains(Member mem) {
        return members.contains(mem);
    }
    
    /**
     * 現在参加しているメンバの配列を返します。
     * @return 現在参加しているメンバの配列
     */
    public Member[] getMember() {
        Member[] mem = new Member[members.size()];
        members.copyInto(mem);
        return mem;
    }
    
    /**
     * 参加者を返します
     * @return 自分を含めない参加者数
     */
    public int getNumber() {
       return members.size(); 
    }
    
    /**
     * このBoardインスタンスに残っている今までの発言等を
     * 返します。
     * @return 今までのLog全部
     */
    public String getLog() {
        String texts = "";
        for(int i = 0; i < logs.size() ; i++) 
            texts += ((Chat)logs.get(i)).getText();
        return texts;
    }
    
    /**
     * 同じBoardかの判定メソッド
     * @return 同じメンバで構成されるBoardなら、trueを返します。
     * つまり、membersの並び順が異なっていても、同じBoardとみなされます。
     * ただし、LastMemberは同じでなければいけません。
     */
    public boolean equals(Object obj) {
        if(obj instanceof Board) {
            Board argBoard = (Board)obj;
            Vector comp = argBoard.members;
            if(members.size() != comp.size())
                return false;
            for(int i = 0; i < comp.size(); i++)
                if(! members.contains( comp.get(i) ) )
                    return false;
            return	this.lastAddress.equals(argBoard.lastAddress);
        } else
            return super.equals(obj);
    }
    
    /**
     * 同じメンバ（並び順は関連性なし）、最後の人が同じアドレスになるような、
     * hashcode値を返す。
     */
    public int hashCode() {
        int code = lastAddress.hashCode();
        int start = code;
        for(int i = 0; i < members.size(); i++) 
            if(members.get(i).hashCode() != start)
                code += members.get(i).hashCode();
        return (int)(code %= Integer.MAX_VALUE);
    }
    
    public String getLastAddress() {
        return lastAddress;
    }

    Chat addLog(Messages msg) {
        Chat chat = new Chat(msg);
        logs.add(chat);
        return chat;
    }    
}