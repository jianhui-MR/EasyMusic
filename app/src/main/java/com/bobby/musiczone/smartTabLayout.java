package com.bobby.musiczone;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

import com.ogaclejapan.smarttablayout.SmartTabLayout;

public class smartTabLayout extends SmartTabLayout {
    public smartTabLayout(Context context) {
        super(context);
    }

    public smartTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public smartTabLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setDefaultTabTextColor(ColorStateList colors) {
        super.setDefaultTabTextColor(colors);
    }

    @Override
    public void setCustomTabColorizer(TabColorizer tabColorizer) {
        super.setCustomTabColorizer(tabColorizer);
    }

    @Override
    public void setViewPager(ViewPager viewPager) {
        super.setViewPager(viewPager);
    }
}
