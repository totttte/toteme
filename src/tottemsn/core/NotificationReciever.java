package tottemsn.core;

import java.net.Socket;
import java.util.Date;

import tottemsn.credits.Msnp11chl;
/**
 * <p>Notification�p�̃f�[�^��M�S���N���X�ł��B</p>
 * �f�[�^����M���A���܂��܂ȃR�}���h�����߂��A���X�i�[�֒ʒm��A���̃R�}���h�ɑ΂��Ă̕ԓ���
 * ��������������ʂ����܂��B
 */
class NotificationReciever extends Reciever {

    NotificationReciever(Messenger messe, TrID trID, Session session,Sender sender) {
        super(messe, trID, session);
        this.sender = sender;
    }
    
    /**
     * <p>run()���\�b�h�̃I�[�o���C�h�ł��B</p>
     * �X�[�p�[�N���X�ł́A�Z�b�V�����������Ă�����薳�����[�v�����Ă���̂ŁA
     * ���̃��\�b�h�𔲂��o���Ƃ��́A�Z�b�V�������I�����Ă��܂������A�Ȃ�炩�̗�O�����������Ƃ���
     * ���Ƃł��B
     */
    public void run() {
        try {
        super.run();
        sender.que.add("OUT");
        } catch (Exception e) {
            messe.errorMSG(new MessengerException(e));
        }
        messe.errorMSG(new MessengerException("�T�[�o����ؒf����܂����B"));
        sender.interrupt();
    }
    
    /**
     * <p>�f�[�^��͗p�p�[�T�ł��B</p>
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
                if(Character.isDigit( datas[i].charAt(0) )) 
                     doErrorCode(datas[i]);
            } catch (Exception e) {
                throw (RuntimeException) new RuntimeException().initCause(e);
            }
        }
        // Sender�Ƃ̓�������
        int id = TrID.getTrID(datas[datas.length - 1]);
        // ���炩��trID����Ȃ��������΂�
        if(id!=-1 && id < trID.getID() + 5 && id > trID.getID() - 5)
            trID.isSame(id+1);
    }
    
    /**
     * �G���[�R�[�h��f���o�����Ƃ��A������O�Ƃ��ē����܂��B
     */
    private void doErrorCode(String data) {
        int errorcode = 0;
        try {
            errorcode = Integer.parseInt( data.substring(0,3) );
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
        messe.errorMSG( new MessengerException(errorcode) );
    }

    /**
     * <p>RNG�R�}���h�̏���</p>
     * SwitchBoard�Z�b�V�����ɏ��҂��ꂽ�Ƃ��ɁA���̃R�}���h���g���܂��B
     */
    private void doRNG(String com) {
        int key = Integer.parseInt(StringUtil.messeToken(com,1));
        String ip = StringUtil.messeToken(com,2);
        int port = Integer.parseInt( ip.substring(ip.indexOf(':') + 1) );
        String key2 = StringUtil.messeToken(com,4);
        String mail = StringUtil.messeToken(com,5);
        ((Notification)session).addSession(key,key2,ip.substring(0,ip.indexOf(':')),port);
    }
    
    /**
     *	<p>XFR�R�}���h�̏���</p>
     *	XFR�R�}���h�ɂ́A�Q�ʂ肪����܂��B��́ANotification�T�[�o�̈ړ��̂��߁B<BR>
     *	������́ASwitchBoard�T�[�o��V���Ɋm�������ꍇ�ł��B<BR>
     *	XFR�R�}���h�̊T�v
     *	<pre>
     *	XFR //TrID NS �VIP�A�h���X:PORT 0 ��IP�A�h���X:PORT
     *	</pre>
     *	�Ȃ��ASwitchBoard�T�[�o�m���̏ꍇ�́A{@link #doXFRSB(String, String, int)}
     *	���������܂��B
     */
    private void doXFR(String data) {
        String ip = StringUtil.messeToken(data,3);
        // �T�[�o�ړ�
        int port = Integer.parseInt( ip.substring(ip.indexOf(':') + 1) );
        ip = ip.substring(0,ip.indexOf(':'));
        if(StringUtil.messeToken(data,2).equals("SB")) {
            doXFRSB(data,ip ,port);
            return;
        }
        try {
            session.socket = new Socket(ip, port);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
        StringUtil.println("[INFO] Move Server>" + socket.getInetAddress().toString());
        moveServer();
        sender.moveServer();
    }
    
    /**
     * <p>SwitchBoard�����܂��B</p>
     * ���ꂼ��AID�Ȃǂ̃g�[�N�����擾���A���ۂɃZ�b�V�����쐬����������̂́A
     * {@link Notification#addSession(int, String, String, int)}�ł��܂��B
     * @param data XFR�R�}���h
     * @parama ip IP�A�h���X
     * @parama port �|�[�g
     */
    private void doXFRSB(String data,String ip, int port) {
        String key2 = StringUtil.messeToken(data,5);
        int key = 0;
        String mail = messe.user.getAccount();
        ((Notification)session).addSession(key,key2,ip,port);
    }

    /**
     * <p>USR�R�}���h�Ɋւ��鏈��</p>
     * USR�R�}���h�́A���[�U�F�؂����ꂩ��s���ꍇ�ƁA
     * �F�؂��s������̊m�F�̃R�}���h�̂Q��ނ�����܂��B<br>
     * USR�R�}���h�̊T�v
     * <pre>
     * �F�ؑO	USR //TrID TWN S �`�������W�L�[
     * �F�،�	USR //TrID OK �A�J�E���g
     * </pre>
     * �܂��A���[�U�F�؂ɐ��������ꍇ��{@link Messenger#finshSignIn(boolean, Exception)}
     * �ɒʒm���܂��B
     */
    private void doUSR(String data) {
        String tok2 = StringUtil.messeToken(data,2);
        if(tok2.equals("TWN")) {         // TWN�̏ꍇ�@�`�������W�L�[�̐ݒ�
            messe.user.challengeKey = StringUtil.messeToken(data,4);
        } else if(tok2.equals("OK")) { // OK �̏ꍇ�A�ڑ������t���O�̐ݒ�
            messe.finshSignIn(true,null);
            synchronized(this){
                notifyAll();
            }
        }
    }
    
    /**
     * UUX�R�}���h���󂯎��܂��B
     * ����́A�p�[�\�i�����b�Z�[�W�ύX�������������Ƃ������Ă��܂��B
     */
    private void doUUX(String data) {
        messe.update(MessengerListener.PSM_CHANGED);
    }
    
    /**
     * <p>CHL�R�}���h�Ƃ́A�T�[�o���瑗���Ă���F�؂����˂�ping�ŁA
     * �����60�b�ȓ��ɕԐM���Ȃ��Ƌ����I�Ƀ��O�I�t����܂��B</p>
     * 
     * �܂��ACHL�R�}���h����M���Ă���QRY�R�}���h����M����܂ł̊ԂɁA
     * ���̃R�}���h�𑗐M����ƁA�G���[�ɂȂ�܂��B
     * <I>MSNP11�ȏォ��d�l���ς��܂����B</I>�ڍׂ͈ȉ���URL�B
     * {@link http://msnpiki.msnfanatic.com/index.php/MSNP11:Challenges}
     */
    private void doCHL(String data) throws Exception {
        final String Product_ID = "PROD0090YUAUV{2B";
        final String Product_Key = "YMM8C_H7KCQ2S_KL";
        String key = Msnp11chl.createQRY( StringUtil.messeToken(data,2) );
        StringUtil.println("[info:TimeStamp]" + new Date().toString());
        sender.que.add("QRY //TrID "+ Product_ID + " 32\r\n" + key);
        sender.que.add("//Time");
        sender.que.add("//Lock");
    }
    
    /**
     * QRY�R�}���h��Challenges�̉����Ƃ��āA�����Ă��܂��B
     * SendQueue�̃��b�N���������܂��B
     */
    private void doQRY(String data) {
        sender.unlock();
    }
    
    /**
     * <p>LST����ǂݍ��݂܂��B</p>
     * �����o�̃��X�g���i�[����Ă��܂��B�R�}���h�̏ڍׂ́A{@link MemberList#addMember(String)}
     * ���Q�Ƃ��Ă��������B<br>
     * �܂��A�S�Ẵ����o��ǂݏI���ƃ��X�g�̍X�V��MessangerListener���������Ă���N���X�ɒʒm���܂��B
     */
    private void doLST(String data) {
        MemberList mem = messe.memberlist;
        mem.addMember(data);
        if(mem.fullList()) 
            messe.getList(mem);    
    }
    
    /**
     * <p>LSG����ǂݍ��݂܂��B</p>
     * �O���[�v���X�g���i�[����Ă��܂��B�R�}���h�̏ڍׂ́A{@link MemberList#addGroupe(String)}
     * ���Q�Ƃ��Ă��������B<br>
     * �܂��A�S�ẴO���[�v��ǂݏI���ƃO���[�v���X�g�̍X�V��MessangerListener���������Ă���N���X�ɒʒm���܂��B
     */
    private void doLSG(String data) {
        MemberList mem = messe.memberlist;
        mem.addGroupe(data);
        if(mem.fullGroupe())
            messe.getGroupe(mem.getGroupes());
    }
    
    /**
     * <p>SYN����ǂݍ��݂܂��B</p>
     * SYN�R�}���h�́A�T�[�o�Ƃ̎����̓�����A�����o�̐��A�O���[�v�̐������ꂼ��i�[����Ă��܂��B<br>
     * SYN�R�}���h�̊T�v
     * <pre>
     * SYN //TrID ���݂̎���(���E�W������) �����Q �����o�̐� �O���[�v�̐�
     * </pre>
     */
    private void doSYN(String data) {
        int member = Integer.parseInt( StringUtil.messeToken(data,4) );
        int groupes = Integer.parseInt( StringUtil.messeToken(data,5) );
        sender.que.add("CHG //TrID " + messe.user.getUserStatus().getStatusCommand());
        if(messe.memberlist == null)
            messe.memberlist = new MemberList(member,groupes,messe);
    }
    
    /**
     * <p>NLN���AINL�������FLN����ǂݍ��݂܂��B</p>
     * ���ꂼ��̃R�}���h�̏ڍׂ́A{@link MemberList#changeStatus(String)}�B
     * �܂��A��Ԃ̍X�V��MessangerListener���������Ă���N���X�ɒʒm���܂��B
     */
    private void doNLN(String data) {
        messe.changedStatus( messe.memberlist.changeStatus(data) );
    }
    
    private void doILN(String data) {
        doNLN(data);
    }
    
    private void doFLN(String data) {
        doNLN(data);
    }

    /**
     * <p>PRP�R�}���h�̉���</p>
     * �T�C���C�������Ƃ��̃f�t�H���g�̖��O�̏ꍇ) PRP MFN ���O
     * ���g�̖��O�ύX�̏ꍇ)PRP //TrID MFN ���O
     */
    private void doPRP(String data) {
        // ����ł́A[PRP MFN]���̂�
        if(StringUtil.messeToken(data,1).equals("MFN"))
            messe.user.reName( StringUtil.messeToken(data,2) );
        else if(StringUtil.messeToken(data,2).equals("MFN")) {
            messe.user.reName(StringUtil.messeToken(data,3));
            messe.update(MessengerListener.NAME_CHANGED);
        }
    }
    
    /**
     * ���g�̏�ԕω�����
     */
    private void doCHG(String data) {
        messe.user.getUserStatus().setStatus( StringUtil.messeToken(data,2) );
        messe.update(MessengerListener.STATUS_CHANGED);
    }
    
    /**
     *�@<p>�Z�b�V�����I���ʒm</p>
     *	"OUT OTH"�̏ꍇ�A���̏ꏊ�ŃT�C���C���������Ƃ�\���B
     *	���̑��ɂ��A����ނ��R�}���h������悤���B
     */
    private void doOUT(String data) {
        if(StringUtil.messeToken(data,1).equals("OTH")) 
            messe.errorMSG( new MessengerException("���̏ꏊ�ŃT�C���C�����܂����B") );
        else 
            messe.errorMSG( new MessengerException("���b�Z���W���[����ؒf����܂����B") );
    }
    
    /**
     * UBX�R�}���h�ɂ��Ă̏ڍׂ́A{@link XMLPayload}�ł��B
     */
    private void doUBX(String data) {
        XMLPayload payload = new XMLPayload(data);
        Member mem = messe.memberlist.equalsMember(payload.getAddress());
        mem.psm = payload.getPersonalMessage();
        messe.changedStatus(mem);
    }
    
    /**
     * MSG�R�}���h��ǂݍ��݂܂��B�ڍׂ́A{@link Messages}�ɂ���܂��B
     * �Ȃ��A���i�K�ł́A�z�b�g���[�������ʂ��邩��ǂݍ��ނ����ł��B
     */
    private void doMSG(String data) {
        Messages msg = new Messages(data);
        if(msg.isMailData()) {
            messe.hotMail = new HotMail(msg.getPayload());
            messe.update(MessengerListener.GET_HOTMAIL);
        }
    }
    
    private void do911(String data) {
         messe.finshSignIn(false,
                 new MessengerException("�F�؃G���[:�A�J�E���g���ƃp�X���[�h���m�F���Ă��������B") );
         this.interrupt();
         sender.interrupt();
    }
}
