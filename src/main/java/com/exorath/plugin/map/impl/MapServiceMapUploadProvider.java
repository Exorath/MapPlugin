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

import com.exorath.plugin.map.MapUploadProvider;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.HttpRequestWithBody;
import org.apache.http.entity.ContentType;
import org.zeroturnaround.zip.ZipUtil;

import java.io.*;

/**
 * Created by toonsev on 1/5/2017.
 */
public class MapServiceMapUploadProvider implements MapUploadProvider {
    private static final Gson GSON = new Gson();
    private String address;
    private String accountId;

    public MapServiceMapUploadProvider(String address, String accountId) {
        this.address = address;
        this.accountId = accountId;
    }

    @Override
    public String upload(final File worldDir, String mapId, String envId) {
        //This is a piped stream solution: see http://blog.ostermiller.org/convert-java-outputstream-inputstream for more info
        try (PipedInputStream in = new PipedInputStream()) {
            try (PipedOutputStream out = new PipedOutputStream(in)) {

                new Thread(() -> {
                    zip(worldDir, out);
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                })
                        .start();

                HttpRequestWithBody req = Unirest.post(address + "/accounts/{accountId}/maps/{mapId}/env/{envId}")
                        .routeParam("accountId", accountId)
                        .routeParam("mapId", mapId)
                        .routeParam("envId", envId);
                req.field("file", in, ContentType.MULTIPART_FORM_DATA, "file");
                HttpResponse<String> res = req.asString();
                String body = res.getBody();
                if (res.getStatus() < 200 || res.getStatus() >= 300 || body == null)
                    return null;
                JsonObject jsonObject = GSON.fromJson(body, JsonObject.class);
                if (jsonObject == null || !jsonObject.has("success") || jsonObject.get("success").getAsBoolean() == false || !jsonObject.has("versionId"))
                    return null;
                return jsonObject.get("versionId").getAsString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void zip(File worldDir, OutputStream zipLocation) {
        ZipUtil.pack(worldDir, zipLocation);
    }
}
