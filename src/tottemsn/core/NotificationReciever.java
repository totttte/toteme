package tottemsn.core;

import java.net.Socket;
import java.util.Date;

import tottemsn.credits.Msnp11chl;
/**
 * <p>Notification用のデータ受信担当クラスです。</p>
 * データを受信し、さまざまなコマンドを解釈し、リスナーへ通知や、そのコマンドに対しての返答を
 * 処理する役割を果たします。
 */
class NotificationReciever extends Reciever {

    NotificationReciever(Messenger messe, TrID trID, Session session,Sender sender) {
        super(messe, trID, session);
        this.sender = sender;
    }
    
    /**
     * <p>run()メソッドのオーバライドです。</p>
     * スーパークラスでは、セッションが続いている限り無限ループをしているので、
     * このメソッドを抜け出すときは、セッションが終了してしまったか、なんらかの例外が発生したという
     * ことです。
     */
    public void run() {
        try {
        super.run();
        sender.que.add("OUT");
        } catch (Exception e) {
            messe.errorMSG(new MessengerException(e));
        }
        messe.errorMSG(new MessengerException("サーバから切断されました。"));
        sender.interrupt();
    }
    
    /**
     * <p>データ解析用パーサです。</p>
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
                if(Character.isDigit( datas[i].charAt(0) )) 
                     doErrorCode(datas[i]);
            } catch (Exception e) {
                throw (RuntimeException) new RuntimeException().initCause(e);
            }
        }
        // Senderとの同期処理
        int id = TrID.getTrID(datas[datas.length - 1]);
        // 明らかにtrIDじゃなかったら飛ばす
        if(id!=-1 && id < trID.getID() + 5 && id > trID.getID() - 5)
            trID.isSame(id+1);
    }
    
    /**
     * エラーコードを吐き出したとき、それを例外として投げます。
     */
    private void doErrorCode(String data) {
        int errorcode = 0;
        try {
            errorcode = Integer.parseInt( data.substring(0,3) );
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
        messe.errorMSG( new MessengerException(errorcode) );
    }

    /**
     * <p>RNGコマンドの処理</p>
     * SwitchBoardセッションに招待されたときに、このコマンドが使われます。
     */
    private void doRNG(String com) {
        int key = Integer.parseInt(StringUtil.messeToken(com,1));
        String ip = StringUtil.messeToken(com,2);
        int port = Integer.parseInt( ip.substring(ip.indexOf(':') + 1) );
        String key2 = StringUtil.messeToken(com,4);
        String mail = StringUtil.messeToken(com,5);
        ((Notification)session).addSession(key,key2,ip.substring(0,ip.indexOf(':')),port);
    }
    
    /**
     *	<p>XFRコマンドの処理</p>
     *	XFRコマンドには、２通りがあります。一つは、Notificationサーバの移動のため。<BR>
     *	もう一つは、SwitchBoardサーバを新たに確立した場合です。<BR>
     *	XFRコマンドの概要
     *	<pre>
     *	XFR //TrID NS 新IPアドレス:PORT 0 旧IPアドレス:PORT
     *	</pre>
     *	なお、SwitchBoardサーバ確立の場合は、{@link #doXFRSB(String, String, int)}
     *	が実装します。
     */
    private void doXFR(String data) {
        String ip = StringUtil.messeToken(data,3);
        // サーバ移動
        int port = Integer.parseInt( ip.substring(ip.indexOf(':') + 1) );
        ip = ip.substring(0,ip.indexOf(':'));
        if(StringUtil.messeToken(data,2).equals("SB")) {
            doXFRSB(data,ip ,port);
            return;
        }
        try {
            session.socket = new Socket(ip, port);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
        StringUtil.println("[INFO] Move Server>" + socket.getInetAddress().toString());
        moveServer();
        sender.moveServer();
    }
    
    /**
     * <p>SwitchBoardを作ります。</p>
     * それぞれ、IDなどのトークンを取得し、実際にセッション作成を実装するのは、
     * {@link Notification#addSession(int, String, String, int)}でやります。
     * @param data XFRコマンド
     * @parama ip IPアドレス
     * @parama port ポート
     */
    private void doXFRSB(String data,String ip, int port) {
        String key2 = StringUtil.messeToken(data,5);
        int key = 0;
        String mail = messe.user.getAccount();
        ((Notification)session).addSession(key,key2,ip,port);
    }

    /**
     * <p>USRコマンドに関する処理</p>
     * USRコマンドは、ユーザ認証をこれから行う場合と、
     * 認証を行った後の確認のコマンドの２種類があります。<br>
     * USRコマンドの概要
     * <pre>
     * 認証前	USR //TrID TWN S チャレンジキー
     * 認証後	USR //TrID OK アカウント
     * </pre>
     * また、ユーザ認証に成功した場合は{@link Messenger#finshSignIn(boolean, Exception)}
     * に通知します。
     */
    private void doUSR(String data) {
        String tok2 = StringUtil.messeToken(data,2);
        if(tok2.equals("TWN")) {         // TWNの場合　チャレンジキーの設定
            messe.user.challengeKey = StringUtil.messeToken(data,4);
        } else if(tok2.equals("OK")) { // OK の場合、接続成功フラグの設定
            messe.finshSignIn(true,null);
            synchronized(this){
                notifyAll();
            }
        }
    }
    
    /**
     * UUXコマンドを受け取ります。
     * これは、パーソナルメッセージ変更が完了したことを示しています。
     */
    private void doUUX(String data) {
        messe.update(MessengerListener.PSM_CHANGED);
    }
    
    /**
     * <p>CHLコマンドとは、サーバから送られてくる認証を兼ねたpingで、
     * これを60秒以内に返信しないと強制的にログオフされます。</p>
     * 
     * また、CHLコマンドを受信してからQRYコマンドを受信するまでの間に、
     * 他のコマンドを送信すると、エラーになります。
     * <I>MSNP11以上から仕様が変わりました。</I>詳細は以下のURL。
     * {@link http://msnpiki.msnfanatic.com/index.php/MSNP11:Challenges}
     */
    private void doCHL(String data) throws Exception {
        final String Product_ID = "PROD0090YUAUV{2B";
        final String Product_Key = "YMM8C_H7KCQ2S_KL";
        String key = Msnp11chl.createQRY( StringUtil.messeToken(data,2) );
        StringUtil.println("[info:TimeStamp]" + new Date().toString());
        sender.que.add("QRY //TrID "+ Product_ID + " 32\r\n" + key);
        sender.que.add("//Time");
        sender.que.add("//Lock");
    }
    
    /**
     * QRYコマンドはChallengesの応答として、送られてきます。
     * SendQueueのロックを解除します。
     */
    private void doQRY(String data) {
        sender.unlock();
    }
    
    /**
     * <p>LST文を読み込みます。</p>
     * メンバのリストが格納されています。コマンドの詳細は、{@link MemberList#addMember(String)}
     * を参照してください。<br>
     * また、全てのメンバを読み終わるとリストの更新をMessangerListenerを実装しているクラスに通知します。
     */
    private void doLST(String data) {
        MemberList mem = messe.memberlist;
        mem.addMember(data);
        if(mem.fullList()) 
            messe.getList(mem);    
    }
    
    /**
     * <p>LSG文を読み込みます。</p>
     * グループリストが格納されています。コマンドの詳細は、{@link MemberList#addGroupe(String)}
     * を参照してください。<br>
     * また、全てのグループを読み終わるとグループリストの更新をMessangerListenerを実装しているクラスに通知します。
     */
    private void doLSG(String data) {
        MemberList mem = messe.memberlist;
        mem.addGroupe(data);
        if(mem.fullGroupe())
            messe.getGroupe(mem.getGroupes());
    }
    
    /**
     * <p>SYN文を読み込みます。</p>
     * SYNコマンドは、サーバとの時刻の同期や、メンバの数、グループの数がそれぞれ格納されています。<br>
     * SYNコマンドの概要
     * <pre>
     * SYN //TrID 現在の時刻(世界標準時間) 時刻２ メンバの数 グループの数
     * </pre>
     */
    private void doSYN(String data) {
        int member = Integer.parseInt( StringUtil.messeToken(data,4) );
        int groupes = Integer.parseInt( StringUtil.messeToken(data,5) );
        sender.que.add("CHG //TrID " + messe.user.getUserStatus().getStatusCommand());
        if(messe.memberlist == null)
            messe.memberlist = new MemberList(member,groupes,messe);
    }
    
    /**
     * <p>NLN文、INL文およびFLN文を読み込みます。</p>
     * それぞれのコマンドの詳細は、{@link MemberList#changeStatus(String)}。
     * また、状態の更新をMessangerListenerを実装しているクラスに通知します。
     */
    private void doNLN(String data) {
        messe.changedStatus( messe.memberlist.changeStatus(data) );
    }
    
    private void doILN(String data) {
        doNLN(data);
    }
    
    private void doFLN(String data) {
        doNLN(data);
    }

    /**
     * <p>PRPコマンドの解釈</p>
     * サインインしたときのデフォルトの名前の場合) PRP MFN 名前
     * 自身の名前変更の場合)PRP //TrID MFN 名前
     */
    private void doPRP(String data) {
        // 現状では、[PRP MFN]文のみ
        if(StringUtil.messeToken(data,1).equals("MFN"))
            messe.user.reName( StringUtil.messeToken(data,2) );
        else if(StringUtil.messeToken(data,2).equals("MFN")) {
            messe.user.reName(StringUtil.messeToken(data,3));
            messe.update(MessengerListener.NAME_CHANGED);
        }
    }
    
    /**
     * 自身の状態変化完了
     */
    private void doCHG(String data) {
        messe.user.getUserStatus().setStatus( StringUtil.messeToken(data,2) );
        messe.update(MessengerListener.STATUS_CHANGED);
    }
    
    /**
     *　<p>セッション終了通知</p>
     *	"OUT OTH"の場合、他の場所でサインインしたことを表す。
     *	その他にも、何種類かコマンドがあるようだ。
     */
    private void doOUT(String data) {
        if(StringUtil.messeToken(data,1).equals("OTH")) 
            messe.errorMSG( new MessengerException("他の場所でサインインしました。") );
        else 
            messe.errorMSG( new MessengerException("メッセンジャーから切断されました。") );
    }
    
    /**
     * UBXコマンドについての詳細は、{@link XMLPayload}です。
     */
    private void doUBX(String data) {
        XMLPayload payload = new XMLPayload(data);
        Member mem = messe.memberlist.equalsMember(payload.getAddress());
        mem.psm = payload.getPersonalMessage();
        messe.changedStatus(mem);
    }
    
    /**
     * MSGコマンドを読み込みます。詳細は、{@link Messages}にあります。
     * なお、現段階では、ホットメールが何通あるかを読み込むだけです。
     */
    private void doMSG(String data) {
        Messages msg = new Messages(data);
        if(msg.isMailData()) {
            messe.hotMail = new HotMail(msg.getPayload());
            messe.update(MessengerListener.GET_HOTMAIL);
        }
    }
    
    private void do911(String data) {
         messe.finshSignIn(false,
                 new MessengerException("認証エラー:アカウント名とパスワードを確認してください。") );
         this.interrupt();
         sender.interrupt();
    }
}
