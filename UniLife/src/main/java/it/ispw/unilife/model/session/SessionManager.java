package it.ispw.unilife.model.session;

import it.ispw.unilife.model.User;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionManager {

    private static SessionManager instance = null;
    private final Map<String, Session> activeSessions;

    private SessionManager() {
        this.activeSessions = new HashMap<>();
    }

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public synchronized String createSession(User user) {
        String token = UUID.randomUUID().toString();
        Session newSession = new Session(user);
        this.activeSessions.put(token, newSession);
        return token;
    }

    public Session getSession(String token) {
        return this.activeSessions.get(token);
    }

    public boolean sessionIsValid(String token) {
        return token != null && this.activeSessions.containsKey(token);
    }

    public synchronized void invalidateSession(String token) {
        this.activeSessions.remove(token);
    }
}