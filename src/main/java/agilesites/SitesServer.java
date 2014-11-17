package agilesites;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;

public class SitesServer {

	private static String proxyHost;

	public static String getProxyHost() {
		return proxyHost;
	}

	private static int proxyPort;

	public static int getProxyPort() {
		return proxyPort;
	}

	private static String proxyPath;

	public static String getProxyPath() {
		return proxyPath;
	}

	public static void main(String[] args) throws Exception {

		if (args.length < 2) {
			System.out.println("usage: [<host>:]<port> <base> | stop");
			System.exit(0);
		}

		if (args.length == 2 && args[0].equals("stop")) {
			try {
				Socket sock = new Socket("127.0.0.1", Integer.parseInt(args[1]));
				sock.getInputStream().read();
				sock.close();
				System.out.println("Shutdown request accepted.");
				System.exit(0);
			} catch (Exception ex) {
				System.out.println("Server not running.");
				System.exit(0);
			}
		}

		String base = args[1];
		String hostname = "localhost";
		int port = 8181;
		int pos = args[0].indexOf(":");
		if (pos == -1) {
			port = Integer.parseInt(args[0]);
		} else {
			hostname = args[0].substring(0, pos);
			port = Integer.parseInt(args[0].substring(pos + 1));
		}

		final Tomcat tomcat = new Tomcat();
		tomcat.setPort(port);
		tomcat.setHostname(hostname);
		tomcat.setBaseDir(base);
		tomcat.enableNaming();
		tomcat.setSilent(false);
		tomcat.getHost();

		File webapps = new File(base, "webapps");
		proxyHost = hostname;
		proxyPort = port;
		if (new File(webapps, "ss").isDirectory())
			proxyPath = "/ss/Satellite";
		else
			proxyPath = "/cs/Satellite";

		System.out.printf("*** %s:%d ***\n", hostname, port);
		for (File filepath : webapps.listFiles()) {
			if (!filepath.isDirectory())
				continue;
			String ctx = filepath.getName();
			if (ctx.equals("ROOT"))
				ctx = "/";
			else if (!ctx.startsWith("/") && !ctx.equals(""))
				ctx = "/" + ctx;
			System.out.println(ctx + " -> " + filepath);
			Context context = tomcat.addWebapp(tomcat.getHost(), ctx,
					filepath.getAbsolutePath());
			File config = new File(new File(filepath, "META-INF"),
					"context.xml");
			if (config.exists()) {
				java.net.URL url = config.toURI().toURL();
				context.setConfigFile(url);
				System.out.println("** with context.xml");
			}
		}

		// stopping socket
		final int killport = port + 1;
		new Thread() {
			public void run() {
				ServerSocket serv = null;
				try {
					serv = new ServerSocket();
					serv.bind(new InetSocketAddress("127.0.0.1", killport));
					Socket sock = serv.accept();
					tomcat.stop();
					sock.getOutputStream().write('\n');
					sock.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					try {
						serv.close();
					} catch (Exception ex) {
					}
				}
				System.exit(0);
			}
		}.start();
		tomcat.start();
		tomcat.getServer().await();
	}
}