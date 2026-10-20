#  Cinemaster - Personal Movie Tracker

Ứng dụng Android hiện đại theo dõi danh sách phim cá nhân, kết nối trực tiếp với cơ sở dữ liệu phim trực tuyến **The Movie Database (TMDB)** và lưu trữ dữ liệu ngoại tuyến qua **Room Database**.

---

##  Tính năng nổi bật

* **🎬 Giao diện chuẩn Cinematic (Jetpack Compose & Material 3):**
  * Hỗ trợ 2 chế độ hiển thị: Dạng lưới Poster rạp chiếu (Grid 2 cột) và Dạng danh sách chi tiết (List).
  * Hiệu ứng vệt sáng tải trang (Shimmer Skeleton Loader).
  * Màn hình chi tiết với hình nền mờ ảo (Immersive Backdrop) và Poster nổi đa tầng.
* **📂 Quản lý danh sách phim theo trạng thái:**
  * 3 danh mục riêng biệt: *Muốn xem*, *Đang xem*, *Đã xem* kèm số lượng phim.
  * Bộ lọc nhanh theo tên và sắp xếp theo: *Mới thêm*, *Tên A-Z*, *Điểm đánh giá*.
* **🎲 Gợi ý ngẫu nhiên:**
  * Tính năng gợi ý phim ngẫu nhiên khi người dùng phân vân chưa biết chọn phim gì.
* **⭐ Đánh giá & Ghi chú cá nhân:**
  * Chấm điểm tương tác trực tiếp qua thanh 5 sao (thang điểm 10).
  * Ghi chú cảm nhận cá nhân cho từng bộ phim đã xem.
* **🔍 Tìm kiếm dữ liệu phim TMDB:**
  * Tìm kiếm hàng ngàn bộ phim trực tuyến kèm thông tin tóm tắt và poster.
  * Kiểm tra trùng lặp thông minh để tránh thêm lại phim đã có trong danh sách.

---

##  Công nghệ sử dụng (Tech Stack)

* **Ngôn ngữ:** Kotlin
* **Giao diện:** 100% Jetpack Compose, Material 3, Navigation Compose
* **Kiến trúc:** MVVM (Model - View - ViewModel) + Repository Pattern
* **Xử lý bất đồng bộ:** Kotlin Coroutines, StateFlow, Reactive Streams
* **Lưu trữ cục bộ:** Android Room Database (KSP)
* **Kết nối mạng:** Retrofit 2 + Gson Converter (TMDB API v3)
* **Tải hình ảnh:** Coil Compose

---

##  Hướng dẫn cài đặt và chạy trên máy mới (Setup Guide)

Nếu bạn hoặc thành viên trong nhóm muốn tải project về máy để chạy:

### 1. Clone repository về máy
```bash
git clone https://github.com/0melett3-2/<tên-repo>.git
```

### 2. Mở dự án trong Android Studio
* Mở **Android Studio** -> chọn **File** -> **Open...** -> trỏ đến thư mục vừa clone.
* Đợi Android Studio tải các gói phụ thuộc qua Gradle.

### 3. Cấu hình TMDB API Key (Bắt buộc)
Do file `local.properties` chứa khóa bí mật đã được ẩn khỏi Git vì lý do bảo mật, bạn cần tạo hoặc mở file `local.properties` ở thư mục gốc của project và thêm dòng sau:

```properties
TMDB_API_KEY=your_tmdb_api_key_here
```
*(Bạn có thể lấy API Key miễn phí bằng cách đăng ký tài khoản tại [themoviedb.org](https://www.themoviedb.org/settings/api)).*

### 4. Build và Run
* Chọn thiết bị máy ảo (Emulator) hoặc điện thoại thật (bật USB Debugging).
* Nhấn nút **Run** (Shift + F10) để khởi chạy ứng dụng!
