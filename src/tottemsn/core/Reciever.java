/**
 * 作成日: 2006/08/07
 */
package tottemsn.core;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * <p>データ受信を担当するクラスです。</p>
 * 送信を担当するSenderクラスとは別スレッドで動きます。<br>
 * 受信したデータをどのように解析するかは、このクラスを継承し、parserメソッドで定義する必要があります。
 */
abstract class Reciever extends Client {
   
    DataInputStream in;
    Sender sender;
    
    Reciever(Messenger messe, TrID trID,Session session) {
        super(messe, trID, session);
    }

    /**
     * サーバ移動を実装します。
     */
    void moveServer() {
        try {
            socket = session.socket;
            in = new DataInputStream(socket.getInputStream());
        } catch(IOException e) {
            throw new MessengerException(e);
        }
    }
    
    /**
     * メッセージを受信したらパーサで処理を行います。
     * Thread#run() のオーバーライド
     */
    public void run(){
        String message = null;
        while((message = recieve()) != null) 
            parser(message);
    }
   
    /**
     * 読み込んだメッセージを解析するメソッドです。
     * @param message 読み込んだメッセージを指定
     */
    protected abstract void parser(String message);

    /**
     *	受信するデータです。
     *	最後が改行で終わっていない場合、まだ続きが来るものとして、次のデータが来るまで待ちます。
     */
    private String recieve() {
        try {
            byte[] bData = new byte[socket.getReceiveBufferSize()];
            int len = in.read(bData);
            if(len <= 0) 
                return null;
            String data = new String(bData, 0, len , "UTF-8");
            if(session instanceof Switchboard)
                StringUtil.println("[C <- SB]" + data);
            else 
                StringUtil.println("[C <- NC]" + data);
            if(StringUtil.isfragmentation(data)) //	パケットの断片化があった場合、続けて読み込む
                data += recieve();
            return data;
        } catch (Exception e) {
            if(session instanceof Switchboard) 
                return null;
             else throw new RuntimeException(e);
        }
    }

}