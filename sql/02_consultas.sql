-- 1. Acervo completo
SELECT codigo, titulo, tipo, disponivel FROM item ORDER BY codigo;

-- 2. Emprestimos em aberto
SELECT u.nome AS usuario, i.titulo AS item, e.data_retirada, e.data_devolucao_prevista
FROM emprestimo e
JOIN usuario u ON u.id = e.usuario_id
JOIN item i ON i.id = e.item_id
WHERE e.data_devolucao IS NULL;

-- 3. Multas acumuladas por usuario (inclusive usuarios sem multas)
SELECT u.nome, COALESCE(SUM(e.valor_multa), 0) AS total_multas
FROM usuario u
LEFT JOIN emprestimo e ON e.usuario_id = u.id
GROUP BY u.id, u.nome
ORDER BY u.nome;

-- 4. Itens nunca emprestados
SELECT i.codigo, i.titulo
FROM item i
LEFT JOIN emprestimo e ON e.item_id = i.id
WHERE e.id IS NULL
ORDER BY i.codigo;
