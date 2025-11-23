package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control;

import jakarta.ejb.MessageDriven;
import jakarta.inject.Inject;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.ws.KardexEndpoint;

import java.util.logging.Level;
import java.util.logging.Logger;

@MessageDriven(activationConfig ={
        @jakarta.ejb.ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "jms/JmsQueue"),
        @jakarta.ejb.ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Queue"),
        @jakarta.ejb.ActivationConfigProperty(propertyName = "connectionFactoryLookup", propertyValue = "jms/jmsfactory")
} )
public class ReceptorKardex implements MessageListener {

    @Inject
    KardexEndpoint KardexEndpoint;

    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;
        try{
            System.out.println("Mensaje recibido en ReceptorKardex: " + textMessage.getText());
            KardexEndpoint.enviarMensajeBroadcast(textMessage.getText());
        }catch (Exception e){
            Logger.getLogger(NotificadorKardex.class.getName()).log(Level.SEVERE, "Error notificando cambio en kardex con id: " + message, e);
        }
    }
}
