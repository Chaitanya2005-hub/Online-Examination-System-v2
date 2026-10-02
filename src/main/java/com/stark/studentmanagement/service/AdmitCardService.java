package com.stark.studentmanagement.service;

import com.stark.studentmanagement.entity.AdmitCard;
import com.stark.studentmanagement.repository.AdmitCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdmitCardService {
    
    @Autowired
    private AdmitCardRepository admitCardRepository;
    
    public AdmitCard getByStudentId(Long studentId) {
        return admitCardRepository.findByStudentId(studentId).orElse(null);
    }
    
    public AdmitCard saveAdmitCard(AdmitCard admitCard) {
        return admitCardRepository.save(admitCard);
    }
    
    public AdmitCard updateStatus(Long studentId, AdmitCard.AdmitCardStatus status) {
        Optional<AdmitCard> admitCardOpt = admitCardRepository.findByStudentId(studentId);
        if (admitCardOpt.isPresent()) {
            AdmitCard admitCard = admitCardOpt.get();
            admitCard.setStatus(status);
            return admitCardRepository.save(admitCard);
        }
        return null;
    }
}
