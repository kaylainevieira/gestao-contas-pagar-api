package com.kaylainevieira.desafio.gestaocontaspagar.domain.model;

import com.kaylainevieira.desafio.gestaocontaspagar.application.exception.ContaPagarRegraDeNegocioException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "conta_pagar")
public class ContaPagar {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    @Setter(AccessLevel.NONE)
    @UuidGenerator
    private UUID id;

    @Column(name = "data_vencimento", nullable = false)
    private LocalDate dataVencimento;

    @Column(name = "data_pagamento")
    private LocalDate dataPagamento;

    @Column(name = "valor", nullable = false, precision = 19, scale = 2)
    private BigDecimal valor;

    @Column(name = "descricao", nullable = false, length = 255)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(name = "situacao", nullable = false, length = 50)
    private SituacaoConta situacao;

    @Column(name = "data_cadastro", nullable = false, updatable = false)
    @Builder.Default
    private OffsetDateTime dataCadastro = OffsetDateTime.now();

    @Column(name = "data_atualizacao")
    @UpdateTimestamp
    private OffsetDateTime dataAtualizacao;

    public void atualizarDadosCadastrais(DadosAtualizacaoConta dadosAtualizacao) {
        if (this.situacao == SituacaoConta.PAGA) {
            throw new ContaPagarRegraDeNegocioException("Não é possível atualizar os dados de uma conta que já foi paga.");
        }

        this.dataVencimento = dadosAtualizacao.getDataVencimento();
        this.valor = dadosAtualizacao.getValor();
        this.descricao = dadosAtualizacao.getDescricao();
    }

    public void marcarComoPaga() {
        if (this.situacao == SituacaoConta.PENDENTE || this.situacao == SituacaoConta.VENCIDA) {
            this.dataPagamento = LocalDate.now();
            this.situacao = SituacaoConta.PAGA;
        } else {
            throw new ContaPagarRegraDeNegocioException("Não é possível pagar uma conta com a situação: " + this.situacao);
        }
    }

    public void alterarSituacaoPara(SituacaoConta novaSituacao) {
        if (novaSituacao == SituacaoConta.PAGA) {
            marcarComoPaga();
        } else {
            this.situacao = novaSituacao;
            this.dataPagamento = null;
        }
    }
}