package com.example.helloworld;

import com.google.common.collect.ImmutableMap;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.Map;

@Path("/communes")
@Produces(MediaType.APPLICATION_JSON)
public class CommuneResource {

    private final HttpClient httpClient;

    public CommuneResource(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @GET
    public HttpResponse communes() throws IOException {
        String abc = "";
        HttpGet request = new HttpGet("http://api.arbetsformedlingen.se/af/v0/platsannonser/soklista/kommuner?lanid=1");
        request.setHeader("accept", "application/json");
        request.setHeader("charset", "utf-8");
        request.setHeader("qs", "1");
        return httpClient.execute(request);
//        return ImmutableMap.of(
//                "name1", "id1",
//                "name2", "id2"
//        );
    }

    @GET
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