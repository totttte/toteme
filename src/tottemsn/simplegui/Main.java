/**
 * �쐬��: 2006/09/02
 */
package tottemsn.simplegui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.UIManager;

import tottemsn.core.StringUtil;


/**
 * <p>MSN���b�Z���W���[�݊��N���C�A���g���s�p�N���X�ł��B</p>
 * �f�o�b�O�p�ȈՓI�ɐݒu���Ă���smallgui�p�b�P�[�W�̒u�������ł��B
 */
public class Main extends JFrame {
    
    public static void main(String args[]) {
        try {
            if(args.length > 0) {
                if(args[0].equals("-debuc")) {
                    StringUtil.debuc = true;
                } else if(args[0].equals("-debuc:none")) {
                    StringUtil.debuc = false;
                } else {
                    StringUtil.println("usage:\njava -jar Tottemsn.jar -debuc\nor\n" +
                    		"java -jar Tottemsn.jar -debuc:none\n");
                }
            }
//            UIManager.setLookAndFeel("ch.randelshofer.quaqua.QuaquaLookAndFeel");
//            UIManager.setLookAndFeel("com.birosoft.liquid.LiquidLookAndFeel");

            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            JFrame.setDefaultLookAndFeelDecorated(true);
        } catch(Exception e) {
            e.printStackTrace();
        }
        Main main = new Main();
        main.setBounds(200,200,200,200);
        main.addWindowListener( new WindowAdapter(){
            public void windowClosing( WindowEvent we ){ 
                StringUtil.println("�I������" +  new Date().toString() );
                System.exit(0); 
            }
        });
        main.disp();
    }
    
    Main() {
        setVisible(true);
    }
    
    void disp() {
        NotePadMainWindow messeCont = new NotePadMainWindow(this);
        getContentPane().add(messeCont);
        pack();
        setSize(getSize().width * 2 , getSize().height);
    }
}