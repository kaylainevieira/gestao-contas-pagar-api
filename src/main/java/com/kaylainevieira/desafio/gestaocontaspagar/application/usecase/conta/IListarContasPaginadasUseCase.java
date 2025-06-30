package com.kaylainevieira.desafio.gestaocontaspagar.application.usecase.conta;

import com.kaylainevieira.desafio.gestaocontaspagar.application.dto.ContaPagarFiltroDTO;
import com.kaylainevieira.desafio.gestaocontaspagar.application.dto.ContaPagarResponseDTO;
import com.kaylainevieira.desafio.gestaocontaspagar.application.dto.PageResponseDTO;
import org.springframework.data.domain.Pageable;

public interface IListarContasPaginadasUseCase {

    PageResponseDTO<ContaPagarResponseDTO> execute(ContaPagarFiltroDTO filtro, Pageable pageable);
}
