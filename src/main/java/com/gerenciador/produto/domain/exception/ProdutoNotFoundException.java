package com.gerenciador.produto.domain.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ProdutoNotFoundException extends RuntimeException implements ExceptionMapper<ProdutoNotFoundException> {
    public ProdutoNotFoundException(String message) {
        super(message);
    }

    public ProdutoNotFoundException() {
        super("Produto não encontrado.");
    }

    @Override
    public Response toResponse(ProdutoNotFoundException exception) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity("{\"mensagem\": \"" + exception.getMessage() + "\"}")
                .type("application/json").build();
    }
}
