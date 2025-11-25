package core.control;

import jakarta.jms.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.NotificadorKardex;

import java.util.logging.Logger; // Mantener la importación por claridad, aunque no se mockee

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class NotificadorKardexTest {

    // Componentes a simular inyectados con @Resource
    @Mock
    private ConnectionFactory mockConnectionFactory;

    @Mock
    private Queue mockQueue;

    // Dependencias internas de JMS a simular
    @Mock
    private Connection mockConnection;

    @Mock
    private Session mockSession;

    @Mock
    private MessageProducer mockProducer;

    @Mock
    private TextMessage mockTextMessage;

    // Componente bajo prueba, donde se inyectan los mocks
    @InjectMocks
    private NotificadorKardex notificadorKardex;

    private static final String MENSAJE_PRUEBA = "Movimiento #123";

    @BeforeEach
    void setUp() throws JMSException {
        MockitoAnnotations.openMocks(this);

        // Configuración estándar para el camino feliz (Success Path)
        when(mockConnectionFactory.createConnection()).thenReturn(mockConnection);
        when(mockConnection.createSession(false, Session.AUTO_ACKNOWLEDGE)).thenReturn(mockSession);
        when(mockSession.createProducer(mockQueue)).thenReturn(mockProducer);
        when(mockSession.createTextMessage(anyString())).thenReturn(mockTextMessage);

        // No se intenta mockear el Logger estático.
    }

    // -------------------------------------------------------------------------
    // Prueba 1: Camino Feliz (Happy Path) - Cobertura del bloque 'try'
    // -------------------------------------------------------------------------

    @Test
    void testNotificarCambioKardex_Success() throws Exception {
        // Act
        // Aseguramos que el método no lance excepciones inesperadas
        assertDoesNotThrow(() -> notificadorKardex.notificarCambioKardex(MENSAJE_PRUEBA));

        // Assert: Verificación de la secuencia de JMS
        verify(mockConnectionFactory, times(1)).createConnection();
        verify(mockConnection, times(1)).createSession(false, Session.AUTO_ACKNOWLEDGE);
        verify(mockSession, times(1)).createProducer(mockQueue);

        // Verificamos que se crea el mensaje (argThat verifica el inicio del string)
        verify(mockSession, times(1)).createTextMessage(argThat(s -> s.startsWith(MENSAJE_PRUEBA)));

        verify(mockProducer, times(1)).send(mockTextMessage);
        verify(mockConnection, times(1)).close();
    }

    // -------------------------------------------------------------------------
    // Prueba 2: Manejo de Errores (Error Path) - Cobertura del bloque 'catch'
    // -------------------------------------------------------------------------

    @Test
    void testNotificarCambioKardex_JMSException_LogsError() throws JMSException {
        // Arrange
        // Forzar que la creación de la conexión lance una excepción.
        // Esto garantiza la ejecución del catch.
        JMSException expectedException = new JMSException("Connection failed");
        when(mockConnectionFactory.createConnection()).thenThrow(expectedException);

        // Act
        // Aseguramos que el método maneja la excepción internamente y no la propaga.
        assertDoesNotThrow(() -> notificadorKardex.notificarCambioKardex(MENSAJE_PRUEBA));

        // Assert:

        // 1. Verificamos que se intentó crear la conexión (lo que provocó el error)
        verify(mockConnectionFactory, times(1)).createConnection();

        // 2. Verificamos que NINGUNA otra operación JMS fue intentada (el código saltó al catch)
        verify(mockConnection, never()).createSession(anyBoolean(), anyInt());
        verify(mockConnection, never()).close();

        // 3. Ya que no podemos mockear el Logger estático, la cobertura de línea
        // del 'catch' se logra con el assertDoesNotThrow y la inyección del error.
        // La ejecución del 'catch' está garantizada por el mock y el assert.
    }
}