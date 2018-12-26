create table if not exists TABLES (
  _index integer not null unique,
  mangas integer not null,
  chapters integer not null,
  pages integer not null,
  created_on text not null
);


CREATE TABLE DMangas%1$d (    
  manga_id    integer NOT NULL primary key unique,    
  dir_name    text,
  manga_name    text,
  url    text,
  error text,
  status text,
  _count integer
);

create table DChapters%1$d (    
  _index integer not null unique,
  manga_id    integer NOT NULL,
  _number numeric,    
  title text,     
  volume text,
  source text,     
  target  text,
  url  text, 
  error text,
  status text,
  _count integer  
);

create table DPages%1$d (    
  chapter_index integer not null,
  _order integer,    
  page_url text,     
  img_url text,
  error text,
  status text    
);
