/**
 * 作成日: 2006/08/09
 */
package tottemsn.core;

/**
 * Switchboardの受信担当クラスです。
 */
class SwitchboardReciever extends Reciever {

    private Board board;

    SwitchboardReciever(Messenger messe, TrID trID, Session session, Sender sender) {
        super(messe, trID, session);
        this.sender = sender;
    }
    
    void setBoard(Board board) {
        this.board = board;
    }
    
    public void run() {
        super.run();
    }
    
    /**
     * <p>データ解析用パーサです。</p>
     * <I>現在では、NotificationRecieverと内容が同一です。今後リファクタリングの可能性あり。</I>
     *	//TODO リファクタリング対象 
     * リフレクションを使って、該当するメソッドを実行します。
     * @param message パケットで受信したメッセージ
     */
    protected void parser(String message) {
        // 複数のコマンドが同時に送られてきたときに分割
        String datas[] = StringUtil.spiltCommand(message);
        for(int i=0; i < datas.length; i++) {
            if(datas[i].length() < 3) continue;
            String s = "do" + datas[i].substring(0,3);
            Object[] o = new Object[1];
            o[0] = datas[i];
            try {
                Class[] cls = new Class[1];
                cls[0] = String.class;
                getClass().getDeclaredMethod(s,cls).invoke(this,o);
            } catch (NoSuchMethodException e) {
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        // Senderとの同期処理
        int id = TrID.getTrID(datas[datas.length - 1]);
        // 明らかにtrIDじゃなかったら飛ばす
        if(id!=-1 && id < trID.getID() + 5 && id > trID.getID() - 5)
            trID.isSame(id+1);
    }

    /**
     * MSGコマンドの処理を担当するメソッドです。
     * なお、MSGコマンドの後に別のコマンドが入ってくることも十分考えられるので、
     * MSGコマンド以外の部分を戻り値として返し、残りは引き続き #parser(String) で処理します。
     */
    private String doMSG(String message) {
        Messages msg = new Messages(StringUtil.getMSG(message,3));
        if(msg.isTextMessage()) {
            messe.getChat( board.addLog(msg) , board);
        }
        String others = Messages.getOthers(message,3);
        if(others!=null && others.startsWith("MSG "))
            others = doMSG(others);
        return others;
    }
    
    /**
     * IROコマンドは、そのセッションに参加している人リストです。
     */
    private void doIRO(String data) {
        observeParticipant(StringUtil.messeToken(data,4));
    }
    
    /**
     * 誰かが退席したときのコマンドです。
     * 概要) BYE 退席した人のアカウント
     */
    private void doBYE(String data) {
        Member oldMember = board.removeParticipant(StringUtil.messeToken(data,1));
        if(board.getNumber() == 0)
            doOUT(data);
        else 
            messe.removeParticipant(board,  oldMember);
    }
    
    /**
     * 途中で誰かが参加するときのコマンドです。
     * [C <- SB]JOI address name
     */
    private void doJOI(String data) {
        sender.unlock();
        sender.que.add("//Cut");
        observeParticipant(StringUtil.messeToken(data,1));
    }
    
    /**
     * <p>このセッションが終了したとき、OUTコマンドが呼び出されます。</p>
     * SwitchBoardとのセッションは頻繁に接続、切断が行われるので、
     * SwitchBoardに関わるThreadに割り込みをかけ、スレッドを終了させます。
     */
    private void doOUT(String data) {
        ((Switchboard)session).noti.removeSession((Switchboard)session);
        // これらを実行している２つのスレッドに割り込みをかけます。
        sender.interrupt();
        this.interrupt();
    }
    
    private void doACK(String data) {
        messe.finishSendMsg(board,true);
    }
    
    private void doNAK(String data) {
        messe.finishSendMsg(board,false);
    }
    
    private void do215(String data) {
        messe.errorMSG(new MessengerException(this.board,new MessengerException(215)));
    }
    
    /*
     * 新たなメンバが増えたときに、通知するためのメソッドです。
     * ただし、既に同じメンバがいた場合は通知しません。
     */
    private void observeParticipant(String address) {
        Member member = messe.memberlist.equalsMember(address);
        if(member == null) { // 登録していないメンバだった場合、仮のメンバ
            member = new Member(address,"",4);
        }
        messe.addParticipant(board,  member);
        Member newMember = board.addParticipant(member);
    }
}