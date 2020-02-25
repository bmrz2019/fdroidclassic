## F-Droid Classic

Join the matrix room: [#fdroidclassic:bubu1.eu](https://matrix.to/#/#fdroidclassic:bubu1.eu)

### Download

Download the (beta) apk here: https://bubu1.eu/fdroidclassic/fdroid/repo/eu.bubu1.fdroidclassic_1000.apk

This comes with it's own F-Droid repository for updates pre-configured.

Or add the repo to any other F-Droid client: https://bubu1.eu/fdroidclassic/fdroid/repo?fingerprint=5187CFD99F084FFAB2AD60D9D10B39203B89A46DD4862397FE1B1A4F3D46627A

### Project description

This is an alternative F-Droid client which is based on the older UI of the official Client (pre 1.0).
While I think that the new client UI looks pretty, it's sometimes quite clunky.
Lots of information that was provided clearly in the old UI is now not available or hidden behind more taps now, or only available in different views.

This project is probably aimed more at the power user who values function over form. Or people who absolutely cannot stand the tile-based new UI.

<img src="https://gitlab.com/Bubu/fdroidclassic/raw/master/screenshots/screenshot.png?inline=false"  width="400">

## Plan/Todo

### What should go in

* First of all will be index-v1 support.
* Support for the additional features provided by the new index format.
  * Screenshots
  * Inline changelogs
  * Localized metadata
  * Feature graphics (maybe, very low priority)
* Privileged Extension support (To be discussed how that will work exactly)
* ...

### What will be out

* Swap. I almost never use it and I don't know many people that do. There's still the official client if you need that functionality.
* App auto install via repo push
* The tile based UI
* The old xml based index support will be dropped
* Support for very old version of Android (< 4.0).
  * Currently minimum Android version is 4.4, supporting 4.0 would be possible with some effort, but I'm not sure that's worth it.
  * Even on Android 4.4 we can't currently support TLSv1.2, which is bad.
  * TLSv1.2 support on Android 4.4 is theoretically possible, but I'm not sure how much effort it would be.


## Building with Gradle

    ./gradlew assembleRelease

## FAQ

* Why does F-Droid Classic require "Unknown Sources" to install apps by default?

Because a regular Android app cannot act as a package manager on its
own. To do so, it would require system privileges (see below), similar
to what Google Play does.

* What about privileged extension support?

See here for some discussion about this: https://gitlab.com/fdroid/privileged-extension/issues/50
Meanwhile you can get a privext that supports both official F-Droid client and F-Droid classic here:

https://gitlab.com/Bubu/privileged-extension/-/jobs/96114583/artifacts/file/app/build/distributions/F-DroidPrivilegedExtension-0.2.8-3-gbffdd48-debug.zip

* Can I avoid enabling "Unknown Sources" by installing F-Droid Classic as a
  privileged system app?

This used to be the case, but no longer is. Now the [Privileged
Extension](https://gitlab.com/fdroid/privileged-extension) is the one that should be placed in
the system. It can be bundled with a ROM or installed via a zip, or
alternatively F-Droid can install it as a system app using root.

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


## Translation

TODO:
Everything can be translated.  See
[Translation and Localization](https://f-droid.org/docs/Translation_and_Localization)
for more info.
