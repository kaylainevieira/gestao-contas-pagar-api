INSERT INTO conta_pagar (id, data_vencimento, valor, descricao, situacao, data_cadastro)
VALUES ('a0000000-0000-0000-0000-000000000001', '2025-07-10', 100.00, 'Conta de Luz Julho', 'PENDENTE', NOW());

INSERT INTO conta_pagar (id, data_vencimento, valor, descricao, situacao, data_cadastro)
VALUES ('a0000000-0000-0000-0000-000000000002', '2025-07-15', 200.00, 'Conta de Água Julho', 'PENDENTE', NOW());

INSERT INTO conta_pagar (id, data_vencimento, valor, descricao, situacao, data_cadastro)
VALUES ('a0000000-0000-0000-0000-000000000003', '2025-08-05', 300.00, 'Aluguel Agosto', 'PENDENTE', NOW());

INSERT INTO conta_pagar (id, data_vencimento, valor, descricao, situacao, data_cadastro)
VALUES ('a0000000-0000-0000-0000-000000000004', '2025-07-20', 50.00, 'Assinatura Internet', 'PENDENTE', NOW());

INSERT INTO conta_pagar (id, data_vencimento, valor, descricao, situacao, data_cadastro, data_pagamento)
VALUES ('a0000000-0000-0000-0000-000000000005', '2025-07-05', 120.00, 'Conta de Gás Julho', 'PAGA', NOW(), '2025-07-03');

INSERT INTO conta_pagar (id, data_vencimento, valor, descricao, situacao, data_cadastro)
VALUES ('a0000000-0000-0000-0000-000000000006', '2025-09-01', 400.00, 'Parcela Empréstimo', 'PENDENTE', NOW());
