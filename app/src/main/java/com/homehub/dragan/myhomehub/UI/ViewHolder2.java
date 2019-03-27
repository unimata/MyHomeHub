package com.homehub.dragan.myhomehub.UI;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.homehub.dragan.myhomehub.R;

public class ViewHolder2 extends RecyclerView.ViewHolder {

    private ImageView ivExample;

    public ViewHolder2(View v) {
        super(v);
        ivExample = (ImageView) v.findViewById(R.id.ivExample);
    }

    public ImageView getImageView() {
        return ivExample;
    }

    public void setImageView(ImageView ivExample) {
        this.ivExample = ivExample;
    }
}