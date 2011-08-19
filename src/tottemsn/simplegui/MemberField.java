/**
 * 作成日: 2006/09/02
 */
package tottemsn.simplegui;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.JTextField;

import tottemsn.core.Member;

/**
 * <p>メンバを表すフィールドです。</p>
 */
public class MemberField {

    JTextField name;
    JTextField icon;
    JPanel memberPanel;
    Member mem;
    public MemberField(Member mem) {
        this.mem = mem;
        reflesh(mem);
    }
    
    public void reflesh(Member mem) {
        name = new JTextField(mem.getName());
        if(! mem.getPersonalMessage().equals("")) {
            name.setText(name.getText() + " - " + mem.getPersonalMessage());
        }
        name.setName(mem.getAddress());
        if( mem.isBlock()) 
            name.setText(name.getText() + "(禁止中)");
        name.setToolTipText(mem.getAddress());
        String iconSt = mem.getStatus().getStatus();
        icon = new JTextField("(" + iconSt + ")");
        icon.setBorder(null);
        name.setBorder(null);
        memberPanel = new JPanel();
        memberPanel.setLayout(new BorderLayout());
        memberPanel.add(icon, BorderLayout.WEST);
        memberPanel.add(name, BorderLayout.CENTER);
        memberPanel.setBackground(Color.WHITE);
    }
}