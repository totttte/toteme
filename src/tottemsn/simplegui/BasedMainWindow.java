/**
 * �쐬��: 2006/09/02
 */
package tottemsn.simplegui;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import tottemsn.core.Board;
import tottemsn.core.Chat;
import tottemsn.core.Member;
import tottemsn.core.MemberList;
import tottemsn.core.Messenger;
import tottemsn.core.MessengerException;
import tottemsn.core.MessengerListener;
import tottemsn.core.StringUtil;
import tottemsn.core.UserStatus;
import tottemsn.credits.BareBonesBrowserLaunch;

/**
 * <p>MSN���b�Z���W���[�̃f�t�H���g�̃��C���E�B���h�E�ł��B</p>
 * <p>���̃N���X�́A�R���e�i���p������N���X����Ϗ�����邽�߂����̂��̂ŁA
 * �X�L������ύX�ł���悤�ɕK�v�Œ���̃R���|�[�l���g���t�B�[���h�Ƃ��ĕۗL���邾���ŁA
 * ���ۂɃR���e�i�Ƃ���GUI���������Ă���킯�ł͂Ȃ��ł��B</p>
 * <p>�܂��̓I�Ɍ����ƁA���̃N���X�́Atottemsn.core�p�b�P�[�W�Ƃ̂��Ƃ�̕�������
 * �������Ă��邾���ŁAGUI�̐F�ݒ��Boarder�ݒ�͂��Ă��Ȃ��Ƃ������Ƃł��B
 * �Ȃ��A���̃N���X�̓T�C���C��������̃R���|�[�l���g�ł��B
 */
public class BasedMainWindow implements MessengerListener , ActionListener, MouseListener {
    
    JMenuBar menubar;
    JTextField userField;
    JComboBox userStatusField;
    JTextField userPSMField;
    JTextField userMailField;
    Vector memberList;
    JPanel membersPanel;
    
    ChatWindowManager manager;
    Messenger messe;
    MemberList list;
    JComponent comp;
    private Object keys[]; // NLN �Ƃ�
    private Object dest[]; // "�ސȒ�"�Ƃ�
    private boolean hasDialog;
    boolean dispGroupe = true; // �O���[�v���Ƃ̕\�����ǂ���
    
    boolean toSimple = false;
    
    // ����̈ꎞ�I�ȃ��b�N���s���܂��B
    private boolean lock;
    
