package Dev.jardel.service;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.jardel.service.dto.TransactionValitadorResponse;

@ApplicationScoped
@RegisterRestClient(configKey = "validador de transação")
public interface TransactionValidatorService {

    @GET
    TransactionValitadorResponse validate();
}
