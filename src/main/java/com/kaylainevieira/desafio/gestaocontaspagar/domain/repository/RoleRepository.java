package com.kaylainevieira.desafio.gestaocontaspagar.domain.repository;

import com.kaylainevieira.desafio.gestaocontaspagar.domain.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(Role.RoleName name);
}
