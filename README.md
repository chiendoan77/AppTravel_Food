# AppTravelFood

Ứng dụng Android dùng Jetpack Compose để tìm kiếm, đánh giá và check-in địa
điểm ăn uống/du lịch. Project sử dụng Firebase, SerpApi, Supabase Storage và
backend PHP gửi OTP qua Resend.

## Yêu cầu

- Android Studio phiên bản mới, kèm Android SDK 36
- JDK 17
- Thiết bị Android hoặc emulator từ API 30
- Tài khoản Firebase, SerpApi, Supabase và Resend

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
OTP_BASE_URL=https://your-otp-backend.example.com/
```

Project ưu tiên biến môi trường nếu có. Các tên biến tương ứng là
`SERP_API_KEY`, `SUPABASE_URL`, `SUPABASE_ANON_KEY` và `OTP_BASE_URL`.

### 3.1. Tạo Supabase project và lấy API configuration

1. Truy cập [Supabase Dashboard](https://supabase.com/dashboard), đăng nhập và
   chọn **New project**.
2. Nhập tên project, mật khẩu database, region rồi chờ project khởi tạo xong.
3. Mở nút **Connect** của project để sao chép **Project URL** và
   **Publishable key**. Có thể xem toàn bộ key tại
   **Project Settings > API Keys**.
4. Điền hai giá trị vào `local.properties`:

```properties
SUPABASE_URL=https://your-project-ref.supabase.co
SUPABASE_ANON_KEY=sb_publishable_your_key
```

Tên `SUPABASE_ANON_KEY` được giữ theo cấu hình hiện tại của project, nhưng giá
trị khuyến nghị cho app Android là **Publishable key**. Project Supabase cũ
cũng có thể dùng legacy **anon key**. Tuyệt đối không dùng **Secret key** hoặc
legacy `service_role` key trong ứng dụng.

### 3.2. Tạo Supabase Storage

App tải ảnh lên ba bucket public có tên chính xác:

- `avatars`
- `food-stores`
- `food-items`

Trong Supabase Dashboard, mở **SQL Editor > New query**, chạy đoạn SQL sau để
tạo bucket và giới hạn file tải lên là ảnh tối đa 5 MB:

```sql
insert into storage.buckets (
  id,
  name,
  public,
  file_size_limit,
  allowed_mime_types
)
values
  ('avatars', 'avatars', true, 5242880, array['image/jpeg', 'image/png', 'image/webp']),
  ('food-stores', 'food-stores', true, 5242880, array['image/jpeg', 'image/png', 'image/webp']),
  ('food-items', 'food-items', true, 5242880, array['image/jpeg', 'image/png', 'image/webp'])
on conflict (id) do update set
  public = excluded.public,
  file_size_limit = excluded.file_size_limit,
  allowed_mime_types = excluded.allowed_mime_types;
```

Supabase Storage chặn upload mặc định nếu chưa có RLS policy. App đang đăng
nhập bằng Firebase Auth, không phải Supabase Auth, nên request Storage dùng
role `anon`. Chạy tiếp các policy sau:

```sql
create policy "App can upload images"
on storage.objects
for insert
to anon
with check (
  bucket_id in ('avatars', 'food-stores', 'food-items')
);

create policy "App can read images for upsert"
on storage.objects
for select
to anon
using (
  bucket_id in ('avatars', 'food-stores', 'food-items')
);

create policy "App can update images"
on storage.objects
for update
to anon
using (
  bucket_id in ('avatars', 'food-stores', 'food-items')
)
with check (
  bucket_id in ('avatars', 'food-stores', 'food-items')
);
```

Hai policy `SELECT` và `UPDATE` cần thiết vì code upload đang bật `upsert`.
Nếu SQL Editor báo policy đã tồn tại, mở **Storage > Policies** để xóa policy
cũ cùng tên hoặc giữ policy cũ nếu quyền tương đương.

Các policy trên giúp chạy đúng kiến trúc hiện tại, nhưng cho phép bất kỳ client
nào có publishable/anon key tải ảnh lên ba bucket. Khi triển khai production,
nên chuyển upload qua backend/Edge Function có xác thực Firebase token hoặc
tích hợp Supabase Auth để giới hạn quyền theo từng người dùng.

### 3.3. Cấu hình backend OTP và Resend

Luồng OTP của app không gọi Resend trực tiếp. Android gọi backend PHP tại
[chiendoan77/webcui](https://github.com/chiendoan77/webcui), sau đó backend
dùng Resend để gửi email:

- `POST /auth/send-otp.php`: nhận `email`, gửi và lưu OTP.
- `POST /auth/reset-password.php`: nhận `email` và `otp` để xác nhận OTP.
  Android có gửi thêm `newPasswordHash`, nhưng source PHP hiện không sử dụng
  trường này; app cập nhật mật khẩu sau khi backend trả về thành công.

#### Tạo Resend API key và địa chỉ gửi

1. Đăng nhập [Resend](https://resend.com/) và mở **API Keys > Create API Key**.
2. Sao chép key có dạng `re_...`. Resend chỉ hiển thị toàn bộ key lúc vừa tạo.
3. Để gửi production, mở **Domains > Add Domain**, thêm các DNS record Resend
   cung cấp và chờ trạng thái **Verified**.
4. Chọn địa chỉ gửi thuộc domain đã xác minh, ví dụ
   `TravelFood <otp@mail.example.com>`.

Source PHP đọc các biến sau từ môi trường hoặc file `.env`:

```dotenv
RESEND_API_KEY=re_your_resend_api_key
RESEND_FROM=TravelFood <otp@mail.example.com>
OTP_LENGTH=6
OTP_EXPIRE_MINUTES=5
```

Khi chỉ thử nghiệm với sender mặc định, có thể đặt:

```dotenv
RESEND_FROM=TravelFood <onboarding@resend.dev>
```

Sender thử nghiệm của Resend có giới hạn người nhận; nên xác minh domain trước
khi dùng với tài khoản người dùng thật. Không đặt `RESEND_API_KEY` trong app
Android, `local.properties` của app hoặc Git.

#### Deploy source PHP lên Render

1. Fork hoặc clone repository `chiendoan77/webcui`.
2. Trong Render, chọn **New > Web Service** và kết nối repository.
3. Chọn **Docker** để Render build bằng `Dockerfile` có sẵn.
4. Mở **Environment** và thêm `RESEND_API_KEY`, `RESEND_FROM`, `OTP_LENGTH`,
   `OTP_EXPIRE_MINUTES`.
5. Chọn **Save, rebuild, and deploy**.
6. Mở URL service. Kết quả thành công sẽ có message
   `TravelFood OTP API is running`.

Sau khi deploy, thêm URL gốc của service vào `local.properties` của app. URL
không chứa `auth/send-otp.php`; dấu `/` cuối URL được app tự chuẩn hóa:

```properties
OTP_BASE_URL=https://your-service.onrender.com/
```

Có thể kiểm tra endpoint gửi OTP bằng PowerShell:

```powershell
$body = @{ email = "your-email@example.com" } | ConvertTo-Json
Invoke-RestMethod `
  -Method Post `
  -Uri "https://your-service.onrender.com/auth/send-otp.php" `
  -ContentType "application/json" `
  -Body $body
```

Kết quả mong đợi:

```json
{
  "success": true,
  "message": "Đã gửi OTP về email"
}
```

Backend hiện lưu OTP tại `storage/otps.json`. Filesystem mặc định của Render
là tạm thời, nên file này có thể mất khi service restart hoặc deploy lại. Bản
production nên chuyển OTP sang database/Redis có thời hạn; nếu dùng persistent
disk với Docker hiện tại thì mount tại `/var/www/html/storage`.

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
