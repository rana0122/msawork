package com.example.catalogsservice.Service;

import com.example.catalogsservice.JPA.CatalogEntity;

import com.example.catalogsservice.JPA.CatalogRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CatalogServiceImpl implements CatalogService {

    private final CatalogRepository catalogRepository;

    @Override
    public Iterable<CatalogEntity> getAllCatalogs() {
        return catalogRepository.findAll();
    }
}
