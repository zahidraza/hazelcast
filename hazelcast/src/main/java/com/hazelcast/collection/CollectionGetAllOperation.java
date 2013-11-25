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

import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.impl.SerializableCollection;

import java.util.Collection;

/**
 * @ali 9/4/13
 */
public class CollectionGetAllOperation extends CollectionOperation {

    public CollectionGetAllOperation() {
    }

    public CollectionGetAllOperation(String name) {
        super(name);
    }

    public int getId() {
        return CollectionDataSerializerHook.COLLECTION_GET_ALL;
    }

    public void beforeRun() throws Exception {

    }

    public void run() throws Exception {
        final Collection<Data> all = getOrCreateContainer().getAll();
        response = new SerializableCollection(all);
    }

    public void afterRun() throws Exception {

    }
}
