package fr.openium.auvergnewebcams.custom;

import com.github.piasy.biv.loader.ImageLoader;

import java.io.File;

public class SimpleImageLoaderCallback implements ImageLoader.Callback {

    @Override
    public void onCacheHit(int imageType, File image) {

    }

    @Override
    public void onCacheMiss(int imageType, File image) {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onProgress(int progress) {

    }

    @Override
    public void onFinish() {

    }

    @Override
    public void onSuccess(File image) {

    }

    @Override
    public void onFail(Exception error) {

    }
}