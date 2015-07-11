package agilesites;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedOptions({"uid", "site", "out"})
@SupportedAnnotationTypes({"agilesites.annotations.Attribute",
        "agilesites.annotations.AttributeEditor",
        "agilesites.annotations.Attribute",
        "agilesites.annotations.Content",
        "agilesites.annotations.ContentDefinition",
        "agilesites.annotations.CSElement",
        "agilesites.annotations.Element",
        "agilesites.annotations.Parent",
        "agilesites.annotations.ParentDefinition",
        "agilesites.annotations.Site",
        "agilesites.annotations.Template",
        "agilesites.annotations.SiteEntry",
        "agilesites.annotations.StartMenu",
        "agilesites.annotations.TreeTab"})
public class IndexProcessor extends AbstractProcessor {

    private String site;
    private Filer filer;
    private UidGenerator uid;
    private boolean created;
    private File annotations;

    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);

        site = env.getOptions().get("site");
        annotations = new File(env.getOptions().get("out"));
        String uidfile = env.getOptions().get("uid");
        //System.out.println("loading " + uidfile);
        try {
            uid = new UidGenerator(uidfile);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            env.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
        }
        filer = env.getFiler();
        created = false;
    }

    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        if (created)
            return true;

        // build an hash map of annotation
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        try {
            // for eacho supported type
            for (String classType : getSupportedAnnotationTypes()) {

                // pick an annotation
                String ann = classType.split("\\.")[2];
                System.out.println(ann);

                Class clazz = Class.forName(classType);
                for (Element element : env.getElementsAnnotatedWith(clazz)) {
                    if (element.getKind().isClass()) {

                        // for each annotated element
                        List<String> list = map.get(ann);
                        if (list == null)
                            list = new LinkedList<String>();

                        // crate a list of sources and ids
                        String elementName = element.toString();
                        list.add(elementName);
                        map.put(ann, list);
                        String key = ann + "." + elementName;
                        uid.add(key);
                        append(key + "=" + uid.get(key));
                    }
                }
            }
            uid.save();

            // generate a index java class of the processing
            JavaFileObject jfo = filer.createSourceFile(site + ".Index");
            Writer w = jfo.openWriter();
            String s = createClass(map);
            w.write(s);
            w.close();

            // once is enough thanks
            created = true;

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return true;
    }

    // generate a line in java source code
    private void ln(StringBuffer b, int n, String l, Object... args) {
        b.append("                          ".substring(0, n));
        b.append(String.format(l.replace("'", "\""), args)).append("\n");
    }

    // generate a line in a resource file
    private void append(String msg) {
        FileWriter fw = null;
        try {
            fw = new FileWriter(annotations, true);
            fw.write(msg);
            fw.write('\n');
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // create the Index class
    private String createClass(Map<String, List<String>> map) {
        StringBuffer b = new StringBuffer();
        ln(b, 0, "package %s;", site);
        ln(b, 0, "class Index {");
        for (String ann : map.keySet()) {
            ln(b, 2, "private java.util.List<String> list%s = new java.util.LinkedList<String>();", ann);
            ln(b, 2, "public java.util.List<String> get%s() { return list%s; }", ann, ann);
        }
        ln(b, 2, "public Index() {");
        for (String ann : map.keySet()) {
            for (String clazz : map.get(ann)) {
                ln(b, 4, "list%s.add('%s');", ann, clazz);
            }
        }
        ln(b, 2, "}");
        ln(b, 0, "}");
        return b.toString();

    }

}
