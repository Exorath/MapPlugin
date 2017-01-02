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
 * Created by toonsev on 12/29/2016.
 */
public class CommandInfo {
    private String label;
    private String[] requiredArgs;
    private String[] optionalArgs;
    private String description;

    public CommandInfo(String label, String[] requiredArgs, String[] optionalArgs, String description) {
        this.label = label;
        this.requiredArgs = requiredArgs;
        this.optionalArgs = optionalArgs;
        this.description = description;
    }

    public String getLabel() {
        return label;
    }

    public String[] getRequiredArgs() {
        return requiredArgs;
    }

    public String[] getOptionalArgs() {
        return optionalArgs;
    }

    public String getDescription() {
        return description;
    }
}