package torgovator.torgovator;

import java.util.logging.Logger;

public class WorkDir {
	private static Logger log = Logger.getLogger(WorkDir.class.getName());
	private String WorkDir = "";
	private String WorkDirIn = "";
	private String WorkDirOk = "";
	private String WorkDirError = "";
	private String WorkDirTmp = "";

	/** @return the workDir */
	public String getWorkDir() {
		return WorkDir;
	}

	/** @param workDir
	 *            the workDir to set */
	public void setWorkDir(String workDir) {
		WorkDir = workDir;
	}

	/** @return the workDirIn */
	public String getWorkDirIn() {
		return WorkDirIn;
	}

	/** @param workDirIn
	 *            the workDirIn to set */
	public void setWorkDirIn(String workDirIn) {
		WorkDirIn = workDirIn;
	}

	/** @return the workDirOk */
	public String getWorkDirOk() {
		return WorkDirOk;
	}

	/** @param workDirOk
	 *            the workDirOk to set */
	public void setWorkDirOk(String workDirOk) {
		WorkDirOk = workDirOk;
	}

	/** @return the workDirError */
	public String getWorkDirError() {
		return WorkDirError;
	}

	/** @param workDirError
	 *            the workDirError to set */
	public void setWorkDirError(String workDirError) {
		WorkDirError = workDirError;
	}

	/** @return the workDirTmp */
	public String getWorkDirTmp() {
		return WorkDirTmp;
	}

	/** @param workDirTmp
	 *            the workDirTmp to set */
	public void setWorkDirTmp(String workDirTmp) {
		WorkDirTmp = workDirTmp;
	}

}
