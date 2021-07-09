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
package org.apache.commons.javaflow.examples.jee;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.javaflow.api.Continuation;

@Stateless
@Path("/")
@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class ContinuableEchoService {
    
    @Inject
    Execution execution;
    
    @Inject
    ExecutionOuter executionOuter;

    @GET
    @Path("simple/{key}")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject executeSimple(@PathParam("key") String key) {
        StringBuilder result = new StringBuilder();
        for (Continuation cc = Continuation.startWith(execution); null != cc;) {
            final Object valueFromContinuation = cc.value();
            result.append("\n" + "Interrupted " + key + " " + valueFromContinuation);
            // Let's continuation resume
            cc = cc.resume(valueFromContinuation);
        }
        
        
        return Json.createObjectBuilder().add("message", result.toString()).build();
    }
    
    
    @GET
    @Path("nested/{key}")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject executeNested(@PathParam("key") String key) {
        StringBuilder result = new StringBuilder();
        for (Continuation cc = Continuation.startWith(executionOuter); null != cc;) {
            final Object valueFromContinuation = cc.value();
            result.append("\n" + "Interrupted " + key + " " + valueFromContinuation);
            // Let's continuation resume
            cc = cc.resume(valueFromContinuation);
        }
        
        
        return Json.createObjectBuilder().add("message", result.toString()).build();
    }

}
