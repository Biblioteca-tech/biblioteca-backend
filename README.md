# 📚 Biblioteca Tech

Uma plataforma de **biblioteca online** moderna, que une **empréstimo (alocação)** e **venda de livros**, proporcionando praticidade tanto para leitores quanto para compradores.  
O projeto foi desenvolvido em **Java (Spring Boot)** no backend e está hospedado na **Google Cloud**, garantindo escalabilidade e alta disponibilidade.

---

## 🚀 Funcionalidades

- 🔍 **Consulta de livros** por título, autor ou gênero
- 📖 **Alocação de livros** (empréstimo online)
- 🛒 **Venda de livros** com controle de estoque
- 👤 **Cadastro e login de usuários**
- 🗂️ **Gerenciamento de catálogo** de livros

---

## 🛠️ Tecnologias Utilizadas

- **Java 21+**
- **Spring Boot** (REST APIs)
- **Spring Security + JWT**
- **Google Cloud Platform (GCP)** para hospedagem e execução
- **Git/GitHub** para versionamento e colaboração
- **Banco de Dados** (MySQL)

---

## ☁️ Arquitetura

O sistema segue o padrão **MVC (Model-View-Controller)**, com APIs REST no backend.  
Toda a aplicação roda na **nuvem do Google Cloud**, permitindo fácil escalabilidade e integração futura com serviços como **Cloud SQL** e **Cloud Storage**.

---

## 📦 Como rodar localmente

### Pré-requisitos
- Java 21+ instalado
- Maven configurado
- Banco de dados configurado (MySQL)

### Passos
```bash
# Clone o repositório
git clone https://github.com/Biblioteca-tech/biblioteca-backend.git

# Acesse a pasta do projeto
cd biblioteca-backend

# Compile o projeto
mvn clean install

# Rode a aplicação
mvn spring-boot:run


```
# Desenvolvido por:
Projeto desenvolvido com foco em boas práticas, segurança, arquitetura escalável e aprendizado contínuo.
