package com.homehub.dragan.myhomehub.Fragments.control;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;
import android.widget.TextView;

import com.homehub.dragan.myhomehub.Classes.model.Entity;
import com.homehub.dragan.myhomehub.Classes.model.rest.CallServiceRequest;
import com.homehub.dragan.myhomehub.Classes.util.CommonUtil;
import com.homehub.dragan.myhomehub.R;

/**
 * Simple fragment with blur effect behind.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SwitchFragment extends BaseControlFragment implements View.OnClickListener {

    private TextView mButtonOff;
    private TextView mButtonOn;

    public static SwitchFragment newInstance(Entity entity) {
        SwitchFragment fragment = new SwitchFragment();
        Bundle args = new Bundle();
        args.putString("entity", CommonUtil.deflate(entity));
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View rootView = getActivity().getLayoutInflater().inflate(R.layout.control_switch, null);
        builder.setView(rootView);
        builder.setTitle(mEntity.getFriendlyName());

        rootView.findViewById(R.id.button_close).setOnClickListener(this);
        mButtonOff = rootView.findViewById(R.id.text_off);
        mButtonOn = rootView.findViewById(R.id.text_on);

        mButtonOff.setOnClickListener(this);
        mButtonOn.setOnClickListener(this);

        refreshUi();

        return builder.create();
    }

    private void refreshUi() {
        if (mEntity.isCurrentStateActive()) {
            mButtonOn.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null));
            mButtonOff.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimaryDark, null));
        } else {
            mButtonOff.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null));
            mButtonOn.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimaryDark, null));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_close:
                dismiss();
                break;
            case R.id.text_off:
                callService("homeassistant", "turn_off", new CallServiceRequest(mEntity.entityId));
                break;
            case R.id.text_on:
                callService("homeassistant", "turn_on", new CallServiceRequest(mEntity.entityId));
                break;
        }
    }

    @Override
    public void onChange(Entity entity) {
        super.onChange(entity);
        refreshUi();
    }

}
