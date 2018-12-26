package sam.downloader.db.entities.meta;

import java.io.PrintWriter;
import java.io.StringWriter;

import sam.reference.WeakAndLazy;

public interface ErrorSetter {
	static WeakAndLazy<Object[]> keep = new WeakAndLazy<>(() -> {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		return new Object[] {sw, pw};
	});  
	
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
			synchronized(ErrorSetter.class) {
				Object[] o = keep.get();
				
				StringWriter sw = (StringWriter) o[0];
				PrintWriter pw = (PrintWriter) o[1];
				sw.getBuffer().setLength(0);
				
				if(msg != null) {
					pw.println(msg);
					pw.println();
				}
				e.printStackTrace(pw);
				s = sw.toString();
			}
			setError(s, status);
		}
	}
	void setError(String error, DStatus status);
	String getError();
}
