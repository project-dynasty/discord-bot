package net.dynasty.api.plugin;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface DynastyModule {

    String name();

    String version();

    String[] authors();

    String parentConnectionPath() default "plugins/%name%";

}
