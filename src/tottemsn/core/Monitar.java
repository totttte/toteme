/**
 * 作成日: 2006/08/23
 */
package tottemsn.core;



/**
 * デバッグ用監視モニターです。
 */
public class Monitar extends Thread {
    
    
    public void run() {
/*        
 * JRE 1.4では、Threadで使えないメソッドがあったので、1.4を動作環境としてるため
 * このクラスはデバッグ以外には使用しない
 * 
        while(true) {
            try {
                Thread.sleep(8000);
                Map map = Thread.getAllStackTraces();
                Set key = map.keySet();
                Object[] obj = key.toArray();
                for(int i = 0; i < obj.length; i++){
                    if(((Thread)obj[i]).getState().equals(Thread.State.WAITING)) {
                        System.err.println("Thread が待機しています。デッドロックの可能性あり。");
                        System.err.println("Thread情報");
                        System.err.println(((Thread)obj[i]).toString());
                        StackTraceElement[] stacks =  ((Thread)obj[i]).getStackTrace();
                        for(int j=0; j < stacks.length; j++) {
                            System.err.println(stacks[j].toString());
                        }
                    }
                }
            } catch (InterruptedException e) {
            }
            
        }
*/
    }

}
