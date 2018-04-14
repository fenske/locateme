package com.example.helloworld;

import com.google.common.collect.ImmutableMap;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;

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
    public String communes() throws IOException {
        HttpGet request = new HttpGet("http://api.arbetsformedlingen.se/af/v0/platsannonser/soklista/kommuner?lanid=1");
        request.setHeader("accept", "application/json;charset=utf-8; qs=1");
        request.setHeader("Accept-Language", "*");
        HttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();
        return EntityUtils.toString(entity);
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