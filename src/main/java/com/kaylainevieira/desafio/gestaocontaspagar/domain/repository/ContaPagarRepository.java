package com.kaylainevieira.desafio.gestaocontaspagar.domain.repository;

import com.kaylainevieira.desafio.gestaocontaspagar.domain.model.ContaPagar;
import com.kaylainevieira.desafio.gestaocontaspagar.domain.model.SituacaoConta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public interface ContaPagarRepository extends JpaRepository<ContaPagar, UUID> {

    Page<ContaPagar> findByDataVencimentoBetweenAndDescricaoContainingIgnoreCase(
            LocalDate dataVencimentoInicio,
            LocalDate dataVencimentoFim,
            String descricao,
            Pageable pageable);

    @Query("SELECT COALESCE(SUM(c.valor), 0) FROM ContaPagar c WHERE c.dataPagamento BETWEEN :dataInicio AND :dataFim AND c.situacao = :situacao")
    BigDecimal sumValorByDataPagamentoBetweenAndSituacao(
            LocalDate dataInicio,
            LocalDate dataFim,
            SituacaoConta situacao);
}
