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
package org.graylog.aisearch;

import org.graylog2.plugin.PluginConfigBean;
import org.graylog2.plugin.PluginModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;

/**
 * Extend the PluginModule abstract class here to add you plugin to the system.
 */
public class AISearchModule extends PluginModule {
    private static final Logger LOG = LoggerFactory.getLogger(AISearchModule.class);

    /**
     * Returns all configuration beans required by this plugin.
     *
     * Implementing this method is optional. The default method returns an empty {@link Set}.
     */
    @Override
    public Set<? extends PluginConfigBean> getConfigBeans() {
        return Collections.emptySet();
    }

    @Override
    protected void configure() {
        /*
         * Register your plugin types here.
         *
         * Examples:
         *
         * addMessageInput(Class<? extends MessageInput>);
         * addMessageFilter(Class<? extends MessageFilter>);
         * addMessageOutput(Class<? extends MessageOutput>);
         * addPeriodical(Class<? extends Periodical>);
         * addAlarmCallback(Class<? extends AlarmCallback>);
         * addInitializer(Class<? extends Service>);
         * addRestResource(Class<? extends PluginRestResource>);
         *
         *
         * Add all configuration beans returned by getConfigBeans():
         *
         * addConfigBeans();
         */
        bind(AISearchService.class).asEagerSingleton();
        addRestResource(AISearchResource.class); // Register the REST endpoint

        bind(AISearchService.class).asEagerSingleton();
        LOG.info("Hello from configure() - personal twist - Manas Gupta");
        LOG.info("Hello from configure() - personal twist - Manas Gupta #2");

        LOG.info("Configuring AISearchModule");
        AISearchService aiSearchService = new AISearchService();
        aiSearchService.fetchLogsFromGraylog(); // Logs Graylog data analyzed by OpenAI






    }
}
