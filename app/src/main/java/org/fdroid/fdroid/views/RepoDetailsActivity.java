package org.fdroid.fdroid.views;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.fdroid.fdroid.FDroidApp;
import org.fdroid.fdroid.QrGenAsyncTask;
import org.fdroid.fdroid.R;
import org.fdroid.fdroid.UpdateService;
import org.fdroid.fdroid.Utils;
import org.fdroid.fdroid.data.Repo;
import org.fdroid.fdroid.data.RepoProvider;
import org.fdroid.fdroid.data.Schema.RepoTable;

public class RepoDetailsActivity extends AppCompatActivity {
    private static final String TAG = "RepoDetailsActivity";

    public static final String ARG_REPO_ID = "repo_id";

    /**
     * If the repo has been updated at least once, then we will show
     * all of this info, otherwise they will be hidden.
     */
    private static final int[] SHOW_IF_EXISTS = {
        R.id.label_repo_name,
        R.id.text_repo_name,
        R.id.text_description,
        R.id.label_num_apps,
        R.id.text_num_apps,
        R.id.label_last_update,
        R.id.text_last_update,
        R.id.label_username,
        R.id.text_username,
        R.id.button_edit_credentials,
        R.id.label_repo_fingerprint,
        R.id.text_repo_fingerprint,
        R.id.text_repo_fingerprint_description,
    };
    /**
     * If the repo has <em>not</em> been updated yet, then we only show
     * these, otherwise they are hidden.
     */
    private static final int[] HIDE_IF_EXISTS = {
        R.id.text_not_yet_updated,
    };
    private Repo repo;
    private long repoId;
    private View repoView;

    /**
     * Help function to make switching between two view states easier.
     * Perhaps there is a better way to do this. I recall that using Adobe
     * Flex, there was a thing called "ViewStates" for exactly this. Wonder if
     * that exists in  Android?
     */
    private static void setMultipleViewVisibility(View parent,
                                                  int[] viewIds,
                                                  int visibility) {
        for (int viewId : viewIds) {
            parent.findViewById(viewId).setVisibility(visibility);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ((FDroidApp) getApplication()).applyTheme(this);
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.repodetails);
        repoView = findViewById(R.id.repoView);

        repoId = getIntent().getLongExtra(ARG_REPO_ID, 0);
        final String[] projection = {
                RepoTable.Cols.NAME,
                RepoTable.Cols.ADDRESS,
                RepoTable.Cols.FINGERPRINT,
        };
        repo = RepoProvider.Helper.findById(this, repoId, projection);

        TextView inputUrl = (TextView) findViewById(R.id.input_repo_url);
        inputUrl.setText(repo.address);

        Uri uri = Uri.parse(repo.address);
        uri = uri.buildUpon().appendQueryParameter("fingerprint", repo.fingerprint).build();
        String qrUriString = uri.toString();
        new QrGenAsyncTask(this, R.id.qr_code).execute(qrUriString);
    }


