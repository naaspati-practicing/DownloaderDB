package sam.downloader.db.entities.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONString;

import sam.collection.Iterators;
import sam.downloader.db.DownloaderDB;
import sam.downloader.db.entities.meta.DStatus;
import sam.downloader.db.entities.meta.IDChapter;
import sam.downloader.db.entities.meta.IDManga;
import sam.downloader.db.entities.meta.IDPage;


public class DChapterImpl extends Utils implements IDChapter, JSONString {
	protected final double number;
	protected final String title;
	protected Object source;
	protected Object target;
	protected final String url;
	protected String volume;
	protected List<IDPage> pages;
	protected transient final IDManga manga;

	public DChapterImpl(IDManga manga, double number, String title, String url){
		this(manga, number, title, null, null, null, url, null, null);
	}

	public DChapterImpl(IDManga manga, double number, String title, String volume, Object source, Object target, String url, String error, DStatus status){
		this.manga = Objects.requireNonNull(manga);
		this.volume = volume;
		this.number = number;
		this.title = title;
		this.source = source;
		this.target = target;
		this.url = Objects.requireNonNull(url);
		this.error = error;
		this.status = status;
	}

	public IDManga getManga() { return manga; }
	@Override public String getVolume() { return volume; }
	@Override public double getNumber(){ return this.number; }
	@Override public String getTitle(){ return this.title; }
	@Override public Object getSource(){ return this.source; }
	@Override public Object getTarget(){ return this.target; }
	@Override public String getUrl(){ return this.url; }
	@Override public String getError(){ return this.error; }
	@Override public DStatus getStatus(){ return this.status; }
	public List<IDPage> getPages() {
		return pages == null ? pages : Collections.unmodifiableList(pages);
	}
	@Override
	public Iterator<IDPage> iterator() {
		return pages == null ? Iterators.empty() : pages.iterator();
	}
	private String urlNotFound;
	@Override
	public IDPage addPage(IDPage page) {
		if(page == null) return null;
		if(pages == null || pages.isEmpty())  {
			pages = new ArrayList<>();
			pages.add(page);
			return page;
		}
		if(page.getPageUrl().equals(urlNotFound)) {
			pages.add(page);
			urlNotFound = null;
			return page;
		}
		IDPage p = findPage(page.getPageUrl());
		if(p == null)
			pages.add(p = page);
		return p;
	}
	public IDPage findPage(String pageUrl) {
		Objects.requireNonNull(pageUrl);
		if(pages == null) return null;

		for (int i = 0; i < pages.size(); i++) {
			String s = pages.get(i).getPageUrl();
			if(s.hashCode() == pageUrl.hashCode() && s.equals(pageUrl))
				return pages.get(i);
		}
		urlNotFound = pageUrl;
		return null;
	}
	@Override
	public String toString() {
		if(DownloaderDB.JSON_TOSTRING)
			return toJSONString();

		return "DChapterImpl [number=" + number + ", title=" + title + ", source=" + source + ", target=" + target
				+ ", url=" + url + ", status=" + status + "]";
	}

	@Override
	public String toJSONString() {
		JSONObject o = new JSONObject();
		new JSONObject(new HashMap<>());
		o.put("number", getNumber());
		o.put("title", getTitle());
		o.put("source", getSource());
		o.put("target", getTarget());
		o.put("url", getUrl());
		o.put("error", getError());
		o.put("status", getStatus());
		o.put("pages", new JSONArray(getPages()));

		return o.toString(4);
	}

	@Override
	public int size() {
		return pages == null ? 0 : pages.size();
	}
	private String filename;

	@Override
	public String getFileName() {
		return filename;	
	}
	public void setFileName(String filename) {
		this.filename = filename;
	}

}
