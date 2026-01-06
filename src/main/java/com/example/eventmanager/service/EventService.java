package com.example.eventmanager.service;

import com.example.eventmanager.dao.EventDAO;
import com.example.eventmanager.entity.Event;
import com.example.eventmanager.entity.User;
import com.example.eventmanager.utils.SessionManager;
import java.util.List;

public class EventService {
    private EventDAO eventDAO = new EventDAO();

    public void saveEvent(Event event) {
        // Business Logic: Link the event to the currently logged-in user
        event.setUser(SessionManager.getCurrentUser());
        eventDAO.saveOrUpdate(event);
    }

    public List<Event> getUserEvents() {
        return eventDAO.findByUser(SessionManager.getCurrentUser());
    }

    public void deleteEvent(Event event) {
        eventDAO.delete(event);
    }
}