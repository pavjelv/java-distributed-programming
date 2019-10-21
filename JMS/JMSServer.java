

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Queue;
import javax.jms.Topic;
import javax.jms.Connection;
import javax.jms.Session;
import javax.jms.MessageConsumer;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.jms.JMSException;
import javax.annotation.Resource;


public class JMSServer {
    @Resource(mappedName = "jms/Example1ConnectionFactory")
    private static ConnectionFactory connectionFactory;
    @Resource(mappedName = "jms/Example1Queue")
    private static Queue queue;

    public static void main(String[] args) {
        String destType = null;
        Connection connection = null;
        Session session = null;
        Destination dest = (Destination) queue;;
        MessageConsumer consumer = null;
        TextMessage message = null;


        try {
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            consumer = session.createConsumer(dest);
            connection.start();

            while (true) {
                Message m = consumer.receive(1);

                if (m != null) {
                    if (m instanceof TextMessage) {
                        message = (TextMessage) m;
                        System.out.println(
                                "Reading message: " + message.getText());
                    } 
                }
            }
        } catch (JMSException e) {
            System.err.println("Exception occurred: " + e.toString());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                }
            }
        }
    }
}
