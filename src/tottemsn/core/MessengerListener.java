/**
 * 作成日: 2006/06/25
 */
package tottemsn.core;

import java.util.EventListener;

/**
 * <p>メッセンジャーのイベントが発生したときに、それを受け取るリスナーです。</p>
 * 外部パッケージからメッセンジャーに関するイベントの通知にはこのクラスを継承する必要があります。
 */
public interface MessengerListener extends EventListener {

    /**
     * 自分の状態変更が完了したことを表す。
     */
    public static final int STATUS_CHANGED = 1;
    /**
     * 自分の名前変更が完了したことを表す。
     */
    public static final int NAME_CHANGED = 2;
    /**
     * 自分のパーソナルメッセージ変更が完了したことを表す。
     */
    public static final int PSM_CHANGED = 3;
    /**
     * ホットメールを受信したことを示します
     */
    public static final int GET_HOTMAIL = 4;
    
    /**
     * メンバリスト取得の通知
     */
    public void getList(MemberList list);
    
    /**
     * メンバリストの誰かのステータス変更の通知
     */
    public void changedStatus(Member member);
   
    /**
     * 誰かの名前変更があったとき
     *
     */
    public void changedName(Member member);
    
    /**
     * グループリスト取得の通知
     * @param table　グループリスト
     */
    public void getGroupe(String s[]);    
    
    /**
     *	サインインし終わったとき。
     *  また、サインインが成功せず例外が発生した場合は、
     *  その例外をeから得ます。例外が発生していない場合、e=nullです。
     */
    public void finshSignIn(boolean isSuccess, Exception e);

    /**
     * チャットウィンドウに誰かが参加したら
     * (ここでのboardインスタンスには、まだそのメンバは追加されていません。)
     */
    public void addParticipant(Board board,Member member);
    
    /**
     * チャットウィンドウで誰かが退席したら
     */
    public void removeParticipant(Board board,Member member);
    
    /**
     *	なんらかの会話があったら。
     */
    public void getChat(Chat chat, Board board);
    
    /**
     * 今送ったメッセージが届いたどうか
     */
    public void finishSendMsg(Board board, boolean isSuccess);
    
    /**
     * その他、何らかの状態変化の通知。
     * 例えば自身の状態変更が完了したとか。
     * @param type 通知の内容をフィールド定数で表します。
     */
    public void update(int type);
    
    /**
     * サインアウトさせられたり、なんらかの例外が発生し、
     * このセッションでメッセンジャーを続けることができなくなった場合。
     */
    public void errorMSG(MessengerException e);
}