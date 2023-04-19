package com.gerenciador.produto.application.input;

import java.util.List;
import java.util.stream.Collectors;
import java.util.UUID;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;

import com.gerenciador.produto.application.output.ProdutoOutput;
import com.gerenciador.produto.domain.exception.CategoriaNotFoundException;
import com.gerenciador.produto.domain.exception.ProdutoAlreadyExistsException;
import com.gerenciador.produto.domain.exception.ProdutoNotFoundException;
import com.gerenciador.produto.domain.model.Produto;
import com.gerenciador.produto.domain.usecase.ProdutoUseCase;

@Path("/produtos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProdutoController {

    private final ProdutoUseCase produtoUseCase;

    public ProdutoController(ProdutoUseCase produtoUseCase) {
        this.produtoUseCase = produtoUseCase;
    }

    @POST
    public Response createProduto(@Valid ProdutoInput produtoInput) {
        try {
            Produto produtoCriado = produtoUseCase.criarProduto(produtoInput.toProduto());
            ProdutoOutput produtoOutput = ProdutoOutput.fromProduto(produtoCriado);
            return Response.status(Response.Status.CREATED).entity(produtoOutput).build();
        } catch (ProdutoAlreadyExistsException e) {
            throw new WebApplicationException("Já existe um produto com este nome", Response.Status.CONFLICT);
        } catch (CategoriaNotFoundException e) {
            throw new WebApplicationException("Categoria não encontrada", Response.Status.BAD_REQUEST);
        }
    }

    @GET
    @Path("/{id}")
    public Response getProdutoById(@PathParam("id") UUID id) {
        try {
            Produto produtoEncontrado = produtoUseCase.buscarProdutoPorId(id);
            ProdutoOutput produtoOutput = ProdutoOutput.fromProduto(produtoEncontrado);
            return Response.ok(produtoOutput).build();
        } catch (ProdutoNotFoundException e) {
            throw new WebApplicationException("Produto não encontrado", Response.Status.NOT_FOUND);
        }
    }

    @GET
    public Response getProdutos(@QueryParam("categoria") String categoria) {
        List<Produto> produtosEncontrados;
        if (categoria != null) {
            produtosEncontrados = produtoUseCase.buscarProdutosPorCategoria(categoria);
        } else {
            produtosEncontrados = produtoUseCase.buscarProdutos();
        }
        List<ProdutoOutput> produtosOutputDTO = produtosEncontrados.stream()
                .map(ProdutoOutput::fromProduto)
                .collect(Collectors.toList());
        return Response.ok(produtosOutputDTO).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateProduto(@PathParam("id") UUID id, @Valid ProdutoInput produtoInput) {
        try {
            Produto produtoAtualizado = produtoUseCase.atualizarProduto(id, produtoInput.toProduto());
            ProdutoOutput produtoOutput = ProdutoOutput.fromProduto(produtoAtualizado);
            return Response.ok(produtoOutput).build();
        } catch (ProdutoNotFoundException e) {
            throw new WebApplicationException("Produto não encontrado", Response.Status.NOT_FOUND);
        } catch (CategoriaNotFoundException e) {
            throw new WebApplicationException("Categoria não encontrada", Response.Status.BAD_REQUEST);
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteProduto(@PathParam("id") UUID id) {
        try {
            produtoUseCase.excluirProduto(id);
            return Response.noContent().build();
        } catch (ProdutoNotFoundException e) {
            throw new WebApplicationException("Produto não encontrado", Response.Status.NOT_FOUND);
        }
    }

}