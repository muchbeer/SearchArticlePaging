package raum.muchbeer.searcharticlepaging.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import raum.muchbeer.searcharticlepaging.util.AppUtils;

public class Feed implements Parcelable {

    private transient long id;
    private String status;
    private long totalResults;
    private List<Articles> articles;

    public long getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public long getTotalResults() {
        return totalResults;
    }

    public List<Articles> getArticles() {
        return articles;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTotalResults(long totalResults) {
        this.totalResults = totalResults;
    }

    public void setArticles(List<Articles> articles) {
        this.articles = articles;
    }

    protected Feed(Parcel in) {
        id = AppUtils.getRandomNumber();
        status = in.readString();
        totalResults = in.readLong();
        articles = in.createTypedArrayList(Articles.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(status);
        dest.writeLong(totalResults);
        dest.writeTypedList(articles);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Feed> CREATOR = new Creator<Feed>() {
        @Override
        public Feed createFromParcel(Parcel in) {
            return new Feed(in);
        }

        @Override
        public Feed[] newArray(int size) {
            return new Feed[size];
        }
    };
}
