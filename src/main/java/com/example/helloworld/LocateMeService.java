package com.example.helloworld;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;

public class LocateMeService extends Service<LocateMeConfiguration> {
    public static void main(String[] args) throws Exception {
        new LocateMeService().run(args);
    }

    @Override
    public void initialize(Bootstrap<LocateMeConfiguration> bootstrap) {
        bootstrap.setName("locateme");
    }

    @Override
    public void run(LocateMeConfiguration configuration,
                    Environment environment) {
        environment.addResource(new CommuneResource());
    }



}