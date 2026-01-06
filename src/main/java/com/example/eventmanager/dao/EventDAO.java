package com.example.eventmanager.dao;

import com.example.eventmanager.entity.Event;
import com.example.eventmanager.entity.User;
import com.example.eventmanager.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class EventDAO {
    public void saveOrUpdate(Event event) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            // Use merge instead of saveOrUpdate for Hibernate 6/7
            session.merge(event);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public List<Event> findByUser(User user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Event WHERE user = :u", Event.class)
                    .setParameter("u", user)
                    .list();
        }
    }

    public void delete(Event event) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // Use session.find() instead of session.get() for Hibernate 7
            Event eventToDelete = session.find(Event.class, event.getId());

            if (eventToDelete != null) {
                session.remove(eventToDelete);
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }
}