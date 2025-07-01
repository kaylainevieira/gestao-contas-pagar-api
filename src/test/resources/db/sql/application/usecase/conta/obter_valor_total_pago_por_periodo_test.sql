-- Contas pagas para teste de soma
INSERT INTO conta_pagar (id, data_vencimento, valor, descricao, situacao, data_cadastro, data_pagamento)
VALUES ('d0000000-0000-0000-0000-000000000001', '2025-07-10', 100.00, 'Luz Julho Paga', 'PAGA', NOW(), '2025-07-05');

INSERT INTO conta_pagar (id, data_vencimento, valor, descricao, situacao, data_cadastro, data_pagamento)
VALUES ('d0000000-0000-0000-0000-000000000002', '2025-07-15', 200.00, 'Água Julho Paga', 'PAGA', NOW(), '2025-07-10');

INSERT INTO conta_pagar (id, data_vencimento, valor, descricao, situacao, data_cadastro, data_pagamento)
VALUES ('d0000000-0000-0000-0000-000000000003', '2025-08-05', 300.00, 'Aluguel Agosto Paga', 'PAGA', NOW(), '2025-08-01');

-- Conta pendente (não deve ser somada)
INSERT INTO conta_pagar (id, data_vencimento, valor, descricao, situacao, data_cadastro)
VALUES ('d0000000-0000-0000-0000-000000000004', '2025-07-20', 50.00, 'Internet Pendente', 'PENDENTE', NOW());

-- Conta paga fora do período (não deve ser somada)
INSERT INTO conta_pagar (id, data_vencimento, valor, descricao, situacao, data_cadastro, data_pagamento)
VALUES ('d0000000-0000-0000-0000-000000000005', '2025-06-01', 75.00, 'Conta Antiga Paga', 'PAGA', NOW(), '2025-05-25');
