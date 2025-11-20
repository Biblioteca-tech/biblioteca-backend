CREATE DATABASE db_biblioteca;
USE db_biblioteca;

drop database db_biblioteca;
SHOW DATABASES;
SHOW TABLES;

-- =====================
-- TABELA: USUARIO (classe abstrata)
-- =====================
CREATE TABLE usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255),
    email VARCHAR(255) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL,
    data_nascimento DATE,
    cpf VARCHAR(14) UNIQUE,
    status_cliente ENUM('ATIVO', 'INATIVO') DEFAULT 'ATIVO'
);
select * from usuario;

-- =====================
-- TABELA: USUARIO_ROLES (coleção de roles)
-- =====================
CREATE TABLE usuario_roles (
    usuario_id BIGINT NOT NULL,
    role VARCHAR(100) NOT NULL,
    PRIMARY KEY (usuario_id, role),
    FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);

-- =====================
-- TABELA: CLIENTE
-- =====================
CREATE TABLE cliente (
    id BIGINT PRIMARY KEY,
    FOREIGN KEY (id) REFERENCES usuario(id)
);

-- =====================
-- TABELA: FUNCIONARIO
-- =====================
CREATE TABLE funcionario (
    id BIGINT PRIMARY KEY,
    data_admissao DATE,
    numero_telefone VARCHAR(20),
    endereco VARCHAR(255),
    FOREIGN KEY (id) REFERENCES usuario(id)
);

-- =====================
-- TABELA: ADMINISTRADOR
-- =====================
CREATE TABLE administrador (
    id BIGINT PRIMARY KEY,
    FOREIGN KEY (id) REFERENCES usuario(id)
);

-- =====================
-- TABELA: LIVRO
-- =====================
CREATE TABLE livro (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    autor VARCHAR(255),
    editora VARCHAR(255),
    ano_publicacao INT,
    genero VARCHAR(100),
    sinopse TEXT,
    idioma VARCHAR(50),
    preco DECIMAL(10,2),
    capa_path VARCHAR(255),
    pdf_path VARCHAR(255),
    status_livro ENUM('ATIVO', 'INATIVO') DEFAULT 'ATIVO'
);

-- =====================
-- TABELA: COMENTARIO
-- =====================
CREATE TABLE comentario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    texto VARCHAR(1000) NOT NULL,
    data_criacao DATETIME DEFAULT CURRENT_TIMESTAMP,
    livro_id BIGINT NOT NULL,
    autor_id BIGINT NOT NULL,
    FOREIGN KEY (livro_id) REFERENCES livro(id),
    FOREIGN KEY (autor_id) REFERENCES usuario(id)
);

-- =====================
-- TABELA: ALUGUEL
-- =====================
CREATE TABLE aluguel (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente_id BIGINT,
    livro_id BIGINT,
    data_aluguel DATETIME,
    data_devolucao DATETIME,
    valor_aluguel DECIMAL(10,2),
    estadoRegistro ENUM('ATIVO', 'DEVOLVIDO', 'ATRASADO', 'CANCELADO'),
    FOREIGN KEY (cliente_id) REFERENCES cliente(id),
    FOREIGN KEY (livro_id) REFERENCES livro(id)
);

-- =====================
-- TABELA: VENDAS
-- =====================
CREATE TABLE vendas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente_id BIGINT,
    livro_id BIGINT,
    valor DECIMAL(10,2),
    data_venda DATETIME,
    FOREIGN KEY (cliente_id) REFERENCES cliente(id),
    FOREIGN KEY (livro_id) REFERENCES livro(id)
);

select * from administrador;




