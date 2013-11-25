/*
 * Copyright (c) 2008-2013, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.collection;

import com.hazelcast.core.ItemEventType;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.Operation;

import java.io.IOException;

/**
 * @ali 8/31/13
 */
public class CollectionRemoveOperation extends CollectionBackupAwareOperation {

    private Data value;

    private transient long itemId = -1;

    public CollectionRemoveOperation() {
    }

    public CollectionRemoveOperation(String name, Data value) {
        super(name);
        this.value = value;
    }

    public void beforeRun() throws Exception {

    }

    public void run() throws Exception {
        response = false;
        final CollectionItem item = getOrCreateContainer().remove(value);
        if (item != null){
            response = true;
            itemId = item.getItemId();
        }
    }

    public void afterRun() throws Exception {
        if (itemId != -1){
            publishEvent(ItemEventType.REMOVED, value);
        }
    }

    public boolean shouldBackup() {
        return itemId != -1;
    }

    public Operation getBackupOperation() {
        return new CollectionRemoveBackupOperation(name, itemId);
    }

    public int getId() {
        return CollectionDataSerializerHook.COLLECTION_REMOVE;
    }

    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        value.writeData(out);
    }

    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        value = new Data();
        value.readData(in);
    }
}
