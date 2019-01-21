package sam.downloader.db.entities.impl;

import java.util.Objects;

import org.json.JSONObject;
import org.json.JSONString;

import sam.downloader.db.DownloaderDB;
import sam.downloader.db.entities.meta.DStatus;
import sam.downloader.db.entities.meta.IDChapter;
import sam.downloader.db.entities.meta.IDPage;


public class DPageImpl extends Utils implements IDPage, JSONString {
    protected int order;
    protected final String page_url;
    protected String img_url;
    protected transient final IDChapter chapter;
    
    public DPageImpl(IDChapter chapter, int order, String page_url){
    	this(chapter, order, page_url, null, null, null);
    }
    public DPageImpl(IDChapter chapter, int order, String page_url, String img_url, String error, DStatus status){
    	this.chapter = Objects.requireNonNull(chapter);
        this.order = order;
        this.page_url = Objects.requireNonNull(page_url);
        this.img_url = img_url;
        this.error = error;
        this.status = status;
    }
    
    public IDChapter getChapter() {
		return chapter;
	}
    @Override public int getOrder(){ return this.order; }
    @Override public String getPageUrl(){ return this.page_url; }
    @Override public String getImgUrl(){ return this.img_url; }
    @Override public String getError(){ return this.error; }
    @Override public DStatus getStatus(){ return this.status; }
    
	@Override
	public String toString() {
		if(DownloaderDB.JSON_TOSTRING)
			return toJSONString();
		
		return getClass().getSimpleName()+" [order=" + order + ", page_url=" + page_url + ", img_url=" + img_url + ", status=" + status + "]";
	}
	@Override
	public void setImgUrl(String img_url) {
		this.img_url = img_url;
	}

	@Override
	public String toJSONString() {
		JSONObject o = new JSONObject();
		o.put("order", getOrder());
		o.put("page_url", getPageUrl());
		o.put("img_url", getImgUrl());
		o.put("error", getError());
		o.put("status", getStatus());
		return o.toString(4);
	}
}
