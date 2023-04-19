package com.gerenciador.produto.domain.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ProdutoAlreadyExistsException extends RuntimeException implements ExceptionMapper<ProdutoAlreadyExistsException> {
    public ProdutoAlreadyExistsException(String message) {
        super(message);
    }

    public ProdutoAlreadyExistsException() {
        super("Produto já existe.");
    }

    @Override
    public Response toResponse(ProdutoAlreadyExistsException exception) {
        return Response.status(Response.Status.CONFLICT)
                .entity("{\"mensagem\": \"" + exception.getMessage() + "\"}")
                .type("application/json").build();
    }
}