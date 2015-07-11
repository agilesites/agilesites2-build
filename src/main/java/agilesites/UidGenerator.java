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
    private long minId = 0;
    private File file;
    Set<Long> idSet = new HashSet<Long>();
    int RANGE = 1000 * 60;

    /**
     * Load the current ids in order to generate new ones
     *
     * @param filename
     */
    public UidGenerator(String filename) throws Exception {
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
                //System.out.println("id:"+id);
                if (idSet.contains(id))
                    throw new Exception("Duplicate id , cannot continue!");
                idSet.add(id);
                minId = Math.min(id, minId);
            }
        }
        if (minId == 0)
            minId = System.currentTimeMillis();
    }

    /**
     * Add a new key assigning a new id if not already there
     *
     * @param key
     */
    public void add(String key) {
        Object val = prp.get(key);
        if (val != null)
            return;

        // generate a new random id in the range
        long newId = 0;
        Random rnd = new Random();
        do {
            newId = minId + 1 + rnd.nextInt(RANGE);
        } while (idSet.contains(newId));

        System.out.println("Generated id " + key + "=" + newId);
        prp.setProperty(key, "" + newId);
        idSet.add(newId);
    }


    /**
     * Read the assigned uid so far
     */
    public Object get(String key) {
        return prp.get(key);
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

    public static void main(String[] args) throws Exception {
        UidGenerator uid = new UidGenerator(args[0]);
        for (int i = 1; i < args.length; i++)
            uid.add(args[i]);
        uid.save();
    }
}
