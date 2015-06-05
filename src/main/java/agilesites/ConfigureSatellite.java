package agilesites;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

public class ConfigureSatellite {

	URL url;

	File satellitePrp;

	public ConfigureSatellite(String url, String satellitePrp)
			throws MalformedURLException {

		this.url = new URL(url);
		this.satellitePrp = new File(satellitePrp);

	}

	public void configure() throws FileNotFoundException, IOException {
		Properties prp = new Properties();
		prp.load(new FileReader(satellitePrp));
		prp.setProperty("host", url.getHost());
		int port = url.getPort();
		if(port==-1) port = 80;
		prp.setProperty("port", ""+port);
		prp.setProperty("protocol", url.getProtocol());
		String path = url.getPath();
		if(!path.endsWith("/")) path=path+"/";
		prp.setProperty("servlet-path", path);
		prp.setProperty("appserverlink", path+"ContentServer?");
		prp.setProperty("service", path+"ContentServer");
		prp.setProperty("bservice", path+"BlobServer");
		
		prp.store(new FileWriter(satellitePrp), "# updated by agilesites2");
		System.out.println("~~~ " + satellitePrp);
	}

	public static void main(String[] args) {

		try {
			if (args.length < 2) {
				System.out.println("usage: cs-url satellite.properties");
			} else {
				ConfigureSatellite cfg = new ConfigureSatellite(args[0],
						args[1]);
				cfg.configure();
			}
		} catch (Exception ex) {
			System.out.println("Error: " + ex.getMessage());
		}
	}

}
