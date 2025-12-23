/**
 * ﻿Copyright 2013-2022 Valery Silaev (http://vsilaev.com)
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
package org.apache.commons.javaflow.examples.cdi.owb;

import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;
import javax.inject.Inject;

import org.apache.commons.javaflow.api.Continuation;

public class OWBApplication {

    @Inject
    Execution execution;
    
    @Inject
    SimpleInterface simpleInterface;

    public void run() {
        int i = 0;
        for (Continuation cc = Continuation.startWith(execution, true); null != cc; cc = cc.resume(i += 100)) {
            System.out.println("SUSPENDED " + cc.value());
        }

        System.out.println("===");
        simpleInterface.execute("ABC");
        ((SimpleBean)simpleInterface).executeNested("XYZ");
    }
    
    public static void main(String[] args) {
        SeContainer container = null;
        try {
            container = SeContainerInitializer.newInstance().initialize();
            // Get a reference to your CDI bean and use it
            OWBApplication app = container.select(OWBApplication.class).get();
            app.run();
        } finally {
            if (null != container) {
                container.close();
            }
        }
    }
}
