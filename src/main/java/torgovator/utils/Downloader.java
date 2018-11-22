package torgovator.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;
import org.ini4j.InvalidFileFormatException;

import torgovator.torgovator.App;
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

	private String WorkDirOk = "";
	private String WorkDirIn = "";
	private String FtpServer = "";
	private String FtpLogin = "";
	private String FtpPassword = "";
	private String DirectoryToDownload = "";

	public Downloader(Builder builder) {
		WorkDirOk = builder.WorkDirOk;
		WorkDirIn = builder.WorkDirIn;
		FtpServer = builder.FtpServer;
		FtpLogin = builder.FtpLogin;
		FtpPassword = builder.FtpPassword;
		DirectoryToDownload = builder.DirectoryToDownload;

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
		private String DirectoryToDownload;

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

		FTPClientWithList ftp = new FTPClientWithList();
		FTPClientConfig config = new FTPClientConfig();
		// config.setXXX(YYY); // change required options
		config.setServerTimeZoneId("Pacific/Pitcairn");
		// for example config.setServerTimeZoneId("Pacific/Pitcairn")
		ftp.configure(config);

		try {
			int reply;

			ftp.setControlKeepAliveTimeout(120); // set timeout to 2 minutes
			ftp.connect(FtpServer);
			log.info("Connected to " + FtpServer + ".");
			log.info(ftp.getReplyString());
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

			// Файлы в папке на ФТП
			String[] filesFtp = ftp.listNames(directory);

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

			for (int i = 0; i < filesFtp.length; i++) { //
				boolean Download = true;
				for (int j = 0; j < filesInOkDir.length; j++) {
					if (filesFtp[i].contains(filesInOkDir[j]) == true) {
						Download = false;
					}
					;
				}
				if (Download == true) {
					// System.out.println(filesFtp[i]);
					System.out.println(FilenameUtils.getName(filesFtp[i]));

					String filename = WorkDirIn + "/" + FilenameUtils.getName(filesFtp[i]);

					FileOutputStream fos = new FileOutputStream(filename);

					ftp.retrieveFile(filesFtp[i], fos);
					fos.flush();
					fos.close();
				}
				;

			}

			ftp.logout();
		} catch (Exception e) {
			log.warning("Возникал ошибка при скачивании файлов с ФТП из папки :" + DirectoryToDownload);
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
			String FtpUserPasswd) {
		Downloader[] arrayDownLoader = new Downloader[dirTodownload.length];
		if ((workdir != null) && (dirTodownload != null) && (FtpUrl != null) && (FtpUserName != null)
				&& (FtpUserPasswd != null))
			try {

				int i = 0;
				//	Запускаем сразу несколько потоков по разным папкам для ускорения				
				for (String NameDir : dirTodownload) {
					Downloader downloader = new Downloader.Builder().DirectoryToDownload(NameDir)
							.WorkDirOk(workdir.getWorkDirOk()).WorkDirIn(workdir.getWorkDirIn()).FtpServer(FtpUrl)
							.FtpLogin(FtpUserName).FtpPassword(FtpUserPasswd).build();

					downloader.start();
					arrayDownLoader[i] = downloader;
					Thread.sleep(100); // без задержки чет заедало, теперь норм
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
		;
	}
}
