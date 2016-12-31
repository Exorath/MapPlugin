/*
 * Copyright 2016 Exorath
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

package com.exorath.plugin.map;

import com.exorath.plugin.map.res.MapInfo;

/**
 * Created by toonsev on 12/29/2016.
 */
public interface MapListProvider {
    /**
     * Gets the map at the given index, this might be cached and may issue a batch request
     * @param index the map index to request
     * @return the mapInfo at the specific index, or null if none exist.
     */
    MapInfo getMap(Integer index);

    /**
     * If there are any cached entries, these will be cleared
     */
    void clearCache();
}
