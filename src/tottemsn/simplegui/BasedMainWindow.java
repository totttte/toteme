/**
 * 作成日: 2006/09/02
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
 * <p>MSNメッセンジャーのデフォルトのメインウィンドウです。</p>
 * <p>このクラスは、コンテナを継承するクラスから委譲されるためだけのもので、
 * スキン等を変更できるように必要最低限のコンポーネントをフィールドとして保有するだけで、
 * 実際にコンテナとしてGUIを実装しているわけではないです。</p>
 * <p>つまり具体的に言うと、このクラスは、tottemsn.coreパッケージとのやりとりの部分等を
 * 実装しているだけで、GUIの色設定やBoarder設定はしていないということです。
 * なお、このクラスはサインイン成功後のコンポーネントです。
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
    private Object keys[]; // NLN とか
    private Object dest[]; // "退席中"とか
    private boolean hasDialog;
    boolean dispGroupe = true; // グループごとの表示かどうか
    
    boolean toSimple = false;
    
    // 操作の一時的なロックを行います。
    private boolean lock;
    
    public BasedMainWindow(Messenger messe, JComponent comp) {
        this.messe = messe;
        this.comp = comp;
        messe.connect();
        messe.addMessengerListener(this);
        menubar = new MenuManager(this, "data/mainmenu.xml").getBar();
        userField = new JTextField("サインイン中");
        userField.addActionListener(this);
        //      ユーザステータスのロード
        LinkedHashMap table = UserStatus.tables();
        Object src[] = table.values().toArray();
        keys = table.keySet().toArray();
        dest = new Object[src.length-1];
        System.arraycopy(src,0,dest,0,dest.length);

        userStatusField = new JComboBox(dest);
        userField.setToolTipText("ここに名前を入れます");
        userStatusField.addActionListener(this);
        userPSMField = new JTextField();
        userPSMField.addActionListener(this);
        userPSMField.setToolTipText("ここにパーソナルメッセージを入れます");
        userMailField = new JTextField();
        userMailField.setToolTipText("ホットメールの情報");
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
    //　グループごと表示
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
    //　グループ表示なし
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
                    "パスワードとアカウントが間違っているか、メッセンジャーのサーバが落ちています",
                    "エラー:サインインできませんでした",JOptionPane.ERROR_MESSAGE);
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
        if(board.getNumber() == 1 && memberNumber != -1) //　1対1ウィンドウに変化した
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
            userMailField.setText("未読:" + messe.getHotMail().getInboxUnread()
                     + "/" + messe.getHotMail().getInbbox());
            userMailField.validate();
        }
		doReflesh();
    }

	public void doReflesh() {
		//TODO ここも変更
	    if(comp instanceof NotePadMainWindow)
	        ((NotePadMainWindow)comp).reflesh();
	    else throw new MessengerException("スキンが登録されていません");
	    if(toSimple) {
	        simpleImpl();
	    }
	}
    
    public void errorMSG(MessengerException e) {
        if(e.getBoard() != null) {
            manager.getMangaer(e.getBoard(),null).error(e.getMessage());
            return;
        }
        if(hasDialog) { //　エラーをいくつか取得して、いっぺんにダイアログが複数開くのを防ぐ
            return;
        }
        JDialog error = new JDialog(getParentFrame(), "エラー");
        error.setBounds(200,300,320,240);
        error.getContentPane().add(new JTextArea("サインアウトしました。\nメニューから再度サインインして" +
        		"ください。\nエラーの詳細\n" + e.getMessage()));
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
        // インスタントメッセージの開始
        for(int i = 0; i < memberList.size(); i++) {
            if(((MemberField)memberList.get(i)).name.equals(e.getSource())
                    || ((MemberField)memberList.get(i)).icon.equals(e.getSource())) {
                JTextField l = ((MemberField)memberList.get(i)).name;
            	Member member = list.equalsMember( l.getName() );
            	if(member.getStatus().getStatus().equals("オフライン"))
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
     * コンポーネントの親階層をたどって、このウィンドウが貼り付けられている
     * フレームを返します。
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
                "状態を変更します",
                "どれかを選択してください",
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
        hasDialog = true; // 正常終了のためダイアログは表示しない
        if(comp instanceof NotePadMainWindow) //TODO 他のスキンも対応できるよう柔軟な対応にする
            new NotePadBeforeSignin((NotePadMainWindow)comp);
        else throw new RuntimeException("スキンが登録されていません");
    }
    
    public void closed(JMenuItem item) {
        Component check = comp.getParent();
        while(true) {
            if(check instanceof JFrame) {
                ((JFrame)check).dispose();
                StringUtil.println("終了日時" +  new Date().toString() );
                System.exit(0);
            }
            check = check.getParent();
        }
    }
    
    public void changePSM(JMenuItem item) {
        String dialog = JOptionPane.showInputDialog(getParentFrame(),"パーソナルメッセージを変更します",
                userPSMField.getText());
        if(dialog != null) {
            messe.changePersonalMessage(dialog);
            userPSMField.setText(dialog);
        }
    }
    
    public void changename(JMenuItem item) {
        String dialog = JOptionPane.showInputDialog(getParentFrame(),"名前を変更します",
                userField.getText());
        if(dialog != null) {
            messe.changeName(dialog);
            userField.setText(dialog);
        }
    }
    
    public void instantmessage(JMenuItem item) {
        JDialog dialog = new  JDialog(getParentFrame(),
                "名前をクリックしてインスタントメッセージを送信します");
        dialog.getContentPane().add(membersPanel);
        Rectangle fb = getParentFrame().getBounds();
        dialog.pack();
        dialog.setBounds(fb.x,fb.y + (fb.height / 2),320,dialog.getHeight());
        dialog.setVisible(true);
        // ウィンドウが破棄されたときに・・・
        dialog.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e) {
                doReflesh();
            }
        });
    }
    
    // 全てのボーダをnullにし、完全にシンプルにします
    public void toSimple(JMenuItem item) {
        if(! toSimple) {
            toSimple = true;
            simpleImpl();
            item.setText("元の画面に戻す");
        } else {
            toSimple = false;
            item.setText("より画面をシンプルにする");
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
            ans = JOptionPane.showConfirmDialog(getParentFrame(),"メンバを一覧で表示しますか？");
        else
            ans = JOptionPane.showConfirmDialog(getParentFrame(),"メンバをグループで表示しますか？");
        if(ans == JOptionPane.YES_OPTION) 
            dispGroupe = ! dispGroupe;
        if(dispGroupe) {
            item.setText("メンバを一覧で表示する");
        } else {
            item.setText("メンバをグループで表示する");
        }
        this.memberListReflesh(this.list);
    }
}