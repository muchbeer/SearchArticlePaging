package raum.muchbeer.searcharticlepaging.repository;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PageKeyedDataSource;

import raum.muchbeer.searcharticlepaging.api.NetworkStatus;
import raum.muchbeer.searcharticlepaging.api.RetroFactory;
import raum.muchbeer.searcharticlepaging.api.RetroService;
import raum.muchbeer.searcharticlepaging.model.Articles;
import raum.muchbeer.searcharticlepaging.model.Feed;
import raum.muchbeer.searcharticlepaging.ui.AppController;
import raum.muchbeer.searcharticlepaging.util.BaseConstant;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArticlePagedKeyedDataSource extends PageKeyedDataSource<Long, Articles> implements BaseConstant {

    private static final String LOG_TAG = ArticlePagedKeyedDataSource.class.getSimpleName();

    private Application appController;

    public RetroService articleDataService;
    private MutableLiveData networkState;
    private MutableLiveData initialLoading;
    private String searchArticles;


    public ArticlePagedKeyedDataSource(Application appController, String searchArticles) {
        this.appController = appController;
        this.searchArticles = searchArticles;
        networkState = new MutableLiveData();
        initialLoading = new MutableLiveData();
        articleDataService = RetroFactory.create();
    }

    public MutableLiveData getNetworkState() {
        return networkState;
    }

    public MutableLiveData getInitialLoading() {
        return initialLoading;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Long> params, @NonNull LoadInitialCallback<Long, Articles> callback) {
        initialLoading.postValue(NetworkStatus.LOADING);
        networkState.postValue(NetworkStatus.LOADING);

        articleDataService.fetchArticles(searchArticles, API_KEY, 1, params.requestedLoadSize)
                                    .enqueue(new Callback<Feed>() {
                                        @Override
                                        public void onResponse(Call<Feed> call, Response<Feed> response) {
                                            if(response.isSuccessful()) {
                                                callback.onResult(response.body().getArticles(), null, 2l);
                                               Log.d(LOG_TAG, "The value is: "+ response.body().getArticles());
                                                initialLoading.postValue(NetworkStatus.LOADED);
                                                networkState.postValue(NetworkStatus.LOADED);

                                            } else {
                                                initialLoading.postValue(new NetworkStatus(NetworkStatus.Status.FAILED, response.message()));
                                                networkState.postValue(new NetworkStatus(NetworkStatus.Status.FAILED, response.message()));
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Feed> call, Throwable t) {
                                            String errorMessage = t == null ? "unknown error" : t.getMessage();
                                            networkState.postValue(new NetworkStatus(NetworkStatus.Status.FAILED, errorMessage));
                                        }
                                    });
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Long> params, @NonNull LoadCallback<Long, Articles> callback) {

    }

    @Override
    public void loadAfter(@NonNull LoadParams<Long> params, @NonNull LoadCallback<Long, Articles> callback) {
        Log.i(LOG_TAG, "Loading Rang " + params.key + " Count " + params.requestedLoadSize);
        networkState.postValue(NetworkStatus.LOADING);

        articleDataService.fetchArticles(searchArticles, API_KEY, params.key, params.requestedLoadSize).enqueue(new Callback<Feed>() {
            @Override
            public void onResponse(Call<Feed> call, Response<Feed> response) {
                if(response.isSuccessful()) {
                    long nextKey = (params.key == response.body().getTotalResults()) ? null : params.key+1;
                    callback.onResult(response.body().getArticles(), nextKey);
                    networkState.postValue(NetworkStatus.LOADED);

                } else networkState.postValue(new NetworkStatus(NetworkStatus.Status.FAILED, response.message()));
            }

            @Override
            public void onFailure(Call<Feed> call, Throwable t) {
                String errorMessage = t == null ? "unknown error" : t.getMessage();
                networkState.postValue(new NetworkStatus(NetworkStatus.Status.FAILED, errorMessage));
            }
        });
    }


}
