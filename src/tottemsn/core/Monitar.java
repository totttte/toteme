/**
 * �쐬��: 2006/08/23
 */
package tottemsn.core;



/**
 * �f�o�b�O�p�Ď����j�^�[�ł��B
 */
public class Monitar extends Thread {
    
    
    public void run() {
/*        
 * JRE 1.4�ł́AThread�Ŏg���Ȃ����\�b�h���������̂ŁA1.4�𓮍���Ƃ��Ă邽��
 * ���̃N���X�̓f�o�b�O�ȊO�ɂ͎g�p���Ȃ�
 * 
        while(true) {
            try {
                Thread.sleep(8000);
                Map map = Thread.getAllStackTraces();
                Set key = map.keySet();
                Object[] obj = key.toArray();
                for(int i = 0; i < obj.length; i++){
                    if(((Thread)obj[i]).getState().equals(Thread.State.WAITING)) {
                        System.err.println("Thread ���ҋ@���Ă��܂��B�f�b�h���b�N�̉\������B");
                        System.err.println("Thread���");
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
