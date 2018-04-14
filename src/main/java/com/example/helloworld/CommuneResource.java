package com.example.helloworld;

import com.google.common.collect.ImmutableMap;
import com.yammer.metrics.annotation.Timed;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.Map;

@Path("/communes")
@Produces(MediaType.APPLICATION_JSON)
public class CommuneResource {

    @GET
    @Timed
    public Map<String, String> communes() {
        return ImmutableMap.of(
                "name1", "id1",
                "name2", "id2"
        );
    }

    @GET
    @Timed
    @Path("/rank")
    public Map<Integer, Integer> communeRank(
            @QueryParam("id1") Integer id1,
            @QueryParam("id2") Integer id2) {
        return ImmutableMap.of(
                id1, 1,
                id2, 2
        );
    }
}