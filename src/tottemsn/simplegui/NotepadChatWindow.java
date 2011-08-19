/**
 * 作成日: 2006/09/04
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
 * チャットウィンドウの概観設定クラスです。メモ帳ライクです。
 */
public class NotepadChatWindow extends ChatWindowManager {

    public NotepadChatWindow(BasedMainWindow owner) {
        super(owner);
    }
    
    /** setSkinをオーバライドして、独自のスキンのメモ帳を生成します。     */
    public JFrame setSkin(BasedChatWindow window) {
        JFrame frame = new JFrame("無題 - メモ帳");
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
	 * 全てのコンポーネントに対して実施する
	 */
    private void initSkin(JFrame frame) {
        for(int i = 0; i <  frame.getContentPane().getComponentCount(); i++) {
            JComponent com = (JComponent)frame.getContentPane().getComponent(i);
            com.setBorder(new NotePadBorder());
        }
    }
}