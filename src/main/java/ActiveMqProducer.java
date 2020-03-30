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

public class ActiveMqProducer implements Runnable {

    private String consumerKey;
    private String token;
    private String consumerSecret;
    private String secret;
    private Logger logger = LoggerFactory.getLogger(ActiveMqProducer.class);

    public ActiveMqProducer(String consumerKey, String token, String consumerSecret, String secret){
        this.consumerKey = consumerKey;
        this.token = token;
        this.consumerSecret = consumerSecret;
        this.secret = secret;
    }

    public void run() {
        try {
            // Create a connection to queue
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
            Connection connection = connectionFactory.createConnection();
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue("myQueue");

            //create connection to Twitter
            BlockingQueue<String> msgQueue = new LinkedBlockingQueue<String>(1000);
            Client client = createTwitterClient(msgQueue);
            client.connect();

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
                    logger.info("Sending Tweet to Queue " + msg);
                    producer.send(session.createTextMessage(msg));
                }
            }

            logger.debug("Shutting down Producer");
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
        List<String> terms = Lists.newArrayList("bitcoin");
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
