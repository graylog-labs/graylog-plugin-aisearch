/*
 * Copyright (C) 2020 Graylog, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Server Side Public License, version 1,
 * as published by MongoDB, Inc.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * Server Side Public License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program. If not, see
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package org.graylog.integrations.aws.resources.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import java.util.List;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class LogGroupsResponse {

    private static final String LOG_GROUPS = "log_groups";
    private static final String TOTAL = "total";

    @JsonProperty(LOG_GROUPS)
    public abstract List<String> logGroups();

    @JsonProperty(TOTAL)
    public abstract long total();

    public static LogGroupsResponse create(@JsonProperty(LOG_GROUPS) List<String> logGroups,
                                           @JsonProperty(TOTAL) long total) {
        return new AutoValue_LogGroupsResponse(logGroups, total);
    }
}