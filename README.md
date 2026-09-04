This PR replaces the placeholder README with a complete project guide so contributors can understand, configure, and run the app without inspecting source files. It documents the app’s scope, Firebase dependency, and developer entry points.

- **Overview and scope**
  - Added a clear project summary for the Doctors Appointment App and its patient/doctor workflows.

- **Capabilities and architecture at a glance**
  - Documented key functional areas (auth, doctor discovery, appointment lifecycle, history, reminders).
  - Added a concise tech stack section (Android/Java, Gradle Kotlin DSL, Firebase services, Material components).

- **Contributor onboarding**
  - Added repository structure highlights for fast navigation.
  - Added prerequisites and setup instructions, including required `app/google-services.json`.

- **Run/build references**
  - Added canonical commands for local build and unit test execution.
  - Added launcher/permission notes relevant to first run and behavior expectations.

```md
## Prerequisites

- Android Studio (latest stable recommended)
- JDK 8+
- A Firebase project
- `google-services.json` placed at: `app/google-services.json`
```
