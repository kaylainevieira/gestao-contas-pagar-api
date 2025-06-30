package com.kaylainevieira.desafio.gestaocontaspagar.application.usecase.conta;

import com.kaylainevieira.desafio.gestaocontaspagar.application.dto.ContaPagarRequestDTO;
import com.kaylainevieira.desafio.gestaocontaspagar.application.dto.ContaPagarResponseDTO;

public interface ICadastrarContaUseCase {

    ContaPagarResponseDTO execute(ContaPagarRequestDTO dto);
}