    public BasedMainWindow(Messenger messe, JComponent comp) {
        this.messe = messe;
        this.comp = comp;
        messe.connect();
        messe.addMessengerListener(this);
        menubar = new MenuManager(this, "data/mainmenu.xml").getBar();
        userField = new JTextField("�T�C���C����");
        userField.addActionListener(this);
        //      ���[�U�X�e�[�^�X�̃��[�h
        LinkedHashMap table = UserStatus.tables();
        Object src[] = table.values().toArray();
        keys = table.keySet().toArray();
        dest = new Object[src.length-1];
        System.arraycopy(src,0,dest,0,dest.length);

        userStatusField = new JComboBox(dest);
        userField.setToolTipText("�����ɖ��O�����܂�");
        userStatusField.addActionListener(this);
        userPSMField = new JTextField();
        userPSMField.addActionListener(this);
        userPSMField.setToolTipText("�����Ƀp�[�\�i�����b�Z�[�W�����܂�");
        userMailField = new JTextField();
        userMailField.setToolTipText("�z�b�g���[���̏��");
        userMailField.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e) {
                BareBonesBrowserLaunch.openURL("http://hotmail.com/");
            }
        });
        memberList = new Vector();
        membersPanel = new JPanel();
        membersPanel.setLayout(new BoxLayout(membersPanel,BoxLayout.Y_AXIS));
    }

    //
    public void getList(MemberList li) {
        memberListReflesh(li);
        this.list = li;
        if(manager == null) {
            manager = new ChatWindowManager(this);
        } else if(manager.list == null) {
            manager.setInit(this);
        }
        ((NotePadMainWindow)comp).reflesh();
    }
    
    public void memberListReflesh(MemberList li) {
        this.memberList.clear();
        membersPanel.removeAll();
        if(this.dispGroupe)	dispGroupe(li);
        else				dispNotGroupe(li);
        membersPanel.validate();
        getParentFrame().validate();
    }
    //�@�O���[�v���ƕ\��
    private void dispGroupe(MemberList li) {
        String[] groupes = li.getGroupes();
        AccordionPanel[] accordion = new AccordionPanel[groupes.length];
        for(int i = 0; i < groupes.length; i++) {
            accordion[i] = new AccordionPanel(groupes[i],false);
            Member[] list = li.getList(groupes[i]);
            for(int j = 0; j < list.length; j++) {
                if(! list[j].isForward())
                    continue;
                MemberField field = new MemberField(list[j]);
                memberList.add(field);
                field.name.addMouseListener(this);
                field.icon.addMouseListener(this);
                accordion[i].add(field.memberPanel);
            }
            membersPanel.add(accordion[i]);
        }
    }
    //�@�O���[�v�\���Ȃ�
    private void dispNotGroupe(MemberList li) {
        Member[] list = li.getList();
        for(int i = 0; i < list.length; i++) {
            if(! list[i].isForward() )
                continue;
            MemberField field = new MemberField(list[i]);
            memberList.add(field);
            field.name.addMouseListener(this);
            field.icon.addMouseListener(this);
            membersPanel.add(field.memberPanel);
        }
    }

    public void changedStatus(Member member) {
        memberListReflesh(list);
        ((NotePadMainWindow)comp).reflesh();
    }

    public void changedName(Member member) {
        changedStatus(member);
    }

    public void getGroupe(String[] s) {
    }

    public void finshSignIn(boolean isSuccess, Exception e) {
        if(isSuccess) {
            userField.setText(messe.getUserInfo().getDefaultName());
            userStatusField.setSelectedItem(messe.getUserInfo().getUserStatus().getStatus());
            ((NotePadMainWindow)comp).reflesh();
        } else {
            if(hasDialog)
                return;
            userField.setText("");
            JOptionPane.showConfirmDialog(getParentFrame(),
                    "�p�X���[�h�ƃA�J�E���g���Ԉ���Ă��邩�A���b�Z���W���[�̃T�[�o�������Ă��܂�",
                    "�G���[:�T�C���C���ł��܂���ł���",JOptionPane.ERROR_MESSAGE);
            hasDialog = true;
            signout(null);
        }
    }

    public void addParticipant(Board board, Member member) {
        lock = false;
        membersPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        if(manager.getMangaer(board,member) == null) {
        	manager.createNewWindow(board,false,Frame.ICONIFIED,member);
        }
        manager.getMangaer(board,member).addMember(member,board);
    }

    public void removeParticipant(Board board, Member member) {
        manager.getMangaer(board,null).removeMember(member);
        int memberNumber = manager.getMemberNumber(board.getMember()[0]);
        if(board.getNumber() == 1 && memberNumber != -1) //�@1��1�E�B���h�E�ɕω�����
            manager.isInvite[memberNumber] = true;
    }

    public void getChat(Chat chat, Board board) {
        if(manager.getMangaer(board,null) == null)
        	manager.createNewWindow(board,false,Frame.ICONIFIED,board.getMember()[0]);
        manager.getMangaer(board,null).reflesh(chat);
    }

    public void finishSendMsg(Board board, boolean isSuccess) {
        manager.getMangaer(board,null).finishSend(isSuccess);
    }

    public void update(int type) {
        if(type == MessengerListener.NAME_CHANGED) {
            userField.setText(messe.getUserInfo().getDefaultName());
        } else if(type == MessengerListener.PSM_CHANGED) {
        } else if(type == MessengerListener.STATUS_CHANGED) {
            userField.setText(messe.getUserInfo().getDefaultName());
            
        } else if(type == MessengerListener.GET_HOTMAIL) {
            userMailField.setText("����:" + messe.getHotMail().getInboxUnread()
                     + "/" + messe.getHotMail().getInbbox());
            userMailField.validate();
        }
		doReflesh();
    }

	public void doReflesh() {
		//TODO �������ύX
	    if(comp instanceof NotePadMainWindow)
	        ((NotePadMainWindow)comp).reflesh();
	    else throw new MessengerException("�X�L�����o�^����Ă��܂���");
	    if(toSimple) {
	        simpleImpl();
	    }
	}
    
    public void errorMSG(MessengerException e) {
        if(e.getBoard() != null) {
            manager.getMangaer(e.getBoard(),null).error(e.getMessage());
            return;
        }
        if(hasDialog) { //�@�G���[���������擾���āA�����؂�Ƀ_�C�A���O�������J���̂�h��
            return;
        }
        JDialog error = new JDialog(getParentFrame(), "�G���[");
        error.setBounds(200,300,320,240);
        error.getContentPane().add(new JTextArea("�T�C���A�E�g���܂����B\n���j���[����ēx�T�C���C������" +
        		"���������B\n�G���[�̏ڍ�\n" + e.getMessage()));
        error.pack();
        error.setVisible(true);
        hasDialog = true;
        signout(null);
    }
    
    public void actionPerformed(ActionEvent e) {
        if(e.getSource().equals(userStatusField)) {
            String old = messe.getUserInfo().getUserStatus().getStatusCommand();
            String next = keys[userStatusField.getSelectedIndex()].toString();
            if(!old.equals(next))
                messe.changeStatus(new UserStatus(next));
        } else if(e.getSource().equals(userField)) {
            messe.changeName(userField.getText());
        } else if(e.getSource().equals(userPSMField)) {
            messe.changePersonalMessage(userPSMField.getText());
        }
    }
    public void mouseClicked(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {
        // �C���X�^���g���b�Z�[�W�̊J�n
        for(int i = 0; i < memberList.size(); i++) {
            if(((MemberField)memberList.get(i)).name.equals(e.getSource())
                    || ((MemberField)memberList.get(i)).icon.equals(e.getSource())) {
                JTextField l = ((MemberField)memberList.get(i)).name;
            	Member member = list.equalsMember( l.getName() );
            	if(member.getStatus().getStatus().equals("�I�t���C��"))
            	    return;
            	if(manager.isTalking(member))
            	    return;
            	if(!lock) {
            	    manager.createNewWindow(messe.invitingPrincipal(member),true,Frame.NORMAL, member);
            	    lock = true;
            	    membersPanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            	}
            }
        }
    }
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    
    /**
     * �R���|�[�l���g�̐e�K�w�����ǂ��āA���̃E�B���h�E���\��t�����Ă���
     * �t���[����Ԃ��܂��B
     */
    private JFrame getParentFrame() {
        Component check = comp.getParent();
        JFrame frameowner;
        while(true) {
            if(check instanceof JFrame) {
                frameowner = (JFrame)check;
                break;
            }
            check = check.getParent();
        }
        return frameowner;
    }
    
    public void changestatus(JMenuItem item) {
        String dialog = (String)JOptionPane.showInputDialog(getParentFrame(), 
                "��Ԃ�ύX���܂�",
                "�ǂꂩ��I�����Ă�������",
                JOptionPane.QUESTION_MESSAGE,
                null,
                dest,
                messe.getUserInfo().getUserStatus().getStatus());
        String old = messe.getUserInfo().getUserStatus().getStatusCommand();
        if(dialog != null) {
            userStatusField.setSelectedItem(dialog);
        }
    }
    
    public void signout(JMenuItem item) {
        messe.singout();
        comp.removeAll();
        hasDialog = true; // ����I���̂��߃_�C�A���O�͕\�����Ȃ�
        if(comp instanceof NotePadMainWindow) //TODO ���̃X�L�����Ή��ł���悤�_��ȑΉ��ɂ���
            new NotePadBeforeSignin((NotePadMainWindow)comp);
        else throw new RuntimeException("�X�L�����o�^����Ă��܂���");
    }
    
    public void closed(JMenuItem item) {
        Component check = comp.getParent();
        while(true) {
            if(check instanceof JFrame) {
                ((JFrame)check).dispose();
                StringUtil.println("�I������" +  new Date().toString() );
                System.exit(0);
            }
            check = check.getParent();
        }
    }
    
    public void changePSM(JMenuItem item) {
        String dialog = JOptionPane.showInputDialog(getParentFrame(),"�p�[�\�i�����b�Z�[�W��ύX���܂�",
                userPSMField.getText());
        if(dialog != null) {
            messe.changePersonalMessage(dialog);
            userPSMField.setText(dialog);
        }
    }
    
    public void changename(JMenuItem item) {
        String dialog = JOptionPane.showInputDialog(getParentFrame(),"���O��ύX���܂�",
                userField.getText());
        if(dialog != null) {
            messe.changeName(dialog);
            userField.setText(dialog);
        }
    }
    
    public void instantmessage(JMenuItem item) {
        JDialog dialog = new  JDialog(getParentFrame(),
                "���O���N���b�N���ăC���X�^���g���b�Z�[�W�𑗐M���܂�");
        dialog.getContentPane().add(membersPanel);
        Rectangle fb = getParentFrame().getBounds();
        dialog.pack();
        dialog.setBounds(fb.x,fb.y + (fb.height / 2),320,dialog.getHeight());
        dialog.setVisible(true);
        // �E�B���h�E���j�����ꂽ�Ƃ��ɁE�E�E
        dialog.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e) {
                doReflesh();
            }
        });
    }
    
    // �S�Ẵ{�[�_��null�ɂ��A���S�ɃV���v���ɂ��܂�
    public void toSimple(JMenuItem item) {
        if(! toSimple) {
            toSimple = true;
            simpleImpl();
            item.setText("���̉�ʂɖ߂�");
        } else {
            toSimple = false;
            item.setText("����ʂ��V���v���ɂ���");
            doReflesh();
        }
    }
    
    private void simpleImpl() {
        this.membersPanel.setBorder(null);
        this.menubar.setBorder(null);
        this.userField.setBorder(null);
        this.userMailField.setBorder(null);
        this.userPSMField.setBorder(null);
        this.userStatusField.setBorder(null);        
    }
    
    public void bunrui(JMenuItem item) {
        int ans;
        if(dispGroupe)
            ans = JOptionPane.showConfirmDialog(getParentFrame(),"�����o���ꗗ�ŕ\�����܂����H");
        else
            ans = JOptionPane.showConfirmDialog(getParentFrame(),"�����o���O���[�v�ŕ\�����܂����H");
        if(ans == JOptionPane.YES_OPTION) 
            dispGroupe = ! dispGroupe;
        if(dispGroupe) {
            item.setText("�����o���ꗗ�ŕ\������");
        } else {
            item.setText("�����o���O���[�v�ŕ\������");
        }
        this.memberListReflesh(this.list);
    }
}