import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;

public class SingleMessageProducer implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(SingleMessageProducer.class);

    public void run() {
        try {
            // Create a ConnectionFactory
            ActiveMQConnectionFactory connectionFactory;
            connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
            // Create a Connection
            Connection connection = connectionFactory.createConnection();
            connection.start();

            // Create a Session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create the destination (Topic or Queue)
            Destination destination = session.createQueue("myQueue");

            // Create a MessageProducer from the Session to the Topic or Queue
            MessageProducer producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            for (int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // Create a messages
                String text = "{\"created_at\":\"Wed Feb 19 12:11:13 +0000 2020\",\"id_str\":\"1010\"}";
                TextMessage message = session.createTextMessage(text);
                logger.info("Sending Tweet to Queue " + message.getText());
                // Tell the producer to send the message
                producer.send(message);
            }
            // Clean up
            // session.close();
            // connection.close();

        } catch (JMSException ex) {
            ex.printStackTrace();
        }

    }
}
