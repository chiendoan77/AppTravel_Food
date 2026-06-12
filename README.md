# AppTravelFood

Ứng dụng Android dùng Jetpack Compose để tìm kiếm, đánh giá và check-in địa
điểm ăn uống/du lịch. Project sử dụng Firebase, SerpApi và Supabase Storage.

## Yêu cầu

- Android Studio phiên bản mới, kèm Android SDK 36
- JDK 17
- Thiết bị Android hoặc emulator từ API 30
- Tài khoản Firebase, SerpApi và Supabase

## 1. Clone và mở project

```bash
git clone https://github.com/chiendoan77/AppTravel_Food.git
cd AppTravel_Food
```

Mở thư mục project bằng Android Studio và chờ Gradle Sync hoàn tất.

## 2. Tạo `local.properties`

File này chứa đường dẫn Android SDK và các cấu hình riêng của máy. File đã
được thêm vào `.gitignore`, tuyệt đối không commit.

### Windows PowerShell

```powershell
.\scripts\setup-local.ps1
```

### macOS/Linux

```bash
chmod +x scripts/setup-local.sh
./scripts/setup-local.sh
```

Hai script sẽ sao chép `local.properties.example` thành `local.properties` và
tự tìm Android SDK từ biến môi trường hoặc vị trí cài đặt mặc định.

Nếu muốn ghi đè file hiện có:

```powershell
.\scripts\setup-local.ps1 -Force
```

```bash
./scripts/setup-local.sh -f
```

### Tìm Android SDK thủ công

Trong Android Studio, mở:

- Windows/Linux: `File > Settings > Languages & Frameworks > Android SDK`
- macOS: `Android Studio > Settings > Languages & Frameworks > Android SDK`

Đường dẫn nằm tại mục **Android SDK Location**. Thêm nó vào
`local.properties`:

```properties
# Windows: phải escape dấu : và \
sdk.dir=C\:\\Users\\YourName\\AppData\\Local\\Android\\Sdk

# macOS
# sdk.dir=/Users/YourName/Library/Android/sdk

# Linux
# sdk.dir=/home/YourName/Android/Sdk
```

Có thể kiểm tra nhanh bằng terminal:

```powershell
# Windows PowerShell
$env:ANDROID_SDK_ROOT
$env:ANDROID_HOME
Test-Path "$env:LOCALAPPDATA\Android\Sdk"
```

```bash
# macOS/Linux
echo "$ANDROID_SDK_ROOT"
echo "$ANDROID_HOME"
ls "$HOME/Library/Android/sdk" 2>/dev/null  # macOS
ls "$HOME/Android/Sdk" 2>/dev/null         # Linux
```

## 3. Thêm API configuration

Mở `local.properties` và thay các giá trị mẫu:

```properties
SERP_API_KEY=your_serp_api_key
SUPABASE_URL=https://your-project-ref.supabase.co
SUPABASE_ANON_KEY=your_supabase_publishable_or_anon_key
```

Project ưu tiên biến môi trường nếu có. Các tên biến tương ứng là
`SERP_API_KEY`, `SUPABASE_URL` và `SUPABASE_ANON_KEY`.

Lưu ý: các giá trị được đóng gói vào APK qua `BuildConfig`, nên người có APK
vẫn có thể đọc được. Không dùng Supabase `service_role` key trong app Android.
Hãy dùng anon/publishable key và bật Row Level Security (RLS) cùng policy phù
hợp cho database/storage.

## 4. Cấu hình Firebase

1. Tạo hoặc mở project tại [Firebase Console](https://console.firebase.google.com/).
2. Thêm Android app với package `com.example.apptravelfood`.
3. Bật các dịch vụ app sử dụng như Authentication, Firestore và Analytics.
4. Tải `google-services.json`.
5. Đặt file tại `app/google-services.json`.

`app/google-services.json` đã được ignore và không được đưa lên Git. Mỗi thành
viên trong nhóm tự tải file từ Firebase Console hoặc nhận qua kênh chia sẻ bí
mật của nhóm.

Để Google Sign-In hoạt động, thêm SHA-1/SHA-256 của debug keystore vào Firebase:

```powershell
.\gradlew.bat signingReport
```

```bash
./gradlew signingReport
```

Sau đó tải lại `google-services.json`.

## 5. Build và chạy

Windows:

```powershell
.\gradlew.bat assembleDebug
```

macOS/Linux:

```bash
./gradlew assembleDebug
```

Hoặc chọn thiết bị/emulator trong Android Studio rồi nhấn **Run**.

## Bảo mật và Git

Các file/dạng file sau đã được ignore:

- `local.properties`, `.env*`, `secrets.properties`
- `google-services.json`
- `*.jks`, `*.keystore`, `keystore.properties`

Trước khi commit, kiểm tra:

```bash
git status
git check-ignore -v local.properties app/google-services.json
```

Nếu một key hoặc file bí mật từng được commit, thêm `.gitignore` không xóa nó
khỏi lịch sử. Cần thu hồi/rotate key ngay, sau đó làm sạch lịch sử bằng
`git filter-repo` hoặc BFG và yêu cầu mọi thành viên clone lại repository.
