import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.io.IOException;
import java.util.Date;

public class Consumer implements Runnable {

    private Logger logger = LoggerFactory.getLogger(Consumer.class);
    private ObjectMapper mapper = new ObjectMapper();
    private int messageRestriction = 0;

    public void run() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        Connection connection = null;
        try {
            connection = connectionFactory.createConnection();
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue("myQueue");
            MessageConsumer consumer = session.createConsumer(destination);

            while (messageRestriction < 20) {
                messageRestriction++;
                Message message = consumer.receive(2000);

                if (message instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message;
                    String text = textMessage.getText();
                    try {
                        Tweet tweet = mapper.readValue(text, Tweet.class);
                        tweet.setConsumed_at(new Date().toString());
                        // TODO do something with the data
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    logger.info("Received text: " + text);
                }
            }

            logger.debug("Shutting down Consumer");
            consumer.close();
            session.close();
            connection.close();

        } catch (JMSException e) {
            e.printStackTrace();
        }

    }

}
