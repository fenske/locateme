package com.example.helloworld;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.config.Configuration;
import org.hibernate.validator.constraints.NotEmpty;

public class LocateMeConfiguration extends Configuration {

    @NotEmpty
    @JsonProperty
    private String defaultName = "Stranger";

    public String getDefaultName() {
        return defaultName;
    }
}