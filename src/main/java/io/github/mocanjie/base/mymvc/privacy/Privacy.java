package io.github.mocanjie.base.mymvc.privacy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Privacy {

    PrivacyType type() default PrivacyType.CUSTOM;

    /** 保留左边 N 位（-1=不启用） */
    int left() default -1;

    /** 保留右边 N 位（-1=不启用） */
    int right() default -1;

    /** 遮蔽中间百分比 0.0~1.0（-1=不启用） */
    double percent() default -1.0;

    char maskChar() default '*';
}
