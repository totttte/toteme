/**
 * �쐬��: 2006/08/07
 */
package tottemsn.core;

/**
 * <p>���b�Z���W���[����݂̗�O�́A���̗�O�𔭐������܂��B</p>
 * 
 * ��ɁA�T�[�o�Ƃ̂��Ƃ�ŁA�Ȃ�炩�̃R�}���h���M�~�X���������ꍇ��A
 * �F�؂Ɏ��s�����ꍇ�Ȃǂ��������܂��B
 */
public class MessengerException extends RuntimeException {
    
    private int errorCode = 0;
    private Board board; // SwitchBoard�T�[�o�ł������G���[�̏ꍇ�̂݁B
    
    public MessengerException(Throwable e) {
        super(e);
    }
    
    public MessengerException(String s) {
        super(s);
    }
    
    public MessengerException(int errorCode) {
        super(Integer.toString(errorCode));
        this.errorCode = errorCode;
    }
    
    public MessengerException(Board board, Exception e) {
        super(e);
        this.board = board;
    }
    
    /**
     * ���b�Z���W���[�T�[�o���瑗���Ă����G���[�R�[�h�𓾂܂��B
     * @return �R�[�h�ԍ�(9xx)
     */
    public int getErrorCode() {
        return errorCode;
    }
    
    public Board getBoard() {
        return this.board;
    }
    
    public String getMessage() {
        String message = super.getMessage();
        if((errorCode >= 600 && errorCode < 700) || errorCode == 910) {
            message = "���b�Z���W���[�̃T�[�o���_�E�����Ă��邩�d���Ȃ��Ă��܂��B\n" +
            		"�G���[�ԍ�:\t" + errorCode;
        }
        return message;
    }
}
