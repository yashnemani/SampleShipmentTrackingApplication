/*package com.Nexterus.TrackShipment.Config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;

@EnableOAuth2Client
@Configuration
public class AuthConfiguration {


    @Bean
    protected OAuth2ProtectedResourceDetails resource() {

        ResourceOwnerPasswordResourceDetails resource = new ResourceOwnerPasswordResourceDetails();

        List<String> scopes = new ArrayList<>();
        scopes.add("write");
        scopes.add("read");
        scopes.add("default");
        resource.setAccessTokenUri("https://api.ltl.xpo.com/token");
        resource.setClientId(null);
        resource.setClientSecret(null);
        resource.setGrantType("password");
        resource.setScope(scopes);

        resource.setUsername("mroberts4");
        resource.setPassword("nxtCNWY802");

        return resource;
    }
}*/
