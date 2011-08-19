/**
 * 作成日: 2006/09/04
 */
package tottemsn.simplegui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import tottemsn.core.Messenger;

/**
 * <p>メインウィンドウの概観設定用クラスです。メモ帳ライクです。</p>
 * なお、サインイン前は、NotePadBeforeSiginに委譲させています。
 */
public class NotePadMainWindow extends JComponent implements ComponentListener {
  
    JFrame frame;
    BasedMainWindow mainwindow;

    public NotePadMainWindow(JFrame frame) {
        this.frame = frame;
        this.frame.setTitle("無題 - メモ帳");
        this.frame.setIconImage(new ImageIcon("data/notepad.PNG").getImage());
        frame.getContentPane().setBackground(Color.WHITE);
        this.setOpaque(true);
        this.setBackground(Color.WHITE);
        frame.addComponentListener(this);
        new NotePadBeforeSignin(this);
    }

  //  NotePadBeforeSigninクラスから呼び出される
  public void signin(String account,String pass,String status) {
      removeAll();
      doLayout();
      Messenger messe = new Messenger(account,pass,status);
      mainwindow = new BasedMainWindow(messe,this);
      // メインウィンドウのmanagerインスタンスを上書きして、ChatWindowもスキン変更
      mainwindow.manager = new NotepadChatWindow(null);
      reflesh();
  }
  
  // 画面を更新するとき
  public void reflesh() {
      removeAll();
      setLayout(new BorderLayout());
      JPanel users = new JPanel();
      users.setLayout(new BorderLayout());
      mainwindow.menubar.setBorder(null);
      users.add(mainwindow.menubar, BorderLayout.NORTH);
      mainwindow.userStatusField.setBorder(new NotePadBorder());
      users.add(mainwindow.userStatusField, BorderLayout.WEST);
      mainwindow.userField.setBorder(new NotePadBorder());
      users.add(mainwindow.userField, BorderLayout.CENTER);
      JPanel usersouth = new JPanel();
      mainwindow.userStatusField.setUI(new NotePadJComboBoxUI());
      mainwindow.userStatusField.setToolTipText("状態変更します");
      mainwindow.userStatusField.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      usersouth.setLayout(new BorderLayout());
      JScrollPane scroll = new JScrollPane(mainwindow.membersPanel,
              JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
              JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
      scroll.setMaximumSize(new Dimension(240,320));
      scroll.setMinimumSize(new Dimension(100,100));
      scroll.setBackground(Color.WHITE);
      mainwindow.membersPanel.setBackground(Color.WHITE);
      mainwindow.userMailField.setBorder(new NotePadBorder());
      usersouth.add(mainwindow.userMailField, BorderLayout.WEST);
      mainwindow.userPSMField.setBorder(new NotePadBorder());
      usersouth.add(mainwindow.userPSMField, BorderLayout.CENTER);
      users.add(usersouth, BorderLayout.SOUTH);
      add(users, BorderLayout.NORTH);
      add(scroll, BorderLayout.CENTER);
      initSkin();
      frame.pack();

  }

  public void initSkin() {
      if(mainwindow.toSimple) {
          for(int i = 0; i < getComponentCount(); i++) {
              ((JComponent)getComponent(i)).setBorder(null);
              ((JComponent)getComponent(i)).setBackground(Color.WHITE);
          }
          return;
      }
      for(int i = 0; i < getComponentCount(); i++) {
          JComponent comp = (JComponent)getComponent(i);
          if(comp instanceof JTextField && ((JTextField)comp).isEditable()) {
              comp.setBorder(new NotePadBorder());
          } else {
              comp.setBorder(null);
              comp.setBackground(Color.WHITE);
          }
      }
  }

  /* 画面の更新ができないことがあるため   */
  public void componentResized(ComponentEvent e) {
      if(mainwindow != null && mainwindow.membersPanel != null)
          mainwindow.membersPanel.validate();
      frame.getContentPane().validate();
  }
  public void componentMoved(ComponentEvent e) {
      if(mainwindow != null && mainwindow.membersPanel != null)
          mainwindow.membersPanel.validate();
      frame.getContentPane().validate();
  }
  public void componentShown(ComponentEvent e) {}
  public void componentHidden(ComponentEvent e) {}
}