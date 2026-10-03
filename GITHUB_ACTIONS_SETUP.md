# 🚀 GitHub Actions - بناء APK تلقائي

تم إضافة workflows للبناء التلقائي للـ APK على GitHub.

## 📁 الملفات المضافة:
```
/root/.github/workflows/
├── build-apk.yml      # بناء Debug APK عند كل push
└── build-release.yml  # بناء Release APK يدوياً/عند tag
```

## ⚡ كيف يعمل:

### 1. **Debug APK تلقائي** (`build-apk.yml`):
- يعمل عند كل `push` إلى `main` أو `develop`
- يعمل عند كل `Pull Request`
- يمكن تشغيله يدوياً من تبويب **Actions**
- يرفع الـ APK كـ **Artifact** (صالح 30 يوم)

### 2. **Release APK** (`build-release.yml`):
- يعمل يدوياً من تبويب **Actions** → **Run workflow**
- يطلب Version Name و Version Code
- يوقع التطبيق (يتطلب Keystore في Secrets)
- ينشئ **GitHub Release** تلقائياً مع الـ APK

---

## 🔧 الإعداد المطلوب (مرة واحدة):

### 1. ارفع المشروع لـ GitHub:
```bash
git init
git add .
git commit -m "Initial commit"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/PhoneRepairShop.git
git push -u origin main
```

### 2. أضف Secrets في GitHub:
اذهب إلى: **Settings → Secrets and variables → Actions → New repository secret**

| Secret Name | القيمة | مطلوبة؟ |
|-------------|---------|---------|
| `KEYSTORE_BASE64` | `base64 -w0 your-keystore.jks` | للـ Release فقط |
| `KEYSTORE_PASSWORD` | كلمة مرور Keystore | للـ Release فقط |
| `KEY_ALIAS` | اسم الـ Key Alias | للـ Release فقط |
| `KEY_PASSWORD` | كلمة مرور الـ Key | للـ Release فقط |

**لإنشاء Keystore وتحويله لـ Base64:**
```bash
# 1. أنشئ Keystore
keytool -genkey -v -keystore release.keystore -alias release_key -keyalg RSA -keysize 2048 -validity 10000

# 2. حول لـ Base64 (انسخ المخرجات لـ KEYSTORE_BASE64)
base64 -w0 release.keystore
```

### 3. فعّل الصلاحيات:
**Settings → Actions → General → Workflow permissions**
- اختر: **Read and write permissions**
- ✅ **Allow GitHub Actions to create and approve pull requests**

---

## 📥 تحميل الـ APK المبني:

### من Actions (Debug):
1. اذهب لتبويب **Actions** في المستودع
2. اختر آخر تشغيل لـ **Build Debug APK**
3. انقر على **Artifacts** → **PhoneRepairShop-debug-apk**
4. حمل الـ zip واستخرج `app-debug.apk`

### من Releases (Release):
1. اذهب لتبويب **Releases**
2. حمل `app-release.apk` من أحدث إصدار

---

## 🔄 للاختبار السريع الآن:

1. **ارفع الكود لـ GitHub** (كما أعلاه)
2. **انتظر 3-5 دقائق** للبناء الأول
3. **حمل الـ APK من Artifacts**

---

## 🛠 استكشاف الأخطاء:

| المشكلة | الحل |
|----------|-------|
| `Gradle daemon` timeout | أضف `--no-daemon` (مضاف بالفعل) |
| `SDK license not accepted` | `android-actions/setup-android` يتعامل معه تلقائياً |
| `Keystore not found` | تأكد من إضافة Secrets بشكل صحيح |
| `Permission denied` | فعّل Workflow permissions في Settings |

---

## 📱 بديل: بناء محلي (إذا لم ترد GitHub):

```bash
# على جهازك (macOS/Linux/Windows مع WSL)
git clone https://github.com/YOUR_USERNAME/PhoneRepairShop.git
cd PhoneRepairShop
chmod +x gradlew
./gradlew assembleDebug
# الناتج: app/build/outputs/apk/debug/app-debug.apk
```

---

**الآن كل push سيبني APK تلقائياً!** 🎉