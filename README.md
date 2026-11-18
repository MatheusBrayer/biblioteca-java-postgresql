# Sistema de Biblioteca — Java POO + PostgreSQL

## Exercicios da lista
- **1 (POO):** classes abstratas, Livro/Revista, Aluno/Professor, `Biblioteca` com arrays fixos, emprestar/devolver/listar, teste de limite (menu opcao 10). Essa demonstracao roda sem banco.
- **2 (SQL):** `sql/01_modelagem.sql` cria as tres tabelas e insere 4 itens, 2 usuarios e 2 emprestimos.
- **3 (SQL):** `sql/02_consultas.sql` traz as quatro consultas solicitadas. Tambem disponiveis no menu 6 a 9.
- **Extra:** JDBC, menu e operacoes persistentes com transacoes no PostgreSQL.

## Executar
1. Requisitos: JDK 17+, Maven e PostgreSQL. Crie um banco chamado `biblioteca` no DBeaver (ou use `CREATE DATABASE biblioteca;` conectado ao banco `postgres`).
2. Conecte o DBeaver ao banco `biblioteca` e execute `sql/01_modelagem.sql` **uma vez**. ATENCAO: ele apaga as tres tabelas existentes para reinicializar os testes. Execute depois `sql/02_consultas.sql`.
3. Configure as variaveis de ambiente `DB_URL`, `DB_USER`, `DB_PASSWORD`. Padroes: `jdbc:postgresql://localhost:5432/biblioteca`, `postgres`, senha vazia.
4. Na pasta do projeto: `mvn compile exec:java`.
5. No menu, `10` executa o teste POO com arrays; `1` a `9` usam o banco.

**Observacoes:** `codigo` no banco e texto (`L001`, `R001` etc.). Para novos cadastros via menu use numeros (`101`, `102`...), pois a demonstracao Java usa codigo numerico. Para emprestar/devolver, informe o codigo como salvo no banco. IDs dos usuarios de teste normalmente sao 1 (Ana) e 2 (Carlos) apos executar o script de inicializacao. `listarAcervo` exibe no maximo 1000 registros, por usar array fixo de exibicao.

Nao e necessario conectar Java ao banco para atender a lista; a integracao e uma funcionalidade adicional. Nao use o script de inicializacao em banco com dados importantes.
