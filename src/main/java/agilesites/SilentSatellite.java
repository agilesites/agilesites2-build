package agilesites;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class SilentSatellite {

	private static String fix(File file) {
		return file.getAbsolutePath().replace(File.separatorChar, '/');
	}

	public static void main(String[] args) throws FileNotFoundException,
			IOException {

		if (args.length < 4) {
			System.out
					.println("usage: <base> <base-ini> <install-ini> <output-ini> [host] [port]");
			System.exit(0);
		}
		
		String host = "localhost";
		String port = "8181";
		//String db = "HSQLDB";
		
		if(args.length>=5) host = args[4];
		if(args.length>=6) port = args[5];
	
		System.out.println("host="+host);
		System.out.println("port="+port);
		
		Properties baseIni = new Properties();
		File baseFile = new File(args[0]);
		File baseIniFile = new File(args[1]);		
		File installIniFile = new File(args[2]);
		File outputIniFile = new File(args[3]);
		baseIni.load(new FileReader(baseIniFile));
		//baseIni.setProperty("CSInstallDBDSN", "csDataSource");
		baseIni.setProperty("CASHostNameActual", host);
		baseIni.setProperty("CSInstallDirectory",
				fix(new File(baseFile, "satellite")));
		baseIni.setProperty("CSFTAppServerRoot",
				fix(new File(baseFile, "home")));
		baseIni.setProperty("sCgiPath", "/ss/");
		baseIni.setProperty("CSInstallSharedDirectory", 
			    fix(new File(baseFile, "shared")));
		baseIni.setProperty("CSInstallWebServerAddress", host);
		baseIni.setProperty("CSInstallWebServerPort", port);
		baseIni.setProperty("CASPortNumberLocal", port);
		baseIni.setProperty("CASHostName", host);
		//baseIni.setProperty("CSInstallDBDSN", "csDataSource");
		baseIni.setProperty("CASPortNumber", port);
		baseIni.setProperty("CASHostNameLocal", host);
		//baseIni.setProperty("CSInstallDatabaseType", db);

		// those are to make happy the configurator
		baseIni.setProperty("CSConnectString", "http://"+host+":"+port+"/cs");
		baseIni.setProperty("CSInstallAppName", "fwadmin");
		baseIni.setProperty("CSInstallAppServerPath", fix(baseFile));

		outputIniFile.delete();
		FileWriter ofw = new FileWriter(outputIniFile);
		baseIni.store(ofw, "AgileSites was here");
		ofw.close();
		System.out.println("+++ "+outputIniFile);
		// baseIni.store(new FileWriter(args[4]), "AgileSites was here");
		Properties installIni = new Properties();
		FileReader fr = new FileReader(installIniFile);
		installIni.load(fr);
		fr.close();
		installIni.setProperty("loadfile", outputIniFile.getAbsolutePath());
		FileWriter ifw = new FileWriter(installIniFile);
		installIni.store(ifw, "AgileSites was here");
		ifw.close();
		System.out.println("+++ "+installIniFile);
	}

}
