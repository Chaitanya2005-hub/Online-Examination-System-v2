package com.stark.studentmanagement.service;

import com.stark.studentmanagement.entity.Attendance;
import com.stark.studentmanagement.entity.LiveCode;
import com.stark.studentmanagement.entity.User;
import com.stark.studentmanagement.repository.AttendanceRepository;
import com.stark.studentmanagement.repository.LiveCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class AttendanceService {
    
    @Autowired
    private AttendanceRepository attendanceRepository;
    
    @Autowired
    private LiveCodeRepository liveCodeRepository;
    
    public boolean markAttendance(Long studentId, String code) {
        Optional<LiveCode> liveCodeOpt = liveCodeRepository.findByCode(code);

        if (liveCodeOpt.isPresent()) {
            LiveCode liveCode = liveCodeOpt.get();

            // Check if code is not expired
            if (liveCode.getExpiresAt().isAfter(java.time.LocalDateTime.now())) {
                // Check if attendance already marked today
                if (attendanceRepository.findByStudentIdAndDate(studentId, LocalDate.now()).isEmpty()) {
                    Attendance attendance = new Attendance();
                    User student = new User();
                    student.setId(studentId);
                    attendance.setStudent(student);
                    attendance.setDate(LocalDate.now());
                    attendance.setStatus(Attendance.AttendanceStatus.PRESENT);
                    attendanceRepository.save(attendance);
                    return true;
                }
            }
        }
        return false;
    }

    public Attendance findByStudentIdAndDate(Long studentId, LocalDate date) {
        return attendanceRepository.findByStudentIdAndDate(studentId, date).orElse(null);
    }

    public Attendance saveAttendance(Attendance attendance) {
        return attendanceRepository.save(attendance);
    }
}
