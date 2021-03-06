/*
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  See the NOTICE file distributed with this work for additional
 *  information regarding copyright ownership.
 */

package org.seaborne.delta.server.local.patchstores.zk;

import org.apache.jena.atlas.logging.Log;
import org.apache.zookeeper.KeeperException;
import org.seaborne.delta.DeltaConst;
import org.seaborne.delta.server.Provider;
import org.seaborne.delta.server.local.DPS;
import org.seaborne.delta.server.local.LocalServerConfig;
import org.seaborne.delta.server.local.PatchStore;
import org.seaborne.delta.server.local.PatchStoreProvider;
import org.seaborne.delta.zk.UncheckedZkConnection;
import org.seaborne.delta.zk.ZkException;
import org.seaborne.delta.zk.WrappedUncheckedZkConnection;
import org.seaborne.delta.zk.direct.DirectZkConnection;

import java.io.IOException;

public class PatchStoreProviderZk implements PatchStoreProvider {

    public PatchStoreProviderZk() { }

    @Override
    public PatchStore create(LocalServerConfig config) {
        UncheckedZkConnection client = zk(config);
        return new PatchStoreZk(client, this);
    }

    /**
     * Build an {@link UncheckedZkConnection} from the {@link LocalServerConfig}.
     * @return A new {@link UncheckedZkConnection}.
     * */
    protected UncheckedZkConnection zk(LocalServerConfig config) {
        String connectionString = config.getProperty(DeltaConst.pDeltaZk);
        if ( connectionString == null )
            Log.error(this, "No connection string in configuration");
        try {
            return new WrappedUncheckedZkConnection(DirectZkConnection.connect(connectionString));
        } catch (final IOException | KeeperException | InterruptedException e) {
            final String message = String.format(
                "Unable to connect to ZooKeeper with connect string %s",
                connectionString
            );
            Log.error(this, message, e);
            throw new ZkException(message, e);
        }
    }

    @Override
    public Provider getType() { return Provider.ZKZK; }

    @Override
    public String getShortName() {
        return DPS.pspZk;
    }
}
