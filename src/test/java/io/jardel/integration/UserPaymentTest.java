package io.jardel.integration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.hamcrest.Matchers;
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
public class UserPaymentTest {

  @InjectMock
  @RestClient
  NotificationService notificationService;

  @InjectMock
  @RestClient
  TransactionValidatorService transactionValidatorService;

  @BeforeEach
  public void setup(){
    Mockito.when(notificationService.sendNotification()).thenReturn(new NotificationResponse());
    Mockito.when(transactionValidatorService.validate()).thenReturn(PaymentFixtures.authorizedResponse());
  }

  @Test
  @DisplayName("Um usuário deve pagar a outros usuários se tiver dinheiro suficiente.")
  public void testUserToUserPayment() {
    PaymentForm userToUser = PaymentFixtures.userToUser(100.0);

    given().when().contentType(ContentType.JSON)
      .body(userToUser)
      .post("/transaction")
    .then()
      .log().all()
      .statusCode(200)
      .body(
        "id", notNullValue(),
        "timestamp", notNullValue()
      );
  }

  @Test
  @DisplayName("Um usuário deve pagar aos lojistas se tiver dinheiro suficiente.")
  public void testUserToShopkeeperPayment() {
    PaymentForm userToUser = PaymentFixtures.userToShopkeeper(100.0);

    given().when().contentType(ContentType.JSON)
      .body(userToUser)
      .post("/transaction")
    .then()
      .log().all()
      .statusCode(200)
      .body(
        "id", notNullValue(),
        "timestamp", notNullValue()
      );
  }
  


}
