package com.kaylainevieira.desafio.gestaocontaspagar.application.usecase.conta;

import com.kaylainevieira.desafio.gestaocontaspagar.application.dto.AlterarSituacaoRequestDTO;
import com.kaylainevieira.desafio.gestaocontaspagar.application.dto.ContaPagarResponseDTO;

import java.util.UUID;

public interface IAlterarSituacaoContaUseCase {

    ContaPagarResponseDTO execute(UUID id, AlterarSituacaoRequestDTO dto);
}
