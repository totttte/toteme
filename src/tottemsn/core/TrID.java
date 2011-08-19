/**
 * �쐬��: 2006/06/15
 */
package tottemsn.core;

/**
 * <p>�g�����U�N�V����ID���Ǘ����܂��B</p>
 * SwitchBoard�T�[�o�����Notification�T�[�o�Ƃ̓����I�Ȃ��Ƃ�ɂ́A�g�����U�N�V����ID���g���܂��B
 * 
 * <p>�g�����U�N�V����ID�́A��ɃN���C�A���g���Ȃ�炩�̃��b�Z�[�W���T�[�o�ɑ��M�������Ƃ��ɁA
 * �ݒ肷����̂ŁA�����l���R�}���h�̂Q�Ԗڂ̃g�[�N���Ɏw�肵�܂��B��{�I�ɁA�l��0����C���N�������g
 * ���Ă����\�t�g�������ł����A�ԍ��̏����ɂ͊֘A�����Ȃ��悤�ł��B
 * �g�����U�N�V�������������b�Z�[�W�ւ̉����́A�����ԍ��Ԃ��Ă��܂��B</p>
 * <p>
 * MSN�v���g�R���̃R�}���h�̒��ɂ́A�ԓ���҂����Ɏ��̃R�}���h�𑗐M����ƃG���[��Ԃ���A
 * �Z�b�V�����������I���������̂�����܂��B���̂��߁A{@link Reciever}����M����܂�
 * {@link SendQueue}�ɂ���R�}���h�𑗂炸�ҋ@���Ȃ���΂����܂���B<br>
 * �@�܂著�M����TrID�̔ԍ�����v����܂őҋ@����΂悢�̂ł��B
 * </p>
 */
class TrID {

    private int trID = 0; // �g�����U�N�V����ID
    private boolean synFlag = false; // �������Ƃꂽ�Ƃ�true
    
    /**
     * ������ID�Ɠ������ǂ������肵�܂��B
     * @param id
     * @return ID�������Ȃ�true
     */
    synchronized boolean isSame(int id) {
        synFlag = (trID == id);
        if(synFlag) notifyAll();
        return synFlag;
    }
    
    synchronized void addTrID() {
        trID++;
    }
    
    synchronized int getID() {
        return trID;
    }
    
    /**
     * TrID�̓���������܂�wait�������܂��B
     */
    synchronized void waitForSame() {
        try {
            while(! synFlag) 
                wait();
            synFlag = false;
        } catch(InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     *	<p>�����񂩂�TrID�𓾂邽�߂̃��\�b�h�ł��B</p>
     *  "command //TrID anything"�̏��ɂȂ��ł���̂ŁA�X�y�[�X��T���A
     * TrID�𓾂܂��B
     * �܂��A���������g�����U�N�V����ID�ł͂Ȃ��A�܂萮���l�ȊO�̏ꍇ�A
     * -1��Ԃ��܂��B
     */
    static int getTrID(String data) {
        int offset = data.indexOf(' ') + 1;
        int end = data.indexOf(' ', offset);
        end = (end == -1) ? data.length() - 2: end;
        int trid;
        try {
            trid =  Integer.parseInt( data.substring(offset, end) );
        }	catch(Exception e) {
            trid = -1;
        }
        return trid;
    }
}