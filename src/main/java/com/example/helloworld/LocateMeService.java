package com.example.helloworld;

import io.dropwizard.Application;
import io.dropwizard.client.HttpClientBuilder;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
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

        final HttpClient httpClient = new HttpClientBuilder(environment)
                .using(configuration.getHttpClientConfiguration())
                .build("client");
        environment.jersey().register(new CommuneResource(httpClient));
    }



}