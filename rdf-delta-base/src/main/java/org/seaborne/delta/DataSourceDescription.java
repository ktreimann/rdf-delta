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

package org.seaborne.delta;

import org.apache.jena.atlas.json.JsonObject;
import org.seaborne.delta.lib.JSONX;
import static org.seaborne.delta.DPConst.*;

public class DataSourceDescription {
    public final Id id;
    public final String uri;
    public final String name;
    
    public DataSourceDescription(Id id, String name, String uri) {
        super();
        this.id = id;
        this.name = name;
        this.uri = uri;
    }
    
    public JsonObject asJson() {
        return JSONX.buildObject(b->{
            b.key(F_ID).value(id.asString());
            b.key(F_NAME).value(name);
            if ( uri != null )
                b.key(F_URI).value(uri);
        });
    }
    
    public static DataSourceDescription fromJson(JsonObject obj) {
        String idStr = obj.get(F_ID).getAsString().value();
        String name = obj.get(F_NAME).getAsString().value();
        String uri = null;
        if ( obj.hasKey(F_URI) )
         uri = obj.get(F_URI).getAsString().value();
        return new DataSourceDescription(Id.fromString(idStr), name, uri);
    }
    
    @Override
    public String toString() {
        return String.format("[%s, %s, <%s>]", id, name, uri);
    }
    
    // Useable as a key into a Map.

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((uri == null) ? 0 : uri.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        DataSourceDescription other = (DataSourceDescription)obj;
        if ( id == null ) {
            if ( other.id != null )
                return false;
        } else if ( !id.equals(other.id) )
            return false;
        if ( uri == null ) {
            if ( other.uri != null )
                return false;
        } else if ( !uri.equals(other.uri) )
            return false;
        return true;
    }
}
