package com.vic.rxnetsdk;

import android.app.Activity;
import android.net.ParseException;
import android.util.Log;

import com.google.gson.JsonParseException;

import org.json.JSONException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.HttpException;
import rx.Subscriber;

/**
 * Created by liu song on 2017/5/4.
 */

public abstract class RxSubscriber<T> extends Subscriber<T> {

    private static final String TAG = RxSubscriber.class.getSimpleName();
    private Activity activity;

    public RxSubscriber() {
    }

    public RxSubscriber(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onCompleted()");
    }

    @Override
    public void onCompleted() {
        Log.i(TAG, "onCompleted()");
    }

    @Override
    public void onError(Throwable e) {
        Log.e(TAG, "onError()");
        String msg="";
        if (e instanceof HttpException) {
            msg="http错误码：" + ((HttpException) e).code();
        } else if (e instanceof ConnectException || e instanceof SocketTimeoutException) {
            msg="链接异常";
        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof ParseException) {
            msg="解析异常";
        } else if (e instanceof UnknownHostException) {
            msg="解析域名异常";
        } else {
            msg="未知异常";
        }

        e.printStackTrace();

        if (activity == null) {
            onFailure(msg);
        } else {
            final String finalMsg = msg;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onFailure(finalMsg);
                }
            });
        }
    }

    @Override
    public void onNext(final T t) {
        Log.i(TAG, "onNext()");
        if (activity == null) {
            callBack(t);
        } else {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    callBack(t);
                }
            });
        }
    }

    protected void onFailure(String msg){}

    //成功回调
    protected abstract void callBack(T t);
}
