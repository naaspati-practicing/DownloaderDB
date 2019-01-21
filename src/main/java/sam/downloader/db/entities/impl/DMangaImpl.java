package sam.downloader.db.entities.impl;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONString;

import sam.collection.Iterators;
import sam.config.MyConfig;
import sam.downloader.db.DownloaderDB;
import sam.downloader.db.entities.meta.DStatus;
import sam.downloader.db.entities.meta.IDChapter;
import sam.downloader.db.entities.meta.IDManga;
import sam.manga.samrock.chapters.MinimalChapter;


public class DMangaImpl extends Utils implements IDManga, JSONString {
	protected final int manga_id;
	protected final String dir_name;
	protected final String manga_name;
	protected final String url;
	protected List<IDChapter> chapters;

	public DMangaImpl(int manga_id, String dir_name, String manga_name, String url, String error, DStatus status){
		this.manga_id = manga_id;
		this.dir_name = dir_name;
		this.manga_name = manga_name;
		this.url = url;
		this.error = error;
		this.status = status;
	}
	@Override public int getMangaId(){ return this.manga_id; }
	@Override public String getDirName(){ return this.dir_name; }
	@Override public String getMangaName(){ return this.manga_name; }
	@Override public String getUrl(){ return this.url; }
	@Override public String getError(){ return this.error; }
	@Override public DStatus getStatus(){ return this.status; }
	public List<? extends IDChapter> getChapters() {
		return chapters == null ? chapters : Collections.unmodifiableList(chapters);
	}

	@Override
	public Iterator<IDChapter> iterator() {
		return chapters == null ? Iterators.empty() : chapters.iterator();
	}
	public void setChapters(List<IDChapter> chapters) {
		this.chapters = chapters;
	}
	private String urlNotFound;
	@Override
	public IDChapter addChapter(IDChapter chapter) {
		if(chapter == null) return null;
		if(chapters == null)  {
			chapters = new ArrayList<>();
			chapters.add(chapter);
			return chapter;
		}
		if(chapter.getUrl().equals(urlNotFound)) {
			chapters.add(chapter);
			urlNotFound = null;
			return chapter;
		}
		
		IDChapter p = findChapter(chapter.getUrl());
		if(p == null)
			chapters.add(p = chapter);
		return p;
	}
	public IDChapter findChapter(String url) {
		Objects.requireNonNull(url);

		if(chapters == null) return null;
		for (int i = 0; i < chapters.size(); i++) {
			String s = chapters.get(i).getUrl();
			if(s.hashCode() == url.hashCode() && s.equals(url))
				return chapters.get(i);
		}
		urlNotFound = url;
		return null;
	}

	@Override
	public String toString() {
		if(DownloaderDB.JSON_TOSTRING)
			return toJSONString();

		return getClass().getSimpleName()+" [manga_id=" + manga_id + ", dir_name=" + dir_name + ", manga_name=" + manga_name + ", url="
		+ url + ", status=" + status + "]";
	}
	@Override
	public String toJSONString() {
		JSONObject o = new JSONObject();
		o.put("manga_id", getMangaId());
		o.put("dir_name", getDirName());
		o.put("manga_name", getMangaName());
		o.put("url", getUrl());
		o.put("error", getError());
		o.put("status", getStatus());
		o.put("chapters", new JSONArray(getChapters()));
		
		return o.toString(4);
	}
	@Override
	public int size() {
		return chapters == null ? 0 : chapters.size();
	}
	@Override
	public Iterable<? extends MinimalChapter> getChapterIterable() {
		return this;
	}
	
	private Path mangapath;
	@Override
	public Path getDirPath() {
		return mangapath != null ? mangapath : (mangapath = Paths.get(MyConfig.MANGA_DIR).resolve(getDirName()));
	}
}
