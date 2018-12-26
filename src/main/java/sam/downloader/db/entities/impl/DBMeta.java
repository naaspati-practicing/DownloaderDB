package sam.downloader.db.entities.impl;



public interface DBMeta {
	
	// String DCHAPTER_TABLE_NAME = "DChapters";

	String INDEX = "_index";    // _index integer not null unique
	String MANGA_ID = "manga_id";    // manga_id    integer NOT NULL 
	String NUMBER = "_number";    // _number numeric
	String TITLE = "title";    // title text
	String VOLUME = "volume";    // title text
	String SOURCE = "source";    // source text
	String TARGET = "target";    // target  text
	String URL = "url";    // url  text
	String ERROR = "error";    // error text
	String STATUS = "status";    // status text
	
	
	// String DPAGE_TABLE_NAME = "DPages";

	String ORDER = "_order";
	// String INDEX = "_index";    // _index integer not null unique
	String CHAPTER_INDEX = "chapter_index";    // parent_index integer not nullString ORDER = "_order";    // _order integer
	String PAGE_URL = "page_url";    // page_url text
	String IMG_URL = "img_url";    // img_url text
	// String ERROR = "error";    // error text
	// String STATUS = "status";    // status text
	String COUNT = "_count";
	
	
	// String DMANGA_TABLE_NAME = "DMangas";

	// String INDEX = "_index";    // _index integer not null unique
	// String MANGA_ID = "manga_id";    // manga_id    integer NOT NULL primary key unique
	String DIR_NAME = "dir_name";    // dir_name    text
	String MANGA_NAME = "manga_name";    // manga_name    text
	// String URL = "url";    // url    text
	// String ERROR = "error";    // error text
	// String STATUS = "status";    // status text

	String TABLES_TABLE_NAME = "TABLES";
	
   // String INDEX = "_index";    // _index integer not null unique
    String MANGAS = "mangas";    // mangas integer not null
    String CHAPTERS = "chapters";    // chapters integer not null
    String PAGES = "pages";    // pages integer not null
    String CREATED_ON = "created_on";    // created_on text not null

}