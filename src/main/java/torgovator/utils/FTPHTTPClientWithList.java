package torgovator.utils;

import org.apache.commons.net.ftp.FTPHTTPClient;

public class FTPHTTPClientWithList extends FTPHTTPClient {

	public FTPHTTPClientWithList(String proxyHost, int proxyPort, String proxyUser, String proxyPass) {
		super(proxyHost, proxyPort, proxyUser, proxyPass);
	}

	public FTPHTTPClientWithList(String proxyHost, int proxyPort) {
		super(proxyHost, proxyPort);
	}

}
