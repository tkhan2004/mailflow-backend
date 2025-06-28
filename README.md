
# 📧 MailFlow - Smart Internal Mail System with AI Assistant

MailFlow là hệ thống gửi/nhận email nội bộ hỗ trợ nhóm hoặc cá nhân, có tích hợp AI để hỗ trợ người dùng soạn thư chuyên nghiệp. Dự án được phát triển với mục tiêu nâng cao hiệu suất giao tiếp nội bộ và trải nghiệm người dùng nhờ các tính năng thông minh như real-time notification và gợi ý soạn thư.

---

## 🚀 Features

- ✉️ Soạn và gửi thư cá nhân hoặc theo nhóm
- 🧠 Tích hợp AI (Gemini) để gợi ý nội dung email từ prompt người dùng
- 🔒 JWT-based authentication & phân quyền người dùng
- 📎 Gửi kèm file và lưu trữ trên Cloudinary
- 🔁 Threaded email (hội thoại)
- 👀 Quản lý trạng thái đọc/chưa đọc/spam
- 🧑‍💻 Realtime notification với WebSocket
- 🔍 Gợi ý email người nhận theo input

---

## 🛠 Tech Stack

- Java 17
- Spring Boot
- MySQL (Railway)
- JWT (Spring Security)
- Cloudinary (upload file)
- Google Gemini API
- WebSocket (STOMP)
- Swagger UI (OpenAPI)
- Render (deploy backend)

---

## 🔗 API Documentation

> ✅ Swagger UI (triển khai trên Render):  
📎 [https://mailflow-backend-mj3r.onrender.com/swagger-ui/index.html) 

---

## 🧪 Hướng Dẫn Chạy Local

```bash
# 1. Clone repo
git clone https://github.com/your-username/mailflow.git
cd mailflow

# 2. Cấu hình file application.yml
# → Bạn có thể copy từ application-sample.yml

# 3. Chạy bằng Maven
./mvnw spring-boot:run
```

> **Note:** Hãy chắc chắn rằng bạn có:
> - MySQL đang chạy và thông tin kết nối chính xác.
> - Cloudinary API key và Gemini API key được cấu hình trong biến môi trường hoặc `application.yml`.

---

## 📁 Cấu Trúc Thư Mục

```bash
src/
├── controller/           # REST API controllers
├── service/              # Service logic
├── entity/               # JPA Entities (Users, Mails, Threads, Attachments...)
├── dto/                  # Data Transfer Objects
├── repository/           # JPA Repositories
├── security/             # JWT config & filter
├── config/               # Config jwt & Websocket
```

---

## 🎯 Mở Rộng Tương Lai

- Thêm AI chat real-time (giả lập trợ lý email)
- Gợi ý trả lời (auto reply suggestion)
- Hỗ trợ Markdown hoặc HTML template cho email
- Lưu lịch sử AI chat và feedback từ người dùng
- Hệ thống phân loại thư bằng AI

---

## 👨‍💻 Author

Nguyễn Thanh Khang – Backend Developer  
📫 Email: nguyenthanhkhang.dev@gmail.com

---
