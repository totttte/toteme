/**
 * �쐬��: 2006/09/03
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
    boolean isColorful; // �J���t���ɂ���Ƃ���true 
    private JFrame frame;
    
    public BasedChatWindow(Board board, BasedMainWindow owner) {
        this.owner = owner;
        this.board = board;
        menubar = new MenuManager(this, "data/chatmenu.xml").getBar();
        infomationBar = new JTextField();
        infomationBar.addKeyListener(this); //�@ESC�L�[�p
        chatArea = new JTextPane();
        chatArea.setToolTipText("���M�ς݂̃��b�Z�[�W");
        chatArea.addKeyListener(this); // ESC�L�[�p
        scroll = new JScrollPane(chatArea,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        chatArea.setSize(scroll.getSize());
        sendArea = new JTextArea(3,0);
        sendArea.setToolTipText("�����Ƀ��b�Z�[�W����͂��ĉ�����");
        sendArea.addKeyListener(this);
        enter = new JButton("���M");
        enter.setToolTipText("���b�Z�[�W�𑗐M���܂�");
        sendBuf = new Vector();
        enter.addActionListener(this);
        refleshInFomation(null);
        isColorful = true;
    }
    
    /* infomationbar���X�V���܂�
     * @param member ���ꂩ�珵�҂���l�B���҈ȊO�̏ꍇ��null
     */
    private void refleshInFomation(Member member) {
        Member[] mem = board.getMember();
        int plus = (member == null)? 0: 1;
        infomationBar.setText((board.getNumber()+ plus) + "�l�̎Q����:\t");
        for(int i = 0; i < mem.length; i++) 
            infomationBar.setText(infomationBar.getText() + " " + mem[i].getName());
        if(member != null) {
            infomationBar.setText(infomationBar.getText() + " " + member.getName());
        }
    }
    
    /**
     * �V�������b�Z�[�W���͂������A�����Ń��t���b�V������K�v���������Ƃ�
     * @param chat chat��null�̂Ƃ��́AinfomationBar�̃��t���b�V���A
     * chat��null�łȂ��Ƃ��́A�`���b�g�G���A�̍X�V
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
    
    // �F�������āA�`���b�g�G���A�ɕ\��
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
                doc.insertString(doc.getLength(), chat.getName() + "�̔���",null);
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
            appendChatArea(new Chat(member.getName() + "���Q�����܂����B"));
            this.board = board;
            refleshInFomation(member);
        } else {
            this.board = board;
        }
    }
    
    /**
     * http:// ���������烊���N�𒣂�
     */
    public void urlLink(Chat chat) {
        //TODO �����N�𒣂���@��������
/*        final JTextField urlLabel = new JTextField();
        urlLabel.setText(StringUtil.getURL(chat.getText()));
        urlLabel.setToolTipText(StringUtil.getURL(chat.getText()) + "�ɃW�����v���܂�");
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
     * ���͂܂����҂��Ă��Ȃ������ꂩ�珵�҂���l
     * �������d���Ȃ邽�ߐ�ɕ\��
     * @param member
     */
    public void addShallMember(Member member) {
        infomationBar.setText("����:"
                + member.getName() + member.getPersonalMessage()
                + "<" + member.getAddress() + ">");
        infomationBar.setName(member.getAddress()); // ���҂���O�ɕ���ꂽ��E�E�E�̂��ߏ���
    }

    public void removeMember(Member member) {
        if(board.getNumber() != 0) 
            appendChatArea(new Chat(member.getName() + "���ސȂ��܂���"));
        // ���o�^�҂Ƃ�1��1�̃`���b�g�ɕω������ꍇ
        if(board.getNumber() == 1 && board.getMember()[0].isBlock()) {
            stopChat();
        }
        if(chatArea.getText().equals("")) {
            // ��b���Ȃ��������Ă��ƂŁB(�A�o�^�[���M���ɉ�b�Ȃ��R�l�N�V�����͂悭����)
            frame.dispose();
        }
        refleshInFomation(null);
    }

    /*	����ȏ�`���b�g�𑱂��邱�Ƃ��ł��Ȃ��Ƃ��̏���
     *	���Ƃ��΁A�o�^���Ă��Ȃ��l�Ƃ̉�b�E�B���h�E�ɂȂ����Ƃ��ȂǁB
     *
     *	sendArea�ƃ{�^����ҏW�s�ɂ���B
     */
    public void stopChat() {
        appendChatArea(new Chat("���̃����o�͓o�^���Ă��Ȃ��̂ł���ȏ�͉�b�ł��܂���B"));
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

    // shift+Enter�́A���s�݂̂Ń��b�Z�[�W�𑗐M���Ȃ�
    public void keyPressed(KeyEvent e) {
        if(!e.getSource().equals(sendArea)) // ���̃G���A�ł̉��s�ɂ͔������Ȃ�
            return;
        if( e.getKeyCode() == KeyEvent.VK_ENTER) {
            if(shiftkey) 
                sendArea.append("\n");
             else send();
        } else if(e.getKeyCode() == KeyEvent.VK_SHIFT) {
            this.shiftkey = true;
        }
    }

    // �L�[�𗣂����Ƃ��A���M���ɉ��������s�����ɖ߂�
    public void keyReleased(KeyEvent e) {
        if(sendArea.getText().equals("\n"))
            sendArea.setText("");
        if(e.getKeyCode() == KeyEvent.VK_SHIFT)
            this.shiftkey = false;
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE) // �G�X�P�[�v�ŉ�ʂ����
            frame.dispose();
    }

    /**
     * �G���[�����������̂ŁA���̗L����\�����܂��B
     */
    public void error(String message) {
        if(message.endsWith("215")) {
            appendChatArea(new Chat("���ɂ��̃����o�͏��҂���Ă��܂��B"));
            return;
        }
        appendChatArea(new Chat("�ؒf����܂����B\n�G���[�̌���\n" + message));
        appendChatArea(new Chat("\n�Ȃ��A��O�̃��b�Z�[�W�͑��M����Ă��܂���B"));
        appendChatArea(new Chat("���̃E�B���h�E�ł͉�b���p�����邱�Ƃ��ł��Ȃ��̂ŁA" +
        		"���炽�ȃE�B���h�E���J���Ȃ����Ă��������B"));
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
            appendChatArea(new Chat("���̃��b�Z�[�W�͑S�Ă̑��M��ɑ��M�ł��܂���ł����B"));
            appendChatArea(new Chat(sendBuf.get(0).toString()));
        }
        sendBuf.remove(0);
    }
        
    public void closed(JMenuItem item) {
        frame.dispose();
    }
    
    public void save(JMenuItem item) {
        JFileChooser chooser = new JFileChooser("�t�@�C���ɂ��ă`���b�g�̗�����ۑ����܂�");
        chooser.showSaveDialog(null);
        File file = chooser.getSelectedFile();
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(chatArea.getText());
            writer.close();
        } catch(IOException e) {
            appendChatArea(new Chat("���炩�̃G���[�ŕۑ��ł��܂���ł����B"));
        }
        appendChatArea(new Chat(file.getName() + "�Ƀ��O��ۑ����܂���"));
    }
    
    public void myFont(JMenuItem item) {
        // ���݂͐F�̂� TODO ����t�H���g�`���[�U�����
        Color after = JColorChooser.showDialog(frame,"�t�H���g�̐F��ݒ肵�܂�",sendArea.getForeground());
        sendArea.setForeground(after);
    }
    
    public void chatFont(JMenuItem item) {
        if(!isColorful) {
            JOptionPane.showMessageDialog(frame, "�`���b�g�G���A���J���[���[�h�ɂ��܂����B\n" +
            		"���ꂩ��J���[�ɂȂ�܂��B");
            return;
        }
        int value = JOptionPane.showConfirmDialog(frame,
                "�Ȃ��A��x�����ɂ��������͌��̐F�ɖ߂����Ƃ͂ł��܂���",
                "���̃E�B���h�E�𔒍��ɂ��܂����H",
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
        JDialog dialog = new JDialog(frame,"���̃`���b�g�ɏ��҂������l���w�肵�Ă�������");
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
    
    // �ȈՓI�ȃA�h���X���ǂ����̔���p���\�b�h�ł��B�����܂ł��ȈՂȂ̂Ŏ��ۂɑ��݂���A�h���X���ǂ���
    // �͕ʂł��B
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
    
    /**	���̃��\�b�h�����j���[����Ăяo���ƁA�S�Ẵ{�[�_��null�ɂ��A���V���v���ɂ��܂��B
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
    // ��ʂ������Ƃ������邽��
    public void windowStateChanged(WindowEvent e) {
        frame.doLayout();
        frame.validate();
    }
}