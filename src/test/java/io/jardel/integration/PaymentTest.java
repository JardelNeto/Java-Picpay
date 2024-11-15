package io.jardel.integration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.http.ContentType;
import io.jardel.api.dto.PaymentForm;
import io.jardel.fixtures.PaymentFixtures;
import io.jardel.service.NotificationService;
import io.jardel.service.TransactionValidatorService;
import io.jardel.service.dto.NotificationResponse;

@QuarkusTest
public class PaymentTest {
  
  @InjectMock
  @RestClient
  NotificationService notificationService;

  @InjectMock
  @RestClient
  TransactionValidatorService transactionValidatorService;

  @BeforeEach
  void setup() {
    Mockito.when(notificationService.sendNotification()).thenReturn(new NotificationResponse());
    Mockito.when(transactionValidatorService.validate()).thenReturn(PaymentFixtures.authorizedResponse());
  }


  @Test
  @DisplayName("Se o usuário não tiver dinheiro suficiente, a transação não será aceita e o usuário receberá um código de status 400 http.")
  public void insufficientMoney() {
    PaymentForm userToUser = PaymentFixtures.userToUser(10000.0);

    given().when().contentType(ContentType.JSON)
      .body(userToUser)
      .post("/transaction")
    .then()
      .log().all()
      .statusCode(400)
      .body("error", is("Dinheiro insuficiente para concluir esta transação."));
  }

  @Test
  @DisplayName("Se o serviço de validação da transação não autorizar o pagamento, a transação não deverá ser concluída. E o usuário deve receber um status 401 http.")
  public void testUnauthorizedTransaction() {
    Mockito.reset(transactionValidatorService);
    Mockito.when(transactionValidatorService.validate()).thenReturn(PaymentFixtures.unauthorizedResponse());

    PaymentForm userToUser = PaymentFixtures.userToUser(100.0);

    given().when().contentType(ContentType.JSON)
      .body(userToUser)
      .post("/transaction")
    .then()
      .log().all()
      .statusCode(401)
      .body("error", is("Transação não autorizada."));
  }

  @Test
  @DisplayName("O valor do pagamento deve ser maior que 0")
  void invalidAmmout() {
    PaymentForm userToUser = PaymentFixtures.userToUser(0.0);

    given().when().contentType(ContentType.JSON)
      .body(userToUser)
      .post("/transaction")
    .then()
      .log().all()
      .statusCode(400)
      .body("parameterViolations.message", hasItem("O valor do pagamento deve ser maior que 0."));
  }
}
