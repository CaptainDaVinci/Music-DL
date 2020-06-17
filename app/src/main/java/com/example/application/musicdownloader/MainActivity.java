package com.example.application.musicdownloader;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.application.musicdownloader.api.APIClientInstance;
import com.example.application.musicdownloader.api.github.model.GithubData;
import com.example.application.musicdownloader.api.github.service.GithubDataService;
import com.example.application.musicdownloader.api.server.model.ServerDownloadData;
import com.example.application.musicdownloader.api.server.model.ServerQueryData;
import com.example.application.musicdownloader.api.server.service.ServerDataService;
import com.example.application.musicdownloader.query.Encoding;
import com.example.application.musicdownloader.query.Quality;
import com.example.application.musicdownloader.query.Query;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MUSICDL";
    private EditText inputText;
    private TextView statusTextView;
    private ProgressBar spinningProgress;
    private Query query;
    private GithubData data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkForUpdate();

        if (getIntent().getExtras() != null) {
            Log.d(TAG, "From notification: " + getIntent().getExtras());
        }

        Log.i(TAG, "Starting main activity");
        inputText = findViewById(R.id.input_text);
        statusTextView = findViewById(R.id.status_text);
        spinningProgress = findViewById(R.id.spinning_progress);

        FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        firebaseRemoteConfig.setDefaults(R.xml.remote_config);
        firebaseRemoteConfig.fetchAndActivate();

        inputText.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN
                    && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                try {
                    buttonClicked(findViewById(R.id.search_button));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
            return false;
        });
    }

    private void checkForUpdate() {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.coordinator_layout),
                "New update available", Snackbar.LENGTH_INDEFINITE)
                .setActionTextColor(getResources().getColor(R.color.colorPrimary))
                .setAction("Update", view -> {
                    if (data != null) {
                        Log.d(MainActivity.TAG, "Opening " + data.getHtml_url());
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data.getHtml_url()));
                        startActivity(browserIntent);
                    }
                });

        GithubDataService service = APIClientInstance.getGithubDataService();
        Call<GithubData> call = service.getLatestRelease();

        call.enqueue(new Callback<GithubData>() {
            @Override
            public void onResponse(Call<GithubData> call, Response<GithubData> response) {
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
                    if (!versionName.equals(data.getTag_name())) {
                        // update available.
                        snackbar.show();
                    }
                }
            }

            @Override
            public void onFailure(Call<GithubData> call, Throwable t) {
            }
        });
    }

    private void setStatus(String msg, int color) {
        statusTextView.setTextColor(color);
        statusTextView.setText(msg);
    }

    public void buttonClicked(final View view) {
        Log.i(TAG, "Download button clicked");

        if (this.getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        setStatus("", Color.BLACK);
        query = new Query();
        query.setSearch(inputText.getText().toString());

        if (query.getSearch().isEmpty()) {
            Toast.makeText(getApplicationContext(),
                    "Search Field is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioGroup format_radio_group = findViewById(R.id.format_radio_group);
        RadioGroup quality_radio_group = findViewById(R.id.qualilty_radio_group);

        switch (format_radio_group.getCheckedRadioButtonId()) {
            case R.id.radio_audio:
                query.setEncoding(Encoding.mp3);
                break;
            case R.id.radio_video:
                query.setEncoding(Encoding.mp4);
                break;
        }

        switch (quality_radio_group.getCheckedRadioButtonId()) {
            case R.id.radio_high:
                query.setQuality(Quality.high);
                break;
            case R.id.radio_low:
                query.setQuality(Quality.low);
                break;
        }

        if (!hasNetwork()) {
            Toast.makeText(getApplicationContext(), "Network connection not found",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        Log.i(TAG, "Query: " + query);
        searchQuery();
    }

    public void clearButton(View view) {
        inputText.setText("");
        setStatus("", Color.BLACK);
        spinningProgress.setVisibility(View.GONE);
    }

    private boolean hasNetwork() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    void searchQuery() {
        setStatus("Searching", Color.BLACK);
        spinningProgress.setVisibility(View.VISIBLE);

        ServerDataService service = APIClientInstance.getServerDataService();
        Call<ServerQueryData> call = service.getYouTubeId(query.getSearch());

        call.enqueue(new Callback<ServerQueryData>() {
            @Override
            public void onResponse(Call<ServerQueryData> call, Response<ServerQueryData> response) {
                Log.i(TAG, "Obtained YouTube response");
                if (!response.isSuccessful() || response.body() == null) {
                    spinningProgress.setVisibility(View.GONE);
                    setStatus("Oh, snap! Search failed", Color.BLACK);
                    Log.d(TAG, "YouTube error: " + response.message());
                    Log.d(TAG, "Headers: " + response.headers().toString());
                    Log.d(TAG, "Body: " + response.body());
                    Log.d(TAG, "Successful: " + response.isSuccessful());
                } else {
                    showResponse(response.body());
                }
            }

            @Override
            public void onFailure(Call<ServerQueryData> call, Throwable t) {
                spinningProgress.setVisibility(View.GONE);
                setStatus("Oh, snap! Search failed", Color.BLACK);
                Log.d(TAG, "YouTube error: " + t.getMessage());
            }
        });
    }

    private void showResponse(final ServerQueryData data) {
        spinningProgress.setVisibility(View.GONE);
        setStatus("", Color.BLACK);

        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.activity_dialog, null);
        TextView titleTextView = view.findViewById(R.id.title);
        ImageView imageView = view.findViewById(R.id.thumbnail);

        Log.i(TAG, "Set information for confirmation dialog");
        titleTextView.setText(data.getTitle());
        setThumbnail(imageView, data.getThumbnailUrl());

        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    getDownloadLink(data);
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    setStatus("Cancelled", Color.BLACK);
                    break;
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view)
                .setTitle("Confirmation Alert")
                .setMessage("Download is about to start\nAre you sure you want to download ?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener)
                .setCancelable(false)
                .show();
    }

    private void setThumbnail(final ImageView imageView, String thumbnailUrl) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(thumbnailUrl).build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.d(TAG, "Thumbnail: " + e.getMessage());
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) {
                try {
                    runOnUiThread(() -> imageView.setImageBitmap(BitmapFactory.decodeStream(response.body().byteStream())));
                } catch (Exception e) {
                    Log.d(TAG, "Thumbnail onResponse: " + e.getMessage());
                    // pass
                }
            }
        });
    }

    private void getDownloadLink(final ServerQueryData data) {
        spinningProgress.setVisibility(View.VISIBLE);
        setStatus("Converting...", Color.BLACK);

        ServerDataService service = APIClientInstance.getServerDataService();
        Call<ServerDownloadData> call = service.getDownloadLink(data.getId(),
                query.getEncoding().toString(),
                query.getQuality().toString());

        call.enqueue(new Callback<ServerDownloadData>() {
            @Override
            public void onResponse(Call<ServerDownloadData> call, Response<ServerDownloadData> response) {
                spinningProgress.setVisibility(View.GONE);
                Log.i(TAG, "Obtained server response");

                if (!response.isSuccessful() || response.body() == null) {
                    setStatus("Oh, snap! Conversion failed", Color.BLACK);
                    Log.d(TAG, "Server error: " + response.message());
                    Log.d(TAG, "Headers: " + response.headers().toString());
                    Log.d(TAG, "Body: " + response.body());
                    Log.d(TAG, "Successful: " + response.isSuccessful());
                } else {
                    String downloadLink = response.body().getDownloadLink();
                    Log.d(TAG, "Download link: " + downloadLink);
                    downloadFile(downloadLink, data.getTitle());
                }
            }

            @Override
            public void onFailure(Call<ServerDownloadData> call, Throwable t) {
                spinningProgress.setVisibility(View.GONE);
                Log.d(TAG, "Server error: " + t.getMessage());
                setStatus("Oh, snap! Conversion failed.", Color.BLACK);
            }
        });
    }

    private void downloadFile(String downloadLink, String fileName) {
        if (downloadLink == null) {
            setStatus("Failed to get download link", Color.BLACK);
            return;
        }

        fileName = fileName.replaceAll("[^a-zA-Z]", "-") + "." +
                query.getEncoding().toString();
        downloadLink = downloadLink + "?filename=" + fileName;
        Log.d(TAG, "Download link: " + downloadLink);

        setStatus("Opening " + downloadLink + " in browser...", Color.BLACK);
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(downloadLink));
        startActivity(browserIntent);
    }

}