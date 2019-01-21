package sam.downloader.db.entities.meta;

import sam.manga.samrock.mangas.MinimalManga;

public interface IDManga extends Iterable<IDChapter>, MinimalManga { 
    String getUrl();
    String getError();
    DStatus getStatus();
    IDChapter addChapter(IDChapter page);
    int size();
}