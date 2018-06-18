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

package org.seaborne.delta.server.local.patchlog;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.jena.atlas.logging.FmtLog;
import org.apache.jena.atlas.logging.Log;
import org.apache.jena.ext.com.google.common.collect.BiMap ;
import org.apache.jena.ext.com.google.common.collect.HashBiMap ;
import org.seaborne.delta.DeltaConfigException;
import org.seaborne.delta.Id;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Known {@link PatchStore}s. A {@link PatchStore} manages a number of {@link PatchLog}s.
 * <p>
 * There is a default {@link PatchStore} where new patch logs are created unless
 * otherwise specificed.
 */
public class PatchStoreMgr {
    
    protected static Logger LOG = LoggerFactory.getLogger(PatchStoreMgr.class); 
    
    // ??extends Registry<String , PatchStoreRegistry>
    // -------- PatchStore.Provider
    // Providers wire themselves in during server startup.
    // Providers should not be be removed if there are any in use. 
    static Map<String, PatchStore> patchStores = new HashMap<>();
    
    // Default PatchStore.
    private static PatchStore dftPatchStore;

    // ---- Short name / long name.
    private static BiMap<String, String> shortName2LongName = HashBiMap.create();
    
    public static void registerShortName(String shortName, String providerName) {
        shortName2LongName.put(shortName, providerName);
    }

    /** Short name to full provider name.
     *  Null short name maps to the default provider.
     *  A return of null means "don't know".
     */
    public static String shortName2LongName(String shortName) {
        if ( shortName == null )
            return getDftPatchStoreName();
        return shortName2LongName.get(shortName);
    }
    
    public static String longName2ShortName(String providerName) {
        return shortName2LongName.inverse().get(providerName);
    }
    // ----
    
    public static Collection<PatchStore> registered() {
        return new HashSet<>(patchStores.values());
    }

    public static boolean isRegistered(String providerName) {
        return patchStores.containsKey(providerName);
    }
    
    /** Add a PatchStore : it is registered by its provider name */ 
    public static void register(PatchStore impl) {
        String providerName = impl.getProviderName();
        if ( longName2ShortName(providerName) == null )
            LOG.warn("No short name for: "+providerName);
        FmtLog.info(LOG, "Register patch store: %s", providerName);
        if ( patchStores.containsKey(providerName) )
            LOG.error("Already registered: "+providerName);
        patchStores.put(providerName, impl);
    }
    
    /** Unregister by provider name */ 
    public static void unregister(String providerName) {
        FmtLog.info(LOG, "Unregister patch store: %s", providerName);
        if ( patchStores.containsKey(providerName) )
            Log.warn(PatchStore.class, "Not registered: "+providerName);
        patchStores.remove(providerName);
    }

    /** Set the default choice of PatchStore */
    public static void setDftPatchStoreName(String providerName) {
        PatchStore impl = patchStores.get(providerName);
        if ( impl == null )
            throw new DeltaConfigException("No provider for '"+providerName+"'");  
        dftPatchStore = impl;
        FmtLog.info(LOG, "Set default patch store: %s", providerName);
    }
    
    /**
     * 
     * Get the {@link PatchStore}. Return the current global default if not
     * specifically found
     */
    public static PatchStore selectPatchStore(Id dsRef) {
//     // Look in existing bindings.
//     PatchStore patchStore = ??? ;
//     if ( patchStore != null )
//         return patchStore;
        return PatchStoreMgr.getDftPatchStore();
    }
    
    /**
     * Get the {@link PatchStore} by provider name.
     */
    public static PatchStore getPatchStoreByProvider(String providerName) {
        return patchStores.get(providerName);
    }

    /**
     * Get the current default {@link PatchStore}, e.g. for creating new {@link PatchLog}s.
     */
    public static PatchStore getDftPatchStore() {
        return dftPatchStore ;
    }
    
    /**
     * Get the current default {@code PatchStore} provider name.
     */
    public static String getDftPatchStoreName() {
        if ( dftPatchStore == null )
            return null;
        return dftPatchStore.getProviderName(); 
    }

    public static void reset() {
        shortName2LongName.clear();
        patchStores.clear();
        dftPatchStore = null;
    }
}
