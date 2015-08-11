package agilesites.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by msciab on 11/08/15.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface StartMenus {
    StartMenu[] value();
}
