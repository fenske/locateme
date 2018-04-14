package com.example.helloworld;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
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
import java.util.Random;
import java.util.Set;

@Path("/communes")
@Produces(MediaType.APPLICATION_JSON)
public class CommuneResource {

    private static final String BASE_URL = "http://api.arbetsformedlingen.se/af/v0/platsannonser/";
    private final HttpClient httpClient;
    private ObjectMapper objectMapper = new ObjectMapper();

    private Map<String, Map<String, Integer>> cachedStubMetrics = Maps.newHashMap();

    public CommuneResource(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @GET
    public String communes() throws IOException {
        return objectMapper.writeValueAsString(getAds("soklista/kommuner?lanid=1").get("soklista"));
    }

    private Map getAds(String path) throws IOException {
        return get(path);
    }

    private Map get(String path) throws IOException {
        HttpGet request = new HttpGet(BASE_URL + path);
        request.setHeader("accept", "application/json;charset=utf-8; qs=1");
        request.setHeader("Accept-Language", "*");
        HttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();
        return objectMapper.readValue(EntityUtils.toString(entity), Map.class);
    }

    private Map readAdd(String adId) throws IOException {
        return get(adId);
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
        final Map matchningslista1 = (Map) getAds("matchning?kommunid=" + id).get("matchningslista");
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

    @GET
    @Path("/rank3")
    public Map<String, Integer> communeRank3(@QueryParam("id1") String id1,
                                             @QueryParam("id2") String id2,
                                             @QueryParam("metric_key") String metricKey) {
        fillCache(id1, id2, metricKey);

        ImmutableMap<String, Integer> result = ImmutableMap.of(
                id1, cachedStubMetrics.get(metricKey).get(id1),
                id2, cachedStubMetrics.get(metricKey).get(id2)
        );

        return result;
    }

    private void fillCache(@QueryParam("id1") String id1, @QueryParam("id2") String id2, @QueryParam("metric_key") String metricKey) {
        cachedStubMetrics.putIfAbsent(metricKey, Maps.newHashMap());
        Map<String, Integer> metricMap = cachedStubMetrics.get(metricKey);
        metricMap.putIfAbsent(id1, new Random().nextInt(500));
        metricMap.putIfAbsent(id2, new Random().nextInt(500));
    }

    private int rankWithKeywords(Integer id, List<String> keywords) throws IOException {
        return getForAll(id, keywords);
    }

    private int getForAll(Integer id, List<String> keywords) throws IOException {
        Set<String> adIds = Sets.newHashSet();

        for (String keyword : keywords) {
            final Map matchningslista1 = (Map) getAds("matchning?lanid=1&nyckelord=" + keyword + "&antalrader=1000").get("matchningslista");
            List<Map> matchings = (List<Map>) matchningslista1.get("matchningdata");

            for (Map<String, Object> x : matchings) {
                if(id.equals(x.get("kommunkod")) ) {
                    String annonsid = (String) x.get("annonsid");
//                    readAdd(annonsid);
                    adIds.add(annonsid);
                }
            }
        }
        return adIds.size();
    }

}