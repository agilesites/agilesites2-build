package site.element;

import agilesites.annotations.CSElement;
import agilesites.annotations.SiteEntry;
import api.Element;
import api.Env;

@SiteEntry
@CSElement
public class BbWrapper implements Element {
    public String apply(Env e) {
        return "";
    }
}
