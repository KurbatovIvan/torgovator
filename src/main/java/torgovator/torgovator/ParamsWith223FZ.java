package torgovator.torgovator;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.prefs.Preferences;

import org.ini4j.Ini;
import org.ini4j.IniPreferences;
import org.ini4j.InvalidFileFormatException;

public class ParamsWith223FZ extends Params {
	private static String progname = "223Fz";
	private static String FtpUrl223Fz = "";
	private static String FtpUserName223Fz = "";
	private static String FtpUserPasswd223Fz = "";
	private static String[] DirToDownload223FZ;

	public ParamsWith223FZ() {
		super();
		try {
			Preferences prefs = new IniPreferences(new Ini(new File(filename)));
			setFtpUrl223Fz(prefs.node(progname).get("UrlFtp223Fz", null));
			setFtpUserName223Fz(prefs.node(progname).get("FtpUserName223Fz", null));
			setFtpUserPasswd223Fz(prefs.node(progname).get("FtpUserName223Fz", null));

		} catch (InvalidFileFormatException e) {
			log.warning(("IOException, BREAK: " + e.toString()));
			e.printStackTrace();
			System.exit(2);
		} catch (IOException e) {
			log.warning(e.toString());
			e.printStackTrace();
			System.exit(2);
		}

		Ini ini = new Ini();
		try {
			ini.load(new FileReader(filename));

			Ini.Section sneezy = ini.get("DirToDownload223Fz");
			if (sneezy == null) {
				throw new NullPointerException("Секция DirToDownload223Fz не найдена в файле настроек");
			}
			setDirToDownload223FZ(sneezy.getAll("DirectoryToDownload", String[].class));

		} catch (IOException E) {
			log.warning(("IOException (BREAK): " + E.toString()));
			E.printStackTrace();
			System.exit(2);
		}
	}

	/** @return the ftpUrl223Fz */
	public static String getFtpUrl223Fz() {
		return FtpUrl223Fz;
	}

	/** @param ftpUrl223Fz
	 *            the ftpUrl223Fz to set */
	public static void setFtpUrl223Fz(String ftpUrl223Fz) {
		FtpUrl223Fz = ftpUrl223Fz;
	}

	/** @return the ftpUserName223Fz */
	public static String getFtpUserName223Fz() {
		return FtpUserName223Fz;
	}

	/** @param ftpUserName223Fz
	 *            the ftpUserName223Fz to set */
	public static void setFtpUserName223Fz(String ftpUserName223Fz) {
		FtpUserName223Fz = ftpUserName223Fz;
	}

	/** @return the ftpUserPasswd223Fz */
	public static String getFtpUserPasswd223Fz() {
		return FtpUserPasswd223Fz;
	}

	/** @param ftpUserPasswd223Fz
	 *            the ftpUserPasswd223Fz to set */
	public static void setFtpUserPasswd223Fz(String ftpUserPasswd223Fz) {
		FtpUserPasswd223Fz = ftpUserPasswd223Fz;
	}

	/** @return the dirToDownload223FZ */
	public static String[] getDirToDownload223FZ() {
		return DirToDownload223FZ;
	}

	/** @param dirToDownload223FZ
	 *            the dirToDownload223FZ to set */
	public static void setDirToDownload223FZ(String[] dirToDownload223FZ) {
		DirToDownload223FZ = dirToDownload223FZ;
	}
}
