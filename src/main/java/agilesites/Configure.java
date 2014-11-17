package agilesites;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class Configure {

	File servletRequests;
	File futuretenseIni;
	File jarDir;

	public Configure(String homeDir, String sharedDir, String webappDir) {

		String sep = File.pathSeparator;
		if (!homeDir.endsWith(sep))
			homeDir = homeDir + sep;
		if (!sharedDir.endsWith(sep))
			sharedDir = sharedDir + sep;
		if (!webappDir.endsWith(sep))
			webappDir = webappDir + sep;

		servletRequests = new File(webappDir + "WEB-INF" + sep + "classes"
				+ sep + "ServletRequests.properties");
		futuretenseIni = new File(homeDir + sep + "futuretense.ini");
		jarDir = new File(sharedDir + sep + "agilesites");
	}

	public void registerAssembler() throws FileNotFoundException, IOException {
		Properties prp = new Properties();
		prp.load(new FileReader(servletRequests));
		int i = 1;
		while (true) {
			String key = prp.getProperty("uri.assembler." + i + ".shortform");
			if (key == null || key.trim().length() == 0)
				break;
			i++;
		}
		prp.setProperty("uri.assembler." + i + ".shortform", "agilesites");
		prp.setProperty("uri.assembler." + i + ".classname",
				"wcs.core.Assembler");
		prp.store(new FileWriter(servletRequests), "# updated by agilesites2");
		System.out.println("~~~ " + servletRequests);
	}

	public void configure() throws FileNotFoundException, IOException {
		jarDir.mkdir();
		Properties prp = new Properties();
		prp.load(new FileReader(futuretenseIni));
		prp.setProperty("agilesites.dir", jarDir.getAbsolutePath());
		prp.setProperty("agilesites.poll", "1000");
		prp.store(new FileWriter(futuretenseIni), "# updated by agilesites2");
		System.out.println("~~~ " + futuretenseIni);
	}

	public static void main(String[] args) {

		try {
			if (args.length < 3) {
				System.out.println("usage: home-dir shared-dir webapp-dir");
			} else {
				Configure cfg = new Configure(args[0], args[1], args[2]);
				cfg.registerAssembler();
				cfg.configure();
			}
		} catch (Exception ex) {
			System.out.println("Error: " + ex.getMessage());
		}
	}

	private void configureIni() {
		// TODO Auto-generated method stub

	}
}
