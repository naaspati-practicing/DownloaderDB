package sam.downloader.db.entities.meta;

import sam.manga.samrock.chapters.MinimalChapterFile;

public interface IDChapter extends Iterable<IDPage>, MinimalChapterFile { 
    String getUrl();
    String getError();
    IDPage addPage(IDPage page);
    DStatus getStatus();
    String getVolume();
	int size();
}