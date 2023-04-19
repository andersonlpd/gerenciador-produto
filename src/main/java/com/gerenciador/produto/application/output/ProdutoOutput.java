package com.gerenciador.produto.application.output;

import java.util.UUID;

import com.gerenciador.produto.domain.model.Produto;

public class ProdutoOutput {

    private UUID id;
    private String nome;
    private Double preco;
    private String categoria;

    public static ProdutoOutput fromProduto(Produto produto) {
        ProdutoOutput dto = new ProdutoOutput();
        dto.setId(produto.getId());
        dto.setNome(produto.getNome());
        dto.setPreco(produto.getPreco());
        dto.setCategoria(produto.getCategoria());
        return dto;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
}
