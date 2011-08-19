/**
 * �쐬��: 2006/06/16
 */
package tottemsn.core;

/**
 * <P>���̃N���X�́A�f�[�^���M�p�̑҂��s��̃o�b�t�@��\���܂��B</p>
 * <p>Reciever�⃆�[�U����̓��͂ɂ���āA�҂��s��Ƀo�b�t�@���ǉ�����Ă����A
 *	Sender�͂�������o���āA�T�[�o�ɑ��M���Ă����܂��B</p>
 * <p>�܂��A���̃N���X�͕����̃X���b�h���A�N�Z�X���邽�߁A�����������܂��B</p>
 *
 * <ol>�S�̂Ƃ��Ĉȉ��̂悤�ȓ���̗���ɂȂ�܂��B
 * <li>�o�b�t�@�͋�̏�ԂŐ��������B</li>
 * <li>�o�b�t�@����̏�Ԃ̂Ƃ��ASender�͑ҋ@����B</li>
 * <li>Reciever�₻�̑��N���X�ɂ���ăo�b�t�@�Ƀf�[�^���ǉ������B</li>
 * <li>�f�[�^���ǉ����ꂽ�Ƃ��Asender��notifyAll()���Ăяo����A�����o���B</li>
 * <li>Sender�̓o�b�t�@�ɓ����Ă���f�[�^����O���珇�Ɏ��o���A������T�[�o�ɑ��M����B</li>
 * <li>������o�b�t�@����ɂȂ�܂ő�����B2.�ɍs���J��Ԃ��B</li></ol>
 */
class SendQueue {

    /*	�R���N�V�����t���[�����[�N�ł́AJDK1.5����A�L���[����������Ă��邪�A
     * 	1.5�͂܂��V�����̂ŁA�Ǝ��ɃL���[�����B								*/
    private String[] s; // �L���[�̑ΏۂƂȂ�String
    private int pointer = 0; // ���݂̃L���[�̐擪���w���Ă���
    private int queue = 0; //�@���݂̃L���[�̌���\���Ă���
    
    /**
     * String�̊�҂��s������܂�
     * @param size �҂��s��̃T�C�Y�����肵�܂��B�����Ŏw�肵���T�C�Y�ȏ�̃L���[���������܂܂�
     * ��Ԃ̎��ARuntimeExcetion����������̂ŁA�T�C�Y�͑傫�߂ɂƂ��Ă����K�v������܂��B
     */
    SendQueue(int size) {
        s = new String[size];
    }
    
    /**
     * �o�b�t�@�̍Ō���ɕ������ǉ����܂��B
     * @throws ���Ƀo�b�t�@�����t�̏ꍇ�ARuntimeException�𔭐������܂��B
     * @param o �ǉ�������������
     */
    synchronized void add(String o) {
        int last = (pointer + queue) % s.length;
        if(pointer - last == 1)
            throw new RuntimeException("Buffer over flow.");
        s[last] = o;
        queue++;
        notifyAll(); //�@��ɂȂ��đҋ@���Ă���\��������̂ōĊJ������B
    }
    
    /**
     * �����s�����؂�ɃL���[����ꂽ���Ƃ��͂�����
     */
    void add(String[] o) {
        for(int i = 0; i < o.length; i++) 
            add(o[i]);
    }
    
    /**
     * ���������擾���A�폜���܂��B
     * @return �����񂪂���ΐ擪�������o���A��̏ꍇ�́Anull��Ԃ��܂��B
     */
    synchronized String poll() {
        if(isEmpty()) return null;
        queue--;
        return s[pointer++];
    }
    
    /**		�o�b�t�@���󂩂ǂ���  */
    synchronized boolean isEmpty() {
        return queue == 0;
    }    
}
