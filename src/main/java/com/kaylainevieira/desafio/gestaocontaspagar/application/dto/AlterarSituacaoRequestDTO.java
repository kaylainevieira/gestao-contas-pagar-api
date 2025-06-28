package com.kaylainevieira.desafio.gestaocontaspagar.application.dto;

import com.kaylainevieira.desafio.gestaocontaspagar.domain.model.SituacaoConta;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AlterarSituacaoRequestDTO {

    @NotNull(message = "A nova situação é obrigatória.")
    private SituacaoConta novaSituacao;
}