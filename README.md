## F-Droid Classic

Join the matrix room: [#fdroidclassic:bubu1.eu](https://matrix.to/#/#fdroidclassic:bubu1.eu)

### Download

[<img src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png" alt="Get it on F-Droid" height="80">](https://f-droid.org/packages/eu.bubu1.fdroidclassic)

Direct APK download [here](https://bubu1.eu/fdroidclassic/fdroid/repo/eu.bubu1.fdroidclassic_1014.apk).

### Beta versions/faster updates
In F-Droid Classic you can just enable the F-Droid Classic Repo, beta versions will be distributed there.
Releases will also be available there a couple of days before they make it into the official repo.

If you need to re-add the repository: https://bubu1.eu/fdroidclassic/fdroid/repo?fingerprint=5187CFD99F084FFAB2AD60D9D10B39203B89A46DD4862397FE1B1A4F3D46627A

<img src="screenshots/repo-qr.png" width="300">

### Project description

This is an alternative F-Droid client which is based on the older UI of the official Client (pre 1.0).
While I think that the new client UI looks pretty, it's sometimes quite clunky.
Lots of information that was provided clearly in the old UI is now not available or hidden behind more taps now, or only available in different views.

This project is probably aimed more at the power user who values function over form. Or people who absolutely cannot stand the tile-based new UI.

<img src="metadata/en-US/images/phoneScreenshots/1.png" width="250">
<img src="metadata/en-US/images/phoneScreenshots/2.png" width="250">
<img src="metadata/en-US/images/phoneScreenshots/3.png" width="250">

## Status

### What works

* index-v1 support.
* Privileged Extension support (in progress, need to release the extensions somewhere)
* Inline changelogs
* Screenshots
* Translation and Donations options
* Localized metadata

### Todo

* proper mirror support. They are currently not completely broken, but I'm not actually sure if mirrors are really used right now.
* There are some occasional performance problems where the UI gets stuttery, this is tracking in #43 (with more background in #23).


### Features NOT included

* Swap. I almost never use it and I don't know many people that do. There's still the official client if you need that functionality.
* App auto install via repo push
* The tile based UI
* The old xml based index format isn't supported
* Support for very old version of Android (< 4.0).
  * Currently minimum Android version is 4.4, supporting 4.0 would be possible with some effort, but I'm not sure that's worth it.
  * Even on Android 4.4 we can't currently support TLSv1.2, which is bad.
  * TLSv1.2 support on Android 4.4 is theoretically possible, but without using googles magic it's a lot of effort for very little benefit.
  * There's some recent discussion around this here: https://forum.f-droid.org/t/lack-of-tls-1-2-breaking-apps-in-older-androids/9823


## Building with Gradle

    ./gradlew assembleRelease

## FAQ

* Why does F-Droid Classic require "Unknown Sources" to install apps by default?

Because a regular Android app cannot act as a package manager on its
own. To do so, it would require system privileges (see below), similar
to what Google Play does.

* What about privileged extension support?

Currently being worked on. There will be a separate privileged extension for Classic. There might be an extension which supports multiple clients in the future though.

See here for some discussion about this: https://gitlab.com/fdroid/privileged-extension/issues/50

* Can I avoid enabling "Unknown Sources" by installing F-Droid Classic as a  privileged system app?

This used to be the case, but no longer is. Now the [Privileged
Extension](https://gitlab.com/fdroid/privileged-extension) is the one that should be placed in
the system. It can be bundled with a ROM or installed via a zip.

## Translation

Translations are here: https://weblate.bubu1.eu/projects/f-droid-classic/

[![Translation status](https://weblate.bubu1.eu/widgets/f-droid-classic/-/svg-badge.svg)](https://weblate.bubu1.eu/engage/f-droid-classic/?utm_source=widget)

## License

This program is Free Software: You can use, study share and improve it at your
will. Specifically you can redistribute and/or modify it under the terms of the
[GNU General Public License](https://www.gnu.org/licenses/gpl.html) as
published by the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Some icons are made by [Picol](http://www.flaticon.com/authors/picol),
[Icomoon](http://www.flaticon.com/authors/icomoon) or
[Dave Gandy](http://www.flaticon.com/authors/dave-gandy) from
[Flaticon](http://www.flaticon.com) or by Google and are licensed by
[Creative Commons BY 3.0](https://creativecommons.org/licenses/by/3.0/).

Other icons are from the
[Material Design Icon set](https://github.com/google/material-design-icons)
released under an
[Attribution 4.0 International license](https://creativecommons.org/licenses/by/4.0/).
