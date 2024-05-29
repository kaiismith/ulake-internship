package org.usth.ict.ulake.acladvance.acladvance;

import dev.openfga.sdk.api.client.OpenFgaClient;
import dev.openfga.sdk.api.client.model.ClientTupleKey;
import dev.openfga.sdk.api.client.model.ClientWriteRequest;
import dev.openfga.sdk.api.configuration.ClientWriteOptions;
import dev.openfga.sdk.errors.FgaInvalidParameterException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.ExecutionException;

@ApplicationScoped
public class FgaService {
    @Inject
    OpenFgaClient fgaClient;

    public void writeExample() throws ExecutionException, InterruptedException, FgaInvalidParameterException, ExecutionException {
        var options = new ClientWriteOptions()
                .authorizationModelId("01HVMMBCMGZNT3SED4Z17ECXCA");

        var body = new ClientWriteRequest()
                .writes(List.of(
                        new ClientTupleKey()
                                .user("user:bob")
                                .relation("editor")
                                ._object("document:meeting_notes.doc")
                ));

        var response = fgaClient.write(body, options).get();
        // Handle response as needed
    }
}
