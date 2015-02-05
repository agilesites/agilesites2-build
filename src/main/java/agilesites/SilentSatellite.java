package agilesites;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class SilentSatellite {

	private static String fix(File file) {
		return file.getAbsolutePath().replace(File.separatorChar, '/');
	}

	public static void main(String[] args) throws FileNotFoundException,
			IOException {

		if (args.length < 7) {
			System.out
					.println("usage: <base> <base-ini> <install-ini> <output-ini> <cs> <host> <public-port:ajp-port:local-port>");
			System.exit(0);
		}

		Properties baseIni = new Properties();
		File baseFile = new File(args[0]);
		File baseIniFile = new File(args[1]);
		File installIniFile = new File(args[2]);
		File outputIniFile = new File(args[3]);

		String cs = args[4];
		String host = args[5];
		String port = args[6];
		String httpLocalPort = port;
		String ajpLocalPort = "-1";
		
		URL csurl = new URL(cs);
				
		String[] ports = port.split(":");
		System.out.println(port + " " + ports.length);

		switch (ports.length) {
		case 0:
			httpLocalPort = port = ports[0];
			ajpLocalPort = "-1";
			break;
		case 1:
			httpLocalPort = port = ports[0];
			ajpLocalPort = ports[1];
			break;
		default:
			port = ports[0];
			httpLocalPort = ports[2];
			ajpLocalPort = ports[1];
			break;
		}
		
		 

		System.out.println("host=" + host);
		System.out.println("public port=" + port);
		System.out.println("local port=" + httpLocalPort);
		System.out.println("ajp port=" + ajpLocalPort);

		baseIni.load(new FileReader(baseIniFile));
		// baseIni.setProperty("CSInstallDBDSN", "csDataSource");
		baseIni.setProperty("CSHostName", host); // for satellite
		baseIni.setProperty("CSPort", port); // for satellite
		baseIni.setProperty("CASHostNameActual", host);
		baseIni.setProperty("CSInstallDirectory", fix(new File(baseFile,
				"satellite")));
		baseIni.setProperty("CSFTAppServerRoot",
				fix(new File(baseFile, "home")));
		baseIni.setProperty("sCgiPath", "/ss/");
		baseIni.setProperty("CSInstallSharedDirectory", fix(new File(baseFile,
				"shared")));
		baseIni.setProperty("CSInstallWebServerAddress", host);
		baseIni.setProperty("CSInstallWebServerPort", port);
		
		baseIni.setProperty("CASPortNumberLocal", ""+csurl.getPort());
		baseIni.setProperty("CASHostName", csurl.getHost());
		// baseIni.setProperty("CSInstallDBDSN", "csDataSource");
		baseIni.setProperty("CASPortNumber", ""+csurl.getPort());
		baseIni.setProperty("CASHostNameLocal", csurl.getHost());
		// baseIni.setProperty("CSInstallDatabaseType", db);

		// those are to make happy the configurator
		baseIni.setProperty("CSConnectString", cs /* "http://"+host+":"+port+"/cs" */);
		baseIni.setProperty("CSInstallAppName", "ss");
		baseIni.setProperty("CSInstallAppServerPath", fix(baseFile));

		// extra configuration for agilesites installer
		baseIni.setProperty("AsLocalHttpPort", httpLocalPort);
		baseIni.setProperty("AsLocalAjpPort", ajpLocalPort);

		outputIniFile.delete();
		FileWriter ofw = new FileWriter(outputIniFile);
		baseIni.store(ofw, "AgileSites was here");
		ofw.close();
		System.out.println("+++ " + outputIniFile);
		// baseIni.store(new FileWriter(args[4]), "AgileSites was here");
		Properties installIni = new Properties();
		FileReader fr = new FileReader(installIniFile);
		installIni.load(fr);
		fr.close();
		installIni.setProperty("loadfile", outputIniFile.getAbsolutePath());
		FileWriter ifw = new FileWriter(installIniFile);
		installIni.store(ifw, "AgileSites was here");
		ifw.close();
		System.out.println("+++ " + installIniFile);

		boolean isUnix = File.separatorChar == '/';

		File conf = new File("agilesites-env." + (isUnix ? "sh" : "cmd"));
		if (!conf.exists()) {
			System.out.println("+++ " + conf);
			FileWriter fw = new FileWriter(conf);
			Properties prp = new Properties();
			prp.setProperty("sites.port", port);
			prp.setProperty("sites.local.port", httpLocalPort);
			prp.setProperty("sites.local.ajp", ajpLocalPort);
			prp.setProperty("sites.java.home", System.getProperty("java.home"));

			for (Object k : prp.keySet()) {
				String key = k.toString().toUpperCase().replace('.', '_');
				fw.write(isUnix ? "export " : "SET ");
				fw.write(key + "=");
				fw.write(prp.getProperty(k.toString()));
				fw.write(isUnix ? "\n" : "\r\n");
			}
			fw.close();
		} else {
			System.out.println("skipping " + conf);

		}
	}
}
