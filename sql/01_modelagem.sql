-- Execute em um banco PostgreSQL de testes.
DROP TABLE IF EXISTS emprestimo;
DROP TABLE IF EXISTS item;
DROP TABLE IF EXISTS usuario;

CREATE TABLE item (
    id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    codigo VARCHAR(30) NOT NULL UNIQUE,
    titulo VARCHAR(200) NOT NULL,
    tipo VARCHAR(10) NOT NULL CHECK (tipo IN ('livro', 'revista')),
    autor VARCHAR(150),
    edicao VARCHAR(50),
    disponivel BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE usuario (
    id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    tipo VARCHAR(15) NOT NULL CHECK (tipo IN ('aluno', 'professor')),
    limite_itens INTEGER NOT NULL CHECK (limite_itens > 0),
    CONSTRAINT limite_por_tipo CHECK (
        (tipo = 'aluno' AND limite_itens = 3) OR
        (tipo = 'professor' AND limite_itens = 5)
    )
);

CREATE TABLE emprestimo (
    id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    item_id INTEGER NOT NULL REFERENCES item(id),
    usuario_id INTEGER NOT NULL REFERENCES usuario(id),
    data_retirada DATE NOT NULL,
    data_devolucao_prevista DATE NOT NULL,
    data_devolucao DATE,
    valor_multa NUMERIC(10,2) NOT NULL DEFAULT 0 CHECK (valor_multa >= 0),
    CHECK (data_devolucao_prevista >= data_retirada),
    CHECK (data_devolucao IS NULL OR data_devolucao >= data_retirada)
);

INSERT INTO item (codigo, titulo, tipo, autor, edicao, disponivel) VALUES
('L001', 'Dom Casmurro', 'livro', 'Machado de Assis', '1a', FALSE),
('L002', 'O Cortico', 'livro', 'Aluisio Azevedo', '2a', TRUE),
('R001', 'Ciencia Hoje', 'revista', 'Equipe editorial', '2026-01', TRUE),
('R002', 'Superinteressante', 'revista', 'Equipe editorial', '2026-02', TRUE);

INSERT INTO usuario (nome, tipo, limite_itens) VALUES
('Ana', 'aluno', 3),
('Carlos', 'professor', 5);

INSERT INTO emprestimo (item_id, usuario_id, data_retirada, data_devolucao_prevista, data_devolucao, valor_multa) VALUES
((SELECT id FROM item WHERE codigo = 'L001'), (SELECT id FROM usuario WHERE nome = 'Ana'), '2026-11-10', '2026-11-24', NULL, 0),
((SELECT id FROM item WHERE codigo = 'R001'), (SELECT id FROM usuario WHERE nome = 'Carlos'), '2026-10-01', '2026-10-08', '2026-10-10', 2.00);
