package fr.openium.auvergnewebcams.injection

import com.bumptech.glide.annotation.Excludes
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpLibraryGlideModule
import com.bumptech.glide.module.AppGlideModule
import fr.openium.auvergnewebcams.utils.CustomGlideImageLoader


/**
 * Created by t.coulange on 10/11/2017.
 */
@GlideModule
@Excludes(OkHttpLibraryGlideModule::class, CustomGlideImageLoader::class)
class MyAppGlideModule : AppGlideModule() {

}

