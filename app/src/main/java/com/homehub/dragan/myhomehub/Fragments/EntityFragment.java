package com.homehub.dragan.myhomehub.Fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.GridLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.homehub.dragan.myhomehub.Classes.model.Entity;
import com.homehub.dragan.myhomehub.Classes.model.Group;
import com.homehub.dragan.myhomehub.Classes.model.rest.RxPayload;
import com.homehub.dragan.myhomehub.Classes.provider.DatabaseManager;
import com.homehub.dragan.myhomehub.Classes.shared.EntityAdapter;
import com.homehub.dragan.myhomehub.Classes.shared.EntityInterface;
import com.homehub.dragan.myhomehub.Classes.shared.EntityProcessInterface;
import com.homehub.dragan.myhomehub.Classes.shared.async.QueryEntitiesByGroupAsyncTask;
import com.homehub.dragan.myhomehub.Classes.shared.async.SortEntityAsyncTask;
import com.homehub.dragan.myhomehub.Classes.shared.interfaces.EntityAsyncCallback;
import com.homehub.dragan.myhomehub.Classes.util.CommonUtil;
import com.homehub.dragan.myhomehub.Classes.util.EntityHandlerHelper;
import com.homehub.dragan.myhomehub.Classes.view.RecyclerViewEmptySupport;
import com.homehub.dragan.myhomehub.R;

import java.util.ArrayList;
import java.util.Locale;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.SafeObserver;

public class EntityFragment extends BaseFragment implements EntityInterface {

    protected RecyclerViewEmptySupport mRecyclerView;
    protected EntityAdapter mAdapter;
    private SharedPreferences mSharedPref;
    private MediaPlayer mClickDown;
    private MediaPlayer mClickUp;
    private Group mGroup;
    private ArrayList<Entity> mEntities;
    private View mProgressBarCircle;

    private MenuItem mMenuClearSearch;
    private View mEmptyView;
    private SafeObserver<RxPayload> mSafeObserver;

    public static EntityFragment getInstance(Group group) {
        EntityFragment fragment = new EntityFragment();
        Bundle bundle = new Bundle();
        bundle.putString("group", CommonUtil.deflate(group));
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        mGroup = CommonUtil.inflate(bundle.getString("group"), Group.class);

        setHasOptionsMenu(true);

        mClickDown = MediaPlayer.create(getContext(),R.raw.vol_down);
        mClickUp = MediaPlayer.create(getContext(), R.raw.vol_up);

        mClickDown.setVolume(0.2f, 0.2f);
        mClickUp.setVolume(0.2f, 0.2f);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_add_device, menu);
        super.onCreateOptionsMenu(menu, inflater);

        //CommonUtil.setMenuDrawableColor(getContext(), menu.findItem(R.id.action_search), R.color.md_white_1000);
        //mMenuSearch = menu.findItem(R.id.action_search);
        //mMenuClearSearch = menu.findItem(R.id.action_clear_search);
        //mMenuClearSearch.setVisible(mAdapter.isFilterState());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_entity, container, false);
        mRecyclerView = rootView.findViewById(R.id.recycler_view);
        mSharedPref = getAppController().getSharedPref();
        mProgressBarCircle = rootView.findViewById(R.id.progressbar_circle);
        mEmptyView = rootView.findViewById(R.id.empty_view);

