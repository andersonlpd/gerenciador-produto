package com.gerenciador.produto.domain.usecase;

import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import com.gerenciador.produto.domain.exception.ProdutoNotFoundException;
import com.gerenciador.produto.domain.model.Produto;
import com.gerenciador.produto.domain.port.ProdutoRepository;

@ApplicationScoped
public class ProdutoUseCaseImpl implements ProdutoUseCase {

    private ProdutoRepository produtoRepository;

    @Inject
    public ProdutoUseCaseImpl(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @Override
    @Transactional
    public Produto criarProduto(Produto produto) {
        return produtoRepository.salvar(produto);
    }

    @Override
    public Produto criarProdutoPorID(UUID id, Produto produto) {
        Produto produtoExistente = produtoRepository.buscarPorId(id);
        if (produtoExistente == null) {
            throw new ProdutoNotFoundException("Produto não encontrado");
        }
        produto.setId(id);
        return produtoRepository.salvar(produto);
    }

    @Override
    public Produto buscarProdutoPorId(UUID id) {
        Produto produto = produtoRepository.buscarPorId(id);
        if (produto == null) {
            throw new ProdutoNotFoundException("Produto com id " + id + " não encontrado.");
        }
        return produtoMapper.fromProduto(produto);
    }

    @Override
    public List<Produto> buscarProdutosPorCategoria(String categoria) {
        return produtoRepository.buscarPorCategoria(categoria);
    }

    @Override
    public List<Produto> buscarProdutos() {
        return produtoRepository.buscarTodos();
    }

    @Override
    @Transactional
    public Produto atualizarProduto(UUID id, Produto produto) {
        Produto produtoExistente = produtoRepository.buscarPorId(id);
        if (produtoExistente == null) {
            throw new ProdutoNotFoundException("Produto não encontrado");
        }
        produto.setId(id);
        return produtoRepository.salvar(produto);
    }

    @Override
    @Transactional
    public void excluirProduto(UUID id) {
        Produto produtoExistente = produtoRepository.buscarPorId(id);
        if (produtoExistente == null) {
            throw new ProdutoNotFoundException("Produto não encontrado");
        }
        produtoRepository.deletar(id);
    }
}
