/**
 * �쐬��: 2006/08/09
 */
package tottemsn.core;

import java.net.Socket;

/**		�Z�b�V�����N���X�BSwitchBoard�����Notification�Ƃ̃\�P�b�g�ʐM�̊��ł��B */
class Session {
    Socket socket;
    protected Messenger messe;
    protected TrID trID;
}