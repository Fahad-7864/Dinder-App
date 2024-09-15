# CE881App

CE881App ("Dinder") is an Android application built primarily for the CE881 module,this app allows users to view, add, and manage recipes.

This app was created in:
- Android Studio Iguana , 2023.2.1 Patch 1 or higher

### Configuration

- The app was primarily developed on Pixel 7 API 31.
- NOTE: In order to build this project please copy and paste the files from ce881App onto the directory of your newly made android project, Once all the files have been uploaded, 
-please go to build- Clean project, then build rebuild project. you may have to invalidate caches and restart the
android studio IDE in order for the project to build properly and work!
- NOTE: Remember to change the package name of your project to the one provided (should not be necessary but just in case.):
    "com.example.ce881app"



### Dependencies

This project makes use of key dependencies:
- Yuyakaido's CardStackView
- Gson
- (References are included in the Activities and classes that use them!)
Ensure that these dependencies are included in your `build.gradle` file as shown in the provided setup.

### Gradle Settings

1. **settings.gradle.kts** - Verify that the JitPack repository URL is included:
    ```
    dependencyResolutionManagement {
        repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
        repositories {
            google()
            mavenCentral()
            maven { url = uri("https://jitpack.io") }
        }
    }
    ```
    and in your project build.gradle.kts:
    include these:
        implementation ("com.github.yuyakaido:CardStackView:v2.3.4")
        implementation ("com.google.code.gson:gson:2.8.8")

### Asset Files

- Ensure that the `recipes.json` file is placed in the `src/main/assets` directory of your project to load the initial recipe data correctly.
- Ensure that the AndroidManifest.xml is in the correct place and has not been modified.
### Running the Application

- Open the project in Android Studio.
- Run the project using an emulator targeting API 31 or above, or connect a physical device via ADB.

### Shared Preferences

- The app uses shared preferences to store recipe data locally.
- To reset all saved preferences, navigate to the `HomeActivity` class and locate the `resetCalendar` function. This function can be used or commented out depending on your testing needs.

## Additional Information

For additional setup and detailed configuration, refer to the official Android and Jetpack Compose documentation.
Ensure your environment is configured correctly to use all features provided in this application.
