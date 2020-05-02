package raum.muchbeer.searcharticlepaging.ui;

import android.app.Application;
import android.content.Context;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import raum.muchbeer.searcharticlepaging.api.RetroFactory;
import raum.muchbeer.searcharticlepaging.api.RetroService;

public class AppController extends Application {
    private RetroService restApi;
    private Scheduler scheduler;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    private static AppController get(Context context) {
        return (AppController) context.getApplicationContext();
    }

    public static AppController create(Context context) {
        return AppController.get(context);
    }

    public RetroService getRestApi() {
        if(restApi == null) {
            restApi = RetroFactory.create();
        }
        return restApi;
    }

    public void setRestApi(RetroService restApi) {
        this.restApi = restApi;
    }

    public Scheduler subscribeScheduler() {
        if (scheduler == null) {
            scheduler = Schedulers.io();
        }
        return scheduler;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }
}
