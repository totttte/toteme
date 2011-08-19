/**
 * �쐬��: 2006/09/03
 */
package tottemsn.simplegui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import tottemsn.core.StringUtil;
import tottemsn.core.UserStatus;

/**
 * ���������C�N�ȃ��C���E�B���h�E�̃T�C���C���O�ł��B
 */
public class NotePadBeforeSignin implements ActionListener {
    
    private JTextField useraccount;
    private JPasswordField userpass;
    private JComboBox userstatus;
    private JButton signin;
    private NotePadMainWindow owner;
    private Object keys[];
    

    public NotePadBeforeSignin(NotePadMainWindow owner) {
        this.owner = owner;
        owner.setLayout(new GridLayout(0,1));
        owner.setBackground(Color.WHITE);
        //      ���[�U�X�e�[�^�X�̃��[�h
        LinkedHashMap table = UserStatus.tables();
        Object src[] = table.values().toArray();
        keys = table.keySet().toArray();
        Object dest[] = new Object[src.length-1];
        System.arraycopy(src,0,dest,0,dest.length);
        
        JTextField useraccountLabel = new JTextField("�d�q���[���A�h���X");
        useraccountLabel.setEditable(false);
        owner.add(useraccountLabel);
        useraccount = new JTextField("");
        useraccount.setToolTipText("�����ɃA�h���X�����Ă�������");
        owner.add(useraccount);
        JTextField userpassLabel = new JTextField("�p�X���[�h");
        userpassLabel.setEditable(false);
        owner.add(userpassLabel);
        String passS = "";
        userpass = new JPasswordField(passS);
        userpass.setToolTipText("�����Ƀp�X���[�h�����Ă�������");
        owner.add(userpass);
        JTextField userstatusLabel = new JTextField("���");
        userstatusLabel.setEditable(false);
        owner.add(userstatusLabel);
        userstatus = new JComboBox(dest);
        userstatus.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        userstatus.setUI(new NotePadJComboBoxUI());
        owner.add(userstatus);
        signin = new JButton("�T�C���C��");
        signin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        signin.addActionListener(this);
        owner.add(signin);
        initSkin();
        owner.frame.pack();
    }
    
    // �S�ẴR���|�[�l���g�����������C�N�ɕύX
    private void initSkin() {
        for(int i = 0; i < owner.getComponentCount(); i++) {
            JComponent comp = (JComponent)owner.getComponent(i);
            if(comp instanceof JTextField && ((JTextField)comp).isEditable()){
                comp.setBorder(new NotePadBorder());
            } else {
                comp.setBorder(null);
                comp.setBackground(Color.WHITE);
            }
        }
        owner.validate();
    }
    
    

    //
    public void actionPerformed(ActionEvent e) {
        if(e.getSource().equals(signin)) {
            owner.signin(this.useraccount.getText(),
                    new String(this.userpass.getPassword()), 
                    keys[this.userstatus.getSelectedIndex()].toString());
        }
    }
}
