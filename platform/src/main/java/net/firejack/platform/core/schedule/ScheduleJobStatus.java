/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package net.firejack.platform.core.schedule;

import net.firejack.platform.core.model.registry.schedule.ScheduleHistoryModel;
import net.firejack.platform.core.model.registry.schedule.ScheduleModel;
import net.firejack.platform.core.utils.SecurityHelper;
import net.firejack.platform.core.utils.StringUtils;

import java.util.Date;

public class ScheduleJobStatus {

    private String pageUID;
    private String requestUID;
    private String token;
    private Integer percents;
    private String message;
    private ScheduleModel scheduleModel;
    private ScheduleHistoryModel scheduleHistoryModel;
    private int countOfEmptyRequests;
    private long startTimeOfEmptyRequests;

    public ScheduleJobStatus() {
    }

    public ScheduleJobStatus(ScheduleModel scheduleModel, Integer percents, String message) {
        this.scheduleModel = scheduleModel;
        this.pageUID = SecurityHelper.generateSecureId();
        this.requestUID = SecurityHelper.generateRandomSequence(6);
        this.percents = percents;
        this.message = message;
    }

    public String getRequestUID() {
        return requestUID;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getPercents() {
        return percents;
    }

    public void setPercents(Integer percents) {
        this.percents = percents;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ScheduleModel getScheduleModel() {
        return scheduleModel;
    }

    public String getPageUID() {
        return pageUID;
    }

    public ScheduleHistoryModel getScheduleHistoryModel() {
        return scheduleHistoryModel;
    }

    public void setScheduleHistoryModel(ScheduleHistoryModel scheduleHistoryModel) {
        this.scheduleHistoryModel = scheduleHistoryModel;
    }

    public int getCountOfEmptyRequests() {
        return countOfEmptyRequests;
    }

    public void incrementCountOfEmptyRequests() {
        countOfEmptyRequests++;
    }

    public void resetCountOfEmptyRequests() {
        countOfEmptyRequests = 0;
        startTimeOfEmptyRequests = new Date().getTime();
    }

    public String getDurationTimeOfEmptyRequests() {
        long currentTimeOfEmptyRequests = new Date().getTime();

        long diff = currentTimeOfEmptyRequests - startTimeOfEmptyRequests, milliseconds, seconds, minutes, hours, days;
        diff = (diff - (milliseconds = diff % 1000)) / 1000;
        diff = (diff - (seconds = diff % 60)) / 60;
        diff = (diff - (minutes = diff % 60)) / 60;
        days = (diff - (hours = diff % 24)) / 24;

        String diffTime =
        (days > 0 ? days + "d, " : "") +
        (days > 0 || hours > 0 ? hours + "h, " : "") +
        (days > 0 || hours > 0 || minutes > 0 ? minutes + "m, " : "") +
        (days > 0 || hours > 0 || minutes > 0 || seconds > 0 ? seconds + "s" : "");
        return StringUtils.defaultIfEmpty(diffTime, "< 1s");
    }
}
