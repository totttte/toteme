/**
 * �쐬��: 2006/08/09
 */
package tottemsn.core;

/**
 * Switchboard�̎�M�S���N���X�ł��B
 */
class SwitchboardReciever extends Reciever {

    private Board board;

    SwitchboardReciever(Messenger messe, TrID trID, Session session, Sender sender) {
        super(messe, trID, session);
        this.sender = sender;
    }
    
    void setBoard(Board board) {
        this.board = board;
    }
    
    public void run() {
        super.run();
    }
    
    /**
     * <p>�f�[�^��͗p�p�[�T�ł��B</p>
     * <I>���݂ł́ANotificationReciever�Ɠ��e������ł��B���ナ�t�@�N�^�����O�̉\������B</I>
     *	//TODO ���t�@�N�^�����O�Ώ� 
     * ���t���N�V�������g���āA�Y�����郁�\�b�h�����s���܂��B
     * @param message �p�P�b�g�Ŏ�M�������b�Z�[�W
     */
    protected void parser(String message) {
        // �����̃R�}���h�������ɑ����Ă����Ƃ��ɕ���
        String datas[] = StringUtil.spiltCommand(message);
        for(int i=0; i < datas.length; i++) {
            if(datas[i].length() < 3) continue;
            String s = "do" + datas[i].substring(0,3);
            Object[] o = new Object[1];
            o[0] = datas[i];
            try {
                Class[] cls = new Class[1];
                cls[0] = String.class;
                getClass().getDeclaredMethod(s,cls).invoke(this,o);
            } catch (NoSuchMethodException e) {
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        // Sender�Ƃ̓�������
        int id = TrID.getTrID(datas[datas.length - 1]);
        // ���炩��trID����Ȃ��������΂�
        if(id!=-1 && id < trID.getID() + 5 && id > trID.getID() - 5)
            trID.isSame(id+1);
    }

    /**
     * MSG�R�}���h�̏�����S�����郁�\�b�h�ł��B
     * �Ȃ��AMSG�R�}���h�̌�ɕʂ̃R�}���h�������Ă��邱�Ƃ��\���l������̂ŁA
     * MSG�R�}���h�ȊO�̕�����߂�l�Ƃ��ĕԂ��A�c��͈������� #parser(String) �ŏ������܂��B
     */
    private String doMSG(String message) {
        Messages msg = new Messages(StringUtil.getMSG(message,3));
        if(msg.isTextMessage()) {
            messe.getChat( board.addLog(msg) , board);
        }
        String others = Messages.getOthers(message,3);
        if(others!=null && others.startsWith("MSG "))
            others = doMSG(others);
        return others;
    }
    
    /**
     * IRO�R�}���h�́A���̃Z�b�V�����ɎQ�����Ă���l���X�g�ł��B
     */
    private void doIRO(String data) {
        observeParticipant(StringUtil.messeToken(data,4));
    }
    
    /**
     * �N�����ސȂ����Ƃ��̃R�}���h�ł��B
     * �T�v) BYE �ސȂ����l�̃A�J�E���g
     */
    private void doBYE(String data) {
        Member oldMember = board.removeParticipant(StringUtil.messeToken(data,1));
        if(board.getNumber() == 0)
            doOUT(data);
        else 
            messe.removeParticipant(board,  oldMember);
    }
    
    /**
     * �r���ŒN�����Q������Ƃ��̃R�}���h�ł��B
     * [C <- SB]JOI address name
     */
    private void doJOI(String data) {
        sender.unlock();
        sender.que.add("//Cut");
        observeParticipant(StringUtil.messeToken(data,1));
    }
    
    /**
     * <p>���̃Z�b�V�������I�������Ƃ��AOUT�R�}���h���Ăяo����܂��B</p>
     * SwitchBoard�Ƃ̃Z�b�V�����͕p�ɂɐڑ��A�ؒf���s����̂ŁA
     * SwitchBoard�Ɋւ��Thread�Ɋ��荞�݂������A�X���b�h���I�������܂��B
     */
    private void doOUT(String data) {
        ((Switchboard)session).noti.removeSession((Switchboard)session);
        // ���������s���Ă���Q�̃X���b�h�Ɋ��荞�݂������܂��B
        sender.interrupt();
        this.interrupt();
    }
    
    private void doACK(String data) {
        messe.finishSendMsg(board,true);
    }
    
    private void doNAK(String data) {
        messe.finishSendMsg(board,false);
    }
    
    private void do215(String data) {
        messe.errorMSG(new MessengerException(this.board,new MessengerException(215)));
    }
    
    /*
     * �V���ȃ����o���������Ƃ��ɁA�ʒm���邽�߂̃��\�b�h�ł��B
     * �������A���ɓ��������o�������ꍇ�͒ʒm���܂���B
     */
    private void observeParticipant(String address) {
        Member member = messe.memberlist.equalsMember(address);
        if(member == null) { // �o�^���Ă��Ȃ������o�������ꍇ�A���̃����o
            member = new Member(address,"",4);
        }
        messe.addParticipant(board,  member);
        Member newMember = board.addParticipant(member);
    }
}