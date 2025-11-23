package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.websocket.Session;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
public class SessionHandler implements Serializable {

    private Set<Session> sessions = new HashSet<>();

    public synchronized void addSession(Session session) {
        sessions.add(session);
        System.out.println("Nueva sesión WebSocket agregada. Total: " + sessions.size());
    }

    public synchronized void removeSession(Session session) {
        sessions.remove(session);
        System.out.println("Sesión WebSocket eliminada. Total: " + sessions.size());
    }

    public synchronized Set<Session> getSessions() {
        return new HashSet<>(sessions);
    }
}
