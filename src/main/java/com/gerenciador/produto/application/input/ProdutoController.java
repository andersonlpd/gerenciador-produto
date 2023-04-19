package com.gerenciador.produto.application.input;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.gerenciador.produto.application.output.ProdutoOutput;
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
        Produto produtoCriado = produtoUseCase.criarProduto(produtoInput.toProduto());
        ProdutoOutput produtoOutput = ProdutoOutput.fromProduto(produtoCriado);
        return Response.status(Response.Status.CREATED).entity(produtoOutput).build();
    }

    @GET
    @Path("/{id}")
    public Response getProdutoById(@PathParam("id") UUID id) {
        Produto produtoEncontrado = produtoUseCase.buscarProdutoPorId(id);
        ProdutoOutput produtoOutput = ProdutoOutput.fromProduto(produtoEncontrado);
        return Response.ok(produtoOutput).build();
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
        Produto produtoAtualizado = produtoUseCase.atualizarProduto(id, produtoInput.toProduto());
        ProdutoOutput produtoOutput = ProdutoOutput.fromProduto(produtoAtualizado);
        return Response.ok(produtoOutput).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteProduto(@PathParam("id") UUID id) {
        produtoUseCase.excluirProduto(id);
        return Response.noContent().build();
    }

}