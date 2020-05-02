package raum.muchbeer.searcharticlepaging.repository;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

import raum.muchbeer.searcharticlepaging.ui.AppController;

public class ArticleDataSourceFactory extends DataSource.Factory {

    private MutableLiveData<ArticlePagedKeyedDataSource> mutableLiveData;
    private ArticlePagedKeyedDataSource articlePagedKeyedDataSource;
    private Application appController;
    private String searchArticle;

    public ArticleDataSourceFactory(Application appController, String searchArticle) {
        this.appController = appController;
        this.searchArticle = searchArticle;
        this.mutableLiveData = new MutableLiveData<ArticlePagedKeyedDataSource>();
    }

    @NonNull
    @Override
    public DataSource create() {
        articlePagedKeyedDataSource = new ArticlePagedKeyedDataSource(appController, searchArticle);
        mutableLiveData.postValue(articlePagedKeyedDataSource);
        return articlePagedKeyedDataSource;
    }

    public LiveData<ArticlePagedKeyedDataSource> getMutableLiveData() {
        return mutableLiveData;
    }
}
