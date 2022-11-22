package com.geodir.apidatacrime.apidatacrime.domain.security;

import lombok.AllArgsConstructor;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.stereotype.Service;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
@Service
@AllArgsConstructor
public class UserServiceOauth2 {

    private final ResourceServerTokenServices resourceServerTokenServices;

    public String getUserAccountByKey(String key){


        try {
            OAuth2Authentication auth = resourceServerTokenServices.loadAuthentication(key);

            return (String)auth.getPrincipal();

        }catch (Exception e){

            throw new KeyFailedException(key);
        }

    }


}
