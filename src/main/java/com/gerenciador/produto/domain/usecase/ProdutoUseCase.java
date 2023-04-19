package com.gerenciador.produto.domain.usecase;

import com.gerenciador.produto.domain.model.Produto;

import java.util.List;
import java.util.UUID;

public interface ProdutoUseCase {

    Produto criarProduto(Produto produto);

    Produto criarProdutoPorID(UUID id, Produto produto);

    Produto buscarProdutoPorId(UUID id);

    List<Produto> buscarProdutosPorCategoria(String categoria);

    List<Produto> buscarProdutos();

    Produto atualizarProduto(UUID id, Produto produto);

    void excluirProduto(UUID id);
}
