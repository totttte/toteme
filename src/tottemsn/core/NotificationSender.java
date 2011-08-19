/**
 * 作成日: 2006/08/08
 */
package tottemsn.core;

import java.util.Date;


/**
 * Notification用のデータ送信担当クラスです。
 */
class NotificationSender extends Sender {

    NotificationSender(Messenger messe, TrID trID,Session session) {
        super(messe, trID,session);
    }
    
    public void run() {
        try {
            login();
            super.run();
        } catch(Exception e) {
            messe.errorMSG(new MessengerException(e));
        }
        messe.errorMSG( new MessengerException("サーバから切断されました。") );
    }

    /**
     * ログインするために、Queにsend.txtの値をセットします。
     */
    private void login() {
        que.add(StringUtil.readSignInFile("data/send.txt"));
    }

    protected String parser(String data) {
        data = StringUtil.replaceAll(data, "//MailAddress",  messe.user.getAccount());
        data = StringUtil.replaceAll(data,"//TrID", Integer.toString( trID.getID() ));
        data = StringUtil.replaceAll(data,"//Status", messe.user.getUserStatus().getStatusCommand());
        if(data.startsWith("//Wait")) {
            trID.waitForSame();
            return null;
        }
        if(data.startsWith("//Lock")) {
            super.lock();
            return null;
        }
        if(data.startsWith("//Time")) {
            StringUtil.println("[INFO:TimeStamp]" + new Date().toString());
            return null;
        }
        final String AUTH_PASS = "//auth_pass"; //　.net Pass portによる認証で得たキーが入る
        if(data.indexOf(AUTH_PASS) != -1) {
            NetPassPort passport = new NetPassPort(messe.user, messe.user.challengeKey);
            if( passport.tryAuthorization() ) 
                data = StringUtil.replaceAll(data, AUTH_PASS , passport.getKey());
            else 
                messe.finshSignIn(false, passport.getErrorMSG());
        }
        return data;
    }
}