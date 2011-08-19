/**
 * �쐬��: 2006/06/25
 */
package tottemsn.core;

import java.util.EventListener;

/**
 * <p>���b�Z���W���[�̃C�x���g�����������Ƃ��ɁA������󂯎�郊�X�i�[�ł��B</p>
 * �O���p�b�P�[�W���烁�b�Z���W���[�Ɋւ���C�x���g�̒ʒm�ɂ͂��̃N���X���p������K�v������܂��B
 */
public interface MessengerListener extends EventListener {

    /**
     * �����̏�ԕύX�������������Ƃ�\���B
     */
    public static final int STATUS_CHANGED = 1;
    /**
     * �����̖��O�ύX�������������Ƃ�\���B
     */
    public static final int NAME_CHANGED = 2;
    /**
     * �����̃p�[�\�i�����b�Z�[�W�ύX�������������Ƃ�\���B
     */
    public static final int PSM_CHANGED = 3;
    /**
     * �z�b�g���[������M�������Ƃ������܂�
     */
    public static final int GET_HOTMAIL = 4;
    
    /**
     * �����o���X�g�擾�̒ʒm
     */
    public void getList(MemberList list);
    
    /**
     * �����o���X�g�̒N���̃X�e�[�^�X�ύX�̒ʒm
     */
    public void changedStatus(Member member);
   
    /**
     * �N���̖��O�ύX���������Ƃ�
     *
     */
    public void changedName(Member member);
    
    /**
     * �O���[�v���X�g�擾�̒ʒm
     * @param table�@�O���[�v���X�g
     */
    public void getGroupe(String s[]);    
    
    /**
     *	�T�C���C�����I������Ƃ��B
     *  �܂��A�T�C���C��������������O�����������ꍇ�́A
     *  ���̗�O��e���瓾�܂��B��O���������Ă��Ȃ��ꍇ�Ae=null�ł��B
     */
    public void finshSignIn(boolean isSuccess, Exception e);

    /**
     * �`���b�g�E�B���h�E�ɒN�����Q��������
     * (�����ł�board�C���X�^���X�ɂ́A�܂����̃����o�͒ǉ�����Ă��܂���B)
     */
    public void addParticipant(Board board,Member member);
    
    /**
     * �`���b�g�E�B���h�E�ŒN�����ސȂ�����
     */
    public void removeParticipant(Board board,Member member);
    
    /**
     *	�Ȃ�炩�̉�b����������B
     */
    public void getChat(Chat chat, Board board);
    
    /**
     * �����������b�Z�[�W���͂����ǂ���
     */
    public void finishSendMsg(Board board, boolean isSuccess);
    
    /**
     * ���̑��A���炩�̏�ԕω��̒ʒm�B
     * �Ⴆ�Ύ��g�̏�ԕύX�����������Ƃ��B
     * @param type �ʒm�̓��e���t�B�[���h�萔�ŕ\���܂��B
     */
    public void update(int type);
    
    /**
     * �T�C���A�E�g������ꂽ��A�Ȃ�炩�̗�O���������A
     * ���̃Z�b�V�����Ń��b�Z���W���[�𑱂��邱�Ƃ��ł��Ȃ��Ȃ����ꍇ�B
     */
    public void errorMSG(MessengerException e);
}