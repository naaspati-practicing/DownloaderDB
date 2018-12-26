package sam.downloader.db.entities.meta;

import sam.manga.samrock.mangas.MinimalManga;

public interface IDManga extends Iterable<IDChapter>, MinimalManga { 
    public String getUrl();
    public String getError();
    public DStatus getStatus();
    public IDChapter addChapter(IDChapter page);
    public int size();
}