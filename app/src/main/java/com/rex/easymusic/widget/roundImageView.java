package com.rex.easymusic.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Bobby on 2018/12/29
 */
@SuppressLint("AppCompatCustomView")
public class roundImageView extends ImageView {
    public roundImageView(Context context) {
        super(context);
    }

    public roundImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public roundImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
