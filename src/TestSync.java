import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * CountDownLatch 当标记递减为0 后,即释放锁
 */
public class TestSync {

    volatile static int tickets=2;
    public static void main(String[] args) throws InterruptedException {

        int amount=process(1000,1500);

        System.out.println("ticket= "+amount);
    }


    public  static int process(int timeout,int sleep) {
        CountDownLatch latch = new CountDownLatch(1);
        new Thread(() -> {

            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            tickets -= 1;
            System.out.println("ticket inthread= " + tickets);
            latch.countDown();
        }).start();
        try {
            latch.await(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return tickets;
    }

}
