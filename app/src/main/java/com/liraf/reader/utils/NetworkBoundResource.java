package com.liraf.reader.utils;

import android.util.Log;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.liraf.reader.models.responses.ApiResponse;

// CacheObject: Type for the Resource data. (database cache)
// RequestObject: Type for the API response. (network request)
public abstract class NetworkBoundResource<CacheObject, RequestObject> {

    private static final String TAG = "NetworkBoundResource";

    private final AppExecutors appExecutors;
    private final MediatorLiveData<Resource<CacheObject>> results = new MediatorLiveData<>();

    public NetworkBoundResource(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
        init();
    }

    private void init() {
        // update LiveData for loading status
        results.setValue(Resource.loading(null));

        final LiveData<CacheObject> dbSource = loadFromDb();

        results.addSource(dbSource, cacheObject -> {

            results.removeSource(dbSource);

            if (shouldFetch(cacheObject)) {
                fetchFromNetwork(dbSource);
            } else {
                results.addSource(dbSource, cacheObject1 -> setValue(Resource.success(cacheObject1)));
            }
        });
    }

    private void fetchFromNetwork(final LiveData<CacheObject> dbSource) {

        Log.d(TAG, "fetchFromNetwork: called.");

        // update LiveData for loading status
        results.addSource(dbSource, cacheObject -> setValue(Resource.loading(cacheObject)));

        final LiveData<ApiResponse<RequestObject>> apiResponse = createCall();

        results.addSource(apiResponse, requestObjectApiResponse -> {
            results.removeSource(dbSource);
            results.removeSource(apiResponse);

            if (requestObjectApiResponse instanceof ApiResponse.ApiSuccessResponse) {
                Log.d(TAG, "onChanged: ApiSuccessResponse.");

                appExecutors.diskIO().execute(new Runnable() {
                    @Override
                    public void run() {

                        // save the response to the local db
                        saveCallResult((RequestObject) processResponse((ApiResponse.ApiSuccessResponse) requestObjectApiResponse));

                        appExecutors.mainThread().execute(new Runnable() {
                            @Override
                            public void run() {
                                results.addSource(loadFromDb(), new Observer<CacheObject>() {
                                    @Override
                                    public void onChanged(@Nullable CacheObject cacheObject) {
                                        setValue(Resource.success(cacheObject));
                                    }
                                });
                            }
                        });
                    }
                });
            } else if (requestObjectApiResponse instanceof ApiResponse.ApiEmptyResponse) {
                Log.d(TAG, "onChanged: ApiEmptyResponse");

                appExecutors.mainThread().execute(() -> results.addSource(loadFromDb(), cacheObject -> setValue(Resource.success(cacheObject))));
            } else if (requestObjectApiResponse instanceof ApiResponse.ApiErrorResponse) {
                Log.d(TAG, "onChanged: ApiErrorResponse.");

                results.addSource(dbSource, cacheObject -> setValue(
                        Resource.error(
                                ((ApiResponse.ApiErrorResponse) requestObjectApiResponse).getErrorMessage(),
                                cacheObject
                        )
                ));
            }
        });
    }

    private CacheObject processResponse(ApiResponse.ApiSuccessResponse response) {
        return (CacheObject) response.getBody();
    }

    private void setValue(Resource<CacheObject> newValue) {
        if (results.getValue() != newValue) {
            results.setValue(newValue);
        }
    }

    // Called to save the result of the API response into the database.
    @WorkerThread
    protected abstract void saveCallResult(@NonNull RequestObject item);

    // Called with the data in the database to decide whether to fetch
    // potentially updated data from the network.
    @MainThread
    protected abstract boolean shouldFetch(@Nullable CacheObject data);

    // Called to get the cached data from the database.
    @NonNull
    @MainThread
    protected abstract LiveData<CacheObject> loadFromDb();

    // Called to create the API call.
    @NonNull
    @MainThread
    protected abstract LiveData<ApiResponse<RequestObject>> createCall();

    // Returns a LiveData object that represents the resource that's implemented
    // in the base class.
    public final LiveData<Resource<CacheObject>> getAsLiveData() {
        return results;
    }

    ;
}