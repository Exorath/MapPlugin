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

package com.exorath.plugin.map;

import java.io.File;

/**
 * Created by toonsev on 1/5/2017.
 */
public interface MapUploadProvider {
    /**
     *
     * @param worldDir
     * @param mapId
     * @param envId
     * @return the new versionId
     */
    String upload(File worldDir, String mapId, String envId);
}
