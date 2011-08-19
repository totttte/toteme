/**
 * 作成日: 2006/09/06
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
 * </p>「コンストラクタの引数で指定したJTextComponentをにあるURLをクリックすると、ブラウザを開く」
 * といった機能を実装します</p>
 * コンポーネントに複数のURLがあるときはマウスカーソルの位置によってURLを選択します。
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
    
    //TODO 以下これから実装
    
    private JTextComponent src;
    // このウィンドウに格納されているURL(Objectをcastしてるのは、Linkクラス)
    private Vector urls;
    
    class Link {
        Rectangle bounds; // そのリンクが動作する範囲
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