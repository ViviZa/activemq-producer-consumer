import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Consumer implements Runnable {

    private int id;

    public Consumer(int id) {
        this.id = id;
    }

    private ObjectMapper mapper = new ObjectMapper();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS");
    private DatabaseConnector databaseConnector = new DatabaseConnector();
    private int messageRestriction = 0;
    private Logger logger = LoggerFactory.getLogger(Consumer.class);

    public void run() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        Connection connection = null;
        try {
            connection = connectionFactory.createConnection();
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue("myQueue");
            MessageConsumer consumer = session.createConsumer(destination);

            while (messageRestriction < 100) {
                messageRestriction++;
                Message message = consumer.receive(2000);

                if (message instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message;
                    String text = textMessage.getText();
                    try {
                        Tweet tweet = mapper.readValue(text, Tweet.class);
                        Date now = new Date();
                        tweet.setConsumed_at(dateFormat.format(now));

                        logger.info("Consumer " + id + " " + text);
                        writeTweetIntoDb(tweet);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            logger.debug("Shutting down");
            consumer.close();
            session.close();
            connection.close();

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private void writeTweetIntoDb(Tweet tweet){
        databaseConnector.insert(tweet.getId(), tweet.getCreated_at(), tweet.getConsumed_at(), "ActiveMq");
    }

}
