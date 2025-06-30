package com.kaylainevieira.desafio.gestaocontaspagar.application.usecase.conta;


import com.kaylainevieira.desafio.gestaocontaspagar.application.exception.ContaPagarRegraDeNegocioException;
import com.kaylainevieira.desafio.gestaocontaspagar.domain.model.SituacaoConta;
import com.kaylainevieira.desafio.gestaocontaspagar.domain.repository.ContaPagarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ObterValorTotalPagoPorPeriodoUseCase implements IObterValorTotalPagoPorPeriodoUseCase {

    private final ContaPagarRepository contaPagarRepository;

    @Transactional(readOnly = true)
    @Override
    public BigDecimal execute(LocalDate dataPagamentoInicio, LocalDate dataPagamentoFim) {
        if (dataPagamentoInicio == null || dataPagamentoFim == null) {
            throw new ContaPagarRegraDeNegocioException("As datas de início e fim do pagamento são obrigatórias para obter o valor total pago por período.");
        }
        if (dataPagamentoInicio.isAfter(dataPagamentoFim)) {
            throw new ContaPagarRegraDeNegocioException("A data de início do pagamento não pode ser posterior à data de fim do pagamento.");
        }

        return contaPagarRepository.sumValorByDataPagamentoBetweenAndSituacao(
                dataPagamentoInicio,
                dataPagamentoFim,
                SituacaoConta.PAGA);
    }
}
