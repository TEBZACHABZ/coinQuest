# CoinQuest - Budget Tracker App

CoinQuest is a comprehensive personal finance management app that helps users track expenses, stay within budget goals, and stay motivated through gamification.

## Final Submission Features
- **Visual Spending Graph**: A custom-built chart showing categorical spending relative to minimum and maximum goals for user-selectable periods.
- **Goal Status Visualization**: Real-time visual feedback on whether the user is staying within their monthly spending limits.
- **Gamification Elements**: Earn badges like "Budget Master", "Active Logger", and "Big Saver" for consistent tracking and meeting goals.
- **Automated Testing**: Comprehensive unit tests for core logic including goal validation and badge criteria.
- **Logging**: System-wide logging for tracking app state and user interactions.

## Additional Custom Features
1. **Photo Receipt Capture**: Attach photos of receipts directly to expense entries for better record-keeping.
2. **Dynamic Category Creation**: Add and manage custom categories on the fly within the expense entry screen.
3. **Total Spending Overview**: Instant total calculation on both History and Reports screens.

## Core Functionality
- **User Authentication**: Secure login and registration.
- **Category Management**: Organize spending into meaningful groups.
- **Expense Entry**: Detailed tracking with date, time, and descriptions.
- **Monthly Goals**: Set spending targets using interactive SeekBars.
- **Offline First**: Reliable local storage using Room Database.

## Tech Stack
- **Language**: Kotlin
- **Database**: Room DB with KSP
- **Architecture**: MVVM (Model-View-ViewModel)
- **UI**: Material Components, Custom Views, ViewBinding

## Installation
1. Clone the repository.
2. Open in Android Studio.
3. Build and Run on a physical Android device (as per requirement).

## Testing
Automated unit tests are located in `app/src/test/java/com/example/coinquest/`.
Run them using `./gradlew test`.

## Logging
Check Logcat with tag `ReportsFragment` or `GoalsFragment` to see the app logic in action.

