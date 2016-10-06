package com.qnium.common.validation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author Kirill Zhukov
 */
@Repeatable(FieldValidators.class)
@Target(value=ElementType.FIELD)
@Retention(value= RetentionPolicy.RUNTIME)
public @interface FieldValidator
{
   Class  validator();
   String param() default "";
   String errorMessage() default "";
}

