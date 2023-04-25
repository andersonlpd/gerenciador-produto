package com.gerenciador.produto.domain.port;


import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.inject.Singleton;

import com.gerenciador.produto.domain.model.Produto;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.redis.client.RedisAPI;

@Singleton
public class ProdutoRepositoryImpl implements ProdutoRepository {

    private static final String SELECT_ALL_QUERY = "SELECT id, name, price, category FROM produtos";
    private static final String SELECT_BY_ID_QUERY = "SELECT id, name, price, category FROM produtos WHERE id = ?";
    private static final String SELECT_BY_CATEGORY_QUERY = "SELECT id, name, price, category FROM produtos WHERE category = ?";
    private static final String INSERT_QUERY = "INSERT INTO produtos (id, name, price, category) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE produtos SET name = ?, price = ?, category = ? WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM produtos WHERE id = ?";
    private static final String redisTTL = "60";

    private final CqlSession session;
    private final RedisAPI redisAPI;

    public ProdutoRepositoryImpl(CqlSession session, RedisAPI redisAPI) {
        this.session = session;
        this.redisAPI = redisAPI;
    }

    @Override
    public List<Produto> findAll() {
        String key = "Produto:all";
        return redisAPI.get(key)
                .onItem()
                .transformToUni((Function<String, Uni<List<Produto>>>) Produtos -> {
                    if (Produtos == null) {
                        ResultSet rs = session.execute(SELECT_ALL_QUERY);
                        List<Produto> mappedProdutos = ProdutoMapper.map(rs);
                        ObjectMapper ObjMapper = new ObjectMapper();
                        String mappedProdutosJson = ObjMapper.writeValueAsString(mappedProdutos);
                        return redisAPI.setex(key, redisTTL, mappedProdutosJson)
                                .map(ignore -> mappedProdutos);
                    } else {
                        return Uni.createFrom().item(Optional.of(Produtos).map(p -> ProdutoMapper.fromString(p)).get());

                    }
                })
                .await().indefinitely();
    }
    
    @Override
    public Optional<Produto> findById(UUID id) {
        String key = "Produto:" + id;
        return redisAPI.get(key)
                .onItem()
                .transformToUni((Function<String, Uni<Optional<Produto>>>) Produto -> {
                    if (Produto == null) {
                        SimpleStatement stmt = SimpleStatement.newInstance(SELECT_BY_ID_QUERY, id);
                        ResultSet rs = session.execute(stmt);
                        Row row = rs.one();
                        Optional<Produto> mappedProduto = Optional.ofNullable(ProdutoMapper.map(row));
                        if (mappedProduto.isPresent()) {
                            return redisAPI.setex(key, 60, mappedProduto.get())
                                    .map(ignore -> mappedProduto);
                        } else {
                            return Uni.createFrom().item(mappedProduto);
                        }
                    } else {
                        return Uni.createFrom().item(Optional.of(Produto).map(ProdutoMapper::map));
                    }
                })
                .await().indefinitely();
    }

    @Override
    public List<Produto> findByCategory(String category) {
        String key = "Produto:category:" + category;
        return redisAPI.get(key)
                .onItem()
                .transformToUni(Produtos -> {
                    if (Produtos == null) {
                        SimpleStatement stmt = SimpleStatement.newInstance(SELECT_BY_CATEGORY_QUERY, category);
                        ResultSet rs = session.execute(stmt);
                        List<Produto> mappedProdutos = ProdutoMapper.map(rs);
                        return redisAPI.setex(key, 60, mappedProdutos)
                                .map(ignore -> mappedProdutos);
                    }
                }
        }

    @Override
    public void save(Produto Produto) {
        redis.set(Arrays.asList("Produto", Produto.getId().toString()), Produto.toJson())
            .subscribe().with(success -> {
                SimpleStatement stmt = SimpleStatement.newInstance(INSERT_QUERY, Produto.getId(), Produto.getName(),
                    Produto.getPrice(), Produto.getCategory());
                session.execute(stmt);
            });
        redis.del(Arrays.asList("Produto", "all")).subscribe();
        redis.del(Arrays.asList("Produto", "category", Produto.getCategory())).subscribe();
    }

    @Override
    public void delete(UUID id) {
        SimpleStatement stmt = SimpleStatement.newInstance(DELETE_QUERY, id);
        session.execute(stmt);
        redisClient.del("product:all");
        Optional<Product> product = findById(id);
        if (product.isPresent()) {
            redisClient.del("product:category:" + product.get().getCategory());
            redisClient.del("product:" + id);
        }
    }

}