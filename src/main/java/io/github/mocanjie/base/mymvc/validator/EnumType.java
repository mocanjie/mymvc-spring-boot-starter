package io.github.mocanjie.base.mymvc.validator;

public @interface EnumType {
    String valKey() default "";
    Class<? extends Enum> type() default Enum.class;
    String[] include() default {};
    String[] exclude() default {};


}
