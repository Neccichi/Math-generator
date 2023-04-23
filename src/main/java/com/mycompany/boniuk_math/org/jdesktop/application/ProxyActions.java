package com.mycompany.boniuk_math.org.jdesktop.application;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ProxyActions {
  String[] value() default {};
}


/* Location:              C:\Users\windo\Desktop\appframework-1.0.3.jar!\org\jdesktop\application\ProxyActions.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */