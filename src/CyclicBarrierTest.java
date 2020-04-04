import java.util.concurrent.CyclicBarrier;

/**
 * 当所有的wait都执行到后,释放锁
 */
public class CyclicBarrierTest {
    static CyclicBarrier c = new CyclicBarrier(2);
    public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override
            public void run() {
            try {
            c.await();
            } catch (Exception e) {
            }
            System.out.println(1);
            }
        }).start();
        try {
            c.await();
        } catch (Exception e) {
        }
        System.out.println(2);
    }
}