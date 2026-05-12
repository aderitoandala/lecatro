-- Garante que a funcao de geracao de UUIDs esta activa
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

--define valor padrao do id publico
ALTER TABLE users 
ALTER COLUMN public_id SET DEFAULT gen_random_uuid();