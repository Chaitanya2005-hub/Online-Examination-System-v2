package com.stark.studentmanagement.service;

import com.stark.studentmanagement.entity.Fee;
import com.stark.studentmanagement.repository.FeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FeeService {
    
    @Autowired
    private FeeRepository feeRepository;
    
    public List<Fee> getAllFees() {
        return feeRepository.findAll();
    }
    
    public Fee getById(Long id) {
        return feeRepository.findById(id).orElse(null);
    }
    
    public Fee getByStudentId(Long studentId) {
        return feeRepository.findByStudentId(studentId).orElse(null);
    }
    
    public List<Fee> getByStatus(Fee.FeeStatus status) {
        return feeRepository.findByStatus(status);
    }
    
    public Fee saveFee(Fee fee) {
        return feeRepository.save(fee);
    }
    
    public Fee editFee(Long feeId, BigDecimal totalAmount, BigDecimal paidAmount, Fee.ApprovalStatus approvalStatus) {
        Optional<Fee> feeOpt = feeRepository.findById(feeId);
        if (feeOpt.isPresent()) {
            Fee fee = feeOpt.get();
            if (totalAmount != null) {
                fee.setTotalAmount(totalAmount);
            }
            if (paidAmount != null) {
                fee.setPaidAmount(paidAmount);
            }
            if (approvalStatus != null) {
                fee.setApprovalStatus(approvalStatus);
            }
            
            // Recalculate financial status based on total and paid amount
            BigDecimal total = fee.getTotalAmount() != null ? fee.getTotalAmount() : BigDecimal.ZERO;
            BigDecimal paid = fee.getPaidAmount() != null ? fee.getPaidAmount() : BigDecimal.ZERO;
            
            if (paid.compareTo(total) >= 0 && total.compareTo(BigDecimal.ZERO) > 0) {
                fee.setStatus(Fee.FeeStatus.PAID);
            } else if (paid.compareTo(BigDecimal.ZERO) > 0) {
                fee.setStatus(Fee.FeeStatus.PARTIAL);
            } else {
                fee.setStatus(Fee.FeeStatus.PENDING);
            }
            
            return feeRepository.save(fee);
        }
        return null;
    }
    
    public Fee updateApprovalStatus(Long feeId, Fee.ApprovalStatus approvalStatus) {
        Optional<Fee> feeOpt = feeRepository.findById(feeId);
        if (feeOpt.isPresent()) {
            Fee fee = feeOpt.get();
            fee.setApprovalStatus(approvalStatus);
            return feeRepository.save(fee);
        }
        return null;
    }
    
    public Fee updatePayment(Long studentId, BigDecimal amount) {
        Optional<Fee> feeOpt = feeRepository.findByStudentId(studentId);
        if (feeOpt.isPresent()) {
            Fee fee = feeOpt.get();
            BigDecimal newPaid = fee.getPaidAmount() != null ? fee.getPaidAmount().add(amount) : amount;
            fee.setPaidAmount(newPaid);
            
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

