package agilesites;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.Writer;
import java.util.*;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedOptions({"uid","site"})
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
    boolean created;

    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        site = env.getOptions().get("site");
        String uidfile = env.getOptions().get("uid");
        System.out.println(uidfile);
        uid = new UidGenerator(uidfile);
        filer = env.getFiler();
        created = false;
    }

    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        if(created)
            return true;
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        try {
            for (String classType : getSupportedAnnotationTypes()) {
                String ann = classType.split("\\.")[2];
                //System.out.println(ann);
                Class clazz = Class.forName(classType);
                for (Element element : env.getElementsAnnotatedWith(clazz)) {
                    //System.out.println(element.toString());
                    if (element.getKind().isClass()) {
                        List<String> list = map.get(ann);
                        if (list == null) list = new LinkedList<String>();
                        String elementName = element.toString();
                        list.add(elementName);
                        map.put(ann, list);
                        uid.add(ann+"."+elementName);
                    }
                }
            }
            uid.save();
            JavaFileObject jfo = filer.createSourceFile(site + ".Index");
            Writer w = jfo.openWriter();
            String s = createClass(map);
            //System.out.println(s);
            w.write(s);
            w.close();
            created = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return true;
    }

    private void ln(StringBuffer b, int n, String l, Object... args) {
        b.append("                          ".substring(0, n));
        b.append(String.format(l.replace("'", "\""), args)).append("\n");
    }

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
