package com.homehub.dragan.myhomehub.Activities;

import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.homehub.dragan.myhomehub.Classes.HomeHub;
import com.homehub.dragan.myhomehub.Classes.model.ErrorMessage;
import com.homehub.dragan.myhomehub.Classes.model.Group;
import com.homehub.dragan.myhomehub.Classes.provider.DatabaseManager;
import com.homehub.dragan.myhomehub.R;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;



/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends HomeHubActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "dragan@unimata.com:password",
            "kevin@unimata.com:password", "jack@unimata.com:password"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    public static final String EXTRA_IPADDRESS = "ip_address";
    public static final String EXTRA_FULL_URI = "full_uri";
    public static final String EXTRA_PASSWORD = "password";
    public static final String EXTRA_LAST_REQUEST = "last_request";

    public SharedPreferences mSharedPref;//dragans kryptonite

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mIpAddressView;
    private EditText mPasswordView;
    private FirebaseApp firebaseApp;
    private FirebaseAuth mAuth;
    private Button _loginButton;
    private TextView _signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        HomeHub.getInstance(); //create the HH websocket


        // Set up the login form.
        //mEmailView = (AutoCompleteTextView) findViewById(R.id.input_email);
        mIpAddressView = findViewById(R.id.input_URL);

        populateAutoComplete();

        mPasswordView = findViewById(R.id.input_password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        _loginButton = findViewById(R.id.btn_login);
        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //signIn();
            }
        });

        _signupButton = findViewById(R.id.link_signup);
        _signupButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
            }
        });

        Button mEmailSignInButton = findViewById(R.id.btn_login);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        //mLoginFormView = findViewById(R.id.login_form);
        //mProgressView = findViewById(R.id.login_progress);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        if (mSharedPref == null) {
            new SharedPreferenceLoadingTask().execute();
        }
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }

    private class SharedPreferenceLoadingTask extends AsyncTask<Void, Void, ErrorMessage> {

        @Override
        protected ErrorMessage doInBackground(Void... param) {
            PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.preferences, false);
            mSharedPref = getAppController().getSharedPref();


            /*int ids[] = AppWidgetManager.getInstance(LoginActivity.this).getAppWidgetIds(new ComponentName(LoginActivity.this, EntityWidgetProvider.class));
            if (ids.length > 0) {
                ArrayList<String> appWidgetIds = new ArrayList<>();
                for (int id : ids) {
                    appWidgetIds.add(Integer.toString(id));
                }

                DatabaseManager databaseManager = DatabaseManager.getInstance(LoginActivity.this);
                databaseManager.forceCreate();
                databaseManager.housekeepWidgets(appWidgetIds);

            }*/
            //mBundle = getIntent().getExtras();
            return null;
        }

        @Override
        protected void onPostExecute(ErrorMessage errorMessage) {
            if (errorMessage == null) {

                if (mSharedPref.getString(EXTRA_IPADDRESS, null) != null) {

                    DatabaseManager databaseManager = DatabaseManager.getInstance(LoginActivity.this);

                    ArrayList<Group> groups = databaseManager.getGroups();
//                    ArrayList<HomeAssistantServer> connections = databaseManager.getConnections();
                    int dashboardCount = databaseManager.getDashboardCount();
                    Log.d("YouQi", "dashboardCount: " + dashboardCount);
//                    if (groups.size() != 0 && connections.size() != 0 && dashboardCount > 0) {
//                        startMainActivity();
//                        return;
//                    }
                }

                //mLayoutMain.setVisibility(View.VISIBLE);

                mIpAddressView.setText(mSharedPref.getString(EXTRA_FULL_URI, ""));
                if (mIpAddressView.getText().toString().trim().length() != 0) {
                    mPasswordView.requestFocus();
                } else {
                    mIpAddressView.requestFocus();
                }
                //showProgress(false, null);
            } else {
                //mProgressBar.setVisibility(View.GONE);
                //m.setVisibility(View.GONE);
                //mTextProgress.setText(errorMessage.message);
            }

            super.onPostExecute(errorMessage);
        }
    }

    private void signIn(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(LoginActivity.this, "signInWithEmail:success",
                                    Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void createAccount(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(LoginActivity.this,"createUserWithEmail:success",
                                    Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }


    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mIpAddressView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        //String email = mEmailView.getText().toString();
        String baseURL = mIpAddressView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        /**if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
         }**/
        // Check for a valid email address2.
        if (TextUtils.isEmpty(baseURL)) {
            mIpAddressView.setError(getString(R.string.error_field_required));
            focusView = mIpAddressView;
            cancel = true;
        } else if (!(baseURL.startsWith("http://") || baseURL.startsWith("192"))) {
            mIpAddressView.setError("Error invalid url");
            focusView = mIpAddressView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.

            Log.d("Yo","login NOT triggered");
            focusView.requestFocus();
        } else {
            String host = Uri.parse(baseURL).getHost();
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            Log.d("Yo","login triggered "+host);
            mAuthTask = new UserLoginTask(baseURL, host, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        //addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        //mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        //private final String mEmail;
        private final String mUri;
        private final String mIpAddress;
        private final String mPassword;
        private final String mBearerHeader;

        UserLoginTask(String uri, String ipAddress, String password) {
            //mEmail = email;
            mUri = uri;
            mIpAddress = ipAddress;
            mPassword = password;
            mBearerHeader = "Bearer " + password;

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {

                Log.d("Yo","SharedPref attempt");
                SharedPreferences.Editor editor = mSharedPref.edit();

                Log.d("Yo","SharedPref worked");
                editor.putString(EXTRA_FULL_URI, mUri);
                editor.putString(EXTRA_IPADDRESS, mIpAddress);
                editor.putString(EXTRA_PASSWORD, mPassword);
                editor.putInt("connectionIndex", 0);
                editor.putLong(EXTRA_LAST_REQUEST, System.currentTimeMillis()).apply();
                editor.apply();

                HomeHub.getInstance().isConnected();
                Log.d("Yo"," holy s*** it worked");


//                DatabaseManager databaseManager = DatabaseManager.getInstance(ConnectActivity.this);
//                databaseManager.updateTables(bootstrapResponse);
//                databaseManager.addConnection(new HomeAssistantServer(mUri, mPassword));
//                ArrayList<Entity> entities = databaseManager.getEntities();
//                for (Entity entity : entities) {
//                    Log.d("YouQi", "Entity: " + entity.entityId);
//                }


            } catch (Exception e) {
                Log.d("Yo", "ERROR!");
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            if (success) {
                if (mSharedPref.getString(EXTRA_IPADDRESS, null) != null) {

                    DatabaseManager databaseManager = DatabaseManager.getInstance(LoginActivity.this);

                    ArrayList<Group> groups = databaseManager.getGroups();
                    int dashboardCount = databaseManager.getDashboardCount();
                    Log.d("Yo", "dashboardCount: " + dashboardCount);
                }
                finish();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            } else {
                Log.d("You","Nope");
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }
}

