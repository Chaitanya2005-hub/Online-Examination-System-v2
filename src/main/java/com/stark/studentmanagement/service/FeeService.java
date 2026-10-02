package com.stark.studentmanagement.service;

import com.stark.studentmanagement.entity.Fee;
import com.stark.studentmanagement.repository.FeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class FeeService {
    
    @Autowired
    private FeeRepository feeRepository;
    
    public Fee getByStudentId(Long studentId) {
        return feeRepository.findByStudentId(studentId).orElse(null);
    }
    
    public List<Fee> getByStatus(Fee.FeeStatus status) {
        return feeRepository.findByStatus(status);
    }
    
    public Fee saveFee(Fee fee) {
        return feeRepository.save(fee);
    }
    
    public Fee updatePayment(Long studentId, BigDecimal amount) {
        Optional<Fee> feeOpt = feeRepository.findByStudentId(studentId);
        if (feeOpt.isPresent()) {
            Fee fee = feeOpt.get();
            fee.setPaidAmount(fee.getPaidAmount().add(amount));
            
            if (fee.getPaidAmount().compareTo(fee.getTotalAmount()) >= 0) {
                fee.setStatus(Fee.FeeStatus.PAID);
            } else {
                fee.setStatus(Fee.FeeStatus.PARTIAL);
            }
            
            return feeRepository.save(fee);
        }
        return null;
    }
}
