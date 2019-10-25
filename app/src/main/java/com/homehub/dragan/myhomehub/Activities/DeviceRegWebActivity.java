package com.homehub.dragan.myhomehub.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.homehub.dragan.myhomehub.R;

public class DeviceRegWebActivity extends AppCompatActivity {

    private WebView browserWebView;
    private Button btnFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_reg_web);


        //get btn object
        btnFinish = findViewById(R.id.btnFinish);


        //TODO: CREATE AUTOFILL SERVICE!!!!!!!!
        //TODO: Edit and improve webview when possible

        // get webview object
        browserWebView = findViewById(R.id.webViewBrowser);

        browserWebView.setWebViewClient(new WebViewClient());
        browserWebView.getSettings().setJavaScriptEnabled(true);
        browserWebView.getSettings().setLoadWithOverviewMode(true);
        browserWebView.getSettings().setUseWideViewPort(true);

        //load dummy url for now
        browserWebView.loadUrl("https://accounts.google.com/");



        //set up event listener for button
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //disable button to prevent double click
                btnFinish.setEnabled(false);

                //TODO: Is there another way to confirm that the user was able to register sucessfully??

                //show dialog to urge user to make sure to CONFIRM that registration process worked
                AlertDialog.Builder builder = new AlertDialog.Builder(DeviceRegWebActivity.this);
                builder.setMessage("Please be sure you have registered successfully.")
                        .setPositiveButton("Continue and Save", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                //TODO: save user data to create new device registration instance to add to the list

                                //User confirmed that they did it, so finish and send broadcast to finish previous activity
                                Intent intent = new Intent("finish_activity");
                                sendBroadcast(intent);

                                finish();

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog

                                //re-enable button
                                btnFinish.setEnabled(true);
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

        //re-enable button
        btnFinish.setEnabled(true);
    }
}
