package sam.downloader.db.entities.meta;

import java.io.PrintWriter;
import java.io.Writer;

import sam.reference.WeakAndLazy;
import sam.string.StringWriter2;

public interface ErrorSetter {
	static WeakAndLazy<StringWriter2> keep = new WeakAndLazy<>(StringWriter2::new);  
	
	default void setFailed(String msg, Throwable e) {
		setError(msg, e, DStatus.FAILED);
	}
	default void setSuccess() {
		setError(null, null, DStatus.SUCCESS);
	}
	default void setError(String msg, Throwable e, DStatus status) {
		if(msg == null && e == null)
			setError(null, status);
		else if(e == null)
			setError(msg, status);
		else {
			String s ;
			synchronized(keep) {
				StringWriter2 sw = keep.get();
				sw.clear();
				
				if(this instanceof IDPage) {
					IDPage p = (IDPage) this;
					sw.append("page_url: ").append(p.getPageUrl()).append('\n');
					sw.append("img_url: ").append(p.getImgUrl()).append('\n');
				}
				if(this instanceof IDChapter) {
					IDChapter p = (IDChapter) this;
					sw.append("url: ").append(p.getUrl()).append('\n');
				}
				if(this instanceof IDManga) {
					IDManga p = (IDManga) this;
					sw.append("url: ").append(p.getUrl()).append('\n');
				}
				
				if(msg != null) 
					sw.append(msg).append('\n');
				
				e.printStackTrace(new PrintWriter(sw));
				s = sw.toString();
			}
			setError(s, status);
		}
	}
	void setError(String error, DStatus status);
	String getError();
}
