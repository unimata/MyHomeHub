package com.homehub.dragan.myhomehub.Classes.fragment.control;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.homehub.dragan.myhomehub.Classes.model.Entity;
import com.homehub.dragan.myhomehub.Classes.model.rest.CallServiceRequest;
import com.homehub.dragan.myhomehub.Classes.model.rest.RxPayload;
import com.homehub.dragan.myhomehub.Classes.shared.EntityProcessInterface;
import com.homehub.dragan.myhomehub.Classes.shared.EventEmitterInterface;
import com.homehub.dragan.myhomehub.Classes.util.CommonUtil;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.SafeObserver;
import io.reactivex.schedulers.Schedulers;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class BaseControlFragment extends DialogFragment implements Observer<RxPayload> {
    protected Entity mEntity;
    private SafeObserver<RxPayload> mSafeObserver;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        if (getActivity() instanceof EventEmitterInterface) {
            mSafeObserver = new SafeObserver<>(this);
            ((EventEmitterInterface) getActivity()).getEventSubject()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(mSafeObserver);
        }

        //getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation2;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mEntity = CommonUtil.inflate(args.getString("entity"), Entity.class);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mSafeObserver != null) mSafeObserver.dispose();
        //https://stackoverflow.com/questions/23786033/dialogfragment-and-ondismiss
        final Activity activity = getActivity();
        if (activity instanceof DialogInterface.OnDismissListener) {
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null) {
            getDialog().setDismissMessage(null);
        }
        super.onDestroyView();
    }

    protected void callService(String domain, String service, CallServiceRequest callServiceRequest) {
        Activity activity = getActivity();
        Log.d("YouQi", "callservice");
        if (activity instanceof EntityProcessInterface) {
            Log.d("YouQi", "Calling EntityProcessInterface");
            ((EntityProcessInterface) activity).callService(domain, service, callServiceRequest);
        } else {
            throw new RuntimeException(activity.getClass().getName() + " IS NOT EntityProcessInterface");
        }
    }

    public void onChange(Entity entity) {
        mEntity = entity;
    }

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(RxPayload payload) {
        switch (payload.event) {
            case "UPDATE":
                if (payload.entity.equals(mEntity)) onChange(payload.entity);
                break;

            case "UPDATE_ALL":
                for (Entity entity : payload.entities) {
                    if (payload.entity.equals(mEntity)) onChange(payload.entity);
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
}
