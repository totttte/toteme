/**
 * �쐬��: 2006/09/05
 */
package tottemsn.simplegui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * <p>�y��̃A�R�[�f�B�I���݂����ȃp�l���ł��B</p>
 * �Q�l�ɂ����T�C�g�E�E�Ehttp://terai.xrea.jp/Swing/AccordionPanel.html
 * �����ł́ABorder=null�A�w�i�F���ɂ��Ă��܂��B
 */
public class AccordionPanel extends JPanel implements MouseListener, ActionListener {

    private String top;
    private boolean close; // close or open
    private JLabel topLabel; // �g�b�v�̃��x��
    private JButton driver; // close��"+" open��"-"�́@�{�^���ł��B
    private JPanel topPanel; /// driver�������ɂȂ��āA�E����topLabel�ł��B
    
    /**
     * �I�[�v�����ꂽ�A�R�[�f�B���O�p�l���𐶐������܂��B
     * @param top �A�R�[�f�B�I���̃p�l��
     */
    public AccordionPanel(String top) {
        this(top,true);
    }
    
    public AccordionPanel(String top, boolean isClose) {
        super();
        this.top = top;
        this.topLabel = new JLabel(top);
        this.topLabel.setOpaque(true);
        this.close = isClose;
        this.driver = new JButton((close)?"+":"-");
        driver.addActionListener(this);
        this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        this.topPanel = new JPanel();
        topPanel.setBackground(Color.WHITE);
        driver.setBorder(null);
        topPanel.setBorder(null);
        topLabel.setBackground(Color.WHITE);
        driver.setBackground(Color.WHITE);
        topPanel.setLayout(new BorderLayout());
        topPanel.add(driver,BorderLayout.WEST);
        topPanel.add(topLabel, BorderLayout.CENTER);
        topLabel.addMouseListener(this);
        super.add(topPanel);
    }
    
    public JButton getDriver() {
        return driver;
    }
    public void setDriver(JButton driver) {
        this.driver = driver;
    }
    public JLabel getTopLabel() {
        return topLabel;
    }
    public void setTopLabel(JLabel topLabel) {
        this.topLabel = topLabel;
    }
    public JPanel getTopPanel() {
        return topPanel;
    }
    public void setTopPanel(JPanel topPanel) {
        this.topPanel = topPanel;
    }
    public void mouseClicked(MouseEvent e) {}

    public void mousePressed(MouseEvent e) {
        if(e.getSource().equals(topLabel)) {
            this.close = ! close;
            this.driver.setText((close)?"+":"-");
            if(close) {
                doClose();
            } else {
                doOpen();
            }
        }
    }
    
    private void doClose() {
        for(int i = 1; i < getComponentCount(); i++){ 
            getComponent(i).setVisible(false);
        }
        validate();
    }
    
    private void doOpen() {
        for(int i = 1; i < getComponentCount(); i++){ 
            getComponent(i).setVisible(true);
        }        
        validate();
    }
    public void mouseReleased(MouseEvent e) {}

    public void mouseEntered(MouseEvent e) {}

    public void mouseExited(MouseEvent e) {}

    public void actionPerformed(ActionEvent e) {
        if(e.getSource().equals(driver)) {
            this.close = ! close;
            this.driver.setText((close)?"+":"-");
            if(close) {
                doClose();
            } else {
                doOpen();
            }
        }
    }
}