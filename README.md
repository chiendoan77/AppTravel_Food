# AppTravelFood

## Giới thiệu
- **Tên:** AppTravelFood
- **Mô tả ngắn:** Ứng dụng mobile Android để tìm kiếm, đánh giá và check-in các quán ăn/địa điểm du lịch. Ứng dụng dùng Firebase (Auth, Firestore, Storage, Analytics) và dịch vụ tìm kiếm địa chỉ/địa điểm bên thứ ba (SerpApi / Google Maps API).

## Yêu cầu
- Android Studio (Arctic Fox hoặc mới hơn)
- JDK 11+
- Kết nối Internet để tải dependencies và dùng API

## Clone repository
- Clone về máy:
# AppTravelFood

## Tổng quan & Lợi ích
- **Tên:** AppTravelFood
- **Mục đích:** Ứng dụng Android giúp người dùng tìm kiếm quán ăn và địa điểm du lịch, xem đánh giá, ảnh, và thực hiện check-in.
- **Lợi ích chính:**
  - Tìm kiếm địa điểm nhanh bằng dữ liệu Google/SerpApi.
  - Lưu và đồng bộ dữ liệu người dùng qua Firebase (Auth, Firestore, Storage).
  - Giao diện mobile được tối ưu cho trải nghiệm tìm-quan ăn nhanh.

## Yêu cầu môi trường
- Android Studio (2021.1 / Arctic Fox hoặc mới hơn)
- JDK 11+
- Kết nối Internet để tải dependencies và gọi API

## Bắt đầu — Clone project
1. Clone repository về máy:

```bash
git clone <https://github.com/chiendoan77/AppTravel_Food.git>
cd AppTravel_Food
```

2. Mở project bằng Android Studio: `File -> Open` và chọn thư mục `AppTravel_Food`.

## Tập trung: Scripts & thao tác tiện lợi
Các script đã có sẵn để giúp thiết lập nhanh:

- `scripts/setup-local.sh` (bash): sao chép `local.properties.example` → `local.properties`.
- `scripts/setup-local.ps1` (PowerShell): tương đương cho Windows.

Sử dụng:

Linux / macOS:
```bash
chmod +x scripts/setup-local.sh
./scripts/setup-local.sh
# Ghi đè nếu muốn
./scripts/setup-local.sh -f
```

Windows PowerShell:
```powershell
.\scripts\setup-local.ps1
# Ghi đè nếu muốn
.\scripts\setup-local.ps1 -Force
```

Hoặc tạo file `local.properties` thủ công bằng lệnh terminal. Lệnh này sẽ tạo file mẫu sẵn, bạn chỉ cần mở file `local.properties` và điền `SERP_API_KEY` sau.

Linux / macOS:
```bash
cat > local.properties <<'EOF'
# AppTravelFood local.properties
# Do not commit this file.
SERP_API_KEY=

# Optional Android SDK/NDK paths (uncomment if needed):
# sdk.dir=/path/to/Android/Sdk
# ndk.dir=/path/to/Android/Sdk/ndk/<version>
EOF
```

Windows PowerShell:
```powershell
@"
# AppTravelFood local.properties
# Do not commit this file.
SERP_API_KEY=

# Optional Android SDK/NDK paths (uncomment if needed):
# sdk.dir=C:\Users\YourName\AppData\Local\Android\Sdk
# ndk.dir=C:\Users\YourName\AppData\Local\Android\Sdk\ndk\<version>
"@ | Set-Content -Path local.properties -Encoding UTF8
```

Sau khi chạy lệnh, mở file `local.properties` và nhập giá trị `SERP_API_KEY` vào.

## Thiết lập API keys (chi tiết)
LUU Ý: KHÔNG commit `local.properties` hoặc `app/google-services.json` có chứa key vào repository công khai.

1) SerpApi (dùng cho tìm kiếm địa điểm và geocoding)
 - Đăng ký tài khoản tại: https://serpapi.com/
 - Tạo API key trong dashboard của SerpApi.
 - Cách cung cấp key cho project (chọn 1):
   - Thêm vào `local.properties` (gốc project):

```
SERP_API_KEY=your_serp_api_key_here
```

   - Hoặc đặt biến môi trường `SERP_API_KEY` (nên dùng cho CI/CD):

Windows PowerShell (phiên hiện tại):
```powershell
$env:SERP_API_KEY = "your_serp_api_key_here"
```
Windows persistent:
```powershell
setx SERP_API_KEY "your_serp_api_key_here"
```
Linux/macOS:
```bash
export SERP_API_KEY=your_serp_api_key_here
```

Project đã cấu hình `app/build.gradle.kts` để đọc `SERP_API_KEY` từ môi trường hoặc `local.properties` và xuất ra `BuildConfig.SERP_API_KEY` — mã nguồn lấy key qua `AppConstant.API_KEY` (đã chuyển sang `BuildConfig`).

2) Google Maps / Places & OAuth (Firebase)
 - Truy cập Google Cloud Console và tạo project (hoặc dùng project Firebase): https://console.cloud.google.com/
 - Bật APIs cần thiết: "Maps SDK for Android", "Places API" (nếu dùng).
 - Tạo API Key cho Android (giới hạn theo package name và SHA-1 để bảo mật).
 - Để sử dụng Firebase Authentication / Analytics v.v., vào Firebase Console https://console.firebase.google.com/:
   1. Tạo project (hoặc dùng project hiện có).
   2. Thêm ứng dụng Android với `applicationId` = `com.example.apptravelfood`.
   3. Tải về file `google-services.json` và đặt vào thư mục `app/`.
 - **Quan trọng:** file `app/google-services.json` trong repo hiện có chứa OAuth client mẫu/giả — bạn phải tạo OAuth client ID / cấu hình chính xác trong Firebase/Google Cloud và tải file `google-services.json` chính thức thay thế. Không dùng các giá trị mẫu công khai.

3) Cập nhật `AndroidManifest.xml` (nếu cần cho Google Maps):

```xml
<application ...>
    <meta-data
        android:name="com.google.android.maps.v2.API_KEY"
        android:value="YOUR_GOOGLE_MAPS_API_KEY"/>
</application>
```

## Cấu hình local (từ file mẫu)
 - File mẫu: `local.properties.example` (gốc project). Sao chép thành `local.properties` hoặc dùng script `setup-local`.
 - Các thuộc tính hiện được sử dụng trong project:
   - `SERP_API_KEY`: khoá SerpApi phục vụ tìm kiếm địa điểm và geocoding.
   - `sdk.dir`: đường dẫn SDK Android (nếu cần, do Android Studio tạo tự động).
   - `ndk.dir`: đường dẫn Android NDK (nếu cần).
   - `GOOGLE_SERVICES_JSON` (không bắt buộc): tham chiếu đường dẫn tới `app/google-services.json` nếu bạn muốn quản lý nó cục bộ.

## Build & Run
1. Đảm bảo `SERP_API_KEY` có trong `local.properties` hoặc biến môi trường.
2. Đảm bảo bạn đã đặt `app/google-services.json` chính thức vào `app/`.
3. Build và chạy:

Android Studio: `Run` trên thiết bị hoặc emulator.
Hoặc terminal (Windows):
```powershell
cd AppTravel_Food
.\gradlew.bat assembleDebug
```

## Bảo mật & làm sạch lịch sử git
- Không commit `local.properties` hoặc `google-services.json` chứa key.
- Nếu đã từng commit key vào repo, key vẫn tồn trong lịch sử git — bạn nên loại bỏ bằng cách dùng `git filter-repo` hoặc `BFG Repo Cleaner`. Tôi có thể hướng dẫn từng bước nếu cần.

## Kiểm tra nhanh
- Kiểm tra `BuildConfig.SERP_API_KEY` trong mã: `AppConstant.API_KEY` lấy giá trị từ `BuildConfig`.
- Nếu build lỗi do key rỗng, kiểm tra `local.properties` hoặc biến môi trường.

## Tài nguyên & Hỗ trợ
- SerpApi docs: https://serpapi.com/
- Google Maps Platform: https://developers.google.com/maps
- Firebase: https://firebase.google.com/

---

Nếu bạn muốn, tôi có thể tiếp tục và:
- Thêm kiểm tra gradle để fail build khi `SERP_API_KEY` trống (tốt cho CI),
- Viết hướng dẫn chi tiết từng bước tạo OAuth client và tải `google-services.json`,
- Hướng dẫn xóa key khỏi lịch sử git (có ví dụ `git filter-repo` / BFG).

Chọn một nhiệm vụ để tôi thực hiện tiếp.

```xml
<application ...>
    <meta-data
        android:name="com.google.android.maps.v2.API_KEY"
        android:value="YOUR_GOOGLE_MAPS_API_KEY"/>
</application>
```

## Build & Run
- Android Studio sẽ tự download Gradle và dependencies.
- Chạy app trên thiết bị thật hoặc máy ảo.

## Vị trí các tệp quan trọng
- `app/google-services.json` — cấu hình Firebase.
- `app/src/main/java/com/example/apptravelfood/core/constant/AppConstant.kt` — nơi chứa `API_KEY` (SerpApi).
- `app/build.gradle.kts` — các dependencies chính (Firebase, Play Services).

## Ghi chú bảo mật
- Không push `google-services.json` hoặc các khóa vào repository công khai.
- Sử dụng biến môi trường hoặc secret management cho CI/CD.

Nếu bạn muốn, tôi có thể:
- Thêm một mẫu `.env.example` và cập nhật code để đọc key từ biến môi trường gradle.
- Viết script nhỏ để tự động chèn `google-services.json` từ một vị trí an toàn.

**Những việc cần làm (Tasks)**
- **Tạo/ cấu hình Firebase project:** tạo project trên Firebase Console, thêm Android app với `applicationId` là `com.example.apptravelfood`, rồi tải `google-services.json` và đặt vào `app/`.
- **Tạo OAuth client mới:** file `app/google-services.json` hiện có dữ liệu OAuth client mẫu/giả — bạn phải tạo OAuth client ID đúng cho project của bạn (Google Cloud Console / Firebase Authentication) và tải lại `google-services.json` từ Firebase để thay thế. Không dùng các giá trị mẫu công khai.
- **Tạo SerpApi key:** đăng ký trên https://serpapi.com/, lấy `SERP_API_KEY` và đặt vào `local.properties` hoặc biến môi trường.
- **(Nếu dùng) Tạo Google Maps/Places API key:** trong Google Cloud Console bật "Maps SDK for Android" và "Places API", tạo API key và lưu an toàn (ví dụ qua `local.properties` hoặc biến môi trường). Nếu bạn thêm key vào `AndroidManifest.xml`, lưu ý không commit.
- **Kiểm tra lịch sử git:** nếu trước đây bạn đã commit API keys, hãy xoá khỏi lịch sử git (tôi có thể hướng dẫn dùng `git filter-repo` hoặc BFG).

**Việc phải làm ngay (Must do now)**
- Sao chép `local.properties.example` thành `local.properties` và điền `SERP_API_KEY` trước khi build.
- Đặt file `google-services.json` đúng từ Firebase vào `app/` (không commit).
- Nếu file `app/google-services.json` trong repo chứa client id mẫu/giả, thay thế bằng file chính thức bạn tải xuống.

Nếu bạn muốn, tôi sẽ hướng dẫn chi tiết cách xoá API key đã commit khỏi lịch sử git, hoặc tự động hóa việc lưu và thay thế `google-services.json` trong pipeline CI. Chọn tác vụ bạn muốn làm tiếp.

