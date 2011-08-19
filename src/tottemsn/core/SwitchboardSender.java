/**
 * 作成日: 2006/08/09
 */
package tottemsn.core;
/**
 * Switchboardの送信に関わる部分を担当します。
 */
class SwitchboardSender extends Sender {

    SwitchboardSender(Messenger messe, TrID trID, Session sb) {
        super(messe, trID, sb);
    }
    
    /**
     * Sender#run() のオーバーライド
     */
    public void run() {
        try {
            if(((Switchboard)session).key == 0)
                que.add("USR //TrID //MailAddress "+((Switchboard)session).key2);
            else
                que.add("ANS //TrID //MailAddress "+
                    ((Switchboard)session).key2+" "+((Switchboard)session).key);
            super.run();
        } catch(Exception e) {
            messe.errorMSG(new MessengerException(((Switchboard)session).getBoard(),e));
        }
    }

    /**
     * 送信直前に決まる情報の変換機です。
     * なお、チャット中に // と打った場合は　/-　と一回変換してから　//　に直します。
     */
    protected String parser(String data) {
        data = StringUtil.replaceAll(data, "//MailAddress",  messe.user.getAccount());
        data = StringUtil.replaceAll(data,"//TrID", Integer.toString( trID.getID() ));
        if(data.startsWith("//Lock")) { // SendQueueのロックします
            super.lock();
            return null;
        }
        if(data.startsWith("//Unlock")) { // ロックを解除します
            super.unlock();
            return null;
        }
        if(data.equals("OUT"))
            return data;
        if(data.startsWith("USR ")) { // コマンド送信順序 (USR → CALを守るため)
            ((Switchboard)session).setFinished();
        }
        if(data.startsWith("//Closed")) { 
            ((Switchboard)session).noti.removeSession((Switchboard)session);
            // これらを実行している２つのスレッドに割り込みをかけます。
            interrupt();
            ((Switchboard)session).reciever.interrupt();
            return null;
        }
        if(data.startsWith("//Cut")) // とりあえず、読み飛ばす
            return null;
        data = StringUtil.replaceAll(data,"/-","//");
        return data;
    }    
}