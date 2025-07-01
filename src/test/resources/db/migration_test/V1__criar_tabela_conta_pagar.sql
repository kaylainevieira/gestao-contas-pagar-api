CREATE TABLE conta_pagar (
    id UUID PRIMARY KEY,
    data_vencimento DATE NOT NULL,
    data_pagamento DATE,
    valor DECIMAL(19, 2) NOT NULL,
    descricao VARCHAR(255) NOT NULL,
    situacao VARCHAR(50) NOT NULL,
    data_cadastro TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_data_vencimento_descricao ON conta_pagar (data_vencimento, descricao);
CREATE INDEX idx_data_pagamento_situacao ON conta_pagar (data_pagamento, situacao);
CREATE INDEX idx_situacao ON conta_pagar (situacao);
