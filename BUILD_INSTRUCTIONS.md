# بناء APK للاختبار المحلي (بدون Firebase)

تم إزالة جميع تبعيات Firebase من المشروع ليعمل محلياً للاختبار.

## 📋 التغييرات المطبقة:
- ✅ أُزيل `google-services` plugin
- ✅ أُزيلت تبعيات Firebase (Auth, Firestore, Storage, Messaging, Crashlytics)
- ✅ أُزيل `FcmMessageService` من Manifest
- ✅ استُبدل `FirestoreDataSource` بنسخة محلية (In-Memory) للاختبار
- ✅ أُزيل تهيئة Firebase من `PhoneRepairApplication`
- ✅ يعمل مع Room Database محلياً فقط

## 🛠 متطلبات البناء على جهازك:

1. **Android Studio** (يحتوي على SDK + Gradle + JDK 17)
2. **JDK 17+** (مطلوب لـ AGP 8.3)
3. **Android SDK 34** (compileSdk)

## 🚀 خطوات البناء:

### الطريقة 1: عبر Android Studio (الأسهل)
```bash
# 1. افتح Android Studio
File → Open → اختر مجلد المشروع (/root)

# 2. انتظر مزامنة Gradle (قد يستغرق 2-5 دقائق لأول مرة)

# 3. ابنِ الـ APK:
Build → Build Bundle(s) / APK(s) → Build APK(s)

# 4. سيظهر إشعار مع رابط الـ APK:
# app/build/outputs/apk/debug/app-debug.apk
```

### الطريقة 2: عبر سطر الأوامر
```bash
# انسخ المشروع لجهازك
cd PhoneRepairShop

# اجعل gradlew قابل للتنفيذ
chmod +x gradlew

# ابنِ Debug APK
./gradlew assembleDebug

# الناتج:
# app/build/outputs/apk/debug/app-debug.apk
```

### الطريقة 3: باستخدام سكريبت البناء المرفق
```bash
chmod +x build_apk.sh
./build_apk.sh debug
# أو للنسخة النهائية:
./build_apk.sh release
```

## 📱 بعد البناء:

**نسخ الـ APK لجهازك:**
```bash
# عبر ADB
adb install -r app/build/outputs/apk/debug/app-debug.apk

# أو انسخ الملف للجهاز وثبته يدوياً
```

## 🧪 ما ستجده في التطبيق (بيانات تجريبية):

| القسم | بيانات تجريبية |
|--------|--------------|
| **المخزون** | 3 قطع (شاشة iPhone 15، بطارية، كاميرا S24) |
| **العملاء** | 3 عملاء مع سجلات ولاء |
| **الفنيون** | فنيان (محمد - شاشات/لوحات، فاطمة - بطاريات/منافذ) |
| **الإعدادات** | إعدادات افتراضية للمحل |

## 🔄 لإعادة تفعيل Firebase لاحقاً:

1. أضف `google-services.json` في `app/`
2. ألغِ التعليق في `app/build.gradle.kts`:
   ```kotlin
   id("com.google.gms.google-services")
   // Firebase dependencies...
   ```
3. عدّل `FirestoreDataSource.kt` للنسخة الأصلية
4. عدّل `PhoneRepairApplication.kt` لتهيئة Firebase
5. أضف خدمة FCM في `AndroidManifest.xml`
6. عدّل `di/Modules.kt` لإضافة `FirebaseModule`

## ⚠️ ملاحظات:
- التطبيق يعمل **بشكل كامل محلياً** (Offline-First)
- البيانات تُحفظ في Room Database
- عند إعادة تشغيل التطبيب، تعود البيانات التجريبية
- للاستخدام الإنتاجي: فعّل Firebase للمزامنة السحابية

---

**الملفات الرئيسية:**
- `/root/app/build.gradle.kts` - تبعيات البناء
- `/root/build_apk.sh` - سكريبت بناء سريع
- `/root/gradle/wrapper/` - Gradle Wrapper