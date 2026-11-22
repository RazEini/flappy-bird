# Flappy Bird - Android

<div dir="rtl">

משחק Flappy Bird מקורי לאנדרואיד, שנבנה ב־<strong>Kotlin</strong> וב־<strong>Jetpack Compose</strong>.
שלוטו בציפור, עברו בין צינורות והישמרו מפני החזרה לאזורי המסך העליונים והתחתונים.

<p>
  <img src="https://img.shields.io/badge/Kotlin-2.0-7F52FF?logo=kotlin&logoColor=white" alt="Kotlin">
  <img src="https://img.shields.io/badge/Jetpack%20Compose-UI-4285F4?logo=jetpackcompose&logoColor=white" alt="Jetpack Compose">
  <img src="https://img.shields.io/badge/Material%203-Design-6200EE" alt="Material 3">
  <img src="https://img.shields.io/badge/Android-24%2B-3DDC84?logo=android&logoColor=white" alt="Android 24+">
  <img src="https://img.shields.io/badge/License-MIT-yellow" alt="MIT License">
</p>

## הצגת המשחק

<p align="center">
  <img src="assets/Screen_recording_20251013_144403.gif" alt="הדגמת המשחק Flappy Bird" width="250">
</p>

## תכונות

- שליטה אינטואיטיבית - לחיצה על המסך גורמת לציפור לקפוץ.
- צינורות דינמיים עם גובה משתנה ושמירה על קושי חלק.
- מערכת ניקוד ושיא ניקוד בתוך הריצה הנוכחית.
- שלושה חיים ומנגנון חסינות קצר לזמן של שנייה לאחר פגיעה.
- אנימציה לשני פרקי התעופה של הציפור.
- מסכים למשחק, לתחילת משחק ולתוצאה.
- ממשק Jetpack Compose מותאם לגודל המסך.

## דרישות

- Android Studio (גרסה התומכת ב־AGP 8.13)
- JDK 11
- Android SDK 35
- מכשיר אנדרואיד עם גרסה 7.0 ומעלה (API 24), או Emulator

## הרצה

1. שכפחו את הפרויקט:

   ```bash
   git clone https://github.com/RazEini/flappy-bird.git
   cd flappy-bird
   ```

2. פתחו את תיקיית הפרויקט ב־Android Studio.

3. המתינו לסנכרון Gradle.

4. בחרו Emulator או מכשיר מחובר ולחצו על <strong>Run</strong>.

### הרצה משורת הפקודה

ב־Linux או macOS:

```bash
./gradlew assembleDebug
```

ב־Windows:

```powershell
.\gradlew.bat assembleDebug
```

קובץ ה־APK יופיע ב־`app/build/outputs/apk/debug/`.

## איך משחקים

| מצב | פעולה |
|---|---|
| מסך פתיחה | לחצו על המסך או על `START` |
| בזמן המשחק | לחצו על המסך כדי להעלות את הציפור |
| Game Over | לחצו על המסך כדי לחזור למסך הפתיחה |

כל מעבר מוצלח בין צינורות מוסיף נקודה אחת. פגיעה בצינור או בגבולות המסך מורידה חיים, והמשחק מסתיים לאחר שלוש פגיעות.

## טכנולוגיות

- [Kotlin](https://kotlinlang.org/)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Material 3](https://m3.material.io/)
- Android Canvas ו‑Coroutines
- Gradle עם Kotlin DSL

## מבנה הפרויקט

```text
.
├── app/src/main/
│   ├── java/com/example/flappybird/
│   │   ├── MainActivity.kt        # לוגיקה, מסכים וממשק המשחק
│   │   └── ui/theme/              # Theme וערכי עיצוב
│   ├── res/drawable/              # רקע, ציפור ומרכיבי גרפיקה
│   └── AndroidManifest.xml
├── assets/                        # GIF להדגמת המשחק
├── build.gradle.kts
└── settings.gradle.kts
```

## בדיקות

הריצו את בדיקות היחידה וה‑instrumented:

```bash
./gradlew test
./gradlew connectedAndroidTest
```

## רישיון

הפרויקט מופץ תחת רישיון <strong>MIT</strong>. לפרטים מלאים ראו את הקובץ [LICENSE](LICENSE).

---

נבנה על ידי <strong>Raz Eini</strong>.

</div>
