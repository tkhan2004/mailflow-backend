
# 📧 MailFlow 

MailFlow là một hệ thống gửi và nhận email được xây dựng với mục tiêu mô phỏng và mở rộng các chức năng chính của Gmail. Ngoài các tính năng cơ bản như gửi email cá nhân hoặc nhóm, hệ thống còn tích hợp các chức năng nâng cao như mã hóa nội dung, gợi ý email người nhận, phân loại thư, hỗ trợ AI viết thư, và thông báo real-time.

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
