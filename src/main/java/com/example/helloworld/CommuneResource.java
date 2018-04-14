package com.example.helloworld;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Path("/communes")
@Produces(MediaType.APPLICATION_JSON)
public class CommuneResource {

    private static final String BASE_URL = "http://api.arbetsformedlingen.se/af/v0/platsannonser/";
    private final HttpClient httpClient;
    private ObjectMapper objectMapper = new ObjectMapper();

    public CommuneResource(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @GET
    public String communes() throws IOException {
        return objectMapper.writeValueAsString(get("soklista/kommuner?lanid=1").get("soklista"));
    }

    private Map get(String path) throws IOException {
        HttpGet request = new HttpGet(BASE_URL + path);
        request.setHeader("accept", "application/json;charset=utf-8; qs=1");
        request.setHeader("Accept-Language", "*");
        HttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();
        return objectMapper.readValue(EntityUtils.toString(entity), Map.class);
    }

    @GET
    @Path("/rank")
    public Map<Integer, Integer> communeRank(@QueryParam("id1") Integer id1, @QueryParam("id2") Integer id2) throws IOException {
        return ImmutableMap.of(
                id1, rankOf(id1),
                id2, rankOf(id2)
        );
    }

    private int rankOf(Integer id) throws IOException {
        final Map matchningslista1 = (Map) get("matchning?kommunid=" + id).get("matchningslista");
        return Integer.parseInt(objectMapper.writeValueAsString(matchningslista1.get("antal_platsannonser_exakta")));
    }

    @GET
    @Path("/rank2")
    public Map<Integer, Integer> communeRank2(@QueryParam("id1") Integer id1,
                                              @QueryParam("id2") Integer id2,
                                              @QueryParam("keyword") List<String> keywords) throws IOException {

        return ImmutableMap.of(
                id1, rankWithKeywords(id1, keywords),
                id2, rankWithKeywords(id2, keywords)
        );
    }

    private int rankWithKeywords(Integer id, List<String> keywords) throws IOException {
        return getForAll(id, keywords);
    }

    private int getForAll(Integer id, List<String> keywords) throws IOException {
        Set<String> adIds = Sets.newHashSet();

        for (String keyword : keywords) {
            final Map matchningslista1 = (Map) get("matchning?lanid=1&nyckelord="+keyword + "&antalrader=1000").get("matchningslista");
            List<Map> matchings = (List<Map>) matchningslista1.get("matchningdata");

            for (Map<String, Object> x : matchings) {
                if(id.equals(x.get("kommunkod")) ) {
                    Object annonsid = x.get("annonsid");
                    adIds.add((String) annonsid);
                }
            }
        }
        return adIds.size();
    }

}