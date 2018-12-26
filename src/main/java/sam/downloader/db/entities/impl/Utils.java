package sam.downloader.db.entities.impl;

import sam.downloader.db.entities.meta.DStatus;
import sam.downloader.db.entities.meta.ErrorSetter;

class Utils implements ErrorSetter {
	protected String error;
	protected DStatus status;

	@Override
	public void setError(String error, DStatus status) {
		if(status == DStatus.SUCCESS && error == null)
			this.error = null;
		else {
			if(this.error == null)
				this.error = error;
			else if(error != null)
				this.error += "\n"+ error; 
		}
		this.status = status;
	}

	public void setStatus(DStatus status) {
		this.status = status;
	}
	@Override
	public String getError() {
		return error;
	}
}