    @Override
    public void onResume() {
        super.onResume();

        /*
         * After, for example, a repo update, the details will have changed in the
         * database. However, or local reference to the Repo object will not
         * have been updated. The safest way to deal with this is to reload the
         * repo object directly from the database.
         */
        repo = RepoProvider.Helper.findById(this, repoId);
        updateRepoView();

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(UpdateService.LOCAL_ACTION_STATUS));
    }

    @Override
    public void onNewIntent(Intent i) {
        // onResume gets called after this to handle the intent
        setIntent(i);
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int statusCode = intent.getIntExtra(UpdateService.EXTRA_STATUS_CODE, -1);
            if (statusCode == UpdateService.STATUS_COMPLETE_WITH_CHANGES) {
                updateRepoView();
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.repo_details_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.menu_delete:
                promptForDelete();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupDescription(View parent, Repo repo) {

        TextView descriptionLabel = (TextView) parent.findViewById(R.id.label_description);
        TextView description = (TextView) parent.findViewById(R.id.text_description);

        if (TextUtils.isEmpty(repo.description)) {
            descriptionLabel.setVisibility(View.GONE);
            description.setVisibility(View.GONE);
            description.setText("");
        } else {
            descriptionLabel.setVisibility(View.VISIBLE);
            description.setVisibility(View.VISIBLE);
            description.setText(repo.description.replaceAll("\n", " "));
        }
    }

    private void setupRepoFingerprint(View parent, Repo repo) {
        TextView repoFingerprintView = (TextView) parent.findViewById(R.id.text_repo_fingerprint);
        TextView repoFingerprintDescView = (TextView) parent.findViewById(R.id.text_repo_fingerprint_description);

        String repoFingerprint;

        // TODO show the current state of the signature check, not just whether there is a key or not
        if (TextUtils.isEmpty(repo.fingerprint) && TextUtils.isEmpty(repo.signingCertificate)) {
            repoFingerprint = getResources().getString(R.string.unsigned);
            repoFingerprintView.setTextColor(getResources().getColor(R.color.unsigned));
            repoFingerprintDescView.setVisibility(View.VISIBLE);
            repoFingerprintDescView.setText(getResources().getString(R.string.unsigned_description));
        } else {
            // this is based on repo.fingerprint always existing, which it should
            repoFingerprint = Utils.formatFingerprint(this, repo.fingerprint);
            repoFingerprintDescView.setVisibility(View.GONE);
        }

        repoFingerprintView.setText(repoFingerprint);
    }

    private void setupCredentials(View parent, Repo repo) {

        TextView usernameLabel = (TextView) parent.findViewById(R.id.label_username);
        TextView username = (TextView) parent.findViewById(R.id.text_username);
        Button changePassword = (Button) parent.findViewById(R.id.button_edit_credentials);

        if (TextUtils.isEmpty(repo.username)) {
            usernameLabel.setVisibility(View.GONE);
            username.setVisibility(View.GONE);
            username.setText("");
            changePassword.setVisibility(View.GONE);
        } else {
            usernameLabel.setVisibility(View.VISIBLE);
            username.setVisibility(View.VISIBLE);
            username.setText(repo.username);
            changePassword.setVisibility(View.VISIBLE);
        }
    }

    private void updateRepoView() {

        if (repo.hasBeenUpdated()) {
            updateViewForExistingRepo(repoView);
        } else {
            updateViewForNewRepo(repoView);
        }

    }

    private void updateViewForNewRepo(View repoView) {
        setMultipleViewVisibility(repoView, HIDE_IF_EXISTS, View.VISIBLE);
        setMultipleViewVisibility(repoView, SHOW_IF_EXISTS, View.GONE);
    }

    private void updateViewForExistingRepo(View repoView) {
        setMultipleViewVisibility(repoView, SHOW_IF_EXISTS, View.VISIBLE);
        setMultipleViewVisibility(repoView, HIDE_IF_EXISTS, View.GONE);

        TextView name = (TextView) repoView.findViewById(R.id.text_repo_name);
        TextView numApps = (TextView) repoView.findViewById(R.id.text_num_apps);
        TextView lastUpdated = (TextView) repoView.findViewById(R.id.text_last_update);

        name.setText(repo.name);

        int appCount = RepoProvider.Helper.countAppsForRepo(this, repoId);
        numApps.setText(Integer.toString(appCount));

        setupDescription(repoView, repo);
        setupRepoFingerprint(repoView, repo);
        setupCredentials(repoView, repo);

        // Repos that existed before this feature was supported will have an
        // "Unknown" last update until next time they update...
        if (repo.lastUpdated == null) {
            lastUpdated.setText(R.string.unknown);
        } else {
            int format = DateUtils.isToday(repo.lastUpdated.getTime()) ?
                    DateUtils.FORMAT_SHOW_TIME :
                    DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE;
            lastUpdated.setText(DateUtils.formatDateTime(this,
                    repo.lastUpdated.getTime(), format));
        }
    }

    private void promptForDelete() {
        new AlertDialog.Builder(this)
            .setTitle(R.string.repo_confirm_delete_title)
            .setMessage(R.string.repo_confirm_delete_body)
            .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    RepoProvider.Helper.remove(getApplicationContext(), repoId);
                    finish();
                }
            }).setNegativeButton(android.R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing...
                    }
                }
            ).show();
    }
}