        Observer<RxPayload> observer = new Observer<RxPayload>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(final RxPayload payload) {
                switch (payload.event) {
                    case "SETTINGS":
                        refreshPreferenceConfigs();
                        break;

                    case "EDIT":
                        if (payload.group.groupId.intValue() == mGroup.groupId.intValue()) {
                            mGroup.sortKey = 0; //hardcode
                            refreshDataSet();
                        }
                        break;

                    case "UPDATE":
                        mAdapter.updateState(payload.entity);
                        break;

                    case "UPDATE_ALL":
                        for (Entity entity : payload.entities) {
                            mAdapter.updateState(entity);
                        }
                        //mAdapter.updateList(payload.entities);
                        break;
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
        mSafeObserver = new SafeObserver<>(observer);

//        ((MainActivity) getActivity()).getEventSubject()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(mSafeObserver);


        setupRecyclerView(rootView);
        return rootView;
    }

    private void refreshDataSet() {
        new QueryEntitiesByGroupAsyncTask(getContext(), mGroup, new EntityAsyncCallback() {
            @Override
            public void onFinished(ArrayList<Entity> entities) {
                mEntities = entities;
                mAdapter.updateList(entities);
                mEmptyView.setVisibility(mEntities.size() == 0 ? View.VISIBLE : View.GONE);
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void setupRecyclerView(View rootView) {
        mRecyclerView = rootView.findViewById(R.id.recycler_view);
        //mSwipeRefresh = rootView.findViewById(R.id.swipe_refresh_layout);
        //rootView.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.md_grey_200, null));
        //rootView.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.md_white_1000, null));
        mRecyclerView.setHasFixedSize(true);

        Log.d("YouQi", "setup: " + mGroup.groupName + " isActive?" + (isActiveFragment() ? "Yes" : "No"));

        //mEntities = DatabaseManager.getInstance(getActivity()).getEntitiesByGroup(mGroup.groupId);
        mEntities = new ArrayList<>();
        new QueryEntitiesByGroupAsyncTask(getContext(), mGroup, new EntityAsyncCallback() {
            @Override
            public void onFinished(final ArrayList<Entity> entities) {
                Log.d("YouQi", "mEntities for " + mGroup.groupId + ": " + entities.size());

                if (!isActiveFragment()) {
                    mProgressBarCircle.setVisibility(View.GONE);
                    mAdapter.updateList(entities);
                    mEntities = entities;
                    mEmptyView.setVisibility(mEntities.size() == 0 ? View.VISIBLE : View.GONE);
                    return;
                }

                mProgressBarCircle.animate().alpha(0.0f).setDuration(200).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator rootAnimation) {
                        super.onAnimationEnd(rootAnimation);
                        mProgressBarCircle.setVisibility(View.GONE);
                        //mEntities.clear();
                        //mEntities.addAll(entities);
                        //mAdapter.notifyDataSetChanged();
                        mAdapter.updateList(entities);

                        Context context = getContext();


                        mEntities = entities;
                        mEmptyView.setVisibility(mEntities.size() == 0 ? View.VISIBLE : View.GONE);
                    }
                });

                //if (mEntities.size() > 0) mProgressBarCircle.setVisibility(View.GONE);
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        mAdapter = new EntityAdapter(getContext(), this, mEntities);
        mRecyclerView.setAdapter(mAdapter);
        //mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getContext()).showLastDivider().paint(getDividerPaint()).build());
        //mRecyclerView.setEmptyView(rootView.findViewById(R.id.empty_view));
        refreshPreferenceConfigs();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //mSafeObserver.dispose();
    }

    private void refreshPreferenceConfigs() {
        Activity activity = getActivity();
        if (activity != null && !activity.isFinishing()) {
            Display display = getActivity().getWindowManager().getDefaultDisplay();
            DisplayMetrics outMetrics = new DisplayMetrics();
            display.getMetrics(outMetrics);

            float density = getResources().getDisplayMetrics().density;
            //float dpHeight = outMetrics.heightPixels / density;
            float dpWidth = outMetrics.widthPixels / density;

            //final int spanCount = getResources().getInteger(R.integer.grid_columns);
            int spanCount = (int) Math.floor(dpWidth / 90.0d);
            final int prefCount = Integer.parseInt(mSharedPref.getString("num_columns", "0"));
            if (prefCount != 0) {
                spanCount = prefCount;
            }

            mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), spanCount));

        }
    }

    public void search(String query) {
        if(mAdapter == null) { return; /* Or raise exception? */ }

        //Do some magic
        ArrayList<Entity> filteredItems = new ArrayList<>();
        for (Entity item : mAdapter.getItems()) {
            if (item.getFriendlyDomainName().toUpperCase(Locale.getDefault()).contains(query.toUpperCase(Locale.getDefault()))
                    || item.getFriendlyName().toUpperCase(Locale.getDefault()).contains(query.toUpperCase(Locale.getDefault()))) {
                filteredItems.add(item);
                Log.d("YouQi", "Added Filter: " + item.entityId);
            }
        }
        mAdapter.updateFilterList(filteredItems);

        mMenuClearSearch.setVisible(true);
        //mMenuSearch.setVisible(false);
    }

    public void clearSearch() {
        mAdapter.clearFilter();
        mMenuClearSearch.setVisible(false);
        //mMenuSearch.setVisible(true);
    }

    public Group getGroup() {
        return mGroup;
    }

    public void sortEntity(int newSortKey) {
        //Update/Save newSortKey to database
        DatabaseManager.getInstance(getActivity()).updateSortKeyForGroup(newSortKey, mGroup.groupId);
        mGroup.sortKey = newSortKey;

        new SortEntityAsyncTask(new ArrayList<>(mAdapter.getDisplayItems()), new EntityAsyncCallback() {
            @Override
            public void onFinished(ArrayList<Entity> entities) {

                Parcelable recyclerViewState = mRecyclerView.getLayoutManager().onSaveInstanceState();
                mAdapter.updateDisplayList(entities);
                mRecyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
                showToast(getResources().getStringArray(R.array.sorted_descriptions)[mGroup.sortKey]);
            }
        }).execute(mGroup.sortKey);
    }


    @Override
    public void onEntityUpperViewClick(EntityAdapter.EntityTileViewHolder viewHolder, final Entity entity) {
        Log.d("YouQi", "onEntityUpperViewClick");
        triggerAnswer(entity, "single");
        if (getActivity() instanceof EntityProcessInterface) {
            boolean isConsumed = EntityHandlerHelper.onEntityClick((EntityProcessInterface) getActivity(), entity);
            if (isConsumed) {
                Log.d("yo","conssumed");
                if (mSharedPref.getBoolean("sound_effect", true)) {
                    if (viewHolder.isActivated()) {
                        mClickUp.start();
                    } else {
                        mClickDown.start();
                    }
                }
            }
        }
    }

    @Override
    public boolean onEntityUpperViewLongClick(EntityAdapter.EntityTileViewHolder viewHolder, Entity entity) {
        Boolean consumed = EntityHandlerHelper.onEntityLongClick((EntityProcessInterface) getActivity(), entity);
        Log.d("YouQi", consumed.toString());
        triggerAnswer(entity, "long");
        if (consumed) {
            if (mSharedPref.getBoolean("sound_effect", true)) {
                mClickDown.start();
            }
        }
        return true;
    }

    public boolean isFilterState() {
        return mAdapter != null && mAdapter.isFilterState();
    }

    private void triggerAnswer(Entity entity, String clickType) {
        Log.d("YouQi", "Stub!");
    }
}

