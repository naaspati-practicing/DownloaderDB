package sam.downloader.db;

import sam.downloader.db.entities.impl.DChapterImpl;
import sam.downloader.db.entities.impl.DMangaImpl;
import sam.downloader.db.entities.impl.DPageImpl;
import sam.downloader.db.entities.meta.DStatus;
import sam.downloader.db.entities.meta.IDChapter;
import sam.downloader.db.entities.meta.IDManga;

public class DefaultDownloaderDBFactory implements DownloaderDBFactory {
	
	@Override
	public DMangaImpl createManga(int manga_id, String dir_name, String manga_name, String url, String error, String status) {
		return new DMangaImpl(manga_id, dir_name, manga_name, url, error, DStatus.status(status));
	}
	@Override
	public DChapterImpl createChapter(IDManga manga, double number, String title, String volume, String source, String target, String url, String error, String status) {
		return new DChapterImpl(manga, number, title,volume, source, target, url, error, DStatus.status(status));
	}
	@Override
	public DPageImpl createPage(IDChapter chapter, int order, String page_url, String img_url, String error, String status){
		return new DPageImpl(chapter, order, page_url, img_url, error, DStatus.status(status));
	}
}
