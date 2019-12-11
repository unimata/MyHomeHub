package com.homehub.dragan.myhomehub.Activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.homehub.dragan.myhomehub.Classes.model.Entity;
import com.homehub.dragan.myhomehub.Classes.model.ErrorMessage;
import com.homehub.dragan.myhomehub.Classes.model.Group;
import com.homehub.dragan.myhomehub.Classes.model.HomeHubServer;
import com.homehub.dragan.myhomehub.Classes.model.rest.CallServiceRequest;
import com.homehub.dragan.myhomehub.Classes.model.rest.RxPayload;
import com.homehub.dragan.myhomehub.Classes.provider.DataSyncService;
import com.homehub.dragan.myhomehub.Classes.provider.DatabaseManager;
import com.homehub.dragan.myhomehub.Classes.provider.EntityContentProvider;
import com.homehub.dragan.myhomehub.Classes.provider.ServiceProvider;
import com.homehub.dragan.myhomehub.Classes.shared.EntityProcessInterface;
import com.homehub.dragan.myhomehub.Classes.shared.EventEmitterInterface;
import com.homehub.dragan.myhomehub.Classes.util.CommonUtil;
import com.homehub.dragan.myhomehub.Classes.view.MultiSwipeRefreshLayout;
import com.homehub.dragan.myhomehub.Fragments.EntityFragment;
import com.homehub.dragan.myhomehub.R;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends HomeHubActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener, EntityProcessInterface, EventEmitterInterface{

    private Subject<RxPayload> mEventEmitter = PublishSubject.create();
    private Call<String> mCall2;
    private Spinner mServerSpinner;
    private int spinnerCheck = 0;
    private ArrayList<HomeHubServer> mServers;
    private ServerAdapter mServerAdapter;
    private AppBarLayout mAppBarLayout;

    @Override
    public Subject<RxPayload> getEventSubject() {
        return mEventEmitter;
    }

    //Cursor Loader
    private EntityChangeObserver mEntityChangeObserver;

    private SharedPreferences mSharedPref;

    private HomeHubServer mCurrentServer;
    private RefreshTask mRefreshTask;
    private Call<ArrayList<Entity>> mCall;
    private boolean doubleBackToExitPressedOnce;
    private MultiSwipeRefreshLayout mSwipeRefresh;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;

    private Toast mToast;
    private boolean runonce = false;

    private BottomNavigationView mBottomNavigation;
    private MenuItem mMenuHoursand;

    private ViewPagerAdapter mViewPagerAdapter;
    private ViewPager mViewPager;

    //Data
    private DatabaseManager mDatabase;
    private ArrayList<Group> mGroups;

    //Bound Service
    private DataSyncService mService;
    private boolean mBound;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We bind to LocalService, cast the IBinder and get LocalService instance
            DataSyncService.LocalBinder binder = (DataSyncService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;

            if (mSharedPref.getBoolean("websocket_mode", true)) {
                mService.startWebSocket(mCurrentServer, true);
            }

            Log.d("YouQi", "Service Bound");
            binder.getEventSubject()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<RxPayload>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }
                        @Override
                        public void onNext(RxPayload rxPayload) {
                            mEventEmitter.onNext(rxPayload);
                        }
                        @Override
                        public void onError(Throwable e) {
                        }
                        @Override
                        public void onComplete() {

                        }
                    });
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_device, menu);

        Log.d("YouQi", "onCreateOptionsMenu");
        mMenuHoursand = menu.findItem(R.id.action_logout);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        setContentView(R.layout.activity_main);

        setupContentObserver();
        mSharedPref = getAppController().getSharedPref();
        mDatabase = DatabaseManager.getInstance(this);
        mServers = mDatabase.getConnections();
        mCurrentServer = mServers.get(mSharedPref.getInt("connectionIndex", 0));

        Log.d("Yo", "onCreate");


        setupToolbar();
        setupDrawer();
        setupViewPager();
        setupBottomNavigation();
        refreshConnections();
    }

    //https://stackoverflow.com/questions/21380914/contentobserver-onchange
    private void setupContentObserver() {
        // creates and starts a new thread set up as a looper
        HandlerThread thread = new HandlerThread("COHandlerThread");
        thread.start();

        Handler handler = new Handler(thread.getLooper());
        mEntityChangeObserver = new EntityChangeObserver(handler);
    }


    private void setupToolbar() {
        final Toolbar toolbar = findViewById(R.id.toolbar);
        mAppBarLayout = findViewById(R.id.appbar);
        mAppBarLayout.setVisibility(View.GONE);
        //setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setTitle(getString(R.string.app_name));
            getSupportActionBar().setSubtitle("A Capstone Project");
        }
    }

    private void setupDrawer() {
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.nav_view);
        final TextView mVersionText = findViewById(R.id.version_text);
        final View mHeaderView = mNavigationView.getHeaderView(0);

        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (Exception e) {
            mVersionText.setText("");
        }

        final TextView websocketButton = mHeaderView.findViewById(R.id.text_websocket);
        websocketButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mService.startWebSocket(mCurrentServer);
            }
        });

        mServerSpinner = mHeaderView.findViewById(R.id.spinner_server);
        mServerAdapter = new ServerAdapter(this, 0, mServers);
        mServerSpinner.setAdapter(mServerAdapter);
        mServerSpinner.setSelection(mSharedPref.getInt("connectionIndex", 0), false); //must
        mServerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                if (++spinnerCheck <= 1) return;

                Log.d("Yo", "mServerSpinner selected: " + pos);
                mSharedPref.edit().putInt("connectionIndex", pos).apply();
                switchConnection(mServers.get(pos));
                mDrawerLayout.closeDrawers();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });



        Window w = getWindow(); // in Activity's onCreate() for instance
        w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        ViewGroup contentView = findViewById(android.R.id.content);


        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                boolean isSelected = false;
                switch (item.getItemId()) {
                    case R.id.lblEdit:
                        //showSettings();
                        Toast.makeText(MainActivity.this, "TODO MAKE THIS DO STUFF", Toast.LENGTH_SHORT).show();
                        mDrawerLayout.closeDrawers();
                        break;

                    case R.id.lblLogout:
                        Toast.makeText(MainActivity.this, "Logging out...", Toast.LENGTH_SHORT).show();
                        logOut();
                        break;

                    default:
                        isSelected = true;
                }
                return isSelected;
            }
        });

    }

    private void switchConnection(HomeHubServer homeHubServer) {
        mCurrentServer = homeHubServer;
        if (mSharedPref.getBoolean("websocket_mode", true)) {
            mService.stopWebSocket();
            mService.startWebSocket(mCurrentServer);
        }
    }



    private void setupViewPager() {
        TabLayout mTabLayout = findViewById(R.id.tabs);
        mViewPager = findViewById(R.id.viewpager);

        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mDatabase.forceCreate();
        mGroups = mDatabase.getGroups();
        for (Group group : mGroups) {
            if (group.groupId == 1) {
                group.groupName = getString(R.string.title_home);
            }
            mViewPagerAdapter.addFragment(EntityFragment.getInstance(group), group.getFriendlyName());
        }
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setOffscreenPageLimit(20);

        mTabLayout.setupWithViewPager(mViewPager);
        for (int i = 0; i < mGroups.size(); ++i) {
            Group group = mGroups.get(i);
            if (group.hasMdiIcon()) {
                TabLayout.Tab currentTab = mTabLayout.getTabAt(i);
                if (currentTab != null) {
                    View tab = LayoutInflater.from(this).inflate(R.layout.custom_tab, mTabLayout, false);
                    TextView mdiText = tab.findViewById(R.id.text_mdi);
                    TextView nameText = tab.findViewById(R.id.text_name);
                    nameText.setText(group.getFriendlyName());
                    currentTab.setCustomView(tab);
                }
            }
        }

        mTabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager) {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                ActionBar actionBar = getSupportActionBar();
                if (actionBar != null) {
                    actionBar.setTitle(tab.getPosition() == 0 ? getString(R.string.app_name) : tab.getText());
                }

            }
        });
    }


    private void setupBottomNavigation() {
        mSwipeRefresh = findViewById(R.id.swipe_refresh_layout);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshApi();
            }
        });
        mSwipeRefresh.setSwipeableChildren(mViewPager);

        mBottomNavigation = findViewById(R.id.navigation);

        mBottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mBottomNavigation.setSelectedItemId(R.id.navigation_dashboard);

    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(this, DataSyncService.class);
        getApplicationContext().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mBound) {
            getApplicationContext().unbindService(mConnection);
            mBound = false;
        }

        mEventEmitter.onComplete();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_settings:
                //showSettings();
                return true;


            case R.id.action_logout:
                logOut();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 2000: {
                if (resultCode == Activity.RESULT_OK) {
                    mEventEmitter.onNext(RxPayload.getInstance("SETTINGS"));
                }
                break;
            }

            case 2001: {
                if (resultCode == Activity.RESULT_OK) {
                    Group group = CommonUtil.inflate(data.getStringExtra("group"), Group.class);

                    RxPayload payload = RxPayload.getInstance("EDIT");
                    payload.group = group;
                    mEventEmitter.onNext(payload);
                }
                break;
            }
            case 2002:{
                String result=data.getStringExtra("attribute");
                if(resultCode == Activity.RESULT_CANCELED || !result.contentEquals("fingerprint_authentication")){
                    finish();
                    return;
                }
                // Everything went fine
            }
        }
    }


    public void refreshConnections() {
        Log.d("YouQi", "refreshConnections");
        mServers = DatabaseManager.getInstance(this).getConnections();
        mServerAdapter.setItems(mServers);
        mServerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {

        if (mDrawerLayout.isDrawerOpen(mNavigationView)) {
            mDrawerLayout.closeDrawers();
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    public void showError(final String status) {
        Toast.makeText(this, status, Toast.LENGTH_SHORT).show();
    }

    public void refreshApi() {
        if (mRefreshTask == null) {
            mRefreshTask = new RefreshTask();
            mRefreshTask.execute((Void) null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        getContentResolver().unregisterContentObserver(mEntityChangeObserver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getContentResolver().registerContentObserver(EntityContentProvider.getUrl(), true, mEntityChangeObserver);
        if (!runonce || (mService != null && !mService.isWebSocketRunning())) {
            runonce = true;
            refreshApi();
        }
    }

    @Override
    public void callService(final String domain, final String service, CallServiceRequest serviceRequest) {
        if (mService != null && mService.isWebSocketRunning()) {
            mService.callService(domain, service, serviceRequest);
        } else if (mCall == null) {
            mCall = ServiceProvider.getApiService(mCurrentServer.getBaseUrl()).callService(mCurrentServer.getBearerHeader(), domain, service, serviceRequest);
            mCall.enqueue(new Callback<ArrayList<Entity>>() {
                @Override
                public void onResponse(@NonNull Call<ArrayList<Entity>> call, @NonNull Response<ArrayList<Entity>> response) {
                    mCall = null;
                    ArrayList<Entity> restResponse = response.body();

                    if ("script".equals(domain) || ("automation".equals(domain) || "scene".equals(domain) || "trigger".equals(service))) {
                        showToast("toast_triggered");
                    }
                    if (restResponse != null) {
                        for (Entity entity : restResponse) {
                            getContentResolver().update(Uri.parse("content://com.homehub.dragan.myhomehub.Classes.provider.EntityContentProvider/"), entity.getContentValues(), "ENTITY_ID='" + entity.entityId + "'", null);
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ArrayList<Entity>> call, @NonNull Throwable t) {
                    mCall = null;
                }
            });
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            Intent intent;
            switch (item.getItemId()) {

                case R.id.navigation_dashboard:
                    return true;
                case R.id.navigation_account:
                    intent = new Intent(MainActivity.this, AccountActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.navigation_automation:
                    intent = new Intent(MainActivity.this, AutomationActivity.class);
                    startActivity(intent);
                    return true;
            }
            return false;
        }
    };

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        mAppBarLayout.setExpanded(true);

        switch (item.getItemId()) {

            case R.id.action_settings:
                refreshApi();
                break;

            case R.id.action_logout:
                Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
                logOut();
                break;
        }
        return false;
    }

    public HomeHubServer getServer() {
        return mCurrentServer;
    }

    @Override
    public Context getActivityContext() {
        return this;
    }



    private class RefreshTask extends AsyncTask<Void, String, ErrorMessage> {
        RefreshTask() {
        }

        @Override
        protected ErrorMessage doInBackground(Void... params) {
            try {
                Response<ArrayList<Entity>> response = ServiceProvider.getApiService(mCurrentServer.getBaseUrl()).getStates(mCurrentServer.getBearerHeader()).execute();
                if (response.code() != 200) {
                    return new ErrorMessage("Error", response.message());
                }

                final ArrayList<Entity> statesResponse = response.body();

                if (statesResponse == null) {
                    throw new RuntimeException("No Data");
                }

                ArrayList<ContentValues> values = new ArrayList<>();
                for (Entity entity : statesResponse) {
                    values.add(entity.getContentValues());
                }

                getContentResolver().bulkInsert(EntityContentProvider.getUrl(),
                        values.toArray(new ContentValues[values.size()]));
                publishProgress((String) null);

            } catch (Exception e) {
                e.printStackTrace();
                return new ErrorMessage("System Exception", e);
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(final ErrorMessage errorMessage) {

            mRefreshTask = null;
            mSwipeRefresh.setRefreshing(false);

            if (errorMessage != null) {
                showError(errorMessage.message);
            }
        }

        @Override
        protected void onCancelled() {

        }
    }


    public EntityFragment getCurrentEntityFragment() {
        return (EntityFragment) mViewPagerAdapter.getItem(mViewPager.getCurrentItem());
    }

    @Override
    public void showToast(String message) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        mToast.show();
    }

    public void showEdit(View v) {
        Bundle bundle = new Bundle();
        bundle.putString("group", CommonUtil.deflate(getCurrentEntityFragment().getGroup()));
    }

    private class ServerAdapter extends ArrayAdapter<HomeHubServer> {
        private List<HomeHubServer> items;

        ServerAdapter(Context context, int resource, List<HomeHubServer> objects) {
            super(context, resource, objects);
            items = objects;
        }

        public void setItems(List<HomeHubServer> objects) {
            items = objects;
        }

        @Override
        public int getCount() {
            return 1 + items.size();
        }


        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(getContext(), android.R.layout.simple_list_item_1, null);
            }

            ((TextView) convertView.findViewById(android.R.id.text1)).setText(items.get(position).getLine(getContext()));
            ((TextView) convertView.findViewById(android.R.id.text1)).setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null));
            return convertView;
        }
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public class EntityChangeObserver extends ContentObserver {

        @Override
        public boolean deliverSelfNotifications() {
            return super.deliverSelfNotifications();
        }

        EntityChangeObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            this.onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            if (uri == null) return;

            DatabaseManager databaseManager = DatabaseManager.getInstance(MainActivity.this);
            if ("ALL".equals(uri.getLastPathSegment())) {
                RxPayload payload = RxPayload.getInstance("UPDATE_ALL");
                payload.entities = databaseManager.getEntities();
                mEventEmitter.onNext(payload);
            } else {
                Entity entity = databaseManager.getEntityById(uri.getLastPathSegment());

                if (entity != null) {
                    RxPayload payload = RxPayload.getInstance("UPDATE");
                    payload.entity = entity;
                    mEventEmitter.onNext(payload);
                }
            }
        }
    }
}
