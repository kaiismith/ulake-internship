package org.usth.ict.ulake.acladvance.acladvance;

import dev.openfga.sdk.api.client.OpenFgaClient;
import dev.openfga.sdk.api.configuration.ClientConfiguration;
import dev.openfga.sdk.errors.FgaInvalidParameterException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class FgaClientProducer {
    private final FgaConfig fgaConfig;

    public FgaClientProducer(FgaConfig fgaConfig) {
        this.fgaConfig = fgaConfig;
    }

    @Produces
    public OpenFgaClient openFgaClient() throws FgaInvalidParameterException {
        var config = new ClientConfiguration()
                .apiUrl(fgaConfig.getApiUrl())
                .storeId(fgaConfig.getStoreId())
                .authorizationModelId(fgaConfig.getAuthorizationModelId());
        return new OpenFgaClient(config);
    }}
