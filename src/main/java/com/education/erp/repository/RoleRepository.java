package com.education.erp.repository;

import com.education.erp.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Role entity.
 * Provides basic CRUD operations automatically via Spring Data JPA.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    // Custom query method find a role by its name (e.g., "ROLE_STUDENT")
    Optional<Role> findByName(String name);
    Optional<Role> findByNameIgnoreCase(String name);
}
