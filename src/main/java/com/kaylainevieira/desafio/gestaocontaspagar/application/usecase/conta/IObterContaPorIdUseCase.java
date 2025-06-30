package com.kaylainevieira.desafio.gestaocontaspagar.application.usecase.conta;

import com.kaylainevieira.desafio.gestaocontaspagar.application.dto.ContaPagarResponseDTO;

import java.util.UUID;

public interface IObterContaPorIdUseCase {

    ContaPagarResponseDTO execute(UUID id);
}
