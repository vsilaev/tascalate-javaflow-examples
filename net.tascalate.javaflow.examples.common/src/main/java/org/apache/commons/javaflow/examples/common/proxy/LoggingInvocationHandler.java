/**
 * ﻿Copyright 2013-2021 Valery Silaev (http://vsilaev.com)
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
package org.apache.commons.javaflow.examples.common.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;

public class LoggingInvocationHandler implements InvocationHandler {
    
    final private Object delegate;
    
    public LoggingInvocationHandler(Object delegate) {
        this.delegate = delegate;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass() == Object.class) {
            String methodName = method.getName();
            if ("equals".equals(methodName) && method.getReturnType() == boolean.class && method.getParameterTypes().length == 1) {
                if (null != args[0] && Proxy.isProxyClass(args[0].getClass())) {
                    InvocationHandler otherHandler = Proxy.getInvocationHandler(args[0]);
                    if (otherHandler instanceof LoggingInvocationHandler) {
                        return Objects.equals(delegate, ((LoggingInvocationHandler)otherHandler).delegate);
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else if ("hashCode".equals(methodName) && method.getReturnType() == int.class && method.getParameterTypes().length == 0) {
                return Objects.hash(delegate) * 37 + System.identityHashCode(this);
            } else if ("toString".equals(methodName) && method.getReturnType() == String.class && method.getParameterTypes().length == 0) {
                return "<proxy[" + this.toString() + "]>{" + delegate + "}";
            } else {
                throw new NoSuchMethodError(method.toString());
            }
        } else {
            System.out.println("::Entering method " + method + " of " + delegate);
            try {
                return method.invoke(delegate, args);
            } finally {
                System.out.println("::Exiting method " + method + " of " + delegate);
            }
        }
    }
}
