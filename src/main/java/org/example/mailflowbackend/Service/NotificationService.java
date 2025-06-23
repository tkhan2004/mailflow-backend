package org.example.mailflowbackend.Service;

import org.example.mailflowbackend.Entity.Users;

public interface NotificationService {
    void notify(String email, String message);
}
