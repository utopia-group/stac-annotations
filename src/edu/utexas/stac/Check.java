package edu.utexas.stac;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


// TODO(wuestholz): Maybe add "@Repeatable(Check.class)" (requires Java 8) instead of @Checks.
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Check {

    Analysis analysis() default Analysis.AVAILABILITY;

    Resource resource() default Resource.TIME;

    public enum Analysis {
        AVAILABILITY, CONFIDENTIALITY
    }

    public enum Resource {
        SPACE, TIME
    }
}
