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
        Consumer consumer = new Consumer();
        executorService.execute(consumer);
    }
}
