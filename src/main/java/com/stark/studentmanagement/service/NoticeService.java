package com.stark.studentmanagement.service;

import com.stark.studentmanagement.entity.Notice;
import com.stark.studentmanagement.repository.NoticeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoticeService {
    
    @Autowired
    private NoticeRepository noticeRepository;
    
    public List<Notice> getAllNotices() {
        return noticeRepository.findAllByOrderByPostedDateDesc();
    }
    
    public Notice saveNotice(Notice notice) {
        return noticeRepository.save(notice);
    }
    
    public void deleteNotice(Long id) {
        noticeRepository.deleteById(id);
    }
}
