package com.kaylainevieira.desafio.gestaocontaspagar.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "roles")
public class Role {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    @Setter(AccessLevel.NONE)
    @UuidGenerator
    private UUID id;

    @Enumerated(EnumType.STRING)
    private RoleName name;

    public enum RoleName {
        ROLE_ADMIN,
        ROLE_USER
    }
}
