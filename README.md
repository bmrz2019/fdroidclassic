## F-Droid Classic

This is an alternative F-Droid client which is based on the older UI of the official Client (pre 1.0).
While I think that the new client UI looks pretty, it's also quite clunky in my opinion.
Lots of information that was provided clearly in the old UI is now not avail;able or hidden at behind more taps or only available in different views.

This project is probably aimed more at the power user who values function over form. Or people which absolutely cannot stand the tile-based new UI.

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

* Swap, at least initially. I almost never use it and I don't know many people that do. There's still the official client if you need that functionality.
* The tile based UI (at least until it becomes far more stable)
* The old xml based index support will be dropped
* Repo push requests

### Undecided

* Support for older Android versions.


## Building with Gradle

    ./gradlew assembleRelease

## FAQ

* Why does F-Droid Classic require "Unknown Sources" to install apps by default?

Because a regular Android app cannot act as a package manager on its
own. To do so, it would require system privileges (see below), similar
to what Google Play does.

* Can I avoid enabling "Unknown Sources" by installing F-Droid Classic as a
  privileged system app?


TODO: See what to do about privext.
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
