INSERT INTO conta_pagar (id, data_vencimento, valor, descricao, situacao, data_cadastro)
VALUES ('1a2b3c4d-e5f6-7890-1234-567890abcdef'::uuid, '2025-07-01', 500.00, 'Conta PENDENTE para Atualizacao', 'PENDENTE', NOW());

INSERT INTO conta_pagar (id, data_vencimento, valor, descricao, situacao, data_cadastro, data_pagamento)
VALUES ('f1e2d3c4-b5a6-7890-1234-567890abcdef'::uuid, '2025-06-15', 300.00, 'Conta PAGA para Teste de Regra', 'PAGA', NOW(), '2025-06-10');
