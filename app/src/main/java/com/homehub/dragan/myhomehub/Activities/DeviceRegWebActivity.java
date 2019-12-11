package com.homehub.dragan.myhomehub.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.homehub.dragan.myhomehub.Classes.firebase.FirebaseDatabaseHelper;
import com.homehub.dragan.myhomehub.Classes.model.Device_Registration;
import com.homehub.dragan.myhomehub.Classes.model.General_Form_User;
import com.homehub.dragan.myhomehub.R;

import java.util.List;

import okhttp3.Cookie;

public class DeviceRegWebActivity extends AppCompatActivity {

    private WebView browserWebView;
    private Button btnFillData;
    private Button btnFinish;
    private String deviceName;
    private String device;
    private String newDeviceRegKey;
    private String deviceURL;
    private ProgressBar loadingWebviewPB;
    private String firstName;
    private String lastName;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_reg_web);


        //get user data from firebase
        new FirebaseDatabaseHelper().readUsers(new FirebaseDatabaseHelper.UserDataStatus() {
            @Override
            public void UserDataIsLoaded(List<General_Form_User> users, List<String> keys) {
                firstName = users.get(0).getFirst_name();
                lastName = users.get(0).getLast_name();
                email = users.get(0).getEmail();
            }

            @Override
            public void UserDataIsUpdated() {

            }

            @Override
            public void DataIsInserted() {

            }



            @Override
            public void DataIsDeleted() {

            }
        });


        //get strings and key from previous activity
        deviceName = getIntent().getStringExtra("deviceName");
        device = getIntent().getStringExtra("device");
        newDeviceRegKey = getIntent().getStringExtra("newDeviceRegKey");
        deviceURL = getIntent().getStringExtra("deviceURL");

        //get btn object
        btnFinish = findViewById(R.id.btnFinish);
        btnFillData = findViewById(R.id.btnFillData);
        btnFinish.setEnabled(false);
        btnFillData.setEnabled(false);

        //get pb object
        loadingWebviewPB = findViewById(R.id.loading_webview_pb);


        //TODO: Edit and improve webview when possible

        // get webview object
        browserWebView = findViewById(R.id.webViewBrowser);

        //dialog to show webview loading until done
        browserWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                if (loadingWebviewPB.isShown()) {
                    loadingWebviewPB.setVisibility(View.GONE);
                    btnFinish.setEnabled(true);
                    btnFillData.setEnabled(true);
                }

            }
        });

        //webvieww settings
        browserWebView.getSettings().setSaveFormData(false);
        browserWebView.getSettings().setJavaScriptEnabled(true);
        browserWebView.getSettings().setLoadWithOverviewMode(true);
        browserWebView.getSettings().setUseWideViewPort(true);
        browserWebView.getSettings().setDomStorageEnabled(true);
        browserWebView.clearCache(true);
        browserWebView.clearHistory();

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookies(null);


        //load dummy url for now
        browserWebView.loadUrl(deviceURL);

        //set up event listener for fill data button
        btnFillData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String js = "javascript:" +
                        "var addFirstName =document.getElementById('firstName').value = '" + firstName + "'; "  +
                        "var addLastName =document.getElementById('lastName').value = '" + lastName + "'; ";

                browserWebView.loadUrl(js);
            }
        });


        //set up event listener for finish button
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO: Is there another way to confirm that the user was able to register sucessfully??

                //show dialog to urge user to make sure to CONFIRM that registration process worked
                AlertDialog.Builder builder = new AlertDialog.Builder(DeviceRegWebActivity.this);
                builder.setMessage("Please be sure you have registered successfully.")
                        .setPositiveButton("Continue and Save", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                //TODO: save user data to create new device registration instance to add to the list
                                //get all new incoming data for device registration and sent to firebase
                                Device_Registration deviceReg = new Device_Registration(device, deviceName);

                                new FirebaseDatabaseHelper().addDeviceReg(newDeviceRegKey, deviceReg, new FirebaseDatabaseHelper.DeviceDataStatus() {
                                    @Override
                                    public void DeviceDataIsLoaded(List<Device_Registration> deviceRegistrations, List<String> keys) {

                                    }

                                    @Override
                                    public void DeviceDataIsInserted() {
                                        Toast.makeText(DeviceRegWebActivity.this, "Device registration added successfully.", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void DeviceDataIsUpdated() {

                                    }

                                    @Override
                                    public void DeviceDataIsDeleted() {

                                    }
                                });

                                //User confirmed that they did it, so finish and send broadcast to finish previous activity
                                Intent intent = new Intent("finish_activity");
                                sendBroadcast(intent);

                                finish();

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });


    }

    @Override
    public void onResume(){
        super.onResume();

    }

    @Override
    public void onDestroy(){
        super.onDestroy();

    }


}
