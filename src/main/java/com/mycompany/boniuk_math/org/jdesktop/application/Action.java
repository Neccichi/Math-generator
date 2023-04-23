package com.mycompany.boniuk_math.org.jdesktop.application;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Action {
  String name() default "";
  
  String enabledProperty() default "";
  
  String selectedProperty() default "";
  
  Task.BlockingScope block() default Task.BlockingScope.NONE;
  
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.PARAMETER})
  public static @interface Parameter {
    String value() default "";
  }
}


/* Location:              C:\Users\windo\Desktop\appframework-1.0.3.jar!\org\jdesktop\application\Action.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */