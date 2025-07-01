INSERT INTO conta_pagar (id, data_vencimento, valor, descricao, situacao, data_cadastro)
VALUES ('c264a982-f19b-4394-bb9e-319f6f3933c0'::uuid, '2025-07-30', 100.00, 'Conta de Teste para Alterar Situação', 'PENDENTE', NOW());

INSERT INTO conta_pagar (id, data_vencimento, valor, descricao, situacao, data_cadastro, data_pagamento)
VALUES ('a1b2c3d4-e5f6-7890-1234-567890abcdef'::uuid, '2025-08-15', 200.00, 'Conta PAGA para Teste de Regra', 'PAGA', NOW(), '2025-08-10');