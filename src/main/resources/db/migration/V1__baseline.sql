-- Baseline migration: temel uzantılar ve kontrol tablosu
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS app_migration_baseline (
    id SMALLINT PRIMARY KEY,
    note TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

INSERT INTO app_migration_baseline (id, note)
VALUES (1, 'baseline')
ON CONFLICT (id) DO NOTHING;
