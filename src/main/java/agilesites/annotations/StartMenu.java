package agilesites.annotations;

import java.lang.annotation.*;

/**
 * Created by msciab on 14/06/15.
 */
@Repeatable(StartMenus.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface StartMenu {
    String value() default "";
    String description();
    String assetType() default "";
    String assetSubtype() default "";
    long options() default 0l;
}
