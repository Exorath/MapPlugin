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

import com.exorath.plugin.map.MapDownloadProvider;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.GetRequest;
import org.bukkit.Bukkit;

import java.io.*;

import org.bukkit.material.TrapDoor;
import org.omg.CORBA.TRANSACTION_MODE;
import org.zeroturnaround.zip.ZipUtil;


/**
 * Created by toonsev on 1/5/2017.
 */
public class MapServiceMapDownloadProvider implements MapDownloadProvider {
    private String address;
    private String accountId;

    public MapServiceMapDownloadProvider(String address, String accountId) {
        this.address = address;
        this.accountId = accountId;
    }

    @Override
    public boolean downloadTo(String mapId, String envId, String versionId, File mapDirectory) {
        if (!mapDirectory.exists())
            mapDirectory.mkdir();
        if (!mapDirectory.isDirectory())
            return false;

        try {
            GetRequest req = Unirest.get(address + "/accounts/{accountId}/maps/{mapId}/env/{envId}/download")
                    .routeParam("accountId", accountId)
                    .routeParam("mapId", mapId)
                    .routeParam("envId", envId);
            if (versionId != null)
                req.queryString("versionId", versionId);
            try (InputStream inputStream = req.asBinary().getBody()) {
                unZip(inputStream, mapDirectory);
                System.out.println("Size: " + getFileSize(mapDirectory));
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;

            }
        } catch (Exception e) {
            Bukkit.broadcastMessage("Exomaps error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private void unZip(InputStream inputStream, File outputFolder) throws IOException {
        File temp = new File("templol");
        temp.createNewFile();
        FileOutputStream fileOutputStream = new FileOutputStream(temp);
        int cByte;
        while ((cByte = inputStream.read()) != -1) {
            fileOutputStream.write(cByte);
        }
        fileOutputStream.close();
        ZipUtil.unpack(temp, outputFolder);
    }

    private long getFileSize(File folder) {
        System.out.println("Folder: " + folder.getName());
        long foldersize = 0;
        File[] filelist = folder.listFiles();
        for (int i = 0; i < filelist.length; i++) {
            if (filelist[i].isDirectory()) {
                foldersize += getFileSize(filelist[i]);
            } else {
                foldersize += filelist[i].length();
            }
        }
        return foldersize;
    }
}
