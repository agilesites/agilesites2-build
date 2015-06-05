package agilesites;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.ajp.AjpNioProtocol;
import org.apache.coyote.http11.Http11NioProtocol;

public class SitesServer {

	private static void exit(int n) {
		try {
			System.exit(n);
		} catch (Exception ex) {
			throw new Error("Exiting with " + n);
		}
	}

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
			System.out.println("usage: <port> <base> [ajpport] [host] | stop <port> | status <port> ");
			exit(0);
		}

		if (args.length == 2 && args[0].equals("stop")) {
			try {
				Socket sock = new Socket("127.0.0.1",
						Integer.parseInt(args[1]) + 1);
				sock.getInputStream().read();
				sock.close();
				System.out.println("Shutdown request accepted.");
			} catch (Exception ex) {
				System.out.println("Server not running.");
			}
			exit(0);
		}

		if (args.length == 2 && args[0].equals("status")) {
			CheckPort.main(new String[] { "127.0.0.1", ""+args[1]});
			exit(0);
		}

		int port = 8181;
		try {
			port = Integer.parseInt(args[0]);
		} catch (Exception ex) {
			System.out.println("bad port" + args[0]);
			exit(0);
		}

		String base = args[1];
		if (!new File(base).isDirectory()) {
			System.out.println("no directory " + args[1]);
			exit(0);
		}

		int ajpport = -1;
		if (args.length > 2)
			try {
				ajpport = Integer.parseInt(args[2]);
				if (ajpport == port) {
					System.out.println("ajp port conflicts with main port");
					exit(1);
				}
				if (ajpport == port + 1) {
					System.out.println("ajp port conflicts with shutdown port");
					exit(1);
				}
				if (ajpport == port - 1) {
					System.out.println("ajp port conflicts with debug port");
					exit(1);

				}
			} catch (Exception ex) {
				System.out.println("bad ajp port format" + args[2]);
				exit(1);
			}

		String hostname = "localhost";
		if (args.length > 3)
			hostname = args[3];

		/*
		 * handle args[0] in format [host:][port][:ajpbport]
		 * 
		 * 
		 * String[] tokens = args[0].split(":");
		 * 
		 * switch (tokens.length) { case 0: port = Integer.parseInt(args[0]);
		 * break; case 1: if (Character.isDigit(args[0].charAt(0))) { port =
		 * Integer.parseInt(tokens[0]); ajpport = Integer.parseInt(tokens[1]); }
		 * else { hostname = tokens[0]; port = Integer.parseInt(tokens[1]); }
		 * break;
		 * 
		 * case 2: hostname = tokens[0]; port = Integer.parseInt(tokens[1]);
		 * ajpport = Integer.parseInt(tokens[2]); break; }
		 */

		final Tomcat tomcat = new Tomcat();
		tomcat.setPort(port);
		tomcat.setHostname(hostname);
		tomcat.setBaseDir(base);
		tomcat.enableNaming();
		tomcat.setSilent(false);
        tomcat.getHost(); // force initializaition - trick found in forums

        // nio connector
        //final Connector nioConnector = new Connector(Http11NioProtocol.class.getName());
        //nioConnector.setPort(port);
        //nioConnector.setSecure(false);
        //nioConnector.setScheme("http");
        //nioConnector.setProtocol("HTTP/1.1");

        //tomcat.getService().removeConnector(tomcat.getConnector());
        //tomcat.getService().addConnector(nioConnector);
        //tomcat.setConnector(nioConnector);
        //setPropertyIfExistEnv("maxThreads", nioConnector);
        //setPropertyIfExistEnv("maxConnections",nioConnector);

        /// set max threads and connections
        // was
        setPropertyIfExistEnv("maxThreads", tomcat.getConnector());
        setPropertyIfExistEnv("maxConnections", tomcat.getConnector());

        // ajp connector
        if (ajpport != -1) {
            Connector c = new Connector(/*AjpNioProtocol.class.getName()*/ "AJP/1.3");
            c.setPort(ajpport);
            setPropertyIfExistEnv("maxThreads", c);
            setPropertyIfExistEnv("maxConnections", c);
            tomcat.getService().addConnector(c);
        }

        // webapps
        File webapps = new File(base, "webapps");
		proxyHost = hostname;
		proxyPort = port;

		if (new File(webapps, "ss").isDirectory())
			proxyPath = "/ss/Satellite";
		else
			proxyPath = "/cs/Satellite";

		System.out.printf("*** %s:%d:%d ***\n", hostname, port, ajpport);

		for (File filepath : webapps.listFiles()) {
			if (!filepath.isDirectory())
				continue;
			String ctx = filepath.getName();
			if (ctx.equals("ROOT"))
				ctx = "";
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
				exit(0);
			}
		}.start();

        // go!
		tomcat.start();
		tomcat.getServer().await();
	}

    private static void setPropertyIfExistEnv(String property, Connector c) {
        if(c!=null) {
            String value = System.getenv("TOMCAT_"+property.toUpperCase());
            if(value!=null) {
                System.out.println(c.getProtocolHandlerClassName()+"."+property+"="+value);
                c.setProperty(property, value);
            }
        }
    }
}
