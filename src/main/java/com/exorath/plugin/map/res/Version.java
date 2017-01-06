/*
 * Copyright 2017 Exorath
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.exorath.plugin.map.res;

/**
 * Created by toonsev on 1/2/2017.
 */
public class Version {
    String id;
    long lastUpdated;
    long size;
    boolean latest;

    public Version() {
    }

    public Version(String id, long lastUpdated, long size, boolean latest) {
        this.id = id;
        this.lastUpdated = lastUpdated;
        this.size = size;
        this.latest = latest;
    }

    public String getId() {
        return id;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public long getSize() {
        return size;
    }

    public boolean isLatest() {
        return latest;
    }
}
