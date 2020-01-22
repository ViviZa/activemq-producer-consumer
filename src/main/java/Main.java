public class Main {

    public static void main (String[] args) {
        Producer producer = new Producer("consumerKeys", "token", "consumerSecret", "secret");
        producer.produceMessages();
        Consumer consumer = new Consumer();
        consumer.processMessages();
    }
}
