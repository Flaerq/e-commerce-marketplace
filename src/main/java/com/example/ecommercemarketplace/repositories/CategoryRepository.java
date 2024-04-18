package com.example.ecommercemarketplace.repositories;

import com.example.ecommercemarketplace.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
