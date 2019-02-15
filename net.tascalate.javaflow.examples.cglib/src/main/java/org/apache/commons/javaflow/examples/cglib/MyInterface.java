package org.apache.commons.javaflow.examples.cglib;

import org.apache.commons.javaflow.api.continuable;

public interface MyInterface {

    @continuable void process(long value);
}
