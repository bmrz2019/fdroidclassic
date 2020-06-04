### 1.0-beta11 (?)

*

### 1.0-beta10 (04.06.2020)

* Slightly prettier About dialog
* Theme changes apply instantly and not only on back button.
* Add an "Update all" button to the menu, this might still be a bit buggy.
* The download cancel button is now visible in night mode. It's also slightly bigger and has a ripple effect now.
* Fix crash on phone boot
* Don't autoverify intent filters (https://developer.android.com/training/app-links/verify-site-associations), we're fine with an app choser dialouge.

### 1.0-beta9 (24.05.2020)

* lot's of improvements to AppDetails view. (658078058106ea868c7f90f675214ff120b6af3d for details)
* add whatsnew section
* Night Theme will now actually have a black background
* use the new F-Droid button style

### 1.0-beta8 (24.05.2020)

* fix crash on install when no privext is installed (introduced in beta7)
* Remove buggy appicon transition

### 1.0-beta7 (24.05.2020)

* Fix a bug where removing a repository without disabling it first would get the app in an inconsistent state.(#29)
* Switch from proguard to d8 for optimizing the apk. This should hopefully not cause any user visible changes.
* Add FileInstaller for installing non-apk files
* Add a list of supported privileged extension packageids. Still need figure out a release process for these.
* Fix a crash when uninstalling an app. (#25)

### 1.0-beta6 (12.05.2020)

* Display app version codes in expert mode (#22)
* Don't show uninstall button for apps that aren't uninstallable (i.e. system apps)
* Some code cleanup and fixes around format string usage. Fixes uninstall error message and makes other messages better translatable.

### 1.0-beta5 (11.04.2020)

* fix  using too much storage space (#24)

### 1.0-beta4 (06.03.2020)

* fix a crash that was somtimes happening when updating apps

### 1.0-beta3 (26.02.2020)

* fix upgrade button sometimes not showing (#11)
* fix repo update button not working in repo list
* fix performance issues on startup/repo update (#19)
  This disables the number of installed apps/number of updates updating for now.

### 1.0-beta2 (25.02.2020)

* backport repository share option
* upgrade to AndroidX, AGP 3.6
* some resource and dependency cleanup

### 1.0-beta1 (25.02.2020)

* support index-v1 format
* removed all swap functionality
* upgrade all libraries to latest versions
* bump Android targetSDK, compileSDK, and minSDK
* remove a lot of workarounds for older android versions
* New Icon!
* some fixes for icons not loading

See [F-Droid Changelog](https://gitlab.com/fdroid/fdroidclient/-/blob/master/CHANGELOG.md#01023-2017-04-01) before 0.102.3 for previous changes to codebase.
