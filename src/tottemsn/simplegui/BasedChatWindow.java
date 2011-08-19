/**
 * 作成日: 2006/09/03
 */
package tottemsn.simplegui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import tottemsn.core.Board;
import tottemsn.core.Chat;
import tottemsn.core.Member;

/**
 * @see tottemsn.simplegui.BasedMainWindow
 */
public class BasedChatWindow implements ActionListener , KeyListener , MouseListener , WindowStateListener{

    JMenuBar menubar;
    JTextField infomationBar;
    JTextPane chatArea;
    JTextArea sendArea;
    JButton enter;
    JScrollPane scroll;
    
    Board board;
    private BasedMainWindow owner;
    private Vector sendBuf;
    private boolean shiftkey;
    boolean isColorful; // カラフルにするときはtrue 
    private JFrame frame;
    
    public BasedChatWindow(Board board, BasedMainWindow owner) {
        this.owner = owner;
        this.board = board;
        menubar = new MenuManager(this, "data/chatmenu.xml").getBar();
        infomationBar = new JTextField();
        infomationBar.addKeyListener(this); //　ESCキー用
        chatArea = new JTextPane();
        chatArea.setToolTipText("送信済みのメッセージ");
        chatArea.addKeyListener(this); // ESCキー用
        scroll = new JScrollPane(chatArea,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        chatArea.setSize(scroll.getSize());
        sendArea = new JTextArea(3,0);
        sendArea.setToolTipText("ここにメッセージを入力して下さい");
        sendArea.addKeyListener(this);
        enter = new JButton("送信");
        enter.setToolTipText("メッセージを送信します");
        sendBuf = new Vector();
        enter.addActionListener(this);
        refleshInFomation(null);
        isColorful = true;
    }
    
    /* infomationbarを更新します
     * @param member これから招待する人。招待以外の場合はnull
     */
    private void refleshInFomation(Member member) {
        Member[] mem = board.getMember();
        int plus = (member == null)? 0: 1;
        infomationBar.setText((board.getNumber()+ plus) + "人の参加者:\t");
        for(int i = 0; i < mem.length; i++) 
            infomationBar.setText(infomationBar.getText() + " " + mem[i].getName());
        if(member != null) {
            infomationBar.setText(infomationBar.getText() + " " + member.getName());
        }
    }
    
    /**
     * 新しくメッセージが届いたか、何かでリフレッシュする必要があったとき
     * @param chat chatがnullのときは、infomationBarのリフレッシュ、
     * chatがnullでないときは、チャットエリアの更新
     */
    public void reflesh(Chat chat) {
        if(chat == null) {
            refleshInFomation(null);
        } else  {
            if(!frame.isVisible()) {
                frame.setVisible(true);
                frame.setExtendedState(JFrame.ICONIFIED);
            }
            appendChatArea(chat);
        }
    }
    
    // 色換えして、チャットエリアに表示
    private void appendChatArea(Chat chat) {
        SimpleAttributeSet sas = null;
        if(isColorful) {
            sas = new SimpleAttributeSet();
            StyleConstants.setForeground(sas, chat.getColor());
            StyleConstants.setFontFamily(sas,chat.getFont().getFamily());
        }
        Document doc = chatArea.getDocument();
        try {
            if(chat.getType() == Chat.INCOMING_MESSAGE
                    ||  chat.getType() == Chat.OUTGOING_MESSAGE ) {
                doc.insertString(doc.getLength(), chat.getName() + "の発言",null);
                doc.insertString(doc.getLength(), chat.getText() + "\n", sas);
            } else if(chat.getType() == Chat.INFOMATION_MESSAGE) {
                doc.insertString(doc.getLength(), chat.getText() + "\n",null);
            }
            chatArea.setCaretPosition(doc.getLength());
            if(chat.getText().indexOf("http://") != -1) 
                urlLink(chat);
        } catch(BadLocationException e) {
            chatArea.setText(chatArea.getText() + chat.getText() + "\n");
        }
    }
    
    public void addMember(Member member,Board board) {
        if(board.getNumber() != 0) {
            appendChatArea(new Chat(member.getName() + "が参加しました。"));
            this.board = board;
            refleshInFomation(member);
        } else {
            this.board = board;
        }
    }
    
    /**
     * http:// を見つけたらリンクを張る
     */
    public void urlLink(Chat chat) {
        //TODO リンクを張る方法を検討中
/*        final JTextField urlLabel = new JTextField();
        urlLabel.setText(StringUtil.getURL(chat.getText()));
        urlLabel.setToolTipText(StringUtil.getURL(chat.getText()) + "にジャンプします");
        urlLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if(isColorful)
            urlLabel.setForeground(Color.BLUE);
        urlLabel.setOpaque(false);
        chatArea.add(urlLabel);
        try {
            urlLabel.setBounds(chatArea.modelToView(chatArea.getDocument().getLength()));
        } catch (BadLocationException e1) {
        }
        urlLabel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                BareBonesBrowserLaunch.openURL(urlLabel.getText());
            }
        });
*/
    }
    
    /**
     * 今はまだ招待していないがこれから招待する人
     * 処理が重くなるため先に表示
     * @param member
     */
    public void addShallMember(Member member) {
        infomationBar.setText("宛先:"
                + member.getName() + member.getPersonalMessage()
                + "<" + member.getAddress() + ">");
        infomationBar.setName(member.getAddress()); // 招待する前に閉じられたら・・・のため処理
    }

    public void removeMember(Member member) {
        if(board.getNumber() != 0) 
            appendChatArea(new Chat(member.getName() + "が退席しました"));
        // 未登録者との1対1のチャットに変化した場合
        if(board.getNumber() == 1 && board.getMember()[0].isBlock()) {
            stopChat();
        }
        if(chatArea.getText().equals("")) {
            // 会話がなかったってことで。(アバター送信時に会話なしコネクションはよくある)
            frame.dispose();
        }
        refleshInFomation(null);
    }

    /*	これ以上チャットを続けることができないときの処理
     *	たとえば、登録していない人との会話ウィンドウになったときなど。
     *
     *	sendAreaとボタンを編集不可にする。
     */
    public void stopChat() {
        appendChatArea(new Chat("このメンバは登録していないのでこれ以上は会話できません。"));
        sendArea.setEnabled(false);
        enter.setEnabled(false);
        sendArea.setBackground(Color.GRAY);
        enter.setBackground(Color.GRAY);
    }
    
    public void actionPerformed(ActionEvent e) {
        if(e.getSource().equals(enter))
           send(); 
    }
    private void send() {
        owner.messe.sendMessage(board,sendArea.getText(),sendArea.getFont(), sendArea.getForeground());
        sendBuf.add("\n"+sendArea.getText());
        sendArea.setText("");
    }

    public void keyTyped(KeyEvent e) {}

    // shift+Enterは、改行のみでメッセージを送信しない
    public void keyPressed(KeyEvent e) {
        if(!e.getSource().equals(sendArea)) // 他のエリアでの改行には反応しない
            return;
        if( e.getKeyCode() == KeyEvent.VK_ENTER) {
            if(shiftkey) 
                sendArea.append("\n");
             else send();
        } else if(e.getKeyCode() == KeyEvent.VK_SHIFT) {
            this.shiftkey = true;
        }
    }

    // キーを離したとき、送信時に押した改行を元に戻す
    public void keyReleased(KeyEvent e) {
        if(sendArea.getText().equals("\n"))
            sendArea.setText("");
        if(e.getKeyCode() == KeyEvent.VK_SHIFT)
            this.shiftkey = false;
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE) // エスケープで画面を閉じる
            frame.dispose();
    }

