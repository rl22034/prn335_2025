package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control;

import jakarta.annotation.Resource;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.jms.*;

import java.io.Serializable;
import java.net.ConnectException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@LocalBean
public class NotificadorKardex implements Serializable {

    @Resource(lookup = "jms/jmsfactory")
    ConnectionFactory connectionFactory;

    @Resource(lookup = "jms/JmsQueue")
    Queue queue;

    public void notificarCambioKardex(String mensaje) {
        TextMessage textMessage;
        try{
            Connection cnx = connectionFactory.createConnection();
            Session session = cnx.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(queue);
            textMessage = session.createTextMessage(mensaje+System.currentTimeMillis());
            producer.send(textMessage);
            cnx.close();

        }catch (Exception e){
            Logger.getLogger(NotificadorKardex.class.getName()).log(Level.SEVERE, "Error notificando cambio en kardex con id: " + mensaje, e);
        }
    }
}
