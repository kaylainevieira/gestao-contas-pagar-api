package com.kaylainevieira.desafio.gestaocontaspagar.application.usecase.conta;

import com.kaylainevieira.desafio.gestaocontaspagar.application.dto.ContaPagarFiltroDTO;
import com.kaylainevieira.desafio.gestaocontaspagar.application.dto.ContaPagarResponseDTO;
import com.kaylainevieira.desafio.gestaocontaspagar.application.dto.PageResponseDTO;
import com.kaylainevieira.desafio.gestaocontaspagar.application.mapper.ContaPagarMapper;
import com.kaylainevieira.desafio.gestaocontaspagar.domain.repository.ContaPagarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListarContasPaginadasUseCase implements IListarContasPaginadasUseCase {

    private final ContaPagarRepository contaPagarRepository;
    private final ContaPagarMapper contaPagarMapper;

    @Override
    public PageResponseDTO<ContaPagarResponseDTO> execute(ContaPagarFiltroDTO filtro, Pageable pageable) {
        var contasPage = contaPagarRepository.findByDataVencimentoBetweenAndDescricaoContainingIgnoreCase(
                filtro.getDataVencimentoInicio(),
                filtro.getDataVencimentoFim(),
                filtro.getDescricao(),
                pageable);

        var contentDTO = contasPage.getContent().stream()
                .map(contaPagarMapper::toContaPagarResponseDTO)
                .collect(Collectors.toList());

        return PageResponseDTO.<ContaPagarResponseDTO>builder()
                .content(contentDTO)
                .pageNumber(contasPage.getNumber())
                .pageSize(contasPage.getSize())
                .totalElements(contasPage.getTotalElements())
                .totalPages(contasPage.getTotalPages())
                .isLast(contasPage.isLast())
                .isFirst(contasPage.isFirst())
                .build();
    }
}
