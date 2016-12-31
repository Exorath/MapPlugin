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
import com.mashape.unirest.request.GetRequest;
import org.bukkit.Bukkit;

import java.util.*;

/**
 * Created by toonsev on 12/29/2016.
 */
public class MapServiceListProvider implements MapListProvider {
    private static final int ENV_FETCH_BATCH_SIZE = 200;
    private static final Gson GSON = new Gson();
    private String address;
    private String accountId;

    private Map<Integer, MapInfo> mapsByIndex = new HashMap<>();

    //private Map<Integer, String> mapEnvFromIndexCache = new HashMap<>();

    public MapServiceListProvider(String address, String accountId) {
        this.address = address;
        this.accountId = accountId;
    }

    @Override
    public MapInfo getMap(Integer index) {
        if (mapsByIndex.containsKey(index))
            return mapsByIndex.get(index);

        clearCache();

        MapsInfo mapsInfo = getMaps(null, getEnvsBatchSize());
        int i = 0;
        int breakAfter = 100;
        while(mapsInfo != null){
            breakAfter--;
            if(breakAfter == 0)
                throw new IllegalStateException("Seems like there's an infinite loop here, broke it early.");
            if(mapsInfo.getMaps().size() == 0)
                return null;
            List<String> sortedKeys = new ArrayList(mapsInfo.getMaps().keySet());
            Collections.sort(sortedKeys);
            for (Map.Entry<String, MapInfo> entry : mapsInfo.getMaps().entrySet()) {
                entry.getValue().setMapName(entry.getKey());
                mapsByIndex.put(i, entry.getValue());
                i++;
            }
            String lastMap = sortedKeys.get(sortedKeys.size() - 1);
            String lastEnv = getLastEnvironment(mapsInfo.getMaps().get(lastMap));
            //TODO: Remove last map if this is the last mapsInfo.

            if(mapsByIndex.containsKey(index))
                return mapsByIndex.get(index);
            mapsInfo = getMaps(lastMap + "/" + lastEnv, getEnvsBatchSize());
        }
        return null;
    }

    private String getLastEnvironment(MapInfo map){
        List<String> sortedKeys = new ArrayList(map.getEnvInfos().keySet());
        Collections.sort(sortedKeys);
        return sortedKeys.get(sortedKeys.size() - 1);
    }
    private MapsInfo getMaps(String startAfter, int maxEnvs) {
        try {
            GetRequest req = Unirest.get(address + "/accounts/{accountId}/maps")
                    .routeParam("accountId", accountId);
            if (startAfter != null)
                req.queryString("startAfter", startAfter);
            req.queryString("maxEnvs", maxEnvs);
            String body = req.asString().getBody();
            MapsInfo mapsInfo = GSON.fromJson(body, MapsInfo.class);
            return mapsInfo;
        } catch (Exception e) {
            Bukkit.broadcastMessage("Exomaps error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void clearCache() {
        mapsByIndex.clear();
       // mapEnvFromIndexCache.clear();
    }

    private int getEnvsBatchSize(){
        return ENV_FETCH_BATCH_SIZE;
    }
}
