package com.mentalfrostbyte.jello.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Marks a method as a listener. The method must take exactly one {@link Event} subclass. */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventTarget {
	byte priority() default Priority.MEDIUM;
}
