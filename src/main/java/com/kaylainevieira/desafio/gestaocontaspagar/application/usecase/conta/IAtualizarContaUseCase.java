package com.kaylainevieira.desafio.gestaocontaspagar.application.usecase.conta;

import com.kaylainevieira.desafio.gestaocontaspagar.application.dto.ContaPagarRequestDTO;
import com.kaylainevieira.desafio.gestaocontaspagar.application.dto.ContaPagarResponseDTO;

import java.util.UUID;

public interface IAtualizarContaUseCase {
    ContaPagarResponseDTO execute(UUID id, ContaPagarRequestDTO dto);
}
