/**
 * �쐬��: 2006/08/30
 */
package tottemsn.simplegui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <p>���j���[�o�[�̊e��ݒ�����߂�N���X�ł��B</p>
 * <p>���j���[�o�[�̓��e�����߂�XML��ǂݍ��݁A���j���[���X�g�𐶐����܂��B
 * �Ȃ��AXML��DTD�́A/data/menu.dtd �ɂ���܂��B�e�v�f�A�����Ɋւ��Ă̐����͈ȉ��̒ʂ�B</p>
 * <ul>
 *  <li>name ���j���[�̖��O�ł��B()���ʂŋ��܂ꂽ�����A�܂��͍ŏ���1������
 * �������I�ɃV���[�g�J�b�g�p�L�[�Ɋ���U���܂��B</li>
 *  <li>submenu ���̃��j���[�̎q���j���[�ł��BJMenuItem�Ŏ�������܂��B</li>
 *  <li>separator ���j���[�Z�p���[�^���q���j���[�Ƃ̊Ԃɔz�u���܂��B</li>
 *  <li>submenu method ���̑����́A���\�b�h�̖��O���w�肵�܂��B���[�U�����̃��j���[��I�������Ƃ��A
 * �����̒l�̃��\�b�h���Ăяo���܂��B</li>
 *  <li>submenu enabled ���̑����́A���̃��j���[��\�����邩�ǂ������w�肵�܂��B���̑����͏ȗ�
 * ���邱�Ƃ��ł��܂��B</li>
 * </ul>
 * ���̗�ł́A�ȉ��̂悤�ȓ�������܂��B
 * <pre>
 * &lt;submenu method="save"&gt;��b���O��ۑ�&lt;/submenu&gt;
 * <pre>
 * <ol>
 *  <li>���̃N���X�̃R���X�g���N�^�ɂ���āA"��b���O��ۑ�"�Ƃ���JMenuItem���ǉ������B</li>
 *  <li>���[�U��"��b���O��ۑ�"��I������ƁAactionPerformed���\�b�h���Ăяo�����B</li>
 *  <li>actionPerformed���ŁAobj#save(menuItem)���\�b�h���Ăяo���B</li>
 * </ol>
 */
public class MenuManager implements ActionListener {

    private Object obj; //�@���[�U���{�^�����������Ƃ����s���������\�b�h��ێ�����Object�ł��B
    private JMenu[] menu;
    // �ǂ�MenuItem���ǂ̃��\�b�h�ɑΉ����邩�̃e�[�u���ł��Bkey=MenuItem,Value=���\�b�h��
    private Hashtable methodTable; 
    
    /**
     * �����Ŏw�肵���t�@�C������XML�t�@�C����ǂݍ��݁AXML�p�[�T���g���āAmenu�����������܂��B
     * @param obj ���[�U���{�^�����������Ƃ��Ɏ��s���郁�\�b�h���܂ރI�u�W�F�N�g
     * @param fileName �ǂݍ���XML�t�@�C��
     */
    public MenuManager(Object obj, String fileName) {
        this.obj = obj;
        methodTable = new Hashtable();
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(fileName);
            NodeList list = doc.getDocumentElement().getElementsByTagName("menu");
            menu = new JMenu[list.getLength()];
            for(int i = 0; i < menu.length; i++) {
                Element child = (Element)list.item(i);
                String name = child.getElementsByTagName("name").item(0).getFirstChild().getNodeValue();
                menu[i] = new JMenu(name);
                menu[i].setMnemonic(name.charAt(name.indexOf('(') + 1));
                NodeList childList = child.getChildNodes();
                for(int j = 0; j < childList.getLength(); j++) {
                    Node menuItem = childList.item(j);
                    if(menuItem.getNodeName().equals("separator")) {
                        menu[i].addSeparator();
                    } else if(menuItem.getNodeName().equals("submenu")) {
                        JMenuItem item = new JMenuItem(menuItem.getFirstChild().getNodeValue());
                        item.addActionListener(this);
                        String enable = ((Element)menuItem).getAttribute("enabled");
                        if(! enable.equals(""))
                            item.setEnabled(Boolean.getBoolean(enable));
                        methodTable.put(item, ((Element)menuItem).getAttribute("method"));
                        menu[i].add(item);
                    }
                }
            }
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * ����JMenu��Ԃ��܂��B
     */
    public JMenu[] getJMenus() {
        return menu;
    }
    
    /**
     * ����XML�œǂݍ���JMenu��o�^����JMenuBar��Ԃ��܂��B
     */
    public JMenuBar getBar() {
        JMenuBar menuBar = new JMenuBar();
        for(int i=0; i < menu.length; i++)
            menuBar.add(menu[i]);
        return menuBar;
    }
    
    public String toString() {
        String buf = getBar().toString() + "\n";
        for(int i = 0; i < menu.length; i++)
            buf += menu[i].toString() + "\n";
        return buf;
    }

    // �{�^�����I�����ꂽ�Ƃ��Ăяo�����B
    public void actionPerformed(ActionEvent e) {
        String mName = methodTable.get(e.getSource()).toString();
        try {
            Class[] cls = {JMenuItem.class};
            Object[] menuItem = {e.getSource()};
            // �����ɉ����ꂽmenuItem�����A���\�b�h�����t���N�V�������g���ČĂяo��
            obj.getClass().getDeclaredMethod(mName, cls).invoke(obj,menuItem);
        } catch(Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}