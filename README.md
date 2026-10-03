# Phone Repair Shop - Android Application

تطبيق أندرويد متكامل وعصري لإدارة محلات صيانة الهواتف، مبني بأحدث التقنيات وأفضل الممارسات.

## ✨ المميزات الرئيسية

### 📱 إدارة طلبات الصيانة
- تسجيل أجهزة العملاء مع تفاصيل كاملة (الماركة، الموديل، الرقم التسلسلي، الحالة)
- تتبع حالة الإصلاح: استلام → تشخيص → انتظار قطع → إصلاح → فحص جودة → جاهز للاستلام → تسليم
- نظام أولويات: عاجل، عالي، عادي، منخفض
- تعيين الفنيين وتتبع أدائهم
- إضافة القطع المستخدمة والتكلفة المتوقعة/النهائية
- إرفاق الصور والملاحظات
- طباعة إيصالات الاستلام

### 👥 إدارة العملاء
- قاعدة بيانات عملاء شاملة مع بيانات الاتصال
- سجل الصيانة الكامل لكل عميل
- نظام نقاط الولاء
- تتبع إجمالي الإنفاق وعدد الطلبات
- قائمة سوداء للعملاء المشكوك فيهم
- طرق تواصل مفضلة (هاتف، واتساب، إيميل، SMS)

### 📦 إدارة المخزون
- إدارة القطع مع SKU والباركود
- مسح الباركود بالكاميرا لإدخال سريع
- تتبع المخزون الحالي، الحد الأدنى، الحد الأقصى
- تنبيهات ذكية للمخزون المنخفض/المنتهي
- إدارة الموردين والمواقع
- حساب هامش الربح تلقائياً
- فئات منظمة: شاشات، بطاريات، كاميرات، منافذ شحن، لوحات أم، إلخ

### 💰 الفواتير والمدفوعات
- إنشاء فواتير احترافية من طلبات الصيانة
- بنود تفصيلية مع الضرائب والخصومات
- طرق دفع متعددة: نقدي، بطاقة، تحويل، محفظة، أقساط
- تتبع حالة الدفع: معلق، جزئي، مدفوع، متأخر، مسترد
- طباعة حرارية عبر البلوتوث (58mm/80mm)
- إرسال الفواتير عبر واتساب/إيميل
- تقارير مالية يومية/شهرية

### 📊 التقارير والإحصائيات
- لوحة تحكم تفاعلية مع مؤشرات الأداء الرئيسية (KPIs)
- رسوم بيانية: الإيرادات اليومية، توزيع الحالات، أعلى القطع مبيعاً، أداء الفنيين
- فترات زمنية متعددة: اليوم، الأسبوع، الشهر، السنة، مخصص
- إحصائيات المخزون والعملاء
- تصدير التقارير (PDF، Excel)

### ⚙️ الإعدادات والتخصيص
- بيانات المحل (الاسم، العنوان، الهاتف، الإيميل)
- إعدادات الضريبة والعملات
- ساعات العمل وأيام العطل
- مدة الضمان الافتراضية
- إعدادات الطابعة الحرارية
- النسخ الاحتياطي التلقائي والمزامنة
- السمات: فاتح، داكن، نظام
- اللغات: العربية، الإنجليزية

## 🛠 التقنيات المستخدمة

### Architecture
- **MVVM** مع **Repository Pattern** و **Clean Architecture**
- **Offline-First** مع Room Database + Firebase Sync
- **Dependency Injection** مع Hilt
- **Navigation** مع Jetpack Navigation Compose

### UI/UX
- **Jetpack Compose** مع **Material 3** (Material You)
- **RTL Support** كامل للغة العربية
- **Dark/Light Theme** مع التبديل الديناميكي
- **Animations** سلسة وانتقالات جميلة
- **Responsive Design** لجميع أحجام الشاشات

### Data & Sync
- **Firebase Firestore** قاعدة بيانات سحابية فورية
- **Firebase Auth** للمصادقة (Email/Password, Google, Apple)
- **Firebase Storage** للصور والمرفقات
- **Firebase Messaging (FCM)** للإشعارات الفورية
- **Firebase Crashlytics** لمراقبة الأخطاء
- **Room Database** للتخزين المحلي
- **DataStore** للإعدادات

### Modern Android Stack
- **Kotlin 1.9+** مع Coroutines & Flow
- **CameraX + ML Kit** لمسح الباركود
- **WorkManager** للمزامنة الخلفية
- **Coil** لتحميل الصور
- **MPAndroidChart** للرسوم البيانية
- **iText7** لتوليد PDF
- **Bluetooth Printing** للطابعات الحرارية

## 📋 المتطلبات

- **Android Studio** Giraffe | 2023.3.1 أو أحدث
- **JDK 17** أو أحدث
- **Android SDK 34** (compileSdk)
- **minSdk 24** (Android 7.0 Nougat)
- **Firebase Project** مُعد مع Authentication, Firestore, Storage, Messaging, Crashlytics

## 🚀 البدء السريع

### 1. استنساخ المشروع
```bash
git clone <repository-url>
cd PhoneRepairShop
```

### 2. إعداد Firebase
1. أنشئ مشروع Firebase في [Firebase Console](https://console.firebase.google.com)
2. أضف تطبيق Android بحزمة `com.phonerepair.shop`
3. حمل `google-services.json` وضعه في `app/`
4. فعّل الخدمات المطلوبة:
   - **Authentication**: Email/Password, Google, Apple
   - **Firestore Database**: ابدأ في وضع الاختبار
   - **Storage**: ابدأ في وضع الاختبار
   - **Cloud Messaging**: للأعشات
   - **Crashlytics**: للمراقبة

### 3. تكوين المتغيرات البيئية
أنشئ ملف `local.properties` أو اضبط متغيرات البيئة:
```properties
KEYSTORE_PATH=/path/to/keystore.jks
KEYSTORE_PASSWORD=your_keystore_password
KEY_ALIAS=your_key_alias
KEY_PASSWORD=your_key_password
```

### 4. بناء وتشغيل
```bash
./gradlew assembleDebug
./gradlew installDebug
```

## 🏗 هيكل المشروع

```
app/
├── src/main/
│   ├── java/com/phonerepair/shop/
│   │   ├── data/
│   │   │   ├── local/          # Room Database, DAOs, Entities
│   │   │   ├── model/          # Data Models (RepairOrder, Customer, Part, Invoice, etc.)
│   │   │   ├── remote/         # Firestore Data Source
│   │   │   └── repository/     # Repository Interfaces & Implementations
│   │   ├── di/                 # Hilt Modules
│   │   ├── messaging/          # FCM Service
│   │   ├── ui/
│   │   │   ├── auth/           # Login/Register Screens
│   │   │   ├── dashboard/      # Main Dashboard
│   │   │   ├── repairs/        # Repair Orders Management
│   │   │   ├── inventory/      # Inventory Management
│   │   │   ├── customers/      # Customers Management
│   │   │   ├── invoices/       # Invoices & Payments
│   │   │   ├── reports/        # Reports & Analytics
│   │   │   ├── settings/       # Settings Screen
│   │   │   ├── components/     # Reusable UI Components
│   │   │   ├── navigation/     # Navigation Graph
│   │   │   └── theme/          # Material 3 Theme
│   │   ├── util/               # Utility Classes
│   │   ├── worker/             # WorkManager Workers
│   │   ├── PhoneRepairApplication.kt
│   │   └── MainActivity.kt
│   ├── res/
│   │   ├── values/             # Strings, Colors, Themes
│   │   ├── drawable/           # Vector Drawables
│   │   └── xml/                # Backup Rules, Data Extraction
│   └── AndroidManifest.xml
├── build.gradle.kts
└── proguard-rules.pro
```

## 🔧 التخصيص والتطوير

### إضافة حالة إصلاح جديدة
1. أضف الحالة في `RepairOrder.RepairStatus` enum
2. أضف الترجمة في `strings.xml`
3. أضف اللون في `colors.xml`
4. حدث `RepairStatusChip` component

### إضافة فئة قطع جديدة
1. أضف الفئة في `Part.PartCategory` enum
2. أضف الترجمة في `strings.xml`
3. حدث `CategoryFilterChips`

### تخصيص السمة
عدل الألوان في `ui/theme/Color.kt` أو `res/values/colors.xml`

### إضافة حقل جديد للنموذج
1. أضف الحقل في Data Class
2. حدث Room Entity و TypeConverters
3. حدث Firestore Data Source
4. حدث Repository و DAO
5. حدث UI

## 📱 لقطات شاشة (مخطط)

> سيتم إضافة لقطات الشاشة بعد البناء

## 🧪 الاختبار

```bash
# Unit Tests
./gradlew test

# Instrumented Tests
./gradlew connectedAndroidTest

# مع Coverage
./gradlew jacocoTestReport
```

## 📦 البناء للإنتاج

```bash
# Release APK
./gradlew assembleRelease

# App Bundle (للنشر على Play Store)
./gradlew bundleRelease
```

## 🔐 الأمان

- جميع مفاتيح Firebase مخزنة في `google-services.json` (غير مُتبع في Git)
- مفاتيح Keystore في متغيرات البيئة
- تشفير البيانات الحساسة محلياً
- قواعد Firestore Security Rules محددة
- App Check مع Play Integrity

## 🤝 المساهمة

1. Fork المشروع
2. أنشئ فرع للميزة (`git checkout -b feature/amazing-feature`)
3. Commit التغييرات (`git commit -m 'Add amazing feature'`)
4. Push للفرع (`git push origin feature/amazing-feature`)
5. افتح Pull Request

## 📄 الترخيص

هذا المشروع مرخص تحت رخصة MIT - راجع ملف [LICENSE](LICENSE) للتفاصيل.

## 👨‍💻 المطور

تم تطوير هذا التطبيق باستخدام أحدث تقنيات Android لتوفير حل متكامل وعصري لإدارة محلات صيانة الهواتف.

---

**ملاحظة**: هذا مشروع نموذجي يوضح البنية الكاملة. للتطبيق في الإنتاج، تأكد من:
- إعداد قواعد أمان Firestore المناسبة
- تكوين شهادات App Signing
- اختبار شامل على أجهزة حقيقية
- إعداد CI/CD للنشر التلقائي
- مراقبة الأداء والأخطاء عبر Firebase Console