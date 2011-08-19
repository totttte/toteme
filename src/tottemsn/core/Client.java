/**
 * �쐬��: 2006/08/07
 */
package tottemsn.core;

import java.net.Socket;
/**
 * �N���C�A���g�́A�f�[�^����M��S������N���X�͂�����p�����܂��B
 * @see Reciever
 * @see Sender
 */
abstract class Client extends Thread {

    Messenger messe;
    TrID trID;
    Session session;
    Socket socket;
    
    Client(Messenger messe, TrID trID, Session session) {
        this.messe = messe;
        this.trID = trID;
        this.session = session;
        this.socket = session.socket;
        moveServer();
        // �X���b�h�����w�肵�܂��B
        this.setName(this.getClass().getName() + this.getName());
    }
    
    /**
     *�@�T�[�o�ړ��̂��߂̎���
     */
    abstract void moveServer();
    
}