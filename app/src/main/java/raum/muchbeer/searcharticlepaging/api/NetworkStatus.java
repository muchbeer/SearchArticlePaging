package raum.muchbeer.searcharticlepaging.api;

public class NetworkStatus {

    public enum Status{
        RUNNING,
        SUCCESS,
        FAILED  }

    private final Status status;
    private final String msg;

    public static final NetworkStatus LOADED;
    public static final NetworkStatus LOADING;

    public NetworkStatus(Status status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    static {
        LOADED=new NetworkStatus(Status.SUCCESS,"Success");
        LOADING=new NetworkStatus(Status.RUNNING,"Running");
    }

    public Status getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }
}
