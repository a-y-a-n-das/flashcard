# Flashcard App

A simple flashcard application designed to help users study and memorize content efficiently. The app allows users to create and organize flashcards, review them in various categories, and track their progress. It uses a spaced repetition system to help retain information over time.

## Features

- **Create Categories**: Organize flashcards into different categories for easy navigation.
- **Add Flashcards**: Create custom flashcards with questions and answers.
- **Spaced Repetition**: Based on user feedback (Bad, Ok, Good), flashcards are reviewed at appropriate intervals.
- **Progress Tracking**: The app tracks the user's review status for each card and provides feedback.
- **Review Flashcards**: Users can go through their flashcards with an option to show answers after viewing the questions.
- **Responsive Interface**: The app provides a simple, user-friendly interface for easy navigation.

## Installation

1. Clone the repository to your local machine:
    ```bash
    git clone https://github.com/a-y-a-n-das/flashcard-app.git
    ```

2. Open the project in Android Studio.

3. Build and run the app on an emulator or physical device.

## Technologies Used

- **Kotlin**: Main language for Android development.
- **Room Database**: Local database for storing flashcards and categories.
- **Coroutines**: Used for asynchronous operations.
- **ViewPager2**: For sliding between flashcards during reviews.
- **RecyclerView**: For displaying categories and flashcards in a list/grid view.
- **TypeConverters**: For converting complex data types like `LocalDate` into storable values in the Room database.

## Getting Started

1. Open the app and navigate to the home screen where you can add categories.
2. Once a category is added, you can start adding flashcards under that category.
3. When you're ready, go to the category and start reviewing the flashcards.
4. After reviewing, give feedback (Bad, Ok, or Good) to update the review status and next review date.

## License

This project is open-source and available under the MIT License. See the [LICENSE](LICENSE) file for more details.
