package agilesites;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class SetProperty {
	public static void main(String[] args) throws FileNotFoundException, IOException {
		if(args.length==0) {
			System.out.println("usage: property-file key[=value] [key[=value] ...]");
		} else {
			Properties prp = new Properties();
			File file = new File(args[0]);
			prp.load(new FileReader(file));
			
			for(int i = 1; i<args.length; i++) {
				String key = args[i];
				String value = "true";
				int pos=key.indexOf("=");
				if(pos!=-1) {
					value = key.substring(pos+1);
					key = key.substring(0,pos);					
				}
				prp.setProperty(key, value);
				System.out.println("~ "+key+"="+value);
			}
			prp.store(new FileWriter(file), "# updated by agilesites2-setup");
			System.out.println("+++ "+file);
		}

		
	}
}
