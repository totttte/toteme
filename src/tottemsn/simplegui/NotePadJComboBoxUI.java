/**
 * 作成日: 2006/09/04
 */
package tottemsn.simplegui;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.plaf.basic.BasicComboBoxUI;

/**
 * 極力シンプルにしたボタンです。
 * ComboBoxのボタンに使われます。
 */
public class NotePadJComboBoxUI extends BasicComboBoxUI {
    
    protected JButton createArrowButton() {
        JButton button = new JButton("↓");
        button.setBackground(Color.WHITE);
        button.setBorder(null);
        return button;
    }
}
