package com.mycompany.boniuk_math.org.jdesktop.application;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Resource {
  String key() default "";
}


/* Location:              C:\Users\windo\Desktop\appframework-1.0.3.jar!\org\jdesktop\application\Resource.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */