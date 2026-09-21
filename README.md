# Bài Tập 09 - Ví Dụ 3 (WEBPR330479)
**Trường Đại học Sư phạm Kỹ thuật TP.HCM (HCMUTE)**  
**Giảng viên:** ThS. Nguyễn Hữu Trung  
**Sinh viên:** Nguyễn Song Hoàng Phúc  
**MSSV:** 24162096  
**Lớp:** WEBPR330479  

---

## 1. Yêu cầu bài tập
Xây dựng ứng dụng hoàn chỉnh cho các bảng `Users`, `Roles` (user, admin), `OtpToken`, `Products`. Mối quan hệ 1 user - n product.
- **Authentication:** Đăng ký nhận mã OTP qua mail, xác thực OTP kích hoạt tài khoản, Đăng nhập lưu session, Quên mật khẩu gửi OTP reset mật khẩu.
- **Quản lý User:** CRUD bảng user, tìm kiếm phân trang, đếm tổng số user, đếm số product của từng user.
- **Quản lý Product:** CRUD bảng product (upload file ảnh), gán product thuộc user, tìm kiếm phân trang.

---

## 2. Công nghệ sử dụng
- Java 21, Spring Boot 3.4.3
- Spring Security 6/7 (Session, BCrypt)
- Spring Data JPA, MySQL (Database `bai9_vd3_db`)
- Thymeleaf Template Engine
- Spring Mail & OTP Service

---

## 3. Tài khoản thử nghiệm
- **Admin:** `phucadmin` / Mật khẩu: `123456` (ROLE_ADMIN)
- **User:** `vanb` / Mật khẩu: `123456` (ROLE_USER)

---

## 4. Hướng dẫn chạy
```bash
mvn clean spring-boot:run
```
Truy cập: `http://localhost:8083/`
