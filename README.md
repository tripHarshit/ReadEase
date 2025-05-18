# 📚 ReadEase

ReadEase is a modern Android application built with **Jetpack Compose** that helps users track their book reading progress. It features seamless UI/UX, Firebase push notifications, and an efficient book management experience.

---

## 🚀 Features

- 📖 Add and manage books in your reading list
- 📅 Track reading status: Want to Read, Currently Reading, Finished
- 🔔 Receive push notifications using **Firebase Cloud Messaging (FCM)**
- 🧭 Modern UI with **Jetpack Compose**
- 📱 Material 3 design with dark/light theme support
- 💾 Firebase integration for notification services
- 🔐 Runtime notification permission support for Android 13+

---

## 🛠️ Tech Stack

- **Kotlin**
- **Jetpack Compose**
- **Firebase Cloud Messaging (FCM)**
- **Material Design 3**
- **MVVM Architecture**
- **Coroutines & Flow**
- **Room (optional if used for persistence)**

---

## 🔔 Notifications

ReadEase uses Firebase Cloud Messaging (FCM) to push personalized notifications. The app includes:
- A custom `FirebaseMessagingService`
- Notification channel support (Android O+)
- Runtime notification permission handling (Android 13+)

---

## 📂 Branch Info

> 🔀 The primary development and production-ready code exists on the `master` branch.  
> Make sure to check it out while cloning or submitting pull requests.

---

## 📦 Installation

1. **Clone the repository**

```bash
git clone -b master https://github.com/your-username/ReadEase.git
cd ReadEase
