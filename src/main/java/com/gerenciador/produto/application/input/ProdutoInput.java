package com.gerenciador.produto.application.input;

import com.gerenciador.produto.domain.model.Produto;

public class ProdutoInput {

    private String nome;
    private Double preco;
    private String categoria;

    public Produto toProduto() {
        Produto produto = new Produto();
        produto.setNome(this.getNome());
        produto.setPreco(this.getPreco());
        produto.setCategoria(this.getCategoria());
        return produto;
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