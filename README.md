# WardrobeAI

WardrobeAI is an Android app that lets users upload a photo of themselves and a clothing item, then generate a virtual try-on preview using AI. Built with Kotlin and Jetpack Compose, the app offers a clean, modern interface for selecting images, generating try-on results, and saving the final output to the device gallery.

## Features

- Upload a person photo
- Upload a clothing image
- Generate a virtual try-on using Gemini AI
- Preview the generated result
- Download or save the final image to the gallery
- Try another outfit without restarting the flow
- Material 3-based modern UI
- Hilt dependency injection setup

## Tech Stack

- Kotlin
- Android Jetpack Compose
- Material 3
- Hilt
- OkHttp
- Google Gemini API
- Android Studio + Gradle

## Project Structure

```text
WardrobeAI/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/wardrobeai/
│   │   │   │   ├── closet/
│   │   │   │   ├── views/
│   │   │   │   │   └── tryOn/
│   │   │   ├── res/
│   │   │   └── AndroidManifest.xml
│   ├── build.gradle.kts
│   ├── google-services.json
│   └── proguard-rules.pro
├── build.gradle.kts
├── settings.gradle.kts
├── gradlew
├── gradlew.bat
├── gradle.properties
├── local.properties.example
├── .gitignore
└── README.md
```
## Screenshots
<p>
  <img width="250" height="500" alt="unnamed" src="https://github.com/user-attachments/assets/06a6e57a-4fa3-4e28-88e0-8f36de3de773" />
  <img width="250" height="500" alt="unnamed" src="https://github.com/user-attachments/assets/8c5e3f62-a91d-48dd-bd34-c202fd0adb1e" />
</p>
