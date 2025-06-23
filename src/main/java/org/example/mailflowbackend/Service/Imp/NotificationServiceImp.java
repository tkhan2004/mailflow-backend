package org.example.mailflowbackend.Service.Imp;

import org.example.mailflowbackend.Service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImp implements NotificationService {
    @Autowired private SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public void notify(String email, String message) {
        simpMessagingTemplate.convertAndSendToUser(
                email, // người nhận
                "/queue/notification",  // Fe lắng nghe
                message // Nội dung thông baáo
        );
    }
}
