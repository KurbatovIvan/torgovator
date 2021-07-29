package torgovator.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.net.ftp.FTPClientConfig;

import torgovator.torgovator.Params;

/** @author ivank */
public class FtpWingGet implements FtpClass {
	private static Logger log = Logger.getLogger(FtpWingGet.class.getName());

	private String ftpLogin = "";
	private String ftpPassword = "";
	private String ftpServer = "";
	private String WinGetDir = "";
	private boolean winGetExist = false;

	public FtpWingGet() {

	}

	public void connect(String ftpServer) {
		File f = new File(getWinGetDir() + "//wget.exe");
		if (f.exists()) {
			// Папка существует и в ней есть файл
			winGetExist = true;
			this.ftpServer = ftpServer;
		}
	}

	public String getReplyString() {
		if (winGetExist) {
			return "Закачиваем через WinGet, все Ok+";
		} else {
			return "Нет файла или папки WinGet";
		}
	}

	public void configure(FTPClientConfig config) {
	}

	public void setControlKeepAliveTimeout(int time) {
	}

	public void setControlKeepAliveReplyTimeout(int time) {
	}

	@Override
	public boolean login(String ftpLogin, String ftpPassword) throws IOException {
		this.ftpLogin = ftpLogin;
		this.ftpPassword = ftpPassword;
		return true;
	}

	@Override
	public String[] listNames(String pathname) {

		log.info("Запускаем list.bat из папкм WinGet");
		runCmd(getWinGetDir() + "//list.bat " + pathname, null);
		ArrayList<String> results = new ArrayList<String>();

		String contentIndexHtml = readUsingScanner(getWinGetDir() + "//index.html");
		// <a href='(.*?)'>		
		results = getAllRegexpString("(?i)<a(.+?)zip", contentIndexHtml, pathname);
		String[] names = new String[results.size()];
		return results.toArray(names);
	}

	@Override
	public int getReplyCode() {
		if (winGetExist) {
			return 200;
		} else {
			return 451;
		}

	}

	@Override
	public void disconnect() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean setFileType(int binaryFileType) {
		// TODO Auto-generated method stub
		return true;

	}

	@Override
	public boolean retrieveFile(String filename, FileOutputStream fos) throws IOException {
		log.info("Запускаем download.bat из папкм WinGet");
		runCmd(getWinGetDir() + "//download.bat " + filename + " " + Params.getWorkDirTorgi().getWorkDirIn(), null);

		return false;
	}

	@Override
	public boolean logout() throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConnected() {
		// TODO Auto-generated method stub
		return false;
	}

	/** @return the winGetDir */
	public String getWinGetDir() {
		return WinGetDir;
	}

	/** @param winGetDir
	 *            the winGetDir to set */
	public void setWinGetDir(String winGetDir) {
		WinGetDir = winGetDir;
	}

	public static void runCmd(String cmd, String[] args) {
		log.info("Выполняю команду:");
		log.info(cmd);
		Runtime run = Runtime.getRuntime();
		Process pr;
		try {
			pr = run.exec(cmd);

			try {
				pr.waitFor();

				BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
				String line = buf.readLine();
				while (line != null) {
					line = buf.readLine();
					log.info(line);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// читаем файл с помощью Scanner
	private static String readUsingScanner(String fileName) {
		Scanner scanner;
		try {
			scanner = new Scanner(Paths.get(fileName), StandardCharsets.UTF_8.name());

			//здесь мы можем использовать разделитель, например: "\\A", "\\Z" или "\\z"
			String data = scanner.useDelimiter("\\A").next();
			scanner.close();

			return data;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	private static ArrayList<String> getAllRegexpString(String regexp, String data, String pathname) {
		ArrayList<String> results = new ArrayList<String>();

		Matcher matcher = Pattern.compile(regexp, Pattern.UNICODE_CASE).matcher(data);
		while (matcher.find()) {
			results.add(pathname + matcher.group().substring(9));
		}
		return results;
	}

}
