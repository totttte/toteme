/**
 * �쐬��: 2006/08/10
 */
package tottemsn.core;

import java.awt.Color;
import java.awt.Font;
import java.util.Date;

/**
 * <p>�P�񕪂̔����̃f�[�^��ێ����܂��B</p>
 * 
 * �����ɂ́A�����҂��N�Ȃ̂��A�F�A�t�H���g�f�[�^�A���b�Z�[�W�A�������ԂȂǂ��i�[����܂��B
 */
public class Chat {

    private Font font;
    private Color color;
    private String name;
    private String text;
    private Date date;
    private int type;
    
    public static final int INCOMING_MESSAGE = 1;
    public static final int OUTGOING_MESSAGE = 2;
    public static final int INFOMATION_MESSAGE = 3;

    /**
     * <p>INCOMING_MESSAGE�`��(���b�Z�[�W��M)�̃R���X�g���N�^�ł��B</p>
     * ���̃R���X�g���N�^���O���p�b�P�[�W���璼�ڐ������邱�Ƃ͂ł��܂���B
     * �O���p�b�P�[�W����Chat�C���X�^���X���Q�Ƃ���ɂ́A{@link MessengerListener}��
     * ������ʂ��čs���܂��B
     * �Ȃ��A���̃C���X�^���X���쐬�����Ƃ����AgetDate()���\�b�h�Ŏ擾���鎞�ԂƂȂ�܂��B
     * @param msg ���b�Z�[�W���w�肵�܂�
     */
    Chat(Messages msg) {
        this.font = msg.getFont();
        this.name = msg.getName();
        this.color = msg.getColor();
        this.text = msg.getPayload();
        this.date = new Date();
        this.type = INCOMING_MESSAGE;
    }
    
    /**
     * <p>���̃R���X�g���N�^�́AOUTGOING_MESSAGE���Ɏg�p���܂��B�܂�A
     * ���������̃��[�U�Ƀ��b�Z�[�W�𑗐M�������Ƃ��ɂ��̃R���X�g���N�^���g���܂��B</p>
     * �Ȃ��A�u�w�w���ސȂ��܂����v�Ȃǂ̏�ԕω��ʒm�p���b�Z�[�W��INFOMATION_MESSAGE���g�p���Ă��������B
     * @param name ���M�҂̖��O
     * @param text ���M�҂̃e�L�X�g
     * @param color ���M�҂̐F
     * @param font ���M�҂̃t�H���g
     */
    public Chat(String name, String text, Color color, Font font) {
        this.name = name;
        this.text = text;
        this.color = color;
        this.font = font;
        this.date = new Date();
        this.type = OUTGOING_MESSAGE;
    }
    
    /**
     * <p>INFOMATION_MESSEAGE�p�̃R���X�g���N�^�ł��B</p>
     * �Ȃ��A�F�͍��Aname�͋󕶎���A�t�H���g�̓T�C�Y11��Default,PLAINTEXT�ł��B
     * @param message ������
     */
    public Chat(String message) {
        this.text = message;
        this.name = "";
        this.color = Color.BLACK;
        this.date = new Date();
        this.font = new Font(null,Font.PLAIN,11);
        this.type = INFOMATION_MESSAGE;
    }
    
    /**
     * <p>�����������Ԃł��B</p>
     * �f�[�^���󂯎�����܂��͑��M�������Ԃ���ɂȂ�܂��B
     */
    public Date getData() {
        return date;
    }

    /**	���b�Z�[�W�̃t�H���g	 */
    public Font getFont() {
        return this.font;
    }
    
    /**	���b�Z�[�W�̐F	 */
    public Color getColor() {
        return this.color;
    }
    
    /**
     * �����҂̖��O��Ԃ��܂�
     * @return �����҂̖��O
     */
    public String getName() {
        return this.name;
    }
    
    /**	���b�Z�[�W�̃e�L�X�g	     */
    public String getText() {
        return this.text;
    }
    
    /**
     * ���̃`���b�g�̃^�C�v�ł��B�^�C�v�ɂ��Ă̓t�B�[���h�Q�ƁB
     */
    public int getType() {
        return this.type;
    }
}
