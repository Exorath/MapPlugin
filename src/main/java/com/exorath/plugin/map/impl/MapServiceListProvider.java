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

package com.exorath.plugin.map.impl;

import com.exorath.plugin.map.MapListProvider;
import com.exorath.plugin.map.res.MapInfo;
import com.google.gson.Gson;
import com.mashape.unirest.http.Unirest;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by toonsev on 12/29/2016.
 */
public class MapServiceListProvider implements MapListProvider {
    private static final Gson GSON = new Gson();
    private String address;
    private String accountId;

    private Map<Integer, MapInfo> mapsByIndex = new HashMap<>();

    private Map<Integer, String> mapEnvFromIndexCache = new HashMap<>();

    public MapServiceListProvider(String address, String accountId){
        this.address = address;
        this.accountId = accountId;
    }
    @Override
    public MapInfo getMap(Integer index) {
        if(mapsByIndex.containsKey(index))
            return mapsByIndex.get(index);
        try {
            String body = Unirest.get(address + "/accounts/{accountId}/maps")
                    .routeParam("accountId", accountId)
                    .asString().getBody();
            MapsInfo mapsInfo = GSON.fromJson(body, MapsInfo.class);

        } catch (Exception e) {
            Bukkit.broadcastMessage("Exomaps error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
        return null;
    }

    @Override
    public void clearCache() {
        mapsByIndex.clear();
        mapEnvFromIndexCache.clear();
    }

    private void addMapIndices(MapsInfo mapsInfo){
        for(Map.Entry<String, MapInfo> maps : mapsInfo.getMaps().entrySet()){

        }
    }
}
