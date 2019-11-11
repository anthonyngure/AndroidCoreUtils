package ke.co.toshngure.basecode.app

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.Excludes
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpLibraryGlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import ke.co.toshngure.basecode.util.NetworkUtils
import okhttp3.OkHttpClient
import java.io.InputStream
import java.util.concurrent.TimeUnit


@GlideModule
@Excludes(OkHttpLibraryGlideModule::class) // initialize OkHttp manually
class MyGlideModule : AppGlideModule() {

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        super.registerComponents(context, glide, registry)
         val okHttpClient = NetworkUtils.getClientInstance()
             .newBuilder()
             .connectTimeout(60, TimeUnit.SECONDS)
             .writeTimeout(60, TimeUnit.SECONDS)
             .readTimeout(60, TimeUnit.SECONDS)
             .build()
        registry.replace(GlideUrl::class.java, InputStream::class.java,
                OkHttpUrlLoader.Factory(okHttpClient))
    }
}