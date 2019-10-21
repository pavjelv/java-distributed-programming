
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Queue;
import javax.jms.Topic;
import javax.jms.Connection;
import javax.jms.Session;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;
import javax.jms.JMSException;
import javax.annotation.Resource;
import java.io.InputStreamReader;
import java.io.IOException;



public class JMSClient {
    @Resource(mappedName = "jms/Example1ConnectionFactory")
    private static ConnectionFactory connectionFactory;
    @Resource(mappedName = "jms/Example1Queue")
    private static Queue queue;

    public static void main(String[] args) {
        final int NUM_MSGS = 10;
        Connection connection = null;

        Destination dest = (Destination) queue;

        try {
            connection = connectionFactory.createConnection();

            Session session = connection.createSession(
                        false,
                        Session.AUTO_ACKNOWLEDGE);

            MessageProducer producer = session.createProducer(dest);
            TextMessage message = session.createTextMessage();


            InputStreamReader inputStreamReader = new InputStreamReader(System.in);
            char c = 'n';
            int i = 0;
            while (!((c == 'q') || (c == 'Q'))) {
                try {
                    c = (char) inputStreamReader.read();
                    message.setText("This is message " + (i + 1));
                    System.out.println("Sending message: " + message.getText());
                    producer.send(message);    
                    i++;
                } catch (IOException e) {
                    System.err.println("I/O exception: " + e.toString());
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
