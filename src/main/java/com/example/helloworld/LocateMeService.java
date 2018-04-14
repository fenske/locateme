package com.example.helloworld;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

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
        environment.jersey().register(new CommuneResource());
    }



}