package com.example.helloworld;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
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
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Path("/communes")
@Produces(MediaType.APPLICATION_JSON)
public class CommuneResource {

    private static final String BASE_URL = "http://api.arbetsformedlingen.se/af/v0/platsannonser/";
    private final HttpClient httpClient;
    private ObjectMapper objectMapper = new ObjectMapper();

    private Map<String, Map<String, Integer>> cachedStubMetrics = Maps.newHashMap();
    private Map<String, Map<String, String>> communeCache = Maps.newHashMap();
    private Map<Integer, Map<Integer, Integer>> rankCache = new ConcurrentHashMap<>();

    public CommuneResource(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @GET
    public String communes() throws IOException {
        if (communeCache.isEmpty()) {
            communeCache = httpGet("soklista/kommuner?lanid=1");
        }
        return objectMapper.writeValueAsString(communeCache.get("soklista"));
    }

    @GET
    @Path("/rank")
    public Map<Integer, Integer> communeRank(@QueryParam("id1") Integer id1,
                                             @QueryParam("id2") Integer id2,
                                             @QueryParam("keyword") List<String> keywords) throws IOException {
        int key = Objects.hash(id1, id2, keywords.stream().collect(Collectors.joining()));
        if (!rankCache.containsKey(key)) {
            rankCache.putIfAbsent(key,
                                  ImmutableMap.of(
                                          id1, rankWithKeywords(id1, keywords),
                                          id2, rankWithKeywords(id2, keywords)
                                  )
            );
        }
        return rankCache.get(key);
    }

    @GET
    @Path("/custom-rank")
    public Map<String, Integer> customCommuneRank(@QueryParam("id1") String id1,
                                                  @QueryParam("id2") String id2,
                                                  @QueryParam("metric_key") String metricKey) {
        fillCache(id1, id2, metricKey);
        return ImmutableMap.of(
                id1, cachedStubMetrics.get(metricKey).get(id1),
                id2, cachedStubMetrics.get(metricKey).get(id2)
        );
    }

    private void fillCache(@QueryParam("id1") String id1, @QueryParam("id2") String id2, @QueryParam("metric_key") String metricKey) {
        cachedStubMetrics.putIfAbsent(metricKey, Maps.newHashMap());
        Map<String, Integer> metricMap = cachedStubMetrics.get(metricKey);
        metricMap.putIfAbsent(id1, new Random().nextInt(500));
        metricMap.putIfAbsent(id2, new Random().nextInt(500));
    }

    private int rankWithKeywords(Integer id, List<String> keywords) throws IOException {
        return isAtLeastOneKeyword(keywords) ? getForAll(id, keywords) : rankOf(id);
    }

    private boolean isAtLeastOneKeyword(List<String> keywords) {
        return keywords != null && keywords.size() > 0 && !StringUtils.isBlank(keywords.get(0));
    }

    private int rankOf(Integer id) throws IOException {
        final Map<String, String> ads = httpGet("matchning?kommunid=" + id).get("matchningslista");
        return Integer.parseInt(objectMapper.writeValueAsString(ads.get("antal_platsannonser_exakta")));
    }

    private int getForAll(Integer id, List<String> keywords) throws IOException {
        Set<String> adIds = Sets.newHashSet();
        for (String keyword : keywords) {
            String keywordPart = !StringUtils.isBlank(keyword) ? ("&nyckelord=" + keyword) : "";
            final Map matchningslista1 = httpGet("matchning?lanid=1" + keywordPart + "&antalrader=1000").get("matchningslista");
            List<Map> matchings = (List<Map>) matchningslista1.get("matchningdata");

            for (Map<String, Object> x : matchings) {
                if(id.equals(x.get("kommunkod")) ) {
                    String annonsid = (String) x.get("annonsid");
                    adIds.add(annonsid);
                }
            }
        }
        return adIds.size();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Map<String, String>> httpGet(String path) throws IOException {
        HttpGet request = new HttpGet(BASE_URL + path);
        request.setHeader("accept", "application/json;charset=utf-8; qs=1");
        request.setHeader("Accept-Language", "*");
        HttpEntity entity = httpClient.execute(request).getEntity();
        return objectMapper.readValue(EntityUtils.toString(entity), Map.class);
    }
}