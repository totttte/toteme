/**
 * �쐬��: 2006/09/06
 */
package tottemsn.simplegui;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.JTextComponent;

/**
 * </p>�u�R���X�g���N�^�̈����Ŏw�肵��JTextComponent���ɂ���URL���N���b�N����ƁA�u���E�U���J���v
 * �Ƃ������@�\���������܂�</p>
 * �R���|�[�l���g�ɕ�����URL������Ƃ��̓}�E�X�J�[�\���̈ʒu�ɂ����URL��I�����܂��B
 * 
 * <pre>
 * -------- SampleCode ----------
 * JTextPane pane = new JTextPane();
 * BrowserTextPane browser = new BrowserTextPane(pane);
 * pane.add("testhoge http://google.com/ aaa");
 * pane.add("abcde http://yahoo.co.jp/ aabbcc");
 * </pre>
 */
public class BrowserTextPane implements MouseListener , CaretListener {
    
    //TODO �ȉ����ꂩ�����
    
    private JTextComponent src;
    // ���̃E�B���h�E�Ɋi�[����Ă���URL(Object��cast���Ă�̂́ALink�N���X)
    private Vector urls;
    
    class Link {
        Rectangle bounds; // ���̃����N�����삷��͈�
        String urls;
    }

    public BrowserTextPane(JTextComponent src) {
        this.src = src;
        src.addMouseListener(this);
        src.addCaretListener(this);
    }
    
    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void caretUpdate(CaretEvent e) {
    }
    
}