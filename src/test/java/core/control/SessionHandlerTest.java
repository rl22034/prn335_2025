package core.control;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import jakarta.websocket.Session;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.SessionHandler;

import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class SessionHandlerTest {

    private SessionHandler sessionHandler;

    @BeforeEach
    void setUp() {
        // Inicializar una nueva instancia antes de cada prueba
        sessionHandler = new SessionHandler();
    }

    @Test
    void testAddAndRemoveSession() {
        // 1. Crear mocks de sesiones
        Session mockSession1 = mock(Session.class);
        Session mockSession2 = mock(Session.class);

        // Ejecución: Agregar la sesión 1 (Cubre sessions.add(session) en addSession)
        sessionHandler.addSession(mockSession1);

        // Aserción 1: Verificar que la sesión 1 fue agregada
        Set<Session> sessions = sessionHandler.getSessions();
        assertEquals(1, sessions.size(), "Debe haber una sesión después de agregar la primera.");
        assertTrue(sessions.contains(mockSession1), "El conjunto debe contener la sesión agregada.");

        // Ejecución: Agregar la sesión 2
        sessionHandler.addSession(mockSession2);

        // Aserción 2: Verificar el tamaño después de la segunda adición
        assertEquals(2, sessionHandler.getSessions().size(), "Debe haber dos sesiones.");

        // Ejecución: Remover la sesión 1 (Cubre sessions.remove(session) en removeSession)
        sessionHandler.removeSession(mockSession1);

        // Aserción 3: Verificar que la sesión 1 fue removida y la 2 permanece
        assertEquals(1, sessionHandler.getSessions().size(), "Debe quedar solo una sesión después de la remoción.");
        assertFalse(sessionHandler.getSessions().contains(mockSession1), "La sesión 1 debe haber sido removida.");
        assertTrue(sessionHandler.getSessions().contains(mockSession2), "La sesión 2 debe permanecer.");

        // Ejecución: Remover la sesión 2
        sessionHandler.removeSession(mockSession2);

        // Aserción 4: Verificar que no quedan sesiones
        assertEquals(0, sessionHandler.getSessions().size(), "No deben quedar sesiones después de remover ambas.");
    }

    @Test
    void testGetSessionsReturnsDefensiveCopy() {
        Session mockSession = mock(Session.class);
        sessionHandler.addSession(mockSession);

        // 1. Obtener la copia del Set (Cubre sessions.getSessions())
        Set<Session> sessionsCopy = sessionHandler.getSessions();

        // Aserción 1: La copia debe tener la sesión inicial
        assertEquals(1, sessionsCopy.size());

        // 2. Modificar la copia devuelta
        Session fakeSession = mock(Session.class);
        sessionsCopy.add(fakeSession);

        // Aserción 2: Verificar que la copia fue modificada
        assertEquals(2, sessionsCopy.size(), "La copia local debe tener 2 elementos.");

        // Aserción 3: Verificar que el Set interno del SessionHandler NO fue modificado
        // Esto es crucial para la cobertura, asegurando que 'return new HashSet<>(sessions);' funciona.
        assertEquals(1, sessionHandler.getSessions().size(),
                "El Set interno del handler no debe cambiar cuando se modifica la copia devuelta.");
        assertFalse(sessionHandler.getSessions().contains(fakeSession), "El handler interno no debe contener la sesión falsa.");
    }

    @Test
    void testNoMaxSessionLimitExists() {
        // La clase NO tiene límite. Probamos un valor alto para demostrar que se agregan todas.
        final int HIGH_COUNT = 150;

        for (int i = 0; i < HIGH_COUNT; i++) {
            Session mockSession = mock(Session.class);
            sessionHandler.addSession(mockSession);
        }

        // Aserción: Debe haber agregado todas las sesiones sin restricción
        assertEquals(HIGH_COUNT, sessionHandler.getSessions().size(),
                "La clase no tiene límite implementado, por lo que debe permitir agregar " + HIGH_COUNT + " sesiones.");
    }
}