package sam.downloader.db.entities.meta;

public interface IDPage { 
    public int getOrder();
    public String getPageUrl();
    public String getImgUrl();
    public String getError();
    public DStatus getStatus();
}