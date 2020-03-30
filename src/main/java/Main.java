import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    private static String secret = "";
    private static String consumerSecret = "";

    private static String consumerKey = "";
    private static String token = "";

    public static void main (String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(20);

        ActiveMqProducer activeMqProducer = new ActiveMqProducer(consumerKey, token, consumerSecret, secret);
        executorService.execute(activeMqProducer);
        //multiple producer
        /*SingleMessageProducer messageProducer = new SingleMessageProducer();
        executorService.execute(messageProducer);*/

        ActiveMqConsumer activeMqConsumer = new ActiveMqConsumer(1);
        executorService.execute(activeMqConsumer);

        //multiple consumer
        /*Consumer consumer1 = new Consumer(2);
        executorService.execute(consumer1);*/
    }
}
