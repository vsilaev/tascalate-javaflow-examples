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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.javaflow.api.continuable;
import org.apache.commons.javaflow.examples.cdi.weld.annotations.LoggableMethod;
import org.jboss.weld.bean.proxy.ProxyObject;

@ApplicationScoped
public class Execution implements Runnable {

    @Inject
    TargetInterface target;

    @LoggableMethod
    public @continuable void run() {
        System.out.println("Target is proxy? " + (target instanceof ProxyObject));
        String[] array = new String[] {"A", "B", "C"};
        for (int i = 0; i < array.length; i++) {
            System.out.println("Execution " + i);
            invokeDependent(array[i]);
        }
    }

    protected @continuable void invokeDependent(String value) {
        target.execute(value);
    }
}
