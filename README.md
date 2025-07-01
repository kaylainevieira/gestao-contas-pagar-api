GestãoContasAPagar-API
Visão Geral do Projeto
Este projeto implementa uma API RESTful para o gerenciamento de contas a pagar, com foco em uma arquitetura modular e testável. A aplicação é desenvolvida com Spring Boot 3.x e Java 21, utilizando PostgreSQL para persistência de dados. O ambiente é orquestrado via Docker Compose, e o controle de versão do esquema de banco de dados é realizado pelo Flyway. A segurança é provida pelo Spring Security.

Funcionalidades da API
A API oferece os seguintes endpoints para gerenciar contas a pagar:

Autenticação
A API utiliza autenticação HTTP Basic.

Credenciais Padrão:

Usuário: admin

Senha: admin

Observação: Este usuário é criado automaticamente na migração do banco de dados (via Flyway), garantindo sua disponibilidade inicial.

Comandos cURL de Exemplo
Para acessar a API, use um cliente HTTP como curl. As credenciais (admin:admin) são passadas com -u admin:admin.

1- Cadastrar Conta (POST /api/contas-a-pagar)
curl -X POST "http://localhost:8080/api/contas-a-pagar" \
-H "Content-Type: application/json" \
-u admin:admin \
-d '{
  "dataVencimento": "2025-07-30",
  "valor": 250.75,
  "descricao": "Conta de Luz"
}'

2- Atualizar Conta (PUT /api/contas-a-pagar/{id})
Substitua {id} pelo UUID da conta.

curl -X PUT "http://localhost:8080/api/contas-a-pagar/{id}" \
-H "Content-Type: application/json" \
-u admin:admin \
-d '{
  "dataVencimento": "2025-08-15",
  "valor": 300.00,
  "descricao": "Aluguel Atualizado"
}'

3- Alterar a Situação da Conta (PATCH /api/contas-a-pagar/{id}/situacao)
Substitua {id} pelo UUID da conta.

curl -X PATCH "http://localhost:8080/api/contas-a-pagar/{id}/situacao" \
-H "Content-Type: application/json" \
-u admin:admin \
-d '{
  "novaSituacao": "PAGA"
}'

4- Listagem Paginada com Filtro (POST /api/contas-a-pagar/search)
Filtros no corpo da requisição, paginação na URL.

curl -X POST "http://localhost:8080/api/contas-a-pagar/search?page=0&size=10&sort=dataVencimento,asc" \
-H "Content-Type: application/json" \
-u admin:admin \
-d '{
  "dataVencimentoInicio": "2025-01-01",
  "dataVencimentoFim": "2025-12-31",
  "descricao": "Conta"
}'

5- Obter Conta por ID (GET /api/contas-a-pagar/{id})
Substitua {id} pelo UUID da conta.

curl -X GET "http://localhost:8080/api/contas-a-pagar/{id}" \
-u admin:admin

6- Obter Valor Total Pago por Período (GET /api/contas-a-pagar/valor-total-pago)
curl -X GET "http://localhost:8080/api/contas-a-pagar/valor-total-pago?dataPagamentoInicio=2025-07-01&dataPagamentoFim=2025-07-31" \
-u admin:admin

7- Importação de Contas a Pagar via Arquivo CSV (POST /api/contas-a-pagar/importar)
Use a opção -F para enviar o arquivo. Substitua caminho/do/seu/arquivo.csv pelo caminho real.

curl -X POST "http://localhost:8080/api/contas-a-pagar/importar" \
-u admin:admin \
-F "file=@caminho/do/seu/arquivo.csv;type=text/csv"

Formato do Arquivo CSV para Importação
O arquivo CSV deve conter um cabeçalho na primeira linha com os seguintes nomes de coluna e na ordem exata:

descricao,valor,data_vencimento,data_pagamento,situacao

Exemplo de Conteúdo CSV:

descricao,valor,data_vencimento,data_pagamento,situacao
Conta de Telefone,150.75,2025-07-01,,PENDENTE
Parcela do Carro,300.00,2025-07-10,2025-07-05,PAGA
Aluguel Apartamento,1200.00,2025-07-20,,VENCIDA
Assinatura Internet,50.00,2025-07-25,2025-07-25,PAGA
Manutenção Predial,75.00,2025-08-01,,

Regras de Negócio e Restrições
As operações da API seguem regras específicas para garantir a consistência dos dados:

Cadastro de Conta
Toda nova conta é criada com a situação PENDENTE.

dataVencimento, valor, descricao são obrigatórios. valor deve ser positivo, descricao não pode ser vazia (máximo 255 caracteres).

Atualização de Conta
Uma conta na situação PAGA não pode ter seus dados cadastrais (dataVencimento, valor, descricao) alterados.

Os campos dataVencimento, valor, descricao têm as mesmas validações do cadastro.

dataPagamento e situacao não são editáveis via este endpoint.

Alteração da Situação da Conta
Uma conta só pode ser alterada para PAGA se estiver PENDENTE ou VENCIDA.

Ao mudar para PAGA, dataPagamento é preenchida com a data atual do sistema.

Se a situação for alterada para outro estado (ex: CANCELADA), dataPagamento é definida como NULL.

novaSituacao é obrigatória e deve ser um dos estados válidos (PENDENTE, PAGA, VENCIDA, CANCELADA).

Importação de Contas via CSV
O arquivo deve ser text/csv e não pode estar vazio.

As colunas descricao, valor, data_vencimento são obrigatórias em cada linha do CSV.

valor deve ser positivo, descricao não pode ser vazia.

Se situacao estiver vazia no CSV, a conta é importada como PENDENTE.

Se data_pagamento for fornecida no CSV, situacao deve ser PAGA. Caso contrário, é um erro.

Se situacao for PAGA no CSV e data_pagamento estiver vazia, data_pagamento é preenchida com a data atual.

Consultas
Listagem Paginada com Filtro: Exige que todos os filtros (dataVencimentoInicio, dataVencimentoFim, descricao) sejam fornecidos.

Valor Total Pago por Período: As datas dataPagamentoInicio e dataPagamentoFim são obrigatórias e dataPagamentoInicio não pode ser posterior a dataPagamentoFim.

Autenticação e Autorização
A API utiliza Spring Security para controle de acesso. As permissões para cada endpoint (ex: POST /api/contas-a-pagar exige ROLE_USER ou ROLE_ADMIN; PUT /api/contas-a-pagar/{id} exige ROLE_ADMIN) são definidas na configuração de segurança.
