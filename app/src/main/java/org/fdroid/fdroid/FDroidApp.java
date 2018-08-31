/*
 * Copyright (C) 2010-12  Ciaran Gultnieks, ciaran@ciarang.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.fdroid.fdroid;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.util.LongSparseArray;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.disc.impl.LimitedAgeDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.apache.commons.net.util.SubnetUtils;
import org.fdroid.fdroid.Preferences.ChangeListener;
import org.fdroid.fdroid.Preferences.Theme;
import org.fdroid.fdroid.compat.PRNGFixes;
import org.fdroid.fdroid.data.AppProvider;
import org.fdroid.fdroid.data.InstalledAppProviderService;
import org.fdroid.fdroid.data.Repo;
import org.fdroid.fdroid.data.RepoProvider;
import org.fdroid.fdroid.installer.InstallHistoryService;
import org.fdroid.fdroid.net.IconDownloader;

import java.io.IOException;
import java.net.URL;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.security.Security;
import java.util.List;
import java.util.Locale;

import info.guardianproject.netcipher.NetCipher;
import info.guardianproject.netcipher.proxy.OrbotHelper;

public class FDroidApp extends Application {

    private static final String TAG = "FDroidApp";

    public static final String SYSTEM_DIR_NAME = Environment.getRootDirectory().getAbsolutePath();

    private static Locale locale;

    // for the local repo on this device, all static since there is only one
    public static volatile int port;
    public static volatile String ipAddressString;
    public static volatile SubnetUtils.SubnetInfo subnetInfo;
    public static volatile String ssid;
    public static volatile String bssid;
    public static volatile Repo repo = new Repo();

    private static Theme curTheme = Theme.light;

    public void reloadTheme() {
        curTheme = Preferences.get().getTheme();
    }

    public void applyTheme(Activity activity) {
        activity.setTheme(getCurThemeResId());
    }
    public static Context getInstance() {
        return instance;
    }
    private static FDroidApp instance;

    public static int getCurThemeResId() {
        switch (curTheme) {
            case light:
                return R.style.AppThemeLight;
            case dark:
                return R.style.AppThemeDark;
            case night:
                return R.style.AppThemeNight;
            default:
                return R.style.AppThemeLight;
        }
    }

    public void applyDialogTheme(Activity activity) {
        activity.setTheme(getCurDialogThemeResId());
    }

    private static int getCurDialogThemeResId() {
        switch (curTheme) {
            case light:
                return R.style.MinWithDialogBaseThemeLight;
            case dark:
                return R.style.MinWithDialogBaseThemeDark;
            case night:
                return R.style.MinWithDialogBaseThemeDark;
            default:
                return R.style.MinWithDialogBaseThemeLight;
        }
    }

    public void updateLanguage() {
        Context ctx = getBaseContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        String lang = prefs.getString(Preferences.PREF_LANGUAGE, "");
        locale = Utils.getLocaleFromAndroidLangTag(lang);
        applyLanguage();
    }

    private void applyLanguage() {
        Context ctx = getBaseContext();
        Configuration cfg = new Configuration();
        cfg.locale = locale == null ? Locale.getDefault() : locale;
        ctx.getResources().updateConfiguration(cfg, null);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        applyLanguage();
    }
    public static int getTimeout() {
        return timeout;
    }
    public static String getMirror(String urlString, long repoId) throws IOException {
        return getMirror(urlString, RepoProvider.Helper.findById(getInstance(), repoId));
    }

    public static String getMirror(String urlString, Repo repo2) throws IOException {
        if (repo2.hasMirrors()) {
            String lastWorkingMirror = lastWorkingMirrorArray.get(repo2.getId());
            if (lastWorkingMirror == null) {
                lastWorkingMirror = repo2.address;
            }
            if (numTries <= 0) {
                if (timeout == 10000) {
                    timeout = 30000;
                    numTries = Integer.MAX_VALUE;
                } else if (timeout == 30000) {
                    timeout = 60000;
                    numTries = Integer.MAX_VALUE;
                } else {
                    Utils.debugLog(TAG, "Mirrors: Giving up");
                    throw new IOException("Ran out of mirrors");
                }
            }
            if (numTries == Integer.MAX_VALUE) {
                numTries = repo2.getMirrorCount();
            }
            String mirror = repo2.getMirror(lastWorkingMirror);
            String newUrl = urlString.replace(lastWorkingMirror, mirror);
            Utils.debugLog(TAG, "Trying mirror " + mirror + " after " + lastWorkingMirror + " failed," +
                    " timeout=" + timeout / 1000 + "s");
            lastWorkingMirrorArray.put(repo2.getId(), mirror);
            numTries--;
            return newUrl;
        } else {
            throw new IOException("No mirrors available");
        }
    }
    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
        }
        updateLanguage();

        PRNGFixes.apply();

        Preferences.setup(this);
        curTheme = Preferences.get().getTheme();
        Preferences.get().configureProxy();

        InstalledAppProviderService.compareToPackageManager(this);

        // If the user changes the preference to do with filtering rooted apps,
        // it is easier to just notify a change in the app provider,
        // so that the newly updated list will correctly filter relevant apps.
        Preferences.get().registerAppsRequiringRootChangeListener(new Preferences.ChangeListener() {
            @Override
            public void onPreferenceChange() {
                getContentResolver().notifyChange(AppProvider.getContentUri(), null);
            }
        });

        // If the user changes the preference to do with filtering anti-feature apps,
        // it is easier to just notify a change in the app provider,
        // so that the newly updated list will correctly filter relevant apps.
        Preferences.get().registerAppsRequiringAntiFeaturesChangeListener(new Preferences.ChangeListener() {
            @Override
            public void onPreferenceChange() {
                getContentResolver().notifyChange(AppProvider.getContentUri(), null);
            }
        });

        final Context context = this;
        Preferences.get().registerUnstableUpdatesChangeListener(new Preferences.ChangeListener() {
            @Override
            public void onPreferenceChange() {
                AppProvider.Helper.calcSuggestedApks(context);
            }
        });

        CleanCacheService.schedule(this);

        UpdateService.schedule(getApplicationContext());

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .imageDownloader(new IconDownloader(getApplicationContext()))
                .diskCache(new LimitedAgeDiskCache(
                        Utils.getIconsCacheDir(this),
                        null,
                        new FileNameGenerator() {
                            @Override
                            public String generate(String imageUri) {
                                return imageUri.substring(
                                        imageUri.lastIndexOf('/') + 1);
                            }
                        },
                        // 30 days in secs: 30*24*60*60 = 2592000
                        2592000)
                )
                .threadPoolSize(4)
                .threadPriority(Thread.NORM_PRIORITY - 2) // Default is NORM_PRIORITY - 1
                .build();
        ImageLoader.getInstance().init(config);

        configureTor(Preferences.get().isTorEnabled());

        if (Preferences.get().isKeepingInstallHistory()) {
            InstallHistoryService.register(this);
        }

        String packageName = getString(R.string.install_history_reader_packageName);
        String unset = getString(R.string.install_history_reader_packageName_UNSET);
        if (!TextUtils.equals(packageName, unset)) {
            int modeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
            if (Build.VERSION.SDK_INT >= 19) {
                modeFlags |= Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION;
            }
            grantUriPermission(packageName, InstallHistoryService.LOG_URI, modeFlags);
        }
    }

    private static volatile LongSparseArray<String> lastWorkingMirrorArray = new LongSparseArray<>(1);
    private static volatile int numTries = Integer.MAX_VALUE;
    private static volatile int timeout = 10000;
    public static void resetMirrorVars() {
        // Reset last working mirror, numtries, and timeout
        for (int i = 0; i < lastWorkingMirrorArray.size(); i++) {
            lastWorkingMirrorArray.removeAt(i);
        }
        numTries = Integer.MAX_VALUE;
        timeout = 10000;
    }

    private static boolean useTor;

    /**
     * Set the proxy settings based on whether Tor should be enabled or not.
     */
    private static void configureTor(boolean enabled) {
        useTor = enabled;
        if (useTor) {
            NetCipher.useTor();
        } else {
            NetCipher.clearProxy();
        }
    }

    public static void checkStartTor(Context context) {
        if (useTor) {
            OrbotHelper.requestStartTor(context);
        }
    }

    public static boolean isUsingTor() {
        return useTor;
    }
}
