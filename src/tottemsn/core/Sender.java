/**
 * �쐬��: 2006/08/09
 */
package tottemsn.core;
import java.io.DataOutputStream;
import java.io.IOException;
/**
 * <p>�f�[�^���M��S������N���X�ł��B</p>
 * ��M��S������Reciever�N���X�Ƃ͕ʃX���b�h�œ����܂��B
 * <p>
 * ���̃N���X�͏��{@link tottemsn.core.SendQueue}���Ď����A�L���[����ɂȂ�܂Ńf�[�^�𑗐M�������A
 * �L���[����ɂȂ����ꍇ�́A�V���ȃL���[������܂őҋ@���܂��B
 * �ǂ̂悤�ȕ��@�ŁASendQueue�Ɏc���Ă���f�[�^�𑗂邩�́Aparser���\�b�h���p�����Č��߂܂��B
 * </p>
 * @see tottemsn.core.NotificationReciever
 * @see tottemsn.core.SwitchboardReciever
 */
abstract class Sender extends Client {

    private DataOutputStream out;
    SendQueue que;
    
    private boolean lock = false;

    Sender(Messenger messe, TrID trID,Session session) {
        super(messe, trID, session);
        que = new SendQueue(64);
    }

    /**
     * �T�[�o���ړ������Ƃ��A�܂��͐V���Ƀ\�P�b�g��ڑ�����Ƃ��ɂ�����Ăяo���܂��B
     */
    void moveServer() {
        try {
            socket = session.socket;
            out = new DataOutputStream(socket.getOutputStream());
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * <p>�L���[����ɂȂ�܂ŁA�\�P�b�g�ʐM��ɃL���[�̒��g�𑗐M���A��ɂȂ�����ҋ@���܂��B</p>
     * Thread#run() �̃I�[�o�[���C�h�ł��B
     * �܂��A���̃��\�b�h�Ɋ��荞�݂�������ꂽ�ꍇ�A���C�����[�v�𔲂��o�����\�b�h���o�܂��B
     * @see SwitchboardReciever#doOut(String)
     */
    public void run() {
        try {
            while(true) {
                while(que.isEmpty() || lock) 
                    synchronized(que){
                        que.wait();
                    }
                String data = parser(que.poll());
                if(data == null) continue;
                else sendCommand(data);
            }
        }catch(InterruptedException e) {
        }
    }
    
    /**
     * ���ݐڑ����Ă���\�P�b�g��Ƀf�[�^�𑗂�܂��B
     * �Ȃ��A���b�Z���W���[�ł́AQRY�R�}���h�ȊO�Ō�ɉ��s�R�[�h�����邱�ƂɂȂ��Ă���̂ŁA
     * ������������܂��B
     * @param data ���M�������f�[�^
     */
    private void sendCommand(String data) {
        // �I�[�ɉ��s�R�[�h(CRLR)���Ȃ��ꍇ�́A�}������B
        if( !data.endsWith("\r\n") && !data.startsWith("QRY") && !data.startsWith("MSG")
                && !data.startsWith("UUX")) {
            data += "\r\n";
        }
        if(! socket.isConnected())
            throw new MessengerException("�T�[�o����ؒf����܂����B");
        // UTF-8�̃o�C�g�z��ɕς��A�\�P�b�g��ɑ��M���܂��B
        try {               
            out.write( data.getBytes("UTF-8") );
            out.flush(); // ���Ȃ炸flush����B
        } catch(IOException e) {
            if(session instanceof Switchboard) {
                this.interrupt();
                ((Switchboard)session).reciever.interrupt();
            } else throw new RuntimeException(e);
        }
        if(session instanceof Switchboard)
            StringUtil.println("[C -> SB]" + data);
        else 
            StringUtil.println("[C -> NC]" + data);
        trID.addTrID();
    }
    
    /**
     * ���̃��\�b�h�́A���̃N���X���p������NotificationSender��SwitchboardSender�ŁA
     * ��������܂��B
     * send2.txt�p�Ǝ��p�[�T�ł��B
     * //�Ŏn�܂�R�}���h�ނ��A�ϊ����܂��B
     * �܂��A//Wait�ȂǁAString�������o�͂��Ȃ�������̏ꍇ�́Anull��Ԃ��܂��B
     * @param data �ϊ�������������
     * @return �ϊ���̕�����
     */
    protected abstract String parser(String data);
    
    /**
     *	SendQueue�̎Q�Ƃ𓾂܂�
     */
    SendQueue getSendQueue() {
        return que;
    }
    
    /**
     *�@���̃��\�b�h���Ăяo���ƁA���C�����[�v��wait���\�b�h�𔲂��o�����Ƃ�
     *	�����while���ɂ���āA�ӂ�����wait���܂��B
     *	���̃��\�b�h���Ăяo������́A�K��unlock()�����Ȃ���΂Ȃ�܂���B
     */
    void lock() {
        lock = true;
    }
    
    /**
     * ���b�N���������܂��B
     * @see #lock()
     */
    void unlock() {
        lock = false;
    }
}