    /**
     * エラーが発生したので、その有無を表示します。
     */
    public void error(String message) {
        if(message.endsWith("215")) {
            appendChatArea(new Chat("既にそのメンバは招待されています。"));
            return;
        }
        appendChatArea(new Chat("切断されました。\nエラーの原因\n" + message));
        appendChatArea(new Chat("\nなお、一つ前のメッセージは送信されていません。"));
        appendChatArea(new Chat("このウィンドウでは会話を継続することができないので、" +
        		"あらたなウィンドウを開きなおしてください。"));
    }
    
    public void finishSend(boolean isSuccess) {
        if(sendBuf.size() == 0) {
            return;
        }
        if(isSuccess) {
            String name = owner.messe.getUserInfo().getDefaultName();
            appendChatArea(new Chat(name,sendBuf.get(0).toString(),
                    sendArea.getForeground(),sendArea.getFont()));
        } else {
            appendChatArea(new Chat("次のメッセージは全ての送信先に送信できませんでした。"));
            appendChatArea(new Chat(sendBuf.get(0).toString()));
        }
        sendBuf.remove(0);
    }
        
    public void closed(JMenuItem item) {
        frame.dispose();
    }
    
    public void save(JMenuItem item) {
        JFileChooser chooser = new JFileChooser("ファイルにしてチャットの履歴を保存します");
        chooser.showSaveDialog(null);
        File file = chooser.getSelectedFile();
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(chatArea.getText());
            writer.close();
        } catch(IOException e) {
            appendChatArea(new Chat("何らかのエラーで保存できませんでした。"));
        }
        appendChatArea(new Chat(file.getName() + "にログを保存しました"));
    }
    
    public void myFont(JMenuItem item) {
        // 現在は色のみ TODO 今後フォントチューザを作る
        Color after = JColorChooser.showDialog(frame,"フォントの色を設定します",sendArea.getForeground());
        sendArea.setForeground(after);
    }
    
    public void chatFont(JMenuItem item) {
        if(!isColorful) {
            JOptionPane.showMessageDialog(frame, "チャットエリアをカラーモードにしました。\n" +
            		"これからカラーになります。");
            return;
        }
        int value = JOptionPane.showConfirmDialog(frame,
                "なお、一度白黒にした部分は元の色に戻すことはできません",
                "このウィンドウを白黒にしますか？",
                JOptionPane.YES_NO_CANCEL_OPTION);
        if(value == JOptionPane.YES_OPTION) {
            String old = chatArea.getText();
            chatArea.setText(old);
            sendArea.setForeground(Color.BLACK);
            isColorful = false;
        } else {
            
        }
    }
    
    public void reflesh(JMenuItem item) {
        chatArea.setText("");
    }

    public void invite(JMenuItem item){
        JDialog dialog = new JDialog(frame,"このチャットに招待したい人を指定してください");
        Member[] list = owner.list.getList();
        JLabel[] listlabels = new JLabel[list.length];
        for(int i = 0; i < list.length; i++) {
            listlabels[i] = new JLabel(list[i].getName());
            listlabels[i].setToolTipText(list[i].getAddress());
            listlabels[i].addMouseListener(this);
            dialog.getContentPane().add(listlabels[i]);
        }
        dialog.pack();
        dialog.setVisible(true);
    }
    
    // 簡易的なアドレスかどうかの判定用メソッドです。あくまでも簡易なので実際に存在するアドレスかどうか
    // は別です。
    private boolean isAddress(String address) {
        if(address == null) return false;
        if(address.indexOf('@') == -1)
            return false;
        if(address.indexOf('.', address.indexOf('@')) == -1)
            return false;
        if(address.indexOf(' ') != -1)
            return false;
        return true;
    }

    public void mouseClicked(MouseEvent e) {
        if(e.getSource() instanceof JComponent) {
            JComponent comp = (JComponent)e.getSource();
            String address = comp.getToolTipText();
            if(isAddress(address)) {
                owner.messe.invitingPrincipal(this.board,owner.list.equalsMember(address));
            }
        }
    }
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    
    /**	このメソッドをメニューから呼び出すと、全てのボーダをnullにし、よりシンプルにします。
     *
     */
    public void toSimple(JMenuItem item) {
        this.chatArea.setBorder(null);
        this.enter.setText("");
        this.enter.setBorder(null);
        this.enter.setSize(0,0);
        this.infomationBar.setBorder(null);
        this.isColorful = false;
        String old = chatArea.getText();
        chatArea.setText(old);
        for(int i = 0; i < chatArea.getComponentCount(); i++) {
            ((JComponent)chatArea.getComponent(i)).setBorder(null);
            ((JComponent)chatArea.getComponent(i)).setForeground(Color.WHITE);
        }
        sendArea.setForeground(Color.BLACK);
        this.menubar.setBorder(null);
        this.sendArea.setBorder(null);
        item.setEnabled(false);
    }

    public void setFrame(JFrame frame) {
        this.frame = frame;
        this.frame.addWindowStateListener(this);
    }
    // 画面が乱れるときがあるため
    public void windowStateChanged(WindowEvent e) {
        frame.doLayout();
        frame.validate();
    }
}