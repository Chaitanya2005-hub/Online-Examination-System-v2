package com.stark.studentmanagement.service;

import com.stark.studentmanagement.entity.AdmitCard;
import com.stark.studentmanagement.entity.User;
import com.stark.studentmanagement.repository.AdmitCardRepository;
import com.stark.studentmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdmitCardService {
    
    @Autowired
    private AdmitCardRepository admitCardRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public AdmitCard getByStudentId(Long studentId) {
        return admitCardRepository.findByStudentId(studentId).orElse(null);
    }
    
    public AdmitCard saveAdmitCard(AdmitCard admitCard) {
        return admitCardRepository.save(admitCard);
    }
    
    public AdmitCard updateStatus(Long studentId, AdmitCard.AdmitCardStatus status) {
        Optional<AdmitCard> admitCardOpt = admitCardRepository.findByStudentId(studentId);
        AdmitCard admitCard;
        if (admitCardOpt.isPresent()) {
            admitCard = admitCardOpt.get();
        } else {
            User student = userRepository.findById(studentId).orElse(null);
            if (student == null) {
                return null;
            }
            admitCard = new AdmitCard();
            admitCard.setStudent(student);
        }
        admitCard.setStatus(status);
        return admitCardRepository.save(admitCard);
    }
}
