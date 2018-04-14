package com.example.helloworld;

import io.dropwizard.Application;
import io.dropwizard.client.HttpClientBuilder;
import io.dropwizard.client.HttpClientConfiguration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.util.Duration;
import org.apache.http.client.HttpClient;

public class LocateMeService extends Application<LocateMeConfiguration> {
    public static void main(String[] args) throws Exception {
        new LocateMeService().run(args);
    }

    @Override
    public void initialize(Bootstrap<LocateMeConfiguration> bootstrap) {
    }

    @Override
    public void run(LocateMeConfiguration configuration,
                    Environment environment) {

        HttpClientConfiguration httpClientConfiguration = configuration.getHttpClientConfiguration();
        httpClientConfiguration.setTimeout(Duration.seconds(5));

        final HttpClient httpClient = new HttpClientBuilder(environment)
                .using(httpClientConfiguration)
                .build("client");
        environment.jersey().register(new CommuneResource(httpClient));
    }



}