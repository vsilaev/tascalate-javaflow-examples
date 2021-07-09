/**
 * ﻿Copyright 2013-2019 Valery Silaev (http://vsilaev.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.javaflow.examples.cdi.weld;

import org.apache.commons.javaflow.api.Continuation;
import org.apache.commons.javaflow.api.continuable;
import org.apache.commons.javaflow.examples.cdi.weld.annotations.LoggableMethod;
import org.apache.commons.javaflow.examples.cdi.weld.annotations.SecureBean;
import org.apache.commons.javaflow.examples.cdi.weld.annotations.TransactionalMethod;
import org.jboss.weld.environment.se.contexts.ThreadScoped;

@SecureBean
@ThreadScoped
public class TargetClass implements TargetInterface {

    @Override
    @TransactionalMethod
    @LoggableMethod
    public @continuable void execute(String prefix) {
        executeNested(prefix);
    }

    // In Weld & OWB @LoggableMethod and @SecureBean 
    // MAY NOT BE INVOKED here here while method is 
    // invoked directly (not via proxy)
    @LoggableMethod
    protected @continuable void executeNested(String prefix) {
        System.out.println("In target BEFORE suspend");
        Object value = Continuation.suspend(this + " @ " + prefix);
        System.out.println("In target AFTER suspend: " + value);
    }

}
