package org.fdroid.fdroid.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.fdroid.fdroid.R;
import org.fdroid.fdroid.Utils;
import org.fdroid.fdroid.data.App;
import org.fdroid.fdroid.data.AppProvider;

public class ScreenShotsActivity extends AppCompatActivity {

    private static final String EXTRA_PACKAGE_NAME = "EXTRA_PACKAGE_NAME";
    private static final String EXTRA_START_POSITION = "EXTRA_START_POSITION";

    public static Intent getStartIntent(Context context, String packageName, int startPosition) {
        Intent intent = new Intent(context, ScreenShotsActivity.class);
        intent.putExtra(EXTRA_PACKAGE_NAME, packageName);
        intent.putExtra(EXTRA_START_POSITION, startPosition);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screenshots);

        String packageName = getIntent().getStringExtra(EXTRA_PACKAGE_NAME);
        int startPosition = getIntent().getIntExtra(EXTRA_START_POSITION, 0);

        App app = AppProvider.Helper.findHighestPriorityMetadata(getContentResolver(), packageName);
        String[] screenshots = app.getAllScreenshots(this);

        ViewPager viewPager = findViewById(R.id.screenshot_view_pager);
        ScreenShotPagerAdapter adapter = new ScreenShotPagerAdapter(getSupportFragmentManager(), screenshots);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(startPosition);

        // display some nice animation while swiping
        viewPager.setPageTransformer(true, new DepthPageTransformer());
    }

    private static class ScreenShotPagerAdapter extends FragmentStatePagerAdapter {

        private final String[] screenshots;

        ScreenShotPagerAdapter(FragmentManager fragmentManager, String[] screenshots) {
            super(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.screenshots = screenshots;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return ScreenShotPageFragment.newInstance(screenshots[position]);
        }

        @Override
        public int getCount() {
            return screenshots.length;
        }
    }

    /**
     * A single screenshot page.
     */
    public static class ScreenShotPageFragment extends Fragment {

        private static final String ARG_SCREENSHOT_URL = "ARG_SCREENSHOT_URL";

        static ScreenShotPageFragment newInstance(String screenshotUrl) {
            ScreenShotPageFragment fragment = new ScreenShotPageFragment();
            Bundle args = new Bundle();
            args.putString(ARG_SCREENSHOT_URL, screenshotUrl);
            fragment.setArguments(args);
            return fragment;
        }

        private String screenshotUrl;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            screenshotUrl = getArguments() != null ? getArguments().getString(ARG_SCREENSHOT_URL) : null;
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {

            DisplayImageOptions displayImageOptions = Utils.getDefaultDisplayImageOptionsBuilder()
                    .showImageOnFail(R.drawable.screenshot_placeholder)
                    .showImageOnLoading(R.drawable.screenshot_placeholder)
                    .showImageForEmptyUri(R.drawable.screenshot_placeholder)
                    .build();

            View rootView = inflater.inflate(R.layout.activity_screenshots_page, container, false);

            ImageView screenshotView = rootView.findViewById(R.id.screenshot);
            ImageLoader.getInstance().displayImage(screenshotUrl, screenshotView, displayImageOptions);

            return rootView;
        }
    }

    public static class DepthPageTransformer implements ViewPager.PageTransformer {

        public void transformPage(@NonNull View view, float position) {
            int pageWidth = view.getWidth();

            if (position <= 0) {
                // use the default slide transition when moving to the left page
                view.setAlpha(1);
                view.setTranslationX(0);

            } else if (position <= 1) {
                // fade the page out.
                view.setAlpha(1 - position);

                // add parallax effect
                view.setTranslationX(pageWidth * -position / 2);

            } else {
                // this page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }
}
