/**
 * �쐬��: 2006/08/07
 */
package tottemsn.core;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * <p>�f�[�^��M��S������N���X�ł��B</p>
 * ���M��S������Sender�N���X�Ƃ͕ʃX���b�h�œ����܂��B<br>
 * ��M�����f�[�^���ǂ̂悤�ɉ�͂��邩�́A���̃N���X���p�����Aparser���\�b�h�Œ�`����K�v������܂��B
 */
abstract class Reciever extends Client {
   
    DataInputStream in;
    Sender sender;
    
    Reciever(Messenger messe, TrID trID,Session session) {
        super(messe, trID, session);
    }

    /**
     * �T�[�o�ړ����������܂��B
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
     * ���b�Z�[�W����M������p�[�T�ŏ������s���܂��B
     * Thread#run() �̃I�[�o�[���C�h
     */
    public void run(){
        String message = null;
        while((message = recieve()) != null) 
            parser(message);
    }
   
    /**
     * �ǂݍ��񂾃��b�Z�[�W����͂��郁�\�b�h�ł��B
     * @param message �ǂݍ��񂾃��b�Z�[�W���w��
     */
    protected abstract void parser(String message);

    /**
     *	��M����f�[�^�ł��B
     *	�Ōオ���s�ŏI����Ă��Ȃ��ꍇ�A�܂�������������̂Ƃ��āA���̃f�[�^������܂ő҂��܂��B
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
            if(StringUtil.isfragmentation(data)) //	�p�P�b�g�̒f�Љ����������ꍇ�A�����ēǂݍ���
                data += recieve();
            return data;
        } catch (Exception e) {
            if(session instanceof Switchboard) 
                return null;
             else throw new RuntimeException(e);
        }
    }

}