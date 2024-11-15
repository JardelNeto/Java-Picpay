package io.jardel.api;

import java.math.BigDecimal;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.jardel.api.dto.PaymentForm;
import io.jardel.api.dto.PaymentResponse;
import io.jardel.model.Transaction;
import io.jardel.model.User;

@Path("/transaction")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PaymentController {

    @POST
    @Transactional
    public Response payment(@Valid PaymentForm form) {
        User payer = form.toPayer();
        User payee = form.toPayee();
        BigDecimal value = form.value;

        Transaction transaction = payer.pay(value, payee);

        return Response.ok(new PaymentResponse(transaction)).build();
    }
}
