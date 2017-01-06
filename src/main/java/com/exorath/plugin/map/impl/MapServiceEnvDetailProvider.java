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

package com.exorath.plugin.map.impl;

import com.exorath.plugin.map.EnvDetailProvider;
import com.exorath.plugin.map.res.EnvDetail;
import com.exorath.plugin.map.res.Version;
import com.google.gson.Gson;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.GetRequest;
import org.bukkit.Bukkit;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by toonsev on 1/3/2017.
 */
public class MapServiceEnvDetailProvider implements EnvDetailProvider {
    private static final Gson GSON = new Gson();

    private String address;
    private String accountId;

    public MapServiceEnvDetailProvider(String address, String accountId) {
        this.address = address;
        this.accountId = accountId;
    }

    @Override
    public EnvDetail getEnvDetail(String mapName, String envName) {
        return new MapServiceEnvDetail(mapName, envName);
    }

    private class MapServiceEnvDetail implements EnvDetail {
        private String mapId;
        private String envId;

        private boolean truncated = true;

        public MapServiceEnvDetail(String mapId, String envId) {
            this.mapId = mapId;
            this.envId = envId;
        }

        //shitty way of doing this, maybe replace with GSON?
        @Override
        public List<Version> getVersions(String lastVersionId, int amount) {
            try {
                GetRequest req = Unirest.get(address + "/accounts/{accountId}/maps/{mapId}/env/{envId}")
                        .routeParam("accountId", accountId)
                        .routeParam("mapId", mapId)
                        .routeParam("envId", envId);
                if (lastVersionId != null)
                    req.queryString("startAfter", lastVersionId);
                req.queryString("maxEnvs", amount);
                JSONObject obj = req.asJson().getBody().getObject();
                truncated = obj.has("truncated") && obj.getBoolean("truncated") == true;
                List<Version> versions = new ArrayList<>();
                if(obj.has("versions")){
                    JSONObject versionsObj = obj.getJSONObject("versions");
                    versions = new ArrayList<>(versionsObj.keySet().size());
                    for(String key : versionsObj.keySet()){
                        JSONObject versionObj = versionsObj.getJSONObject(key);
                        boolean latest = versionObj.has("latest") ? versionObj.getBoolean("latest") : false;
                        versions.add(new Version(key, versionObj.getLong("lastModified"), versionObj.getLong("size"), latest));
                    }
                }
                return versions;
            } catch (Exception e) {
                Bukkit.broadcastMessage("Exomaps error: " + e.getMessage());
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public boolean isTruncated() {
            return truncated;
        }
    }
}
