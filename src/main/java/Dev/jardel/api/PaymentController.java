package Dev.jardel.api;

import java.math.BigDecimal;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import Dev.jardel.api.dto.PaymentForm;
import Dev.jardel.api.dto.PaymentResponse;
import Dev.jardel.model.Transaction;
import Dev.jardel.model.User;

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
