package com.gerenciador.produto.domain.port;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.gerenciador.produto.domain.model.Produto;

public interface ProdutoRepository {
    
    List<Produto> findAll();
    
    Optional<Produto> findById(UUID id);
    
    List<Produto> findByCategory(String category);
    
    void save(Produto produto);

    void delete(UUID id);
}