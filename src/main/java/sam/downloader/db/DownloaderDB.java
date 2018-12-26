package sam.downloader.db;

import static sam.downloader.db.entities.impl.DBMeta.CHAPTERS;
import static sam.downloader.db.entities.impl.DBMeta.CHAPTER_INDEX;
import static sam.downloader.db.entities.impl.DBMeta.COUNT;
import static sam.downloader.db.entities.impl.DBMeta.CREATED_ON;
import static sam.downloader.db.entities.impl.DBMeta.DIR_NAME;
import static sam.downloader.db.entities.impl.DBMeta.ERROR;
import static sam.downloader.db.entities.impl.DBMeta.IMG_URL;
import static sam.downloader.db.entities.impl.DBMeta.INDEX;
import static sam.downloader.db.entities.impl.DBMeta.MANGAS;
import static sam.downloader.db.entities.impl.DBMeta.MANGA_ID;
import static sam.downloader.db.entities.impl.DBMeta.MANGA_NAME;
import static sam.downloader.db.entities.impl.DBMeta.NUMBER;
import static sam.downloader.db.entities.impl.DBMeta.ORDER;
import static sam.downloader.db.entities.impl.DBMeta.PAGES;
import static sam.downloader.db.entities.impl.DBMeta.PAGE_URL;
import static sam.downloader.db.entities.impl.DBMeta.SOURCE;
import static sam.downloader.db.entities.impl.DBMeta.STATUS;
import static sam.downloader.db.entities.impl.DBMeta.TABLES_TABLE_NAME;
import static sam.downloader.db.entities.impl.DBMeta.TARGET;
import static sam.downloader.db.entities.impl.DBMeta.TITLE;
import static sam.downloader.db.entities.impl.DBMeta.URL;
import static sam.downloader.db.entities.impl.DBMeta.VOLUME;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import sam.collection.IterableWithSize;
import sam.downloader.db.entities.meta.IDChapter;
import sam.downloader.db.entities.meta.IDManga;
import sam.downloader.db.entities.meta.IDPage;
import sam.io.serilizers.StringReader2;
import sam.logging.MyLoggerFactory;
import sam.myutils.MyUtilsException;
import sam.myutils.System2;
import sam.reference.WeakAndLazy;
import sam.sql.sqlite.SQLiteDB;

public class DownloaderDB {
	public static final boolean JSON_TOSTRING = System2.lookupBoolean("JSON_TOSTRING", false);

	private final Path dbpath;
	private static final Logger LOGGER = MyLoggerFactory.logger(DownloaderDB.class);

	private static String DCHAPTER_TABLE_NAME;
	private static String DPAGE_TABLE_NAME;
	private static String DMANGA_TABLE_NAME;
	private static String CREATE_SQL;
	private static int _index = 0; 

	public DownloaderDB(Path dbpath) {
		this.dbpath = dbpath;
	}

	private int index() {
		return _index;
	}
	private final WeakAndLazy<String> sql_base =  new WeakAndLazy<>(() -> MyUtilsException.noError(() -> StringReader2.reader().source(getClass().getResourceAsStream("sql.sql")).read()));

	private void init(int index) {
		if(index < 1)
			throw new IllegalArgumentException("invalid index: "+index);
		if(index == index()) return;

		DCHAPTER_TABLE_NAME = "DChapters"+index;
		DPAGE_TABLE_NAME = "DPages"+index;
		DMANGA_TABLE_NAME = "DMangas"+index;

		CREATE_SQL = String.format(sql_base.get(), index);
		_index = index;
		LOGGER.fine(() -> "INDEX: "+index);
	}

	private int maxIndex(SQLiteDB db) throws SQLException {
		Integer s = db.findFirst("SELECT max("+INDEX+") FROM "+TABLES_TABLE_NAME, rs -> rs.getInt(1));
		return s == null ? -1 : s;
	}

	private DownloaderDBFactory factory;
	@SuppressWarnings("unchecked")
	public synchronized <E extends IDManga> List<E> read(DownloaderDBFactory factory) throws SQLException {
		List<E> mangas = new ArrayList<>();
		if(Files.notExists(dbpath)) {
			LOGGER.warning("DB not found: "+dbpath);
			return mangas;
		}
		try(SQLiteDB db = new SQLiteDB(dbpath)) {

			ResultSet rs0 = db.executeQuery("SELECT * FROM "+TABLES_TABLE_NAME+" WHERE "+INDEX+"=(SELECT MAX("+INDEX+") FROM "+TABLES_TABLE_NAME+")");
			if(!rs0.next()) {
				LOGGER.warning("DB isEmpty: "+dbpath);
				return mangas;
			}
			init(rs0.getInt(INDEX));
			int mangasCount = rs0.getInt(MANGAS);
			if(mangasCount == 0) return mangas;

			HashMap<Integer, E> mangasMap = new HashMap<>(mangasCount);
			IDChapter[] chapters = new IDChapter[rs0.getInt(CHAPTERS)];

			rs0.close();

			this.factory = factory;

			try(ResultSet rs = db.executeQuery("SELECT * FROM "+DMANGA_TABLE_NAME)) {
				while(rs.next()) {
					E  m = (E) manga(rs);
					mangas.add(m);
					mangasMap.put(m.getMangaId(), m);
				}
			}
			try(ResultSet rs = db.executeQuery("SELECT * FROM "+DCHAPTER_TABLE_NAME)) {
				IDManga m = null; 
				while(rs.next()) {
					int id = rs.getInt(MANGA_ID);
					if(m == null || m.getMangaId() != id)
						m = mangasMap.get(id);
					chapters[rs.getInt(INDEX)] = chapter(m, rs);
				}
			}
			
			if(!factory.loadPages())
				return mangas;
			
			try(ResultSet rs = db.executeQuery("SELECT * FROM "+DPAGE_TABLE_NAME)) {
				while(rs.next()) 
					page(chapters[rs.getInt(CHAPTER_INDEX)], rs);
			}
			return mangas;
		}
	}
	private void page(IDChapter chapter, ResultSet rs) throws SQLException {
		IDPage p = factory.createPage(chapter, 
				rs.getInt(ORDER),
				rs.getString(PAGE_URL),
				rs.getString(IMG_URL),
				rs.getString(ERROR),
				rs.getString(STATUS)
				);

		chapter.addPage(p);
	}
	private IDChapter chapter(IDManga manga, ResultSet rs) throws SQLException {
		IDChapter c = factory.createChapter(manga, 
				rs.getDouble(NUMBER),
				rs.getString(TITLE),
				rs.getString(VOLUME),
				rs.getString(SOURCE),
				rs.getString(TARGET),
				rs.getString(URL),
				rs.getString(ERROR),
				rs.getString(STATUS)
				);
		manga.addChapter(c);
		return c;
	}

	private IDManga manga(ResultSet rs) throws SQLException {
		return factory.createManga(
				rs.getInt(MANGA_ID), 
				rs.getString(DIR_NAME), 
				rs.getString(MANGA_NAME), 
				rs.getString(URL), 
				rs.getString(ERROR), 
				rs.getString(STATUS)
				);
	}

	private String insertSQL(String tablename, StringBuilder sink, String...columnNames) {
		sink.setLength(0);

		sink.append("INSERT INTO ")
		.append(tablename).append('(');
		for (String c : columnNames)
			sink.append(c).append(',');
		sink.setCharAt(sink.length() - 1, ')');
		sink.append(" VALUES(");

		for (int i = 0; i < columnNames.length; i++) 
			sink.append('?').append(',');
		sink.setCharAt(sink.length() - 1, ')');

		return sink.toString();
	}

	public synchronized int[] save(Iterable<? extends IDManga> mangas) throws SQLException, IOException {
		Objects.requireNonNull(mangas);
		boolean create = Files.notExists(dbpath);
		if(create)
			init(1);

		try(SQLiteDB db = new SQLiteDB(dbpath, create);) {
			if(create)
				init(1);
			else {
				int n = maxIndex(db);
				init(n < 0 ? 1 : n+1);
			}

			db.executeUpdate(CREATE_SQL);

			IterableWithSize<? extends IDManga> data = IterableWithSize.wrap(mangas);
			if(!data.hasNext()) return new int[3];

			StringBuilder sink = new StringBuilder(50);
			int chapters = 0;
			int pages = 0;
			int mangasCount = 0;

			try(PreparedStatement mangaP = db.prepareStatement(insertSQL(DMANGA_TABLE_NAME, sink, MANGA_ID,DIR_NAME,MANGA_NAME,URL,ERROR,STATUS, COUNT));
					PreparedStatement chapterP = db.prepareStatement(insertSQL(DCHAPTER_TABLE_NAME, sink, INDEX,MANGA_ID,NUMBER,TITLE, VOLUME,SOURCE,TARGET,URL,ERROR,STATUS, COUNT));
					PreparedStatement pageP = db.prepareStatement(insertSQL(DPAGE_TABLE_NAME, sink, CHAPTER_INDEX,ORDER,PAGE_URL,IMG_URL,ERROR,STATUS));
					) {
				for (IDManga manga : data) {
					insertManga(manga, mangaP);
					mangasCount++;

					for (IDChapter chap : manga) {
						int chap_index = chapters++;
						insertChapter(chap_index, manga, chap, chapterP);

						for (IDPage page : chap) { 
							pages++;
							insertPage(chap_index, page, pageP);
						}
					}
				}

				int[] rsult =  new int[] {mangaP.executeBatch().length, chapterP.executeBatch().length, pageP.executeBatch().length};

				try(PreparedStatement p = db.prepareStatement(insertSQL(TABLES_TABLE_NAME, sink, INDEX,MANGAS,CHAPTERS,PAGES,CREATED_ON))) {
					insertTable(mangasCount, chapters, pages, p);
					p.executeUpdate();
				}
				db.commit();
				return rsult;
			}
		}
	}
	private void insertPage(int chapter_index, IDPage page, PreparedStatement p) throws SQLException  {
		p.setInt(1,chapter_index);
		p.setInt(2,page.getOrder());
		p.setString(3,page.getPageUrl());
		p.setString(4,page.getImgUrl());
		p.setString(5,page.getError());
		p.setString(6,tostring(page.getStatus()));
		p.addBatch();
	}
	private void insertChapter(int index, IDManga manga, IDChapter c, PreparedStatement p) throws SQLException  {
		int n = 1;
		p.setInt(n++,index);
		p.setInt(n++,manga.getMangaId());
		p.setDouble(n++,c.getNumber());
		p.setString(n++,c.getTitle());
		p.setString(n++,c.getVolume());
		p.setString(n++,tostring(c.getSource()));
		p.setString(n++,tostring(c.getTarget()));
		p.setString(n++,c.getUrl());
		p.setString(n++,c.getError());
		p.setString(n++,tostring(c.getStatus()));
		p.setInt(n++,c.size());
		p.addBatch();
	}
	private void insertManga(IDManga m, PreparedStatement p) throws SQLException {
		p.setInt(1,m.getMangaId());
		p.setString(2,m.getDirName());
		p.setString(3,m.getMangaName());
		p.setString(4,m.getUrl());
		p.setString(5,m.getError());
		p.setString(6,tostring(m.getStatus()));
		p.setInt(7,m.size());
		p.addBatch();
	}
	private void insertTable(int mangasCount, int chaptersCount, int pagesCount, PreparedStatement p) throws SQLException  {
		p.setInt(1,index());
		p.setInt(2,mangasCount);
		p.setInt(3,chaptersCount);
		p.setInt(4,pagesCount);
		p.setString(5,LocalDateTime.now().toString());
	}
	private String tostring(Object obj) {
		return obj == null ? null : obj.toString();
	}
}
