

package org.graylog.aisearch;

import jakarta.inject.Inject;
import org.graylog2.plugin.rest.PluginRestResource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;


@Path("/aisearch")
@Produces(MediaType.APPLICATION_JSON)
public class AISearchResource implements PluginRestResource {
    private final AISearchService aiSearchService;


    @Inject
    public AISearchResource(AISearchService aiSearchService) {
        this.aiSearchService = aiSearchService;
    }

    @GET
    @Path("/fetch-logs")
    public String fetchLogs() {
        //return aiSearchService.fetchLogsFromGraylog();
        return aiSearchService.doFetchLogic();
    }

    @GET
    @Path("/old-logs")
    public String oldLogs() {
        //String json = "{\"message\": \"Hello World!\"}";
        //return json;

        String json = aiSearchService.returnResponse();
        if (!json.equals("Initial Render")){
            return json;
        }
        else return "{\"message\": \"Fail\"}";
    }
}

