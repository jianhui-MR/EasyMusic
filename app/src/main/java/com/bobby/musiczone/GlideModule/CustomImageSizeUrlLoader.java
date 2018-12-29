package com.bobby.musiczone.GlideModule;

import android.content.Context;

import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader;

public class CustomImageSizeUrlLoader extends BaseGlideUrlLoader<CustomImageSizeModel> {
    public CustomImageSizeUrlLoader(Context context) {
        super( context );
    }
    @Override
    protected String getUrl(CustomImageSizeModel model, int width, int height) {
        return model.requestCustomSizeUrl( width, height );
    }
}
