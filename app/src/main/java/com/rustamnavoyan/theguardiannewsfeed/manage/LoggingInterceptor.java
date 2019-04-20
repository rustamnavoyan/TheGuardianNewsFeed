package com.rustamnavoyan.theguardiannewsfeed.manage;

import android.os.SystemClock;
import android.util.Log;

import com.rustamnavoyan.theguardiannewsfeed.BuildConfig;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okio.Buffer;
import okio.BufferedSource;

public class LoggingInterceptor implements Interceptor {
    private static final Charset UTF8 = Charset.forName("UTF-8");

    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        log(String.format("--> BEGIN %s %s", request.method(), request.url()));
        if (BuildConfig.DEBUG) {
            log(request.headers());
        }

        long startMillis = SystemClock.elapsedRealtime();
        okhttp3.Response response;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            log(String.format("<-- ERROR: %s %s %sms\n%s", request.method(), request.url(),
                    SystemClock.elapsedRealtime() - startMillis, e));
            throw e;
        }

        log(String.format("<-- END %s %s %s %sms", response.request().method(), response.request().url(),
                response.code(), SystemClock.elapsedRealtime() - startMillis));

        if (BuildConfig.DEBUG) {
            log(response.headers());
            log(response);
        }

        return response;
    }

    private static void log(Headers headers) {
        for (int i = 0; i < headers.size(); i++) {
            log(headers.name(i) + ": " + headers.value(i));
        }
    }

    private static void log(okhttp3.Response response) throws IOException {
        BufferedSource source = response.body().source();
        source.request(Long.MAX_VALUE);
        Buffer buffer = source.buffer();

        Charset charset = UTF8;
        MediaType contentType = response.body().contentType();
        if (contentType != null) {
            try {
                charset = contentType.charset(UTF8);
            } catch (UnsupportedCharsetException e) {
                log("Unsupported charset: " + e.getMessage());
                return;
            }
        }

        if (!isPlaintext(buffer)) {
            log("Binary body size: " + buffer.size());
            return;
        }

        Buffer cloneBuffer = buffer.clone();
        while (cloneBuffer.size() > 0) {
            log(cloneBuffer.readString(Math.min(cloneBuffer.size(), 4096), charset));
        }
    }

    /**
     * Returns true if the body in question probably contains human readable text. Uses a small sample
     * of code points to detect unicode control characters commonly used in binary file signatures.
     */
    private static boolean isPlaintext(Buffer buffer) {
        try {
            Buffer prefix = new Buffer();
            long byteCount = Math.min(buffer.size(), 64);
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false;
        }
    }

    private static void log(String message) {
        Log.d("LoggingInterceptor", "log: " + message);
    }
}
