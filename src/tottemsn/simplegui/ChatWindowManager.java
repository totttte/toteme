/**
 * 作成日: 2006/09/03
 */
package tottemsn.simplegui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.JFrame;

import tottemsn.core.Board;
import tottemsn.core.Member;


//TODO 開発再開したとき、まずこれから、書き直す。 hashtableから書き直す
/**
 * リファクタリング最重要対象
 * チャットウィンドウを管理するクラスです。
 * @see Board
 */
public class ChatWindowManager {
    
    Member[] list;
    BasedMainWindow owner;
    // 1対1の会話ウィンドウが開いているかどうか　配列の並び順は、listに同じ
    boolean[] isInvite;
    
    private Hashtable manager; // 全てのチャットウィンドウを保持 key:board value:basedchatwindow
    
    ChatWindowManager(BasedMainWindow owner) {
        if(owner == null) //　スキン設定のため先にクラスだけ作る場合があるため。
            return;
        setInit(owner);
    }
    
    void setInit(BasedMainWindow owner) {
        this.owner = owner;
        this.list = owner.list.getList();
        this.isInvite = new boolean[list.length];
        manager = new Hashtable();        
    }

   //TODO リファクタリング対象だが変更時は慎重に・・・
    /*	新たに、ウィンドウを生成します。
     * 
     */
    void createNewWindow(final Board board, boolean isVisible, int frameType, Member mem) {
        if(isTalking(mem)) { // コネクションが２つあるということ
        /* 同時期に、お互いが話しかけようとしたとき、同じメンバのコネクションが２つ存在する場合
         * があります。そのときは、旧boardを新たなboardで上書きさせ、新しいウィンドウは開かない
         * ようにします */
            Enumeration e = manager.keys();
            Board old;
            while(e.hasMoreElements()) {
                if((old = (Board)e.nextElement()).getNumber() == 1) {
                    if(old.getLastAddress().equals(mem.getAddress())) {
                        //  manager.get(old) がなぜかnullになってしまう為、とりあえず線形探索
                        Object[] values = manager.values().toArray();
                        BasedChatWindow oldwindow = null;
                        for(int i = 0; i < values.length; i++) {
                            if(((BasedChatWindow)values[i]).
                                    board.getLastAddress().equals(old.getLastAddress())) {
                                oldwindow = ((BasedChatWindow)values[i]);
                                break;
                            }
                        }
                        if(oldwindow != null) {
                            manager.remove(old);
                            manager.put(board,oldwindow);
                            oldwindow.board = board;
                            owner.messe.bye(old); // SwitchBoardセッションを閉じ、１個にする
                            return;
                        }
                    }
                }
            }
        }  // end if(isTalking(mem))
        
        final BasedChatWindow chatwindow = new BasedChatWindow(board,owner);
        chatwindow.addShallMember(mem);
        JFrame frame = setSkin(chatwindow);
        chatwindow.setFrame(frame);
        manager.put(board,chatwindow);
        frame.setExtendedState(frameType);
        isInvite[getMemberNumber(mem)] = true;
        frame.addWindowListener( new WindowAdapter(){
            public void windowClosed( WindowEvent we ){
                close();
            }
            
            public void windowClosing( WindowEvent we) {
                close();
            }
            
            public void close() {
                if(board.getNumber()==1) {
                    int memberNumber = getMemberNumber(board.getMember()[0]);
                    if(memberNumber != -1) // 未登録者のみの場合
                        isInvite[memberNumber] = false;
                } else if(board.getNumber() == 0) { // 招待する前に閉じられたら
                    isInvite[getMemberNumber(
                           owner.list.equalsMember(chatwindow.infomationBar.getName()))]
                           = false;
                }
                owner.messe.bye(board);
                manager.remove(board);                
            }
        });
        frame.setVisible(isVisible);

    }
    
    /**
     * @param member 第二引数には、新たにこれから招待したメンバを指定します。招待時以外はnull
     */
    BasedChatWindow getMangaer(Board board, Member member) {
        // まずboardを一意に認識する boar生成時間を調べます
        Object[] windowob = manager.values().toArray();
        for(int i = 0; i < windowob.length; i++){
            BasedChatWindow old = (BasedChatWindow)windowob[i];
            if(old.board.getTime() == board.getTime())
                return old;
        }

        // 次にマネージャに登録されているウィンドウを調べます
        BasedChatWindow window =  (BasedChatWindow)manager.get(board);
        if(window != null)
            return window;
        // １対１のメンバを調べます
        if(board.getNumber() == 1 && isTalking(board.getMember()[0])){
            Object[] o = manager.values().toArray();
            for(int i = 0; i < o.length;i++)
                if(((BasedChatWindow)o[i]).board.getNumber() == 1)
                    if(((BasedChatWindow)o[i]).board.getMember()[0].equals(board.getMember()[0]))
                        return (BasedChatWindow)o[i];
        }
        // addParticipant時の　まだメンバがいない時を調べます
        Member mem = owner.list.equalsMember(board.getLastAddress());
        if(board.getNumber() == 0) {
            Object[] o = manager.values().toArray();
            for(int i = 0; i < o.length;i++) {
                if(((BasedChatWindow)o[i]).board.getNumber() == 0) {
                    String a = ((BasedChatWindow)o[i]).board.getLastAddress();
                    String b = member.getAddress();
                    if(a.equals(b))
                        return (BasedChatWindow)o[i];
                }
            }            
        }
        return null;
    }
    
    //　そのメンバとのチャットウィンドウが開いているかどうか
    boolean isTalking(Member mem) {
        for(int i = 0; i< list.length; i++)
            if(list[i].equals(mem)) 
                return isInvite[i];
        return false;
    }
    
    // フィールドのメンバの何番目か
    int getMemberNumber(Member mem) {
        for(int i = 0; i < list.length; i++)
            if(list[i].equals(mem))
                return i;
        return -1;
    }
    
    /**
     * このメソッドをオーバーライドして、スキンを変更させます。
     */
    public JFrame setSkin(BasedChatWindow window) {
        return new JFrame();
    }
}