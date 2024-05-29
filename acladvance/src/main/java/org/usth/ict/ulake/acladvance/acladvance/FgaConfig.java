package org.usth.ict.ulake.acladvance.acladvance;

import org.eclipse.microprofile.config.inject.ConfigProperties;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ConfigProperties(prefix = "fga")
public class FgaConfig {
    @ConfigProperty(name = "api-url")
    String apiUrl;

    @ConfigProperty(name = "store-id")
    String storeId;

    @ConfigProperty(name = "authorization-model-id")
    String authorizationModelId;

    public String getApiUrl() {
        return apiUrl;
    }

    public String getStoreId() {
        return storeId;
    }

    public String getAuthorizationModelId() {
        return authorizationModelId;
    }
}
