package com.kaylainevieira.desafio.gestaocontaspagar.infrastructure.csv;

import com.kaylainevieira.desafio.gestaocontaspagar.application.dto.ContaPagarCsvDTO;
import com.kaylainevieira.desafio.gestaocontaspagar.application.exception.ContaPagarCsvException;
import com.kaylainevieira.desafio.gestaocontaspagar.domain.model.SituacaoConta;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Component
public class ContaPagarCsvParser {

    private static final String HEADER_DESCRICAO = "descricao";
    private static final String HEADER_VALOR = "valor";
    private static final String HEADER_DATA_VENCIMENTO = "data_vencimento";
    private static final String HEADER_DATA_PAGAMENTO = "data_pagamento";
    private static final String HEADER_SITUACAO = "situacao";

    public List<ContaPagarCsvDTO> parse(InputStream is) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(fileReader,
                     CSVFormat.DEFAULT.builder()
                             .setHeader(HEADER_DESCRICAO, HEADER_VALOR, HEADER_DATA_VENCIMENTO, HEADER_DATA_PAGAMENTO, HEADER_SITUACAO)
                             .setSkipHeaderRecord(true)
                             .setTrim(true)
                             .build())) {

            List<ContaPagarCsvDTO> contasCsv = new ArrayList<>();
            int lineNumber = 1;

            for (CSVRecord csvRecord : csvParser.getRecords()) {
                lineNumber++;
                try {
                    contasCsv.add(parseRecord(csvRecord, lineNumber));
                } catch (ContaPagarCsvException e) {
                    throw e;
                } catch (Exception e) {
                    throw new ContaPagarCsvException(
                            "Erro inesperado ao processar linha %d do CSV: %s", lineNumber, e.getMessage(), e);
                }
            }
            return contasCsv;
        } catch (IOException e) {
            throw new ContaPagarCsvException("Não foi possível ler o arquivo CSV: " + e.getMessage(), e);
        }
    }

    private ContaPagarCsvDTO parseRecord(CSVRecord csvRecord, int lineNumber) {
        try {
            var descricao = csvRecord.get(HEADER_DESCRICAO);
            var valor = new BigDecimal(csvRecord.get(HEADER_VALOR));
            var dataVencimento = LocalDate.parse(csvRecord.get(HEADER_DATA_VENCIMENTO));

            LocalDate dataPagamento = null;
            if (csvRecord.isSet(HEADER_DATA_PAGAMENTO) && !csvRecord.get(HEADER_DATA_PAGAMENTO).isBlank()) {
                dataPagamento = LocalDate.parse(csvRecord.get(HEADER_DATA_PAGAMENTO));
            }

            SituacaoConta situacao = null;
            if (csvRecord.isSet(HEADER_SITUACAO) && !csvRecord.get(HEADER_SITUACAO).isBlank()) {
                situacao = SituacaoConta.valueOf(csvRecord.get(HEADER_SITUACAO).toUpperCase());
            }

            if (valor.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Valor deve ser positivo.");
            }
            if (descricao.isBlank()) {
                throw new IllegalArgumentException("Descrição não pode ser vazia.");
            }

            if (situacao == null) {
                situacao = SituacaoConta.PENDENTE;
            }

            if (dataPagamento != null) {
                if (situacao != SituacaoConta.PAGA) {
                    throw new IllegalArgumentException(
                            String.format("Inconsistência: Conta '%s' tem data de pagamento mas situação '%s'. Esperado 'PAGA'.",
                                    descricao, situacao));
                }
            } else {
                if (situacao == SituacaoConta.PAGA) {
                    dataPagamento = LocalDate.now();
                }
            }

            return ContaPagarCsvDTO.builder()
                    .dataVencimento(dataVencimento)
                    .valor(valor)
                    .descricao(descricao)
                    .dataPagamento(dataPagamento)
                    .situacao(situacao)
                    .build();

        } catch (DateTimeParseException e) {
            throw new ContaPagarCsvException(
                    "Erro de formato de data na linha %d do CSV. Verifique 'data_vencimento' e 'data_pagamento'.", lineNumber, e);
        } catch (NumberFormatException e) {
            throw new ContaPagarCsvException(
                    "Erro de formato de valor na linha %d do CSV. Verifique 'valor'.", lineNumber, e);
        } catch (IllegalArgumentException e) {
            throw new ContaPagarCsvException(
                    "Erro de dados na linha %d do CSV: %s", lineNumber, e.getMessage(), e);
        }
    }
}
