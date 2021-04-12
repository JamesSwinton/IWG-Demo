package com.zebra.jamesswinton.wfctileinterfacepoc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.arthenica.mobileffmpeg.ExecuteCallback;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.zebra.jamesswinton.wfctileinterfacepoc.adapters.TileAdapter;
import com.zebra.jamesswinton.wfctileinterfacepoc.data.AnnouncerResponse;
import com.zebra.jamesswinton.wfctileinterfacepoc.data.Config;
import com.zebra.jamesswinton.wfctileinterfacepoc.databinding.ActivityMainBinding;
import com.zebra.jamesswinton.wfctileinterfacepoc.networking.AnnouncerApi;
import com.zebra.jamesswinton.wfctileinterfacepoc.networking.RetrofitInstance;
import com.zebra.jamesswinton.wfctileinterfacepoc.utilities.CustomDialog;
import com.zebra.jamesswinton.wfctileinterfacepoc.utilities.FileHelper;
import com.zebra.jamesswinton.wfctileinterfacepoc.utilities.PermissionsHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;
import static com.zebra.jamesswinton.wfctileinterfacepoc.utilities.CustomDialog.DialogType.ERROR;
import static com.zebra.jamesswinton.wfctileinterfacepoc.utilities.CustomDialog.DialogType.SUCCESS;

public class MainActivity extends AppCompatActivity implements TileAdapter.OnTileClickedListener,
        Callback<AnnouncerResponse> {

    // Debugging
    private static final String TAG = "MainActivity";

    // UI
    private ActivityMainBinding mDataBinding;
    private AlertDialog mProgressDialog;

    // Data
    private Config mConfig;
    private TileAdapter mTileAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setSupportActionBar(mDataBinding.toolbar);

        // Init (Empty) Recycler View
        mTileAdapter = new TileAdapter(MainActivity.this, new ArrayList<>(),
                MainActivity.this);
        mDataBinding.tileRecyclerView.setLayoutManager(new GridLayoutManager(
                MainActivity.this, 1));
        mDataBinding.tileRecyclerView.setAdapter(mTileAdapter);

        new PermissionsHelper(this, () -> {
            Log.i(TAG, "Permissions Granted");
            try {
                // Load Config
                mConfig = FileHelper.loadConfigToMemoryFromFile(MainActivity.this);

                // Update Tile Adapter
                mTileAdapter.updateTiles(mConfig.getTiles());

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Error reading config file: "
                                + e.getMessage(), Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    /**
     * IWG Requests
     */

    @Override
    public void onTileClicked(Config.Tile tile) {
        Log.i(TAG, "Tile Clicked: " + tile.getUiConfig().getText());

        // Base Variables
        String apiKey = mConfig.getApiPassword();
        AnnouncerApi mAnnouncerApi = RetrofitInstance.getInstance(mConfig.getIwgUrl()).create(AnnouncerApi.class);
        String message = tile.getIwgConfig().getMessage();
        int eid = tile.getIwgConfig().getEid()[0];

        if (tile.getIwgConfig().getAttachmentType() == null) {
            // Show Dialog
            mProgressDialog = CustomDialog.buildLoadingDialog(this,
                    String.format(getString(R.string.sending_message_progress_dialog_text),
                            message, eid),
                    false);
            mProgressDialog.show();

            // Send Request
            mAnnouncerApi.sendMessage(apiKey, message, eid).enqueue(this);
        } else {
            // Get Extra Variables
            File file = new File(tile.getIwgConfig().getFilePath());
            String attachmentType = tile.getIwgConfig().getAttachmentType();

            // Show Dialog
            mProgressDialog = CustomDialog.buildLoadingDialog(this,
                    String.format(getString(R.string.sending_attachment_progress_dialog_text),
                            attachmentType, file.getAbsolutePath(), eid, message),
                    false);
            mProgressDialog.show();

            // Build Body
            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("message", message)
                    .addFormDataPart("eid", String.valueOf(eid))
                    .addFormDataPart("file", file.getName(),
                            RequestBody.create(MediaType.parse("application/octet-stream"),
                                    file)
                    )
                    .addFormDataPart("attachment",attachmentType.toLowerCase())
                    .build();

            // Send Request
            mAnnouncerApi.sendMessageWithContent(apiKey, body).enqueue(this);
        }
    }

    /**
     * API Response
     */

    @Override
    public void onResponse(@NonNull Call<AnnouncerResponse> call,
                           @NonNull Response<AnnouncerResponse> response) {
        // Remove Progress
        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }

        // Validate HTTP Response (200, 404, etc...)
        if (!response.isSuccessful()) {
            Log.e(TAG, "Unsuccessful Response: " + response.code());

            String responseDetail = "";
            if (response.errorBody() != null) {
                try {
                    responseDetail = response.errorBody().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            CustomDialog.showCustomDialog(this, ERROR,
                    "Unsuccessful Response!",
                    "The IWG returned an unsuccessful response code: <b>" + response.code()
                            + "</b> due to: <b>" + response.message() + "</b><br><br>" + responseDetail);
            return;
        }

        // Validate Body
        if (response.body() == null) {
            Log.e(TAG, "Response Body Was Null!");
            CustomDialog.showCustomDialog(this, ERROR,
                    "Unsuccessful Response!",
                    "The IWG returned an empty response body");
            return;
        }

        // Show Response
        CustomDialog.showCustomDialog(this, SUCCESS, response.body().getTitle(),
                response.body().getDetail());
    }

    @Override
    public void onFailure(@NonNull Call<AnnouncerResponse> call, @NonNull Throwable t) {
        Log.e(TAG, "onResponse: Unsuccessful - " + t.getMessage(), t);

        // Remove Progress
        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }

        // Show dialog
        CustomDialog.showCustomDialog(this, ERROR,
                "HTTP Request Failed!",
                "The server returned an unsuccessful response: " + t.getMessage());
    }

    /**
     * Menu Handling
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.open_wfc) {
            Intent launchIntent = getPackageManager()
                    .getLaunchIntentForPackage("com.symbol.wfc.pttpro");
            if (launchIntent != null) {
                startActivity(launchIntent);
            } else {
                Toast.makeText(this, "WFC Not Installed!", Toast.LENGTH_LONG).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}