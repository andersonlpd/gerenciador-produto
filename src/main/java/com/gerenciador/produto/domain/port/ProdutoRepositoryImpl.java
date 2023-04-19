package com.gerenciador.produto.domain.port;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;

import javax.inject.Singleton;

import com.gerenciador.produto.domain.model.Produto;

import io.quarkus.redis.client.RedisClient;

@Singleton
public class ProductRepositoryImpl implements ProductRepository {

    private static final String SELECT_ALL_QUERY = "SELECT id, name, price, category FROM products";
    private static final String SELECT_BY_ID_QUERY = "SELECT id, name, price, category FROM products WHERE id = ?";
    private static final String SELECT_BY_CATEGORY_QUERY = "SELECT id, name, price, category FROM products WHERE category = ?";
    private static final String INSERT_QUERY = "INSERT INTO products (id, name, price, category) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE products SET name = ?, price = ?, category = ? WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM products WHERE id = ?";

    private final CqlSession session;
    private final RedisClient redisClient;

    public ProductRepositoryImpl(CqlSession session, RedisClient redisClient) {
        this.session = session;
        this.redisClient = redisClient;
    }

    @Override
    public List<Product> findAll() {
        String key = "product:all";
        List<Product> products = redisClient.get(key, List.class);
        if (products == null) {
            ResultSet rs = session.execute(SELECT_ALL_QUERY);
            products = ProductMapper.map(rs);
            redisClient.setex(key, 60, products);
        }
        return products;
    }

    @Override
    public Optional<Product> findById(UUID id) {
        String key = "product:" + id;
        Optional<Product> product = redisClient.get(key, Product.class);
        if (product.isEmpty()) {
            SimpleStatement stmt = SimpleStatement.newInstance(SELECT_BY_ID_QUERY, id);
            ResultSet rs = session.execute(stmt);
            Row row = rs.one();
            product = Optional.ofNullable(ProductMapper.map(row));
            if (product.isPresent()) {
                redisClient.setex(key, 60, product.get());
            }
        }
        return product;
    }

    @Override
    public List<Product> findByCategory(String category) {
        String key = "product:category:" + category;
        List<Product> products = redisClient.get(key, List.class);
        if (products == null) {
            SimpleStatement stmt = SimpleStatement.newInstance(SELECT_BY_CATEGORY_QUERY, category);
            ResultSet rs = session.execute(stmt);
            products = ProductMapper.map(rs);
            redisClient.setex(key, 60, products);
        }
        return products;
    }

    @Override
    public void save(Product product) {
        SimpleStatement stmt = SimpleStatement.newInstance(INSERT_QUERY, product.getId(), product.getName(),
                product.getPrice(), product.getCategory());
        session.execute(stmt);
        redisClient.del("product:all");
        redisClient.del("product:category:" + product.getCategory());
        redisClient.del("product:" + product.getId());
    }
