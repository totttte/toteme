/**
 * 作成日: 2006/09/04
 */
package tottemsn.simplegui;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

/**
 * 簡易のダイアログ的なもの
 */
class NotePadFrame extends JFrame {

    NotePadFrame(String title) {
        super(title);
        setIconImage(new ImageIcon("data/notepad.PNG").getImage());
        setSize(320,240);
        setVisible(true);
    }
}