package com.konsula.app.service.task;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Konsula on 07/05/2016.
 */
public abstract class CancelableCallback<T> implements Callback<T> {

    private boolean canceled;
    private T pendingT;
    private Response pendingResponse;
    private RetrofitError pendingError;

    public CancelableCallback() {
        this.canceled = false;
    }

    public void cancel(boolean remove) {
        canceled = true;
    }

    @Override
    public void success(T t, Response response) {
        if (canceled) {
            return;
        }
        onSuccess(t, response);
    }

    @Override
    public void failure(RetrofitError error) {
        if (canceled) {
            return;
        }
        onFailure(error);
    }

    protected abstract void onSuccess(T t, Response response);

    protected abstract void onFailure(RetrofitError error);


    public void cancel() {
        canceled = true;
    }
}