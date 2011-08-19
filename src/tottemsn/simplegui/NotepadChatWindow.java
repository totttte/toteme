/**
 * �쐬��: 2006/09/04
 */
package tottemsn.simplegui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * �`���b�g�E�B���h�E�̊T�ϐݒ�N���X�ł��B���������C�N�ł��B
 */
public class NotepadChatWindow extends ChatWindowManager {

    public NotepadChatWindow(BasedMainWindow owner) {
        super(owner);
    }
    
    /** setSkin���I�[�o���C�h���āA�Ǝ��̃X�L���̃������𐶐����܂��B     */
    public JFrame setSkin(BasedChatWindow window) {
        JFrame frame = new JFrame("���� - ������");
        frame.setSize(640,480);
        Container pane = frame.getContentPane();
        frame.setIconImage(new ImageIcon("data/notepad.PNG").getImage());
        pane.setLayout(new BorderLayout());
        frame.setJMenuBar(window.menubar);
        window.scroll.setBorder(null);
        window.scroll.setBackground(Color.WHITE);
        pane.add(window.infomationBar,BorderLayout.NORTH);
        pane.add(window.scroll, BorderLayout.CENTER);
        window.scroll.setSize(frame.getSize().width,frame.getSize().height-80);
        JPanel south = new JPanel();
        south.setLayout(new BorderLayout());
        south.add(window.sendArea, BorderLayout.CENTER);
        window.enter.setBackground(Color.white);
        window.enter.setBorder(new NotePadBorder());
        south.add(window.enter, BorderLayout.EAST);
        pane.add(south,BorderLayout.SOUTH);
        initSkin(frame);
        frame.doLayout();
        return frame;
    }
    
	/**
	 * �S�ẴR���|�[�l���g�ɑ΂��Ď��{����
	 */
    private void initSkin(JFrame frame) {
        for(int i = 0; i <  frame.getContentPane().getComponentCount(); i++) {
            JComponent com = (JComponent)frame.getContentPane().getComponent(i);
            com.setBorder(new NotePadBorder());
        }
    }
}