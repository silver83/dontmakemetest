/*
 *    Copyright (c) 2006 LiXiao.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.thoughtworks.fireworks.core.table;

import com.thoughtworks.fireworks.core.Utils;

public class TraceLog {
    private static final String PREFIX = "\tat ";

    public static boolean isTraceLog(String traceLog) {
        if (Utils.isEmpty(traceLog)) {
            return false;
        }
        return traceLog.startsWith(PREFIX);
    }

    public static String removeTraceLogPrefix(String traceLog) {
        if (isTraceLog(traceLog)) {
            return traceLog.substring(PREFIX.length()).trim();
        }
        return traceLog;
    }

    public static String toTraceLog(String str) {
        if (str == null) {
            return PREFIX;
        }
        return PREFIX + str;
    }
}
