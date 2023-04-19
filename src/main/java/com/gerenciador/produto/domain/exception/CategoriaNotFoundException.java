package com.gerenciador.produto.domain.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class CategoriaNotFoundException extends RuntimeException implements ExceptionMapper<CategoriaNotFoundException> {
    public CategoriaNotFoundException(String message) {
        super(message);
    }

    public CategoriaNotFoundException() {
        super("Categoria não encontrada.");
    }

    @Override
    public Response toResponse(CategoriaNotFoundException exception) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity("{\"mensagem\": \"" + exception.getMessage() + "\"}")
                .type("application/json").build();
    }
}
