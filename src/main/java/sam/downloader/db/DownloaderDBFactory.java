package sam.downloader.db;

import sam.downloader.db.entities.meta.IDChapter;
import sam.downloader.db.entities.meta.IDManga;
import sam.downloader.db.entities.meta.IDPage;

public interface DownloaderDBFactory {
	public IDManga     createManga(int manga_id, String dir_name, String manga_name, String url, String error, String status);
	public IDChapter   createChapter(IDManga manga, double number, String title, String volume, String source, String target, String url, String error, String status);
	public IDPage      createPage(IDChapter chapter, int order, String page_url, String img_url, String error, String status);
	public default boolean loadPages() {
		return true;
	}
}
