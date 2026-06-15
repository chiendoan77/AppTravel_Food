# UI DESIGN SKILL - GREEN TRAVEL & FOOD APP

## Mục tiêu

Thiết kế lại UI/UX cho ứng dụng Travel & Food.

QUAN TRỌNG:

* CHỈ được chỉnh sửa giao diện.
* KHÔNG được thay đổi ViewModel.
* KHÔNG được thay đổi Repository.
* KHÔNG được thay đổi API.
* KHÔNG được thay đổi Navigation.
* KHÔNG được thay đổi Business Logic.
* KHÔNG được thay đổi Database.
* KHÔNG được đổi tên biến.
* KHÔNG được sửa chức năng đang hoạt động.

Chỉ được:

* Thay đổi màu sắc.
* Thay đổi khoảng cách.
* Thay đổi typography.
* Thay đổi shape.
* Thay đổi card.
* Thay đổi icon.
* Thay đổi hiệu ứng.
* Thay đổi bố cục hiển thị.

---

# Design System

## Phong cách

Ứng dụng du lịch và ẩm thực hiện đại.

Từ khóa:

* Fresh
* Clean
* Friendly
* Natural
* Premium
* Easy To Read

---

## Quy tắc màu sắc

### Tỷ lệ màu

* 90% màu dịu
* 10% màu nhấn

### Primary

Xanh lá cây:

#2E7D32

### Secondary

#4CAF50

### Accent

#66BB6A

### Background

#F7FAF7

### Surface

#FFFFFF

### Text Primary

#1A1A1A

### Text Secondary

#666666

### Divider

#EAEAEA

### Rating Star

#FFC107

### Error

#D32F2F

---

# Typography

## Font

Ưu tiên:

* Inter
* Roboto

## Quy tắc

Tiêu đề:

* Bold
* Rõ ràng

Nội dung:

* Regular

Không dùng:

* Chữ quá nhỏ
* Chữ mờ
* Chữ khó đọc

Tất cả text phải có độ tương phản cao.

---

# Hình ảnh

## Quy tắc đặc biệt

Mọi hình ảnh:

Bo góc:

* Góc trên phải: 24dp
* Góc dưới trái: 24dp

Hai góc còn lại:

* 8dp

Ví dụ:

TopLeft = 8dp
TopRight = 24dp
BottomLeft = 24dp
BottomRight = 8dp

---

# Card Design

## Card

Bo góc:

16dp

Shadow:

nhẹ

Elevation:

6dp

Padding:

12dp - 16dp

---

# Floating Bottom Bar

Component:

floatbottom

## Yêu cầu

Thanh bottom phải nổi bật.

Có chiều sâu.

Không phẳng.

Thiết kế dạng floating.

### Chi tiết

* Nổi lên khỏi màn hình
* Shadow lớn hơn card thường
* Bo góc 24dp
* Cách mép dưới 12dp
* Có hiệu ứng chiều sâu

Ví dụ cảm giác:

Google Maps
Airbnb
Grab

### Item

Icon:

24dp

Label:

12-13sp

Icon active:

màu xanh lá

Icon inactive:

màu xám dịu

---

# Home Screen

Màn hình:

home

## Layout

Dùng card ngang.

Ảnh bên trái.

Thông tin bên phải.

KHÔNG đặt ảnh phía trên.

### Card địa điểm

Bên trái:

* ảnh

Bên phải:

* tên địa điểm
* rating
* địa chỉ
* mô tả ngắn

### Rating

* icon sao nhỏ gọn
* đặt cạnh số rating

### CTA

* Lưu
* Xem chi tiết

Thiết kế gọn.

---

# Store Detail Screen

Màn hình:

storedetail

## Layout

Ảnh một bên.

Thông tin một bên.

Không dùng banner quá cao.

### Nội dung

Tên quán

Rating

Địa chỉ

Mô tả

Nút hành động

### Review

Review nằm dưới.

Card review riêng.

Khoảng cách thoáng.

---

# PlaceItem Component

Component:

placeitem

## Layout bắt buộc

Ảnh trái

Thông tin phải

Không dùng card dọc.

### Hiển thị

Ảnh:

35%

Thông tin:

65%

### Thông tin

Tên

Rating

Khoảng cách

Mô tả ngắn

### CTA

Nút:

* Xem
* Lưu

Nhỏ gọn.

---

# Review Component

Component:

review

## Thiết kế

Avatar nhỏ

Tên người dùng

Rating

Nội dung review

Ngày đánh giá

### Rating

Sao màu vàng

Canh hàng ngang

---

# Profile Screen

Màn hình:

profile

## Thiết kế

Header đẹp

Avatar lớn

Thông tin cá nhân

Menu dạng card

### Card menu

* Thông tin cá nhân
* Điều khoản
* Lịch sử
* Đăng xuất

---

# Profile Detail Screen

Màn hình:

profiledetail

## Thiết kế

Form hiện đại

Input bo góc

Padding rộng

Avatar ở trên

Nút lưu nổi bật

---

# Term Screen

Màn hình:

term

## Thiết kế

Dễ đọc

Khoảng cách lớn

