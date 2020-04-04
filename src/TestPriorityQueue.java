import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 队列优先级
 */
public class TestPriorityQueue {

    static Random r = new Random(47);

    public static void main(String args[]) {

        final PriorityBlockingQueue q = new PriorityBlockingQueue();

        ExecutorService se = Executors.newFixedThreadPool(20);

        final int qTime = r.nextInt(100);

//execute producer 
        se.execute(new Runnable() {
            public void run() {
                int i = 0;
                while (i<20) {
                    q.put(new PriorityEntity(r.nextInt(10), i++));  //优先级，索引
                    try {
                        TimeUnit.MILLISECONDS.sleep(qTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

//execute consumer 
        se.execute(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        int rTime = r.nextInt(500);

                        //打印进队和出队的时间可以看到队列一直在累积
                        System.out.println(qTime + "： "+rTime + " --take-- " + q.take() + " left: " + q.size() + " --     [" + q.toString() + "]");
                        try {
                            TimeUnit.MILLISECONDS.sleep(rTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

//        try {
//            TimeUnit.SECONDS.sleep(5);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        System.out.println("shutdown");
    }

}

class PriorityEntity implements Comparable<PriorityEntity> {

    private int priority;
    private int index = 0;

    public PriorityEntity(int _priority, int _index) {
        this.priority = _priority;
        this.index = _index;
    }

    public String toString() {
        return "# [index=" + index + " priority=" + priority + "]";
    }

    //数字小，优先级高
    public int compareTo(PriorityEntity o) {
        return this.priority > o.priority ? 1 : this.priority < o.priority ? -1 : 0;
    }

//数字大，优先级高 
// public int compareTo(PriorityTask o) { 
//     return this.priority < o.priority ? 1 : this.priority > o.priority ? -1 : 0;  
// } 
}