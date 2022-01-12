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
package org.apache.commons.javaflow.examples.common.interceptor;

public class SecurityInterceptor implements InterceptorInterface {

    private final TargetInterface target;

    public SecurityInterceptor(TargetInterface target) {
        this.target = target;
    }

    public void decorateCall(String param) {
        System.out.println("Security Interceptor before call");
        target.execute(param);
        System.out.println("Security Interceptor after call");
    }
}
