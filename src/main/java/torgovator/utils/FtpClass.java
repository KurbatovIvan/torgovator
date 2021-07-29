package torgovator.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTPClientConfig;

public interface FtpClass {
	public String[] listNames(String pathname) throws IOException;

	public void configure(FTPClientConfig config);

	public void setControlKeepAliveTimeout(int a);

	public void connect(String ftpServer) throws SocketException, IOException;

	public void setControlKeepAliveReplyTimeout(int i);

	public String getReplyString();

	public boolean login(String ftpLogin, String ftpPassword) throws IOException;

	public int getReplyCode();

	public void disconnect() throws IOException;

	public boolean setFileType(int binaryFileType) throws IOException;

	public boolean retrieveFile(String string, FileOutputStream fos) throws IOException;

	public boolean logout() throws IOException;

	public boolean isConnected();

	public String getWinGetDir();

	public void setWinGetDir(String winGetDir);

	public void enterLocalPassiveMode();
}
