package com.example.application.musicdownloader;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.application.musicdownloader.api.APIClientInstance;
import com.example.application.musicdownloader.api.github.GithubData;
import com.example.application.musicdownloader.api.github.GithubDataService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AboutActivity extends AppCompatActivity {
    GithubData data;
    private LinearLayout updateLayout;
    private ProgressBar spinProgress;
    private TextView checkUpdateTextView;
    private Button updateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        updateLayout = findViewById(R.id.update_layout);
        spinProgress = findViewById(R.id.spin_progress);
        checkUpdateTextView = findViewById(R.id.check_updates_textview);
        updateButton = findViewById(R.id.update_button);

        checkUpdates();
    }

    private void checkUpdates() {
        updateLayout.setVisibility(View.VISIBLE);

        GithubDataService service = APIClientInstance.getGithubRetrofitInstance().create(GithubDataService.class);
        Call<GithubData> call = service.getLatestRelease();

        call.enqueue(new Callback<GithubData>() {
            @Override
            public void onResponse(Call<GithubData> call, Response<GithubData> response) {
                runOnUiThread(() -> spinProgress.setVisibility(View.GONE));
                Log.i(MainActivity.TAG, "Obtained Github response");

                if (!response.isSuccessful() || response.body() == null) {
                    Log.d(MainActivity.TAG, "Gtihub error: " + response.message());
                } else {
                    data = response.body();
                    PackageInfo packageInfo = null;
                    try {
                        packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }

                    String versionName = packageInfo != null ? packageInfo.versionName : "";
                    Log.d(MainActivity.TAG, "Current version: " + data.getTag_name() +
                            " Latest version: " + versionName);
                    if (versionName.equals(data.getTag_name())) {
                        // latest version.
                        updateLayout.setVisibility(View.GONE);
                    } else {
                        // update available.
                        checkUpdateTextView.setTextColor(Color.BLACK);
                        checkUpdateTextView.setText("New update available!");
                        updateButton.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<GithubData> call, Throwable t) {
                spinProgress.setVisibility(View.GONE);
                Log.d(MainActivity.TAG, "GitHub error: " + t.getMessage());
            }
        });
    }

    public void update(View view) {
        Log.d(MainActivity.TAG, "Opening " + data.getHtml_url());
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data.getHtml_url()));
        startActivity(browserIntent);
    }
}
