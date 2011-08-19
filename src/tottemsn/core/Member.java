/**
 * �쐬��: 2006/08/07
 */
package tottemsn.core;


/**
 * <p>�����o�̏�Ԃ�֎~�E�폜�̗L���������N���X�ł��B</p>
 */
public class Member implements Cloneable {
    private UserStatus status;
    private String address; // N
    private String name;	// F
    /* �O���[�vID�ł��BID���w�肵�܂��B
     * �����O���[�v���Ȃ��A�������͂ǂ��̃O���[�v�ɂ��������Ă��Ȃ��ꍇ�A
     * "0"���w�肳��܂��B
     * �������̃����o�����d�̃O���[�v�ɏ������Ă����ꍇ�A
     * �O���[�v�L�[�͈ȉ��̂悤�ɂȂ�܂��B
     * 275f467b-309a-41c0-b442-5012fb018ae8,983b331b-c5db-4b9c-84e7-a8c9395c7467
     * �܂�A[,]�J���}�ŋ�؂��Ĉ�̃g�[�N���ɂ܂Ƃ߂đ����Ă���̂ł��B
     */
    String groupeKey = "0"; 
    private boolean isBlock; // �֎~�̗L��
    private boolean isForward; // �폜�̗L��
    String psm = ""; // �p�[�\�i�����b�Z�[�W�ł��B(MSNP11�ȏ�)
    
    Member(String address, String name, int bitwise) {
        status = new UserStatus("FLN");
        this.address = address;
        this.name = name;
        isBlock = (bitwise & 0x0004) == 0x0004;
        isForward = (bitwise & 0x0001) == 0x0001;
    }
    
    /** ���̃����o�[�̃A�h���X�𓾂܂� */
    public String getAddress(){
        return address;
    }
    
    /** ���̃����o�[�̖��O��ύX���܂��B */
    void rename(String name) {
        this.name = name;
    }
    
    /**
     * ���̃����o�̌��݂̖��O���擾���܂�
     * �����A���O���Ȃ������ꍇ�A�A�h���X��Ԃ��܂�
     * @return ���̃����o�̖��O
     */
    public String getName() {
        if(name.equals(""))
            return this.address;
        return this.name;
    }
    /**
     * ����̃����o�[���ǂ������肵�܂��B
     * equals���\�b�h�̃I�[�o�[���C�h�Ŕ�����@�́A�A�h���X�̈�v���ǂ����ł��B
     * @return ����̃����o�Ȃ� true ��Ԃ��B
     */
    public boolean equals(Object obj) {
        if(obj instanceof Member) 
            return address.equals( ((Member)obj).address );
        else 
            return super.equals(obj);
    }
    
    public int hashCode() {
        return address.hashCode();
    }
    
    /**
     * �����o�[�̏�Ԃ��擾���܂��B
     */
    public UserStatus getStatus() {
        return status;
    }
    
    /**
     * String���\�b�h�̃I�[�o�[���C�h
     */
    public String toString() {
       return "[status=" + status + ",address=" + address
       			+ ",name=" + name  + ",psm=" + psm +  ",groupeKey=" + groupeKey
       			+ ",block=" + isBlock + ",Forward=" + isForward + "]";
    }
    
    /**
     * ���̃I�u�W�F�N�g�̃R�s�[�𐶐����܂��B
     */
    public Object clone() {
        try {
            Member mem = (Member)super.clone();
            mem.address = address;
            mem.name = name;
            mem.isBlock = isBlock;
            mem.isForward = isForward;
            mem.groupeKey = groupeKey;
            mem.psm = psm;
            mem.status = new UserStatus(status.getStatusCommand());
            return mem;
        } catch(CloneNotSupportedException e) {
            return null;
        }
    }
    
    /**
     * ���̃����o�̃p�[�\�i�����b�Z�[�W��Ԃ��܂��B
     * @return �p�[�\�i�����b�Z�[�W���Ȃ��ꍇ�󕶎�
     */
    public String getPersonalMessage() {
        return psm;
    }
    
    /**
     * <p>�����Ŏw�肳�ꂽ�����o�ł��̃C���X�^���X���㏑�����܂��B</p>
     * �����o�̑S�Ă̓��e�����t���b�V���������Ƃ��Ɏg���܂��B
     * @param mem �ύX�����������o
     */
    void setMember(Member mem) {
        this.address = mem.address;
        this.groupeKey = mem.groupeKey;
        this.isBlock = mem.isBlock;
        this.isForward = mem.isForward;
        this.psm = mem.psm;
        this.name = mem.name;
        this.status = mem.status;
    }
    
    /**
     * ���̃����o���֎~���Ă��邩�ǂ���
     * @return ���̃����o���֎~���Ă�����true
     */
    public boolean isBlock() {
        return isBlock;
    }
    
    /**
     * ���̃����o���폜���ꂽ�����o���ǂ���
     * @return ���̃����o����ԕω���ʒm�����ׂ������o�Ȃ�true
     */
    public boolean isForward() {
       return isForward; 
    }
}