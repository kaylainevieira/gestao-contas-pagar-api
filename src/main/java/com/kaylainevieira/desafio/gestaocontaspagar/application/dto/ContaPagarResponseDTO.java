package com.kaylainevieira.desafio.gestaocontaspagar.application.dto;

import com.kaylainevieira.desafio.gestaocontaspagar.domain.model.SituacaoConta;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class ContaPagarResponseDTO {

    private UUID id;
    private LocalDate dataVencimento;
    private LocalDate dataPagamento;
    private BigDecimal valor;
    private String descricao;
    private SituacaoConta situacao;
    private OffsetDateTime dataCadastro;
    private OffsetDateTime dataAtualizacao;
}