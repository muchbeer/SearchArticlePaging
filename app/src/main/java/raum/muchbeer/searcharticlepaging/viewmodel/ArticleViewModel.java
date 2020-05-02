package raum.muchbeer.searcharticlepaging.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import raum.muchbeer.searcharticlepaging.api.NetworkStatus;
import raum.muchbeer.searcharticlepaging.model.Articles;
import raum.muchbeer.searcharticlepaging.repository.ArticleDataSourceFactory;
import raum.muchbeer.searcharticlepaging.ui.AppController;

public class ArticleViewModel extends AndroidViewModel {

    private static final String LOG_TAG = ArticleViewModel.class.getSimpleName();
    private Executor executor;
    private LiveData<NetworkStatus> networkState;
    private LiveData<PagedList<Articles>> articleLiveData;
    private LiveData<PagedList<Articles>> articleLiveDataFilter;

    private Application appController;
    public MutableLiveData<String> filterArticle = new MutableLiveData<>();

    public ArticleViewModel(Application appController) {
        super(appController);
        init();
    }

    private void init() {

        executor = Executors.newFixedThreadPool(5);

     //   ArticleDataSourceFactory articleDataSourceFactory = new ArticleDataSourceFactory(appController);
        ArticleDataSourceFactory articleDataSourceFactoryDefault = new ArticleDataSourceFactory(appController, "kotlin");
        networkState = Transformations.switchMap(articleDataSourceFactoryDefault.getMutableLiveData(),
                dataSource -> dataSource.getNetworkState());

        PagedList.Config pagedListConfig =
                (new PagedList.Config.Builder())
                        .setEnablePlaceholders(false)
                        .setInitialLoadSizeHint(10)
                        .setPageSize(20).build();


        articleLiveData = Transformations.switchMap(new DebouncedLiveData<>(filterArticle , 400),
                                        input -> {
                                            if (input == null || input.equals("") || input.equals("%%")) {
                                                //check if the current value is empty load all data else search
                                                synchronized (this) {
                                                    //check data is loaded before or not
                                                    if (articleLiveDataFilter == null)
                                                        (new LivePagedListBuilder(articleDataSourceFactoryDefault, pagedListConfig))
                                                                .setFetchExecutor(executor)
                                                                .build();
                                                }
                                                return articleLiveDataFilter;
                                        }

                                            else {
   LiveData<PagedList<Articles>>    articleLiveDataAfterDefault = (new LivePagedListBuilder(
                                          new ArticleDataSourceFactory(appController, input), pagedListConfig))
                                                        .setFetchExecutor(executor)
                                                        .build();

                                                return articleLiveDataAfterDefault;
                                            }
                                        });

    }
    public LiveData<NetworkStatus> getNetworkState() {
        return networkState;
    }

    public LiveData<PagedList<Articles>> getArticleLiveData() {
        return articleLiveData;
    }

    }

