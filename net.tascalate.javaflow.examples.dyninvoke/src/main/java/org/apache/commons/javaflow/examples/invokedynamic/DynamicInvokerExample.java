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
package org.apache.commons.javaflow.examples.invokedynamic;

import org.apache.commons.javaflow.api.Continuation;
import org.apache.commons.javaflow.providers.core.ContinuableClassTransformationFactory;
import org.apache.commons.javaflow.tools.runtime.ResourceTransformingClassLoader;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

public class DynamicInvokerExample {

    public static void main(String[] args) throws Exception {
        Class<Runnable> dynamicClass = generateDynamicInvokerClass(
            "org/apache/commons/javaflow/examples/invokedynamic/SimpleDynamicInvoker"
        );
        Runnable demo = dynamicClass.getDeclaredConstructor().newInstance();

        for (Continuation cc = Continuation.startWith(demo, true); null != cc; cc = cc.resume()) {
            System.out.println("Interrupted " + cc.value());
        }
    }

    private static Class<Runnable> generateDynamicInvokerClass(String dynamicInvokerClassName) {
        AbstractDynamicInvokerGenerator generator = new AbstractDynamicInvokerGenerator() {
            @Override
            protected int addMethodParameters(final MethodVisitor mv) {
                return 0;
            }
        };

        byte[] dynamicClassBytes = generator.generateInvokeDynamicRunnable(dynamicInvokerClassName,
                Type.getType(SimpleDynamicLinkage.class).getInternalName(), "bootstrapDynamic", "()V");

        ResourceTransformingClassLoader delegateClassLoader = new ResourceTransformingClassLoader(
            DynamicInvokerExample.class.getClassLoader(), new ContinuableClassTransformationFactory()
        );

        @SuppressWarnings("unchecked")
        Class<Runnable> dynamicClass = (Class<Runnable>) delegateClassLoader.defineClassFromData(
            dynamicClassBytes, 
            dynamicInvokerClassName.replace('/', '.'), // may be just null
            SimpleDynamicLinkage.class.getProtectionDomain()
        );
        return dynamicClass;
    }

}
