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
import org.jboss.weld.bean.proxy.ProxyObject;
import org.jboss.weld.environment.se.events.ContainerInitialized;

import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

// Run with main class org.jboss.weld.environment.se.StartMain
@Singleton
public class WeldApplication {

    @Inject
    Execution execution;
    
    @Inject
    SimpleInterface simpleInterface;

    public void run(@Observes ContainerInitialized event) {
        System.out.println("Execution is proxy? " + (execution instanceof ProxyObject));
        int i = 0;
        for (Continuation cc = Continuation.startWith(execution, true); null != cc; cc = cc.resume(i += 100)) {
            System.out.println("SUSPENDED " + cc.value());
        }

        System.out.println("===");
        simpleInterface.execute("ABC");
        ((SimpleBean)simpleInterface).executeNested("XYZ");
    }
}
