package raum.muchbeer.searcharticlepaging.api;

import raum.muchbeer.searcharticlepaging.model.Feed;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetroService {

    //https://newsapi.org/v2/everything?q=kotlin&apiKey=e8aaaf15a7554a33a397ce08f9d48c18&pageSize=20&page=2

    @GET("/v2/everything")
    Call<Feed> fetchArticles(@Query("q") String q,
                         @Query("apiKey") String apiKey,
                         @Query("page") long page,
                         @Query("pageSize") int pageSize);
}
