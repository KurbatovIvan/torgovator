package torgovator.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPCmd;

class FTPClientWithList extends FTPClient {
	public String[] listNames(String pathname) throws IOException {
		Socket socket = _openDataConnection_(FTPCmd.NLST, getListArguments(pathname));

		if (socket == null) {
			return null;
		}

		BufferedReader reader = new BufferedReader(
				new InputStreamReader(socket.getInputStream(), getControlEncoding()));

		ArrayList<String> results = new ArrayList<String>();
		String line;
		while ((line = reader.readLine()) != null) {
			results.add(line);
		}

		reader.close();
		socket.close();

		if (completePendingCommand()) {
			String[] names = new String[results.size()];
			return results.toArray(names);
		}

		return null;
	}
}