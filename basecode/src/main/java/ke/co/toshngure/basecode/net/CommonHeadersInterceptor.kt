package ke.co.toshngure.basecode.net

import okhttp3.Interceptor
import okhttp3.Response

class CommonHeadersInterceptor(private val callback: NetworkUtils.Callback) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        // Get the request from the chain.
        val original = chain.request()

        // add the headers
        val request = original.newBuilder()
            .url(original.url())
            .method(original.method(), original.body())

        // Get common headers
        val commonHeaders = callback.getCommonHeaders()
        // Add common params to the url
        for (key in commonHeaders.keys) {
            request.header(key, commonHeaders[key].toString())
        }

        return chain.proceed(request.build())
    }
}