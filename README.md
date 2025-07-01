GestãoContasAPagar-API

Este projeto implementa uma API RESTful para o gerenciamento de contas a pagar, com foco em uma arquitetura modular e testável. A aplicação é desenvolvida com Spring Boot 3.x e Java 21, utilizando PostgreSQL para persistência de dados. O ambiente de desenvolvimento e execução é orquestrado via Docker Compose, e o controle de versão do esquema de banco de dados é realizado pelo Flyway. A camada de segurança é provida pelo Spring Security.



Arquitetura e Princípios de Design

A aplicação segue uma arquitetura em camadas clara para separar as responsabilidades:

- Camada de Domínio (domain): Contém o modelo de negócio central (entidades como ContaPagar, Usuario, Role, DadosAtualizacaoConta), seus comportamentos intrínsecos e as interfaces de repositório (ContaPagarRepository, UsuarioRepository, RoleRepository).
- Camada de Aplicação (application): Define os casos de uso da aplicação (Use Cases) e orquestra a lógica de negócio. Inclui DTOs de entrada e saída (ContaPagarRequestDTO, ContaPagarResponseDTO, ContaPagarFiltroDTO, AlterarSituacaoRequestDTO, ContaPagarCsvDTO, PageResponseDTO), mappers (ContaPagarMapper), serviços de consulta (ContaPagarQueryService) e exceções específicas da aplicação.
- Camada de Infraestrutura (infrastructure): Contém os detalhes de implementação técnica, como a persistência de dados (implementações JPA dos repositórios), configuração de segurança (SecurityConfig, CustomUserDetailsService) e parsers de arquivo CSV (ContaPagarCsvParser).
- Camada de Apresentação (presentation): Expõe a API REST através de controladores (ContaPagarController) e gerencia o tratamento global de exceções (GlobalExceptionHandler, ErroResponse).
- Os testes de unidade e integração (com H2 em memória) espelham a estrutura do código de produção, garantindo a validação da funcionalidade em cada camada.



Regras de Negócio Implementadas

1. Cadastro de Conta (POST /api/contas-a-pagar)

Esta operação permite o registro de novas contas a pagar no sistema.

- Situação Inicial Padrão: Toda nova conta é automaticamente definida com a situação PENDENTE no momento de seu cadastro. Não é possível especificar uma situação inicial diferente através da API de cadastro, garantindo que o ciclo de vida da conta comece de um estado conhecido.
- Validações de Dados de Entrada:
    dataVencimento: Campo obrigatório. Deve ser fornecido no formato de data válido (AAAA-MM-DD).
    valor: Campo obrigatório e deve ser um valor numérico positivo (maior que zero). Garante a integridade monetária.
    descricao: Campo obrigatório, não pode estar em branco e possui um limite máximo de 255 caracteres. Isso assegura que a conta tenha uma identificação clara e concisa.
- Comportamento do Sistema: Caso as validações de entrada não sejam atendidas, a requisição será rejeitada com uma resposta de erro indicando os campos inválidos.

2. Atualização de Conta (PUT /api/contas-a-pagar/{id})

Esta operação permite a modificação dos dados cadastrais de uma conta existente.

- Restrição de Atualização por Situação: Uma conta que já se encontra na situação PAGA não pode ter seus dados cadastrais (dataVencimento, valor, descricao) alterados. Esta regra visa preservar a imutabilidade de registros financeiros já liquidados, evitando inconsistências.
- Validações de Dados de Entrada: Os campos dataVencimento, valor e descricao estão sujeitos às mesmas validações de obrigatoriedade, formato e conteúdo aplicadas no cadastro.
- Comportamento do Sistema: Se a conta não for encontrada, uma resposta de erro indicará a ausência do recurso. Se a regra de atualização por situação for violada (tentar atualizar uma conta PAGA), uma resposta de erro específica será retornada.

3. Alteração da Situação da Conta (PATCH /api/contas-a-pagar/{id}/situacao)

Esta operação é dedicada exclusivamente à mudança do status de uma conta.

- Transição para PAGA: Uma conta somente pode ser alterada para a situação PAGA se sua situação atual for PENDENTE ou VENCIDA. Outras transições de estado para PAGA não são permitidas, assegurando um fluxo de pagamento controlado.
- Atualização Consequente da Data de Pagamento: Quando uma conta é alterada para a situação PAGA, o campo dataPagamento é automaticamente preenchido com a data atual do sistema. Se a situação for alterada para qualquer outro estado (ex: CANCELADA), o campo dataPagamento será definido como NULL, garantindo a consistência entre o status e a data do pagamento.
- Validações de Dados de Entrada: O campo novaSituacao é obrigatório e deve corresponder a um dos estados válidos (PENDENTE, PAGA, VENCIDA, CANCELADA).
- Comportamento do Sistema: Tentativas de transições de estado inválidas (ex: pagar uma conta já PAGA) ou o fornecimento de uma situação inválida resultarão em respostas de erro.

4. Importação de Contas a Pagar via Arquivo CSV (POST /api/contas-a-pagar/importar)

Este mecanismo permite o carregamento em massa de contas a partir de um arquivo CSV.

- Validação do Arquivo: O arquivo enviado deve ser do tipo text/csv e não pode estar vazio. Arquivos com formatos incorretos ou sem conteúdo serão rejeitados.
- Formato e Ordem das Colunas CSV: O arquivo CSV deve conter um cabeçalho na primeira linha com os seguintes nomes de coluna e na ordem exata: descricao,valor,data_vencimento,data_pagamento,situacao.
- Campos Obrigatórios no CSV: As colunas descricao, valor e data_vencimento são obrigatórias em cada linha do CSV.
- Validações de Dados por Linha:
    valor: Deve ser um número positivo.
    descricao: Não pode ser vazia.
- Situação Padrão: Se a coluna situacao estiver vazia ou ausente em uma linha do CSV, a conta correspondente será importada com a situação PENDENTE.
- Consistência entre Data de Pagamento e Situação:
    Se uma data_pagamento for fornecida no CSV para uma conta, sua situacao deve ser PAGA. Caso contrário, será considerado um erro de inconsistência de dados na linha.
    Se a situacao for PAGA no CSV, mas a data_pagamento não for fornecida, a data_pagamento será automaticamente preenchida com a data atual do sistema para aquela conta.
- Comportamento do Sistema: Erros de formato, inconsistências de dados por linha ou violações das regras de validação do arquivo resultarão em respostas de erro detalhadas, indicando a natureza do problema.

5. Consultas

As operações de consulta permitem a recuperação e agregação de dados das contas a pagar.

- Listagem Paginada com Filtro (POST /api/contas-a-pagar/search):
    Filtros Obrigatórios: Esta busca exige que todos os seguintes critérios de filtro sejam fornecidos no corpo da requisição JSON: dataVencimentoInicio, dataVencimentoFim e descricao.
    Paginação e Ordenação: A paginação (número da página, tamanho da página) e a ordenação (campo e direção) são aplicadas aos resultados, permitindo a recuperação eficiente de grandes volumes de dados.
- Obter Conta por ID (GET /api/contas-a-pagar/{id}):
    Recupera os dados completos de uma única conta a pagar, identificada por seu UUID na URL.
- Obter Valor Total Pago por Período (GET /api/contas-a-pagar/valor-total-pago):
    Filtros Obrigatórios: As datas dataPagamentoInicio e dataPagamentoFim são obrigatórias e definem o intervalo para a soma dos valores.
    Restrição de Período: A dataPagamentoInicio não pode ser posterior à dataPagamentoFim.



Autenticação e Autorização

A API utiliza Spring Security para autenticação e autorização via HTTP Basic.

- Papéis (Roles): O sistema define papéis de usuário como ROLE_ADMIN (com acesso total a todas as operações) e ROLE_USER (com acesso limitado a operações específicas de consulta e cadastro).
- Usuário Padrão: Um usuário admin com senha admin (codificada) é criado na migração inicial do banco de dados para facilitar testes e desenvolvimento.
- Regras de Autorização: As permissões para cada endpoint (ex: POST /api/contas-a-pagar exige ROLE_USER ou ROLE_ADMIN; PUT /api/contas-a-pagar/{id} exige ROLE_ADMIN) são definidas na configuração de segurança.


