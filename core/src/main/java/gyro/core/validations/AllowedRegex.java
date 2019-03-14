package gyro.core.validations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@AnnotationProcessorClass(AllowedRegexValidator.class)
public @interface AllowedRegex {
    String[] value();

    String[] display() default {};

    String message() default "Valid string should be one of format %s.";
}
