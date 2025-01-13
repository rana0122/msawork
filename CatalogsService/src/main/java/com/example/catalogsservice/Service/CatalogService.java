package com.example.catalogsservice.Service;

import com.example.catalogsservice.JPA.CatalogEntity;

public interface CatalogService {
    Iterable<CatalogEntity> getAllCatalogs();
}
