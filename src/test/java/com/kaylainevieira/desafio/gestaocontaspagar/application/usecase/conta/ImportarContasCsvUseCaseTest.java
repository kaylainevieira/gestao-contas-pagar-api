package com.kaylainevieira.desafio.gestaocontaspagar.application.usecase.conta;

import com.kaylainevieira.desafio.gestaocontaspagar.application.dto.ContaPagarResponseDTO;
import com.kaylainevieira.desafio.gestaocontaspagar.application.exception.ContaPagarCsvException;
import com.kaylainevieira.desafio.gestaocontaspagar.domain.model.SituacaoConta;
import com.kaylainevieira.desafio.gestaocontaspagar.domain.repository.ContaPagarRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql({"classpath:db/sql/application/usecase/conta/importar_contas_csv_test.sql"})
class ImportarContasCsvUseCaseTest {

    @Autowired
    private ImportarContasCsvUseCase importarContasCsvUseCase;

    @Autowired
    private ContaPagarRepository contaPagarRepository;

    @Test
    @DisplayName("Deve importar contas a pagar de um arquivo CSV com sucesso")
    void importarContasCsvUseCase_01() throws IOException {
        var csvContent = "descricao,valor,data_vencimento,data_pagamento,situacao\n" +
                "Conta de Telefone,150.75,2025-07-01,,PENDENTE\n" +
                "Parcela do Carro,300.00,2025-07-10,2025-07-05,PAGA\n" +
                "Aluguel Apartamento,1200.00,2025-07-20,,VENCIDA\n" +
                "Assinatura Internet,50.00,2025-07-25,2025-07-25,PAGA";

        var file = new MockMultipartFile(
                "file",
                "contas.csv",
                "text/csv",
                csvContent.getBytes(StandardCharsets.UTF_8)
        );

        var contasImportadas = importarContasCsvUseCase.execute(file);

        assertThat(contasImportadas).isNotNull().hasSize(4);

        assertThat(contaPagarRepository.count()).isEqualTo(4);

        assertThat(contasImportadas)
                .extracting(ContaPagarResponseDTO::getDescricao, ContaPagarResponseDTO::getValor, ContaPagarResponseDTO::getSituacao)
                .containsExactlyInAnyOrder(
                        org.assertj.core.api.Assertions.tuple("Conta de Telefone", new BigDecimal("150.75"), SituacaoConta.PENDENTE),
                        org.assertj.core.api.Assertions.tuple("Parcela do Carro", new BigDecimal("300.00"), SituacaoConta.PAGA),
                        org.assertj.core.api.Assertions.tuple("Aluguel Apartamento", new BigDecimal("1200.00"), SituacaoConta.VENCIDA),
                        org.assertj.core.api.Assertions.tuple("Assinatura Internet", new BigDecimal("50.00"), SituacaoConta.PAGA)
                );
    }

    @Test
    @DisplayName("Deve lançar ContaPagarCsvException se o arquivo CSV estiver vazio")
    void importarContasCsvUseCase_02() {
        var emptyFile = new MockMultipartFile(
                "file",
                "empty.csv",
                "text/csv",
                new byte[0]
        );

        var thrown = assertThrows(ContaPagarCsvException.class, () ->
                importarContasCsvUseCase.execute(emptyFile));

        assertThat(thrown.getMessage()).contains("O arquivo CSV para importação está vazio.");
    }

    @Test
    @DisplayName("Deve lançar ContaPagarCsvException se o formato do arquivo não for CSV")
    void importarContasCsvUseCase_03() {
        MultipartFile invalidTypeFile = new MockMultipartFile(
                "file",
                "image.jpg",
                "image/jpeg",
                "conteudo".getBytes()
        );

        var thrown = assertThrows(ContaPagarCsvException.class, () ->
                importarContasCsvUseCase.execute(invalidTypeFile));

        assertThat(thrown.getMessage()).contains("Formato de arquivo inválido. Apenas arquivos CSV (text/csv) são aceitos.");
    }

    @Test
    @DisplayName("Deve lançar ContaPagarCsvException se o CSV tiver formato inválido (erro de parsing)")
    void importarContasCsvUseCase_04() throws IOException {
        String invalidCsvContent = "descricao,valor,data_vencimento,data_pagamento,situacao\n" +
                "Conta Inválida,abc,DATA_INVALIDA,,PENDENTE"; // Valor inválido para 'valor'

        var invalidFormatFile = new MockMultipartFile(
                "file",
                "invalid.csv",
                "text/csv",
                invalidCsvContent.getBytes(StandardCharsets.UTF_8)
        );

        var thrown = assertThrows(ContaPagarCsvException.class, () ->
                importarContasCsvUseCase.execute(invalidFormatFile));

        assertThat(thrown.getMessage()).contains("Erro de formato de valor na linha 2 do CSV. Verifique 'valor'.");
    }

    @Test
    @DisplayName("Deve lançar ContaPagarCsvException se o CSV tiver inconsistência de dados (data pagamento sem status PAGA)")
    void importarContasCsvUseCase_05() throws IOException {
        var inconsistentCsvContent = "descricao,valor,data_vencimento,data_pagamento,situacao\n" +
                "Conta Inconsistente,100.00,2025-07-01,2025-06-30,PENDENTE";

        var inconsistentFile = new MockMultipartFile(
                "file",
                "inconsistent.csv",
                "text/csv",
                inconsistentCsvContent.getBytes(StandardCharsets.UTF_8)
        );

        var thrown = assertThrows(ContaPagarCsvException.class, () ->
                importarContasCsvUseCase.execute(inconsistentFile));

        assertThat(thrown.getMessage()).contains("Inconsistência: Conta 'Conta Inconsistente' tem data de pagamento mas situação 'PENDENTE'. Esperado 'PAGA'.");
    }
}
