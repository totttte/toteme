/**
 * 作成日: 2006/09/03
 */
package tottemsn.simplegui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.border.AbstractBorder;

/**
 * メモ帳ライクな画面構成にするために、ボーダーを必要最低限にする
 */
public class NotePadBorder extends AbstractBorder {
    
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Color oldColor = g.getColor();
        g.setColor(Color.BLACK);
        g.drawLine(x - width , y + height, x + width, y + height - 1);
        g.drawLine(x + width, y - height, x + width - 1, y + height - 1);
        g.setColor(oldColor);
    }
}