Tiêu đề nổi bật

Không dùng text sát mép

---

# Checkin Screen

Màn hình:

checkin

## Thiết kế

Hiện đại

Có card checkin

Có hình ảnh

Có trạng thái checkin

---

# History Screen

Màn hình:

history

## Thiết kế

Danh sách card

Timeline nhẹ

Dễ xem lịch sử

---

# Add Food Screen

Màn hình:

addfood

## Thiết kế

Form nhập liệu

Upload ảnh nổi bật

Input rõ ràng

Button lưu nổi bật

---

# Add Store Screen

Màn hình:

addstore

## Thiết kế

Form hiện đại

Upload ảnh

Chọn vị trí

Các trường nhập liệu rõ ràng

---

# UX Rules

## Khoảng cách

8dp

12dp

16dp

24dp

Chỉ sử dụng các spacing này.

---

## Button

Primary:

xanh lá

Secondary:

viền xanh lá

Danger:

đỏ

---

## Icon

Material Design Icons

Không dùng icon phức tạp.

---

## Animation

Chỉ animation nhẹ.

Không dùng animation gây rối.

---

# Accessibility

Bắt buộc:

* Chữ luôn rõ ràng.
* Độ tương phản cao.
* Không dùng màu khó đọc.
* Không dùng chữ đè lên ảnh.
* Mọi nút đều dễ nhận biết.

---

# Kết quả mong muốn

Ứng dụng có cảm giác:

* Chuyên nghiệp
* Hiện đại
* Du lịch
* Ẩm thực
* Dễ sử dụng
* Màu xanh lá chủ đạo
* Thanh Bottom Navigation nổi bật có chiều sâu
* Hình ảnh bo góc đặc trưng (góc trên phải + góc dưới trái)
* Home, StoreDetail và PlaceItem dùng layout ảnh một bên - nội dung một bên
* Không thay đổi bất kỳ logic nào
* Chỉ nâng cấp UI/UX
# Food List Design Rule

Áp dụng cho:

* Home Screen
* Store Detail Screen
* Các màn hình hiển thị danh sách món ăn

---

## Layout bắt buộc

Danh sách món ăn KHÔNG hiển thị dạng dọc.

Sử dụng:

Horizontal Carousel

hoặc

LazyRow

hoặc

Horizontal Scroll List

---

## Mục tiêu

Người dùng có thể vuốt ngang để xem món ăn.

Giúp tiết kiệm không gian màn hình.

Tạo cảm giác hiện đại giống:

* GrabFood
* ShopeeFood
* Traveloka Eats
* Airbnb Experience

---

## Food Card

Mỗi món ăn là một card riêng.

### Kích thước

Chiều rộng:

220dp - 280dp

Chiều cao:

120dp - 180dp

---

## Bố cục

Bên trái:

Ảnh món ăn

Bên phải:

* Tên món
* Giá
* Rating
* Mô tả ngắn

### Tỷ lệ

Ảnh:

40%

Thông tin:

60%

---

## Hình ảnh

Bo góc đặc trưng:

TopLeft = 8dp

TopRight = 24dp

BottomLeft = 24dp

BottomRight = 8dp

---

## Nội dung

Hiển thị:

* Tên món
* Giá
* Rating
* Mô tả ngắn

Không hiển thị quá nhiều text.

Tên món tối đa:

2 dòng

Mô tả tối đa:

2 dòng

---

## Rating

Hiển thị:

⭐ 4.8

Theo hàng ngang.

Màu sao:

#FFC107

---

## Giá

Nổi bật hơn mô tả.

Font:

SemiBold

Màu:

Primary Green

---

## Nút thêm món

Thiết kế nhỏ gọn.

Dạng:

* Thêm

hoặc

Icon Add

Nằm góc phải dưới card.

Không chiếm nhiều diện tích.

---

## Khoảng cách

Card cách nhau:

12dp

Padding ngoài:

16dp

---

## Hiệu ứng

Card có:

* Elevation nhẹ
* Shadow mềm
* Scale nhẹ khi click

Không dùng animation phức tạp.

---

## Header danh sách món ăn

Ví dụ:

Món nổi bật

Món được đánh giá cao

Món mới

Có nút:

Xem tất cả

ở bên phải tiêu đề.

---

## Store Detail

Trong Store Detail:

Danh sách món ăn phải là Horizontal Carousel.

Không dùng RecyclerView dọc cho món ăn.

Người dùng vuốt ngang để xem món.

---

## Home Screen

Home hiển thị:

### Địa điểm nổi bật

Danh sách dọc

(Ảnh trái - Nội dung phải)

### Món ăn nổi bật

Danh sách ngang

(Horizontal Carousel)

### Quán ăn nổi bật

Danh sách dọc

(Ảnh trái - Nội dung phải)

---

## UI Goal

Tạo cảm giác giống ứng dụng:

* GrabFood
* ShopeeFood
* Traveloka
* Google Travel

Hiện đại

Dễ nhìn

Tiết kiệm không gian

Ưu tiên thao tác bằng một tay

Không thay đổi bất kỳ logic hiện tại nào.
