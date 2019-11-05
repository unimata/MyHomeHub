package com.homehub.dragan.myhomehub.Activities;


import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.homehub.dragan.myhomehub.Classes.model.Entity;
import com.homehub.dragan.myhomehub.Classes.model.ErrorMessage;
import com.homehub.dragan.myhomehub.Classes.model.Group;
import com.homehub.dragan.myhomehub.Classes.model.HomeHubServer;
import com.homehub.dragan.myhomehub.Classes.provider.DatabaseManager;
import com.homehub.dragan.myhomehub.Classes.provider.EntityWidgetProvider;
import com.homehub.dragan.myhomehub.Classes.provider.ServiceProvider;
import com.homehub.dragan.myhomehub.Classes.util.CommonUtil;
import com.homehub.dragan.myhomehub.R;

import java.util.ArrayList;

import retrofit2.Response;

/**
 * A login screen that offers login via username/password.
 */
public class LoginActivity extends HomeHubActivity {
    //Data
    public static final String EXTRA_IPADDRESS = "ip_address";
    public static final String EXTRA_FULL_URI = "full_uri";
    public static final String EXTRA_PASSWORD = "password";
    public static final String EXTRA_LAST_REQUEST = "last_request";
    private SharedPreferences mSharedPref;

    // UI
    private EditText mIpAddressView;
    private EditText mPasswordView;
    private TextView mSignupButton;
    private Snackbar mSnackbar;
    private Button mConnectButton;
    private UserLoginTask mAuthTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mIpAddressView = findViewById(R.id.input_URL);
        mPasswordView = findViewById(R.id.input_password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.btn_login || id == EditorInfo.IME_NULL || id == EditorInfo.IME_ACTION_DONE) {
                    attemptLogin();
                }
                return false;
            }
        });

        mConnectButton = findViewById(R.id.btn_login);
        mConnectButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mSignupButton = findViewById(R.id.link_signup);
        mSignupButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Sign-up activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mSharedPref == null) {
            new SharedPreferenceLoadingTask().execute();
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mSnackbar != null) {
            mSnackbar.dismiss();
        }

        if (mSharedPref == null) {
            Toast.makeText(this, "Please wait before retrying", Toast.LENGTH_SHORT).show();
            return;
        }



        // Reset errors.
        mIpAddressView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String baseURL = mIpAddressView.getText().toString().trim();
        String password = mPasswordView.getText().toString();

        if (baseURL.endsWith("/")) {
            baseURL = baseURL.substring(0, baseURL.length() - 1);
            mIpAddressView.setText(baseURL);
        }

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }
        //Ghetto but its for alpha
        if (password.equals("homehub1")){
            password = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiI5OTA2ODZjNDMwMjI0YjhhODZiNjYwOWJiMTM4N2MyOCIsImlhdCI6MTU3MTkwNzQwNiwiZXhwIjoxODg3MjY3NDA2fQ.IfiY8Vt5ie9IV9aGwtGsvXtJ53R5vfwzjSO9q8CTBK4";
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(baseURL)) {
            mIpAddressView.setError(getString(R.string.error_field_required));
            focusView = mIpAddressView;
            cancel = true;
        } else if (!(baseURL.startsWith("http://") || baseURL.startsWith("https://"))) {
            mIpAddressView.setError("error_invalid_baseurl");
            focusView = mIpAddressView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            String host = Uri.parse(baseURL).getHost();
            if (mAuthTask == null) {
                mAuthTask = new UserLoginTask(baseURL, host, password);
                mAuthTask.execute((Void) null);
            }
        }
    }

    private class UserLoginTask extends AsyncTask<Void, String, ErrorMessage> {

        private final String mUri;
        private final String mIpAddress;
        private final String mPassword;
        private final String mBearerHeader;
        private String mBoostrapData;

        UserLoginTask(String uri, String ipAddress, String password) {
            mUri = uri;
            mIpAddress = ipAddress;
            mPassword = password;
            mBearerHeader = "Bearer " + password;

            mIpAddressView.setEnabled(false);
            mPasswordView.setEnabled(false);
            mConnectButton.setEnabled(false);
        }

        @Override
        protected ErrorMessage doInBackground(Void... params) {
            try {
                Response<String> response = ServiceProvider.getRawApiService(mUri).rawStates(mBearerHeader).execute();

                if (response.code() != 200) {
                    if (response.code() == 401) {
                        return new ErrorMessage("Error 401", "error_invalid_password");
                    }
                    if (response.code() == 404) {
                        return new ErrorMessage("Error 404", "error_invalid_server");
                    }
                    return new ErrorMessage("Error" + response.code(), response.message());
                }

                mBoostrapData = response.body();
                final ArrayList<Entity> bootstrapResponse = CommonUtil.inflate(mBoostrapData, new TypeToken<ArrayList<Entity>>() {
                }.getType());

                SharedPreferences.Editor editor = mSharedPref.edit();
                editor.putString(EXTRA_FULL_URI, mUri);
                editor.putString(EXTRA_IPADDRESS, mIpAddress);
                editor.putString(EXTRA_PASSWORD, mPassword);
                editor.putInt("connectionIndex", 0);
                editor.putLong(EXTRA_LAST_REQUEST, System.currentTimeMillis()).apply();
                editor.apply();


                DatabaseManager databaseManager = DatabaseManager.getInstance(LoginActivity.this);
                databaseManager.updateTables(bootstrapResponse);
                databaseManager.addConnection(new HomeHubServer(mUri, mPassword));
            }
            catch (Exception e) {
                Log.d("Yo", "ERROR!");
                return new ErrorMessage(e.getMessage(), e.toString());
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            mConnectButton.setText(values[0]);
        }

        @Override
        protected void onPostExecute(final ErrorMessage errorMessage) {
            mAuthTask = null;

            if (errorMessage == null) {
                startMainActivity();
            } else {
                mIpAddressView.setEnabled(true);
                mPasswordView.setEnabled(true);
                mConnectButton.setEnabled(true);

                mPasswordView.requestFocus();
                showError(errorMessage.message);

            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }

    private void showError(String message) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(message);

        TextView textView = mSnackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        mSnackbar.getView().setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.common_google_signin_btn_text_light_disabled, null));
        mSnackbar.show();
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 0;
    }


    private void startMainActivity() {
        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }


    private class SharedPreferenceLoadingTask extends AsyncTask<Void, Void, ErrorMessage> {

        SharedPreferenceLoadingTask() {
        }

        @Override
        protected ErrorMessage doInBackground(Void... param) {

            PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.preferences, false);
            mSharedPref = getAppController().getSharedPref();

            int[] ids = AppWidgetManager.getInstance(LoginActivity.this).getAppWidgetIds(new ComponentName(LoginActivity.this, EntityWidgetProvider.class));
            if (ids.length > 0) {
                ArrayList<String> appWidgetIds = new ArrayList<>();
                for (int id : ids) {
                    appWidgetIds.add(Integer.toString(id));
                }
                DatabaseManager databaseManager = DatabaseManager.getInstance(LoginActivity.this);
                databaseManager.forceCreate();
                databaseManager.housekeepWidgets(appWidgetIds);
            }
            return null;
        }

        @Override
        protected void onPostExecute(ErrorMessage errorMessage) {
            if (errorMessage == null) {

                if (mSharedPref.getString(EXTRA_IPADDRESS, null) != null) {

                    DatabaseManager databaseManager = DatabaseManager.getInstance(LoginActivity.this);
                    ArrayList<Group> groups = databaseManager.getGroups();
                    ArrayList<HomeHubServer> connections = databaseManager.getConnections();

                    int dashboardCount = databaseManager.getDashboardCount();
                    if (groups.size() != 0 && connections.size() != 0 && dashboardCount > 0) {
                        startMainActivity();
                        return;
                    }
                }

                mIpAddressView.setText(mSharedPref.getString(EXTRA_FULL_URI, ""));
                if (mIpAddressView.getText().toString().trim().length() != 0) {
                    mPasswordView.requestFocus();
                } else {
                    mIpAddressView.requestFocus();
                }
            } else {
                mConnectButton.setVisibility(View.GONE);
            }
            super.onPostExecute(errorMessage);
        }
    }
}


