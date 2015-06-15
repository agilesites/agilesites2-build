package agilesites;

/**
 * Created by msciab on 15/06/15.
 */

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * This class generate uids for annotation/class combinations (that maps in assets)
 * It will store the data in a property file
 */
public class UidGenerator {

    private Properties prp = new Properties();
    private long maxId = 0;
    private File file;

    /**
     * Load the current ids in order to generate new ones
     *
     * @param filename
     */
    public UidGenerator(String filename) {
        file = new File(filename);

        if (file.exists()) {
            try {
                prp.load(new FileReader(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Enumeration e = prp.propertyNames();
            while (e.hasMoreElements()) {
                String key = e.nextElement().toString();
                long id = 0;
                try {
                    id = Long.parseLong(prp.getProperty(key));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                maxId = Math.max(id, maxId);
            }
        }
        if (maxId == 0)
            maxId = System.currentTimeMillis();
    }

    /**
     * Add a new key assing a new id if not already there
     *
     * @param key
     */
    public void add(String key) {
        Object val = prp.get(key);
        if (val == null) {
            maxId++;
            System.out.println("assigning an id " + key + "=" + maxId);
            prp.setProperty(key, "" + maxId);
        }
    }

    /**
     * Save properties in a file in alphabetical order
     */
    public void save() {
        List<String> list = new LinkedList<String>();
        list.addAll(prp.stringPropertyNames());
        Collections.sort(list);
        FileWriter fw = null;
        try {
            fw = new FileWriter(file);
            for (String key : list)
                fw.write(String.format("%s=%s\n", key, prp.getProperty(key)));
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        UidGenerator uid = new UidGenerator(args[0]);
        for(int i=1; i<args.length; i++)
            uid.add(args[i]);
        uid.save();
    }
}
