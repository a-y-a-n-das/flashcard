# Flashcard 📚

A modern Android flashcard application designed to help you learn and retain information effectively using spaced repetition techniques.

## Features ✨

- **Smart Flashcards**: Create and manage flashcards organized by categories
- **Spaced Repetition**: Cards are reviewed based on your performance (Bad, OK, Good ratings)
- **Progress Tracking**: Monitor your learning with detailed statistics and charts
- **Streak Counter**: Stay motivated with daily streak tracking
- **Cloud Backup**: Sync your flashcards across devices using Firebase Storage
- **Google Sign-In**: Secure authentication with your Google account
- **Offline Support**: Study your cards even without internet connection using local Room database
- **Beautiful UI**: Clean, intuitive interface with smooth animations

## Screenshots 📱

*Coming soon*

## Tech Stack 🛠️

- **Language**: Kotlin
- **Architecture**: Android Activity-based architecture
- **Local Database**: Room (SQLite)
- **Cloud Storage**: Firebase Storage
- **Authentication**: Google Sign-In
- **Charts**: MPAndroidChart
- **Image Loading**: Glide
- **JSON Parsing**: Gson
- **Minimum SDK**: 28 (Android 9.0)
- **Target SDK**: 35

## Prerequisites 📋

Before you begin, ensure you have the following installed:

- [Android Studio](https://developer.android.com/studio) (Latest stable version recommended)
- JDK 8 or higher
- Android SDK with API level 28 or higher
- A Firebase project (for cloud backup functionality)

## Getting Started 🚀

### 1. Clone the repository

```bash
git clone https://github.com/a-y-a-n-das/flashcard.git
cd flashcard
```

### 2. Firebase Setup

1. Go to the [Firebase Console](https://console.firebase.google.com/)
2. Create a new project or use an existing one
3. Add an Android app with package name `com.example.flashcard`
4. Download the `google-services.json` file
5. Place it in the `app/` directory (already included in this repo)
6. Enable Firebase Storage in your Firebase project
7. Enable Google Sign-In in Authentication settings

### 3. Build and Run

Open the project in Android Studio and let it sync the Gradle files. Then:

```bash
./gradlew assembleDebug
```

Or simply click the **Run** button in Android Studio.

## Project Structure 📁

```
flashcard/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/flashcard/
│   │   │   │   ├── MainActivity.kt          # Home screen with categories
│   │   │   │   ├── Card.kt                  # Flashcard review activity
│   │   │   │   ├── Stats.kt                 # Statistics and profile screen
│   │   │   │   ├── CategoriesActivity.kt    # Category management
│   │   │   │   ├── addCard.kt               # Add new flashcard
│   │   │   │   ├── ViewCategory.kt          # View cards in category
│   │   │   │   ├── SignInActivity.kt        # Google Sign-In
│   │   │   │   ├── AppDatabase.kt           # Room database configuration
│   │   │   │   ├── CardDao.kt               # Card data access object
│   │   │   │   ├── CategoryDao.kt           # Category data access object
│   │   │   │   ├── CardDataClass.kt         # Data models
│   │   │   │   ├── BackupManager.kt         # Firebase backup/restore
│   │   │   │   └── UsageTracker.kt          # Time tracking
│   │   │   └── res/                         # Resources (layouts, drawables, etc.)
│   │   ├── androidTest/                     # Instrumentation tests
│   │   └── test/                            # Unit tests
│   └── build.gradle.kts                     # App-level build configuration
├── build.gradle.kts                         # Project-level build configuration
├── settings.gradle.kts                      # Gradle settings
└── gradle/                                  # Gradle wrapper files
```

## How It Works 🎯

### Spaced Repetition Algorithm

The app uses a simple but effective spaced repetition system:

1. **New cards** are shown immediately
2. After reviewing a card, rate your recall:
   - **Bad (0)**: Card will be shown again soon
   - **OK (1)**: Card will be shown in moderate intervals
   - **Good (2)**: Card will be shown less frequently
3. Cards are filtered based on score and last review date to optimize learning

### Data Persistence

- **Local**: Room database stores all flashcards, categories, and progress locally
- **Cloud**: Firebase Storage backs up your data for cross-device sync
- **Offline-first**: The app works offline and syncs when connectivity is available

## Dependencies 📦

| Library | Purpose |
|---------|---------|
| Room | Local SQLite database |
| Firebase Storage | Cloud backup |
| Firebase Analytics | Usage analytics |
| Google Play Services Auth | Google Sign-In |
| MPAndroidChart | Statistics visualization |
| Glide | Image loading and caching |
| Gson | JSON serialization |

## Contributing 🤝

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License 📄

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Author 👨‍💻

**Ayan Das**

- GitHub: [@a-y-a-n-das](https://github.com/a-y-a-n-das)

## Acknowledgments 🙏

- [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart) for beautiful charts
- [Glide](https://github.com/bumptech/glide) for efficient image loading
- Firebase team for excellent backend services

---

⭐ If you find this project useful, please consider giving it a star!
