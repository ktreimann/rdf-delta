/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.seaborne.delta.lib;

import java.util.function.Consumer ;

import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonBuilder ;
import org.apache.jena.atlas.json.JsonObject ;
import org.apache.jena.atlas.json.JsonValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Additional JSON code */ 
public class JSONX {
    private static Logger LOG = LoggerFactory.getLogger("JsonAccess");
    
    private static String LABEL = "%%object%%" ;

    public static JsonObject buildObject(Consumer<JsonBuilder> setup) {
        JsonBuilder b = JsonBuilder.create().startObject(LABEL) ;
        setup.accept(b);
        return b.finishObject(LABEL).build().getAsObject() ;
    }
    
    /** Access a field of a JSON object : return as strign regardless of value type */ 
    public static String getStrOrNull(JsonObject obj, String field) {
        JsonValue jv = obj.get(field);
        if ( jv == null ) {
            //LOG.warn("Field '"+field+"' not found");
            return null;
        }
        if ( jv.isString() )
            return jv.getAsString().value();
        if ( jv.isNumber() )
            return jv.getAsNumber().value().toString();
        LOG.warn("field "+field+" : not a string or number : returning null for the string value");
        return null ;
    }
    
    public static int getInt(JsonObject obj, String field, int dftValue) {
        JsonValue jv = obj.get(field);
        if ( jv == null )
            return dftValue;
        if ( jv.isString() )
            return Integer.parseInt(jv.getAsString().value());
        if ( jv.isNumber() )
            return jv.getAsNumber().value().intValue();
        LOG.warn("field "+field+" : not string or number : returning default value");
        return dftValue ;
        
    }
    
    /** Create a safe copy of a {@link JsonValue}.
     * <p>
     *  If the JsonValue is a structure (object or array), copy the structure recursively.
     *  <p>
     *  If the JsonValue is a primitive (string, number, boolean or null),
     *  it is immutable so return the same object.  
     */
    public static JsonValue copy(JsonValue arg) {
        JsonBuilder builder = builder(arg) ;
        return builder==null ? arg : builder.build() ;
    }
    
    /** Create a builder from a {@link JsonValue}.
     *  <p>If the argument is an object or array, use it to initailize the builder.
     *  <p>if the argument is a JSON primitive (string, number, boolean or null),
     *  return null (the {@code JsonValue} is immutable).
     */
    public static JsonBuilder builder(JsonValue arg) {
        if ( arg.isObject() ) {
            JsonObject obj = arg.getAsObject() ;
            JsonBuilder builder = JsonBuilder.create() ;
            builder.startObject() ;
            obj.forEach((k,v) -> builder.key(k).value(copy(v))) ;
            builder.finishObject() ;
            return builder ; 
        }
        if ( arg.isArray() ) {
            JsonArray array = arg.getAsArray() ;
            JsonBuilder builder = JsonBuilder.create() ;
            builder.startArray() ;
            array.forEach((a)->builder.value(copy(a))) ;
            builder.finishArray() ;
            return builder ; 
        }
        return null ;
    }
}