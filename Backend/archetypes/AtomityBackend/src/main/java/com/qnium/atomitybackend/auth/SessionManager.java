/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.atomitybackend.auth;

import com.qnium.atomitybackend.auth.SessionContext;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

/**
 *
 * @author Kirill Zhukov
 */
public class SessionManager {
    
    private int _TTL;// (minutes)
    
    private class SessionRecord{
        private final SessionContext _context;
        private Date _sessionTimeStamp;

        public SessionRecord(SessionContext _context, Date _sessionTimeStamp) {
            this._context = _context;
            this._sessionTimeStamp = _sessionTimeStamp;
        }
    }
    ArrayList<SessionRecord> _sessions;

    private SessionManager() {
        _TTL = 30;
        this._sessions = new ArrayList();
    }
    
    public SessionContext createSession()
    {
      SessionContext context = new SessionContext();
      
      UUID session = UUID.randomUUID();
      context.setSessionKey(session.toString());
      
      this._sessions.add(new SessionRecord(context, new Date()));
      
      return context;
    }
    
    public void setTTL(int minutes)
    {
        this._TTL = minutes;
    }
    
    public SessionContext getSession(String key)
    {
        SessionContext sessionContext = new SessionContext();
        
        // bug
        this._sessions.removeIf( s -> s == null);
        
        //Delete old sessions
        this._sessions.removeIf( s -> ((new Date()).getTime() - s._sessionTimeStamp.getTime()) > this._TTL*1000*60);
        
        Optional<SessionRecord> session = this._sessions.stream().filter( s -> s._context.getSessionKey().equals(key)).findAny();
        if (session.isPresent())
        {
            session.get()._sessionTimeStamp = new Date();
            sessionContext = session.get()._context;
        }         
        return sessionContext;
    }
    
    
    public static SessionManager getInstance() {
        return SessionManagerHolder.INSTANCE;
    }
    
    private static class SessionManagerHolder {

        private static final SessionManager INSTANCE = new SessionManager();
    }
    
    public void removeSession(String key)
    {
        this._sessions.removeIf(s -> s._context.getSessionKey().equals(key));
    }
    
    public long getSessionsCount(long userId)
    {
        long sessionsCount = _sessions.stream()
            .filter(s -> s._context.getUserId() == userId).count();
        return sessionsCount;
    }
}
