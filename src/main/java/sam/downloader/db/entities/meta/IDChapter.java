package sam.downloader.db.entities.meta;

import sam.manga.samrock.chapters.MinimalChapterFile;

public interface IDChapter extends Iterable<IDPage>, MinimalChapterFile { 
    public String getUrl();
    public String getError();
    public IDPage addPage(IDPage page);
    public DStatus getStatus();
    public String getVolume();
	public int size();
}