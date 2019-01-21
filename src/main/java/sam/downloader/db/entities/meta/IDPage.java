package sam.downloader.db.entities.meta;

public interface IDPage { 
    int getOrder();
    String getPageUrl();
    String getImgUrl();
    void setImgUrl(String url);
    String getError();
    DStatus getStatus();
}