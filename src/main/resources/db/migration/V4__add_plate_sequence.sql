-- tabela que guarda o último índice emitido por província
CREATE TABLE plate_sequence (
    province_code    VARCHAR(50) PRIMARY KEY,
    last_index  BIGINT      NOT NULL DEFAULT -1 
);