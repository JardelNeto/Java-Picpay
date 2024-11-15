package io.jardel.integration;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.hamcrest.CoreMatchers;
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

import static io.restassured.RestAssured.expect;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
public class ShopkeeperPaymentTest {

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
  @DisplayName("Os lojistas deverão receber pagamentos dos usuários.")
  void receiveFromUser() {
    PaymentForm request = PaymentFixtures.userToShopkeeper(100.0);

    given().when().contentType(ContentType.JSON).body(request).post("/transaction").then().log().all().statusCode(200)
        .body("id", notNullValue(), "timestamp", notNullValue());
  }

  @Test
  @DisplayName("Os lojistas não devem efetuar pagamentos, apenas receber.")
  void shoopkeeperInvalidPayment() {
    
    PaymentForm request1 = PaymentFixtures.shopkeeperToShopkeeper(100.0);
    PaymentForm request2 = PaymentFixtures.shopkeeperToUser(100.0);

    expect()
      .statusCode(400)
      .body("error", CoreMatchers.is("Este usuário não pode pagar, apenas receber pagamentos"))
    .given().when().contentType(ContentType.JSON)
      .body(request1)
      .post("/transaction");

    expect()
      .statusCode(400)
      .body("error", CoreMatchers.is("Este usuário não pode pagar, apenas receber pagamentos."))
    .given().when().contentType(ContentType.JSON)
      .body(request2)
      .post("/transaction");
  }
}
