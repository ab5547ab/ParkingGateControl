# 🅿️ אפליקציית פתיחת שערי חנייה

אפליקציה דינאמית ונוחה לפתיחת שערי חנייה בחיוג אוטומטי דרך הטלפון.

## 📋 מידע חיוני

- **חנייה עילית**: 0559643981
- **חנייה תת קרקעית**: 0559643987

## ⚙️ דרישות הפיתוח

- **Android Studio** 2023.1 ומעלה
- **SDK 34** (Android 14)
- **JDK 17**
- **Gradle 8.0+**

## 🛠️ התקנה והפעלה

### 1️⃣ שכפול הפרויקט
\`\`\`bash
git clone https://github.com/YOUR_USERNAME/ParkingGateControl.git
cd ParkingGateControl
\`\`\`

### 2️⃣ פתיחה ב-Android Studio
1. פתח את Android Studio
2. לחץ על "Open" וברר את תיקיית הפרויקט

### 3️⃣ בנייה והרצה
\`\`\`bash
./gradlew build
./gradlew installDebug
\`\`\`

או פשוט בחר **Run > Run 'app'** ב-Android Studio

## 🎯 תכונות

✅ פתיחת שערי חנייה בלחיצה אחת
✅ תמיכה בבלוטוס לרכבים עם מולטימדיה
✅ צליל של פתיחת שער
✅ עדכון דינאמי של מספרי הטלפון
✅ הוספת שערים חדשים
✅ ממשק פשוט ונקי
✅ תמיכה בעברית

## 📱 הרשאות

האפליקציה דורשת את ההרשאות הבאות:
- `CALL_PHONE` - לחיוג למספר
- `BLUETOOTH` - התחברות לבלוטוס של הרכב
- `BLUETOOTH_ADMIN` - ניהול התחברויות בלוטוס

## 🔧 הגדרות

לחץ על כפתור ⚙️ בפינה השמאלית למעלה לגישה להגדרות:
- עריכת מספרי טלפון של שערים קיימים
- הוספת שערים חדשים
- מחיקת שערים

## 📦 בנייה ל-Release

\`\`\`bash
./gradlew bundleRelease
\`\`\`

קבצי ה-APK יהיו ב: `app/build/outputs/apk/`

## 👨‍💻 פיתוח

### מבנה הפרויקט
\`\`\`
ParkingGateControl/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/parking/gate/control/
│   │   │   │   └── MainActivity.kt
│   │   │   └── res/
│   │   │       ├── layout/
│   │   │       ├── values/
│   │   │       └── mipmap/
│   │   └── ...
│   └── build.gradle
├── build.gradle
├── settings.gradle
└── ...
\`\`\`

## 📝 עדכון הגרסה

ערוך את `versionCode` ו-`versionName` ב-`app/build.gradle` לפני כל שחרור חדש.

## 🐛 דיווח על באגים

בעיה? [לחץ כאן](../../issues) ודווח על זה!

## 📄 רישיון

MIT License - ראה [LICENSE](LICENSE) לפרטים

## 👤 יוצר

פותחה עם ❤️ לנהגי הדרך

---

**עדכון אחרון:** """ + datetime.now().strftime("%Y-%m-%d") + """
