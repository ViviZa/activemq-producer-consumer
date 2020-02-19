import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    private static String secret = "";
    private static String consumerSecret = "";

    private static String consumerKey = "";
    private static String token = "";

    public static void main (String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(20);

        Producer producer = new Producer(consumerKey, token, consumerSecret, secret);
        executorService.execute(producer);
        //multiple producer
        /*SingleMessageProducer messageProducer = new SingleMessageProducer();
        executorService.execute(messageProducer);*/

        Consumer consumer = new Consumer(1);
        executorService.execute(consumer);

        //multiple consumer
        /*Consumer consumer1 = new Consumer(2);
        executorService.execute(consumer1);*/
    }
}
