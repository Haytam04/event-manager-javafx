package com.example.eventmanager.dao;

import com.example.eventmanager.entity.Event;
import com.example.eventmanager.entity.Participant;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import java.util.List;

public class ParticipantDAO {

    private SessionFactory sessionFactory;

    public ParticipantDAO() {
        try {
            Configuration config = new Configuration();
            config.configure("hibernate.cfg.xml");

            config.addAnnotatedClass(com.example.eventmanager.entity.User.class);
            config.addAnnotatedClass(com.example.eventmanager.entity.Event.class);
            config.addAnnotatedClass(com.example.eventmanager.entity.Participant.class);
            config.addAnnotatedClass(com.example.eventmanager.entity.Category.class);

            this.sessionFactory = config.buildSessionFactory();
            System.out.println("ParticipantDAO: Hibernate SessionFactory created!");

        } catch (Exception e) {
            System.err.println("Failed to create SessionFactory in ParticipantDAO: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public Event findEventById(Long eventId) {
        Session session = sessionFactory.openSession();
        try {
            return session.get(Event.class, eventId);
        } finally {
            session.close();
        }
    }

    public Participant findByEmailAndEvent(String email, Long eventId) {
        Session session = sessionFactory.openSession();
        try {
            String hql = "FROM Participant p WHERE p.email = :email AND p.event.id = :eventId";
            Query<Participant> query = session.createQuery(hql, Participant.class);
            query.setParameter("email", email);
            query.setParameter("eventId", eventId);

            List<Participant> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } finally {
            session.close();
        }
    }

    public boolean save(Participant participant) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.persist(participant); // For new entities
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            session.close();
        }
    }

    // NEW: Update method for existing participants
    public boolean update(Participant participant) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.merge(participant); // Use merge() for updating existing entities
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            session.close();
        }
    }

    // Enhanced saveOrUpdate method (can handle both insert and update)
    public boolean saveOrUpdate(Participant participant) {
        if (participant.getId() == null) {
            return save(participant);
        } else {
            return update(participant);
        }
    }

    public List<Participant> findAllByEvent(Long eventId) {
        Session session = sessionFactory.openSession();
        try {
            String hql = "FROM Participant p WHERE p.event.id = :eventId ORDER BY p.fullName";
            Query<Participant> query = session.createQuery(hql, Participant.class);
            query.setParameter("eventId", eventId);
            return query.getResultList();
        } finally {
            session.close();
        }
    }

    public List<Participant> findAll() {
        Session session = sessionFactory.openSession();
        try {
            Query<Participant> query = session.createQuery("FROM Participant p ORDER BY p.fullName", Participant.class);
            return query.getResultList();
        } finally {
            session.close();
        }
    }

    public Participant findById(Long participantId) {
        Session session = sessionFactory.openSession();
        try {
            return session.get(Participant.class, participantId);
        } finally {
            session.close();
        }
    }

    public boolean delete(Long participantId) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            Participant participant = session.get(Participant.class, participantId);
            if (participant != null) {
                session.remove(participant);
            }
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            session.close();
        }
    }

    public void close() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }
}