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
package org.apache.commons.javaflow.examples.jee;

import org.apache.commons.javaflow.api.continuable;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.commons.javaflow.api.Continuation;

@Stateless
@LocalBean
public class ExecutionInner implements Runnable {
    private final int i;

    public ExecutionInner() {
        this(100);
    }
    
    public ExecutionInner(int i) {
        this.i = i;
    }

    @Override
    public @continuable void run() {
        for (char c = 'A'; c < 'E'; c++) {
            StringBuilder v = new StringBuilder();
            v.append(c).append(i);
            System.out.println("\tInner " + v);
            Continuation.suspend(v);
        }
    }
}
