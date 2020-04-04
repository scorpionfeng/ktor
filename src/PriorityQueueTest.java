import java.util.PriorityQueue;
import java.util.Random;

/**
 *  优先级队列
 *  容量可以大于构造参数
 *
 * 注意1：该队列是用数组实现，但是数组大小可以动态增加，容量无限。
 *
 * 注意2：队列的实现不是同步的。不是线程安全的。如果多个线程中的任意线程从结构上修改了列表， 则这些线程不应同时访问 PriorityQueue实例。保证线程安全可以使用PriorityBlockingQueue 类。
 *
 * 注意3：不允许使用 null 元素。
 *
 * 注意4：插入方法（offer()、poll()、remove() 、add() 方法）时间复杂度为O(log(n))
 */
public class PriorityQueueTest{
    public static void main(String args[]){  
        PriorityQueue<People> queue = new PriorityQueue<People>(11,
                (p1, p2) -> p2.age - p1.age);
              
        for (int i = 1; i <= 14; i++) {
            queue.add(new People("张"+ i, (new Random().nextInt(100))));
        }  
        while (!queue.isEmpty()) {  
              System.out.println(queue.poll().toString());  
        }  
    }  
}  
  
class People {   
    String name;  
    int age;  
    public People(String name, int age){  
        this.name = name;  
        this.age = age;  
    }      
    public String toString() {  
        return "姓名："+name + " 年龄：" + age;  
    }  
}