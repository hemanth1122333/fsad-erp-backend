package com.education.erp.repository;

import com.education.erp.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySender_IdOrRecipient_IdOrderBySentAtDesc(Long senderId, Long recipientId);
}