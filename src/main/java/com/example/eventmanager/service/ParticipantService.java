package com.example.eventmanager.service;

import com.example.eventmanager.dao.ParticipantDAO;
import com.example.eventmanager.entity.Event;
import com.example.eventmanager.entity.Participant;
import java.util.List;

public class ParticipantService {

    private ParticipantDAO participantDAO;

    public ParticipantService() {
        this.participantDAO = new ParticipantDAO();
    }

    public boolean add(String username, String email, Long eventId) {
        Event event = participantDAO.findEventById(eventId);

        if (event == null) {
            System.err.println("Event not found with ID: " + eventId);
            return false;
        }

        Participant existingParticipant = participantDAO.findByEmailAndEvent(email, eventId);
        if (existingParticipant != null) {
            return false;
        }

        Participant participant = new Participant();
        participant.setFullName(username);
        participant.setEmail(email);
        participant.setEvent(event);

        return participantDAO.save(participant);
    }

    public Participant getParticipantById(Long participantId) {
        return participantDAO.findById(participantId);
    }

    public boolean update(Participant participant) {
        return participantDAO.update(participant);
    }

    public boolean delete(Long participantId) {
        return participantDAO.delete(participantId);
    }

    public List<Participant> getParticipantsByEvent(Long eventId) {
        return participantDAO.findAllByEvent(eventId);
    }
}