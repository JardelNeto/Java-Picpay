package Dev.jardel.service;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.jardel.service.dto.NotificationResponse;

@ApplicationScoped
@RegisterRestClient(configKey = "notificação")
public interface NotificationService {

	@GET
	NotificationResponse sendNotification();
}
