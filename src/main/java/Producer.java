import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Producer {

    private final String consumerKey = "vC4bsWbRLKyRIiuubqulY6oTZ";
    private final String consumerSecret = "SjsFzeMU6XAZZWgFS9MjxDqZXG2rhHgAPQY4w7Y3VKkhYH3evt";
    private final String token = "1218470536482893824-C0lBoZSTFs9KyuoeAv77XOolSkUkl1";
    private final String secret = "6RSx33UhjSrRd3i0n1IbZYsEvi5cimHuFivkblc86e6Zf";
    private Logger logger = LoggerFactory.getLogger(Producer.class);
    private Connection connection;


    public static void main(String[] args) {
        new Producer().produceMessages();
    }

    private void produceMessages() {
        try {
            // Create a connection to queue
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
            connection = connectionFactory.createConnection();
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue("myQueue");

            //create connection to Twitter
            BlockingQueue<String> msgQueue = new LinkedBlockingQueue<String>(1000);
            Client client = createTwitterClient(msgQueue);
            client.connect();

            // Create a MessageProducer from the Session to the Topic or Queue
            MessageProducer producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            // send the message
            while (!client.isDone()) {
                String msg = null;
                try {
                    msg = msgQueue.poll(5, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    client.stop();
                }
                if (msg != null) {
                    logger.info(msg);
                    producer.send(session.createTextMessage(msg));
                }
            }

            try {
                Thread.sleep(20000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Clean up
            session.close();
            connection.close();

        } catch (JMSException ex) {
            ex.printStackTrace();
        }
    }

    private Client createTwitterClient(BlockingQueue<String> msgQueue) {
        Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
        StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();

        //set term to listen to in twitter tweets
        List<String> terms = Lists.newArrayList("Kafka");
        hosebirdEndpoint.trackTerms(terms);

        Authentication hosebirdAuth = new OAuth1(consumerKey, consumerSecret, token, secret);

        ClientBuilder builder = new ClientBuilder()
                .name("Hosebird-Client-01")
                .hosts(hosebirdHosts)
                .authentication(hosebirdAuth)
                .endpoint(hosebirdEndpoint)
                .processor(new StringDelimitedProcessor(msgQueue));

        return builder.build();
    }
}
