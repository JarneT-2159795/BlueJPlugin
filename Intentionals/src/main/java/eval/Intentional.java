package eval;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Intentional
{
   boolean noGetter() default true;

   boolean noSetter() default true;

   boolean publicVariable() default false;
   boolean longName() default false;
}
