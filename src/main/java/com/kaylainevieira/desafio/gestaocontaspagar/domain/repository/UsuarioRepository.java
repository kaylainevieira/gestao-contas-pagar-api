package com.kaylainevieira.desafio.gestaocontaspagar.domain.repository;

import com.kaylainevieira.desafio.gestaocontaspagar.domain.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);
}
