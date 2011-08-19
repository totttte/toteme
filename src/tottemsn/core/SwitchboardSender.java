/**
 * �쐬��: 2006/08/09
 */
package tottemsn.core;
/**
 * Switchboard�̑��M�Ɋւ�镔����S�����܂��B
 */
class SwitchboardSender extends Sender {

    SwitchboardSender(Messenger messe, TrID trID, Session sb) {
        super(messe, trID, sb);
    }
    
    /**
     * Sender#run() �̃I�[�o�[���C�h
     */
    public void run() {
        try {
            if(((Switchboard)session).key == 0)
                que.add("USR //TrID //MailAddress "+((Switchboard)session).key2);
            else
                que.add("ANS //TrID //MailAddress "+
                    ((Switchboard)session).key2+" "+((Switchboard)session).key);
            super.run();
        } catch(Exception e) {
            messe.errorMSG(new MessengerException(((Switchboard)session).getBoard(),e));
        }
    }

    /**
     * ���M���O�Ɍ��܂���̕ϊ��@�ł��B
     * �Ȃ��A�`���b�g���� // �Ƒł����ꍇ�́@/-�@�ƈ��ϊ����Ă���@//�@�ɒ����܂��B
     */
    protected String parser(String data) {
        data = StringUtil.replaceAll(data, "//MailAddress",  messe.user.getAccount());
        data = StringUtil.replaceAll(data,"//TrID", Integer.toString( trID.getID() ));
        if(data.startsWith("//Lock")) { // SendQueue�̃��b�N���܂�
            super.lock();
            return null;
        }
        if(data.startsWith("//Unlock")) { // ���b�N���������܂�
            super.unlock();
            return null;
        }
        if(data.equals("OUT"))
            return data;
        if(data.startsWith("USR ")) { // �R�}���h���M���� (USR �� CAL����邽��)
            ((Switchboard)session).setFinished();
        }
        if(data.startsWith("//Closed")) { 
            ((Switchboard)session).noti.removeSession((Switchboard)session);
            // ���������s���Ă���Q�̃X���b�h�Ɋ��荞�݂������܂��B
            interrupt();
            ((Switchboard)session).reciever.interrupt();
            return null;
        }
        if(data.startsWith("//Cut")) // �Ƃ肠�����A�ǂݔ�΂�
            return null;
        data = StringUtil.replaceAll(data,"/-","//");
        return data;
    }    
}