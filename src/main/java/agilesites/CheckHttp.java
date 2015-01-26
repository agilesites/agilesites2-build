package agilesites;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;

public class CheckHttp {

	public static void main(String[] args) throws Exception {
		if (args.length != 2) {
			System.out
					.println("usage: CheckHttp <host> <port>[:ajpport][:localport]");
			System.exit(0);
		}

		String host = args[0];
		String port = "8080";
		String httpLocalPort = port;
		String ajpLocalPort = "-1";

		String[] ports = args[1].split(":");
		switch (ports.length) {
		case 0:
			httpLocalPort = port = ports[0];
			ajpLocalPort = "-1";
		case 1:
			httpLocalPort = port = ports[0];
			ajpLocalPort = ports[1];
		default:
			port = ports[0];
			httpLocalPort = ports[2];
			ajpLocalPort = ports[1];
		}

		// check socket
		boolean wait = true;
		if (!ajpLocalPort.equals("-1"))
			while (wait) {
				System.out.println("checking for local ajp port in "
						+ ajpLocalPort);
				Socket socket = null;
				try {
					socket = new Socket("127.0.0.1",
							Integer.parseInt(ajpLocalPort));
					wait = false;
				} catch (Exception ex) {
					Thread.sleep(1000);
				} finally {
					if (socket != null)
						try {
							socket.close();
						} catch (IOException e) {
						}
				}

			}

		// check local http
		/* disabled as not needed
		wait = true;
		while (wait) {
			URL localUrl = new URL("http", "127.0.0.1",
					Integer.parseInt(httpLocalPort), "/cs/HelloCS");
			System.out.println("checking for " + localUrl);
			HttpURLConnection huc = (HttpURLConnection) localUrl
					.openConnection();
			huc.setRequestMethod("GET");
			huc.connect();
			if (huc.getResponseCode() == 200)
				wait = false;
			Thread.sleep(1000);
		}
		*/

		// check remote http
		wait = true;
		while (wait) {
			URL url = new URL("http", host, Integer.parseInt(port),
					"/cs/HelloCS");
			System.out.println("checking for " + url);
			HttpURLConnection huc = (HttpURLConnection) url.openConnection();
			huc.setRequestMethod("GET");
			huc.connect();
			if (huc.getResponseCode() == 200)
				wait = false;
			Thread.sleep(1000);
		}
	}
}
