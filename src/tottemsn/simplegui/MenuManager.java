/**
 * 作成日: 2006/08/30
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
 * <p>メニューバーの各種設定を決めるクラスです。</p>
 * <p>メニューバーの内容を決めたXMLを読み込み、メニューリストを生成します。
 * なお、XMLのDTDは、/data/menu.dtd にあります。各要素、属性に関しての説明は以下の通り。</p>
 * <ul>
 *  <li>name メニューの名前です。()括弧で挟まれた文字、または最初の1文字目
 * が自動的にショートカット用キーに割り振られます。</li>
 *  <li>submenu このメニューの子メニューです。JMenuItemで実装されます。</li>
 *  <li>separator メニューセパレータを子メニューとの間に配置します。</li>
 *  <li>submenu method この属性は、メソッドの名前を指定します。ユーザがこのメニューを選択したとき、
 * 属性の値のメソッドを呼び出します。</li>
 *  <li>submenu enabled この属性は、そのメニューを表示するかどうかを指定します。この属性は省略
 * することができます。</li>
 * </ul>
 * 次の例では、以下のような動作をします。
 * <pre>
 * &lt;submenu method="save"&gt;会話ログを保存&lt;/submenu&gt;
 * <pre>
 * <ol>
 *  <li>このクラスのコンストラクタによって、"会話ログを保存"というJMenuItemが追加される。</li>
 *  <li>ユーザが"会話ログを保存"を選択すると、actionPerformedメソッドが呼び出される。</li>
 *  <li>actionPerformed内で、obj#save(menuItem)メソッドを呼び出す。</li>
 * </ol>
 */
public class MenuManager implements ActionListener {

    private Object obj; //　ユーザがボタンを押したとき実行したいメソッドを保持するObjectです。
    private JMenu[] menu;
    // どのMenuItemがどのメソッドに対応するかのテーブルです。key=MenuItem,Value=メソッド名
    private Hashtable methodTable; 
    
    /**
     * 引数で指定したファイル名のXMLファイルを読み込み、XMLパーサを使って、menuを初期化します。
     * @param obj ユーザがボタンを押したときに実行するメソッドを含むオブジェクト
     * @param fileName 読み込むXMLファイル
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
     * このJMenuを返します。
     */
    public JMenu[] getJMenus() {
        return menu;
    }
    
    /**
     * このXMLで読み込んだJMenuを登録したJMenuBarを返します。
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

    // ボタンが選択されたとき呼び出される。
    public void actionPerformed(ActionEvent e) {
        String mName = methodTable.get(e.getSource()).toString();
        try {
            Class[] cls = {JMenuItem.class};
            Object[] menuItem = {e.getSource()};
            // 引数に押されたmenuItemを持つ、メソッドをリフレクションを使って呼び出す
            obj.getClass().getDeclaredMethod(mName, cls).invoke(obj,menuItem);
        } catch(Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}