package com.gerenciador.produto.domain.port;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.gerenciador.produto.domain.model.Produto;

public class ProdutoMapper {

    public static List<Produto> map(ResultSet rs) {
        List<Produto> produtos = new ArrayList<>();
        rs.forEach(row -> produtos.add(map(row)));
        return produtos;
    }

    public static Produto map(Row row) {
        UUID id = row.getUuid("id");
        String nome = row.getString("name");
        Double preco = row.getDouble("preco");
        String categoria = row.getString("categoria");

        return new Produto(id, nome, preco, categoria);
    }

    public static String toJson(Produto produto) {
        return String.format("{\"id\":\"%s\", \"name\":\"%s\", \"price\":\"%s\", \"category\":\"%s\"}", produto.getId(), produto.getNome(), produto.getPreco(), produto.getCategoria());
    }

    public static Produto fromJson(String json) {
        String[] parts = json.split(",");
        UUID id = UUID.fromString(parts[0].split(":")[1].replaceAll("\"", ""));
        String nome = parts[1].split(":")[1].replaceAll("\"", "");
        Double preco = new Double(parts[2].split(":")[1]);
        String categoria = parts[3].split(":")[1].replaceAll("\"", "").replaceAll("}", "");

        return new Produto(id, nome, preco, categoria);
    }
}