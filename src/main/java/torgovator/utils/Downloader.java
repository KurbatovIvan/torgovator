package torgovator.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;
import org.ini4j.InvalidFileFormatException;

import torgovator.torgovator.App;
import torgovator.torgovator.Params;
import torgovator.torgovator.WorkDir;

/** Downloads a file from a ftp server.
 * 
 * @param filename
 *            the name to store the file locally
 * @param url
 *            the url of the server
 * @throws IOException
 */
public class Downloader extends Thread {
	private static Logger log = Logger.getLogger(App.class.getName());

	private String FtpMaska = "";
	private String WorkDirOk = "";
	private String WorkDirIn = "";
	private String FtpServer = "";
	private String FtpLogin = "";
	private String FtpPassword = "";
	private String DirectoryToDownload = "";
	private boolean overWinGet = false;

	public Downloader(Builder builder) {
		FtpMaska = builder.FtpMaska;
		WorkDirOk = builder.WorkDirOk;
		WorkDirIn = builder.WorkDirIn;
		FtpServer = builder.FtpServer;
		FtpLogin = builder.FtpLogin;
		FtpPassword = builder.FtpPassword;
		DirectoryToDownload = builder.DirectoryToDownload;
		overWinGet = builder.overWinGet;

	}

	/** @param workDirOk
	 *            Папка в которой хранятся все скачанные за все время
	 * @param workDirIn
	 *            Папка в которую скачиваются свежие архивы
	 * @param ftpServer
	 *            Адрес ФТП сервера
	 * @param ftpLogin
	 *            Логин для ФТП сервера
	 * @param ftpPassword
	 *            Пароль для ФТП сервера
	 * @param directoryToDownload
	 *            Папка на ФТП сервере для скачивания */

	public static class Builder {
		private String WorkDirOk;
		private String WorkDirIn;
		private String FtpServer;
		private String FtpLogin;
		private String FtpPassword;
		private String FtpMaska; // TODO ТУТ надо будет доделывать маску для 223 ФЗ
		private String DirectoryToDownload;
		private boolean overWinGet;

		public Builder WorkDirOk(String val) {
			WorkDirOk = val;
			return this;
		}

		public Builder WorkDirIn(String val) {
			WorkDirIn = val;
			return this;
		}

		public Builder FtpServer(String val) {
			FtpServer = val;
			return this;
		}

		public Builder FtpLogin(String val) {
			FtpLogin = val;
			return this;
		}

		public Builder FtpPassword(String val) {
			FtpPassword = val;
			return this;
		}

		public Builder DirectoryToDownload(String val) {
			DirectoryToDownload = val;
			return this;
		}

		public Builder FtpMaska(String val) {
			FtpMaska = val;
			return this;
		}

		public Builder FtpOverWinGet(boolean overWinGet) {
			this.overWinGet = overWinGet;
			return this;
		}

		public Downloader build() {
			return new Downloader(this);
		}

	}

	/** Скачивает файл с фтп
	 * 
	 * @throws InvalidFileFormatException
	 * @throws IOException
	 */
	public void Download() throws InvalidFileFormatException, IOException {
		Params p = new Params();
		FtpClass ftp = null;
		FTPClientConfig config = new FTPClientConfig();
		config.setServerTimeZoneId("Pacific/Pitcairn");
		if (overWinGet == false) {
			log.info("Скачиваю встроенными средствами");
			ftp = new FTPClientWithList();
		} else

		{
			log.info("Скачиваю через WinGet");
			ftp = new FtpWingGet();
			ftp.setWinGetDir(p.getWinGetDir());
		}

		// Ниже с проксей, но не работает для анонимных проксей, и надо в имени сервера сразу логин и пароль писать free:free
		//		FTPHTTPClientWithList ftp = new FTPHTTPClientWithList("127.0.0.1", 3128);

		ftp.configure(config);

		try {
			int reply;

			ftp.setControlKeepAliveTimeout(300); // set timeout to 5 minutes
			ftp.setControlKeepAliveReplyTimeout(3000);
			//ftp.enterLocalActiveMode();
			//ftp.enterLocalPassiveMode();
			ftp.connect(FtpServer);
			log.info("Connected to " + FtpServer + ".");
			log.info(ftp.getReplyString());
			log.info("enterLocalPassiveMode");
			ftp.enterLocalPassiveMode();
			ftp.login(FtpLogin, FtpPassword);
			String ReplyString = ftp.getReplyString();
			log.info(ftp.getReplyString());
			// After connection attempt, you should check the reply code to verify
			// success.
			reply = ftp.getReplyCode();

			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				log.warning("FTP server refused connection.: " + ReplyString);
				throw new Exception(ReplyString);

			}
			// ... // transfer files
			String directory = DirectoryToDownload;
			log.info("Папка для скачивания:" + directory);

			// Получаем Файлы в папке на ФТП
			String[] filesFtp = ftp.listNames(directory);
			log.info("Файлы для скачивания" + filesFtp);

			filesFtp = extractMask(filesFtp);
			// Тут скачиваем файлы
			// Получаем список файлов в рабочей папке
			File F = new File(WorkDirOk);
			String[] filesInOkDir = F.list();

			ftp.setFileType(FTP.BINARY_FILE_TYPE);
			if (filesInOkDir == null) {
				throw new IOException(
						"Не существует папки с обработанными архивами, обычно называеся 'OK'. Удалите рабочую папку и программа автоматически создаст все нужные ей папки");
			}
			;
			if (filesFtp == null) {
				System.out.println("Нет файлов для скачивания");
				ftp.disconnect();
				return;
			}
			for (int i = 0; i < filesFtp.length; i++) { //
				boolean Download = true;
				for (int j = 0; j < filesInOkDir.length; j++) {
					if (filesFtp[i].contains(filesInOkDir[j]) == true) {
						Download = false;
					}
					;
				}
				if (Download == true) {
					System.out.println(filesFtp[i]);
					System.out.println(FilenameUtils.getName(filesFtp[i]));

					String filename = WorkDirIn + "/" + FilenameUtils.getName(filesFtp[i]);

					if (Params.isOverwinget()) {
						ftp.retrieveFile(filesFtp[i], null);
					} else {
						FileOutputStream fos = new FileOutputStream(filename);
						ftp.retrieveFile(filesFtp[i], fos);
						fos.flush();
						fos.close();
					}
				}
				;

			}

			ftp.logout();
		} catch (Exception e) {
			log.warning("Возникла ошибка при скачивании файлов с ФТП из папки :" + DirectoryToDownload);
			log.warning(ExceptionUtils.ExceptionStackToString(e));

		} finally {
			if (ftp.isConnected()) {
				try {
					ftp.disconnect();
				} catch (IOException ioe) {
					// do nothing
				}
			}
			log.info("All Ok+ Download complete");
		}

	}

	private String[] extractMask(String[] filesFtp) {
		if (filesFtp == null) {
			return filesFtp;
		}
		ArrayList<String> newfilesFtp = new ArrayList<String>();
		if (!FtpMaska.equals("")) {
			for (int i = 0; i < filesFtp.length; i++) {
				if (filesFtp[i].contains(FtpMaska)) {
					newfilesFtp.add(filesFtp[i]);
				}
			}
			filesFtp = newfilesFtp.toArray(new String[newfilesFtp.size()]);
		}
		return filesFtp;
	}

	@Override
	public void run() //Этот метод будет выполнен в побочном потоке
	{
		try {
			Download();
		} catch (Exception e) {
			log.warning("Не скачали файлы..");
			log.warning(e.getMessage());
			log.warning(ExceptionUtils.ExceptionStackToString(e));
		}
	}

	public static void Download(WorkDir workdir, String[] dirTodownload, String FtpUrl, String FtpUserName,
			String FtpUserPasswd, String FtpMaska) {
		log.info("Начинаем скачивать");
		Params p = new Params();
		Downloader[] arrayDownLoader = new Downloader[dirTodownload.length];
		log.info("Количество папок для скачивания:" + dirTodownload.length);
		if ((workdir != null) && (dirTodownload != null) && (FtpUrl != null) && (FtpUserName != null)
				&& (FtpUserPasswd != null))
			try {

				int i = 0;
				//	Запускаем сразу несколько потоков по разным папкам для ускорения				
				for (String NameDir : dirTodownload) {
					Downloader downloader = new Downloader.Builder().DirectoryToDownload(NameDir)
							.WorkDirOk(workdir.getWorkDirOk()).WorkDirIn(workdir.getWorkDirIn()).FtpServer(FtpUrl)
							.FtpLogin(FtpUserName).FtpMaska(FtpMaska).FtpPassword(FtpUserPasswd)
							.FtpOverWinGet(p.isOverwinget()).build();

					Thread.sleep(100); // без задержки чет заедало, теперь норм
					if (p.isOverwinget() == true) {
						// Без многопоточности	через WinGet					
						downloader.Download();
					} else {
						downloader.start();
						arrayDownLoader[i] = downloader;
					}
					i++;
				}
			} catch (Exception e) {
				log.warning("Не скачали файлы..");
				log.warning(e.getMessage());
				log.warning(ExceptionUtils.ExceptionStackToString(e));
				e.printStackTrace();
			}

		for (int i = 0; i < arrayDownLoader.length; i++) {
			System.out.println("Ждем завершения потока № " + i);
			if (arrayDownLoader[i] != null) {
				while (arrayDownLoader[i].isAlive())

					;
			}
			System.out.println("Дождались завершения потока № " + i);

		}
		System.out.println("Дождались завершения всех потоков по скачиванию ");
		;
	}

	/** @return the overWinGet */
	public boolean isOverWinGet() {
		return overWinGet;
	}

	/** @param overWinGet
	 *            the overWinGet to set */
	public void setOverWinGet(boolean overWinGet) {
		this.overWinGet = overWinGet;
	}
}
