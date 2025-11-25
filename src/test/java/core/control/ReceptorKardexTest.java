package core.control;

import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.ws.KardexEndpoint;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.ReceptorKardex;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Permite stubs no utilizados en todos los tests (soluciona UnnecessaryStubbingException)
@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class ReceptorKardexTest {

    private static final String TEST_MESSAGE_TEXT = "{\"kardexId\": 123, \"accion\": \"ENTRADA\"}";

    @Mock
    KardexEndpoint mockKardexEndpoint;

    @Mock
    TextMessage mockTextMessage;

    // Inyección de la clase bajo prueba
    @InjectMocks
    ReceptorKardex receptorKardex;

    @BeforeEach
    void setUp() {
        // No se requiere stubbing general aquí si vamos a hacerlo en cada test.
    }

    // ----------------------------------------------------
    // 1. Caso de Éxito
    // ----------------------------------------------------

    @Test
    void testOnMessage_Success_ShouldBroadcastMessage() throws Exception {
        // Configuración específica para este test
        when(mockTextMessage.getText()).thenReturn(TEST_MESSAGE_TEXT);

        // Ejecutar el método
        receptorKardex.onMessage(mockTextMessage);

        // 🚨 CAMBIO CLAVE: Esperar 2 llamadas a getText(), una por línea del MDB.
        verify(mockTextMessage, times(2)).getText();

        // Verificación 2: Debe llamar al método de broadcast una vez con el texto
        verify(mockKardexEndpoint, times(1)).enviarMensajeBroadcast(TEST_MESSAGE_TEXT);
    }

    // ----------------------------------------------------
    // 2. Manejo de Excepciones (Bloque catch)
    // ----------------------------------------------------

    @Test
    void testOnMessage_ExceptionInBroadcast_ShouldCatchAndLog() throws Exception {
        // Configuración específica para este test
        when(mockTextMessage.getText()).thenReturn(TEST_MESSAGE_TEXT);

        // Simular que el broadcast lanza una excepción
        doThrow(new RuntimeException("Error simulado en el endpoint WS")).when(mockKardexEndpoint)
                .enviarMensajeBroadcast(anyString());

        // Se ejecuta el método. Debe manejar la excepción internamente (cubre el catch)
        assertDoesNotThrow(() -> receptorKardex.onMessage(mockTextMessage));

        // Verificación: Se debe haber llamado a getText() (2 veces) y se intentó enviar el broadcast (1 vez)
        verify(mockTextMessage, times(2)).getText();
        verify(mockKardexEndpoint, times(1)).enviarMensajeBroadcast(TEST_MESSAGE_TEXT);
    }

    // ----------------------------------------------------
    // 3. Manejo de Mensajes que No son TextMessage
    // ----------------------------------------------------

    @Test
    void testOnMessage_NonTextMessage_ShouldThrowClassCastException() {
        // Usamos un mock de Message genérico (no TextMessage)
        Message mockGenericMessage = mock(Message.class);

        // Se verifica que la excepción ClassCastException se propague (no es manejada por el MDB)
        assertThrows(ClassCastException.class, () -> receptorKardex.onMessage(mockGenericMessage));

        // Verificación: No se llama a ningún método de los mocks
        verifyNoInteractions(mockKardexEndpoint);
        verifyNoInteractions(mockTextMessage);
    }
}
