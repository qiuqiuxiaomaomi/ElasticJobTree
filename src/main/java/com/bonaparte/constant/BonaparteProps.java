package com.bonaparte.constant;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "elasticjob.zookeeper")
public class BonaparteProps {
    private String serverlists;
    private String namespace;

    public String getServerlists() {
        return serverlists;
    }

    public void setServerlists(String serverlists) {
        this.serverlists = serverlists;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
}
