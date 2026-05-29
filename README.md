# 🚗 LECATRO - Sistema de Gestão e Emissão de Matrículas de Veículos

<div align="center">

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.x-brightgreen?style=for-the-badge&logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-18-blue?style=for-the-badge&logo=postgresql)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-3.x-green?style=for-the-badge)
![Bootstrap](https://img.shields.io/badge/Bootstrap-5.3-purple?style=for-the-badge&logo=bootstrap)
![Status](https://img.shields.io/badge/Status-Em%20Desenvolvimento-yellow?style=for-the-badge)

**Sistema web institucional para gestão e emissão de matrículas de veículos,  
desenvolvido para o contexto moçambicano com foco em boas práticas e arquitetura limpa.**

</div>

---

## 📋 Índice

- [Sobre o Projecto](#-sobre-o-projecto)
- [Funcionalidades](#-funcionalidades)
- [Arquitetura e Stack](#-arquitetura-e-stack)
- [Modelo de Dados](#-modelo-de-dados)
- [Regras de Negócio](#-regras-de-negócio)
- [Segurança](#-segurança)
- [Como Executar](#-como-executar)
- [Estrutura do Projecto](#-estrutura-do-projecto)
- [Decisões Técnicas](#-decisões-técnicas)
- [Autor](#-autor)

---

## 📌 Sobre o Projecto

O **LECATRO** é um sistema web institucional desenvolvido para centralizar e digitalizar o processo de emissão e gestão de matrículas de veículos, substituindo fluxos manuais em papel por um processo rastreável, seguro e auditável.

O sistema foi concebido para operar em balcões de atendimento presencial, com dois perfis de utilizador distintos : **Administrador** e **Operador**, e um fluxo de negócio rigoroso que garante que nenhuma matrícula é emitida sem o devido registo e confirmação de pagamento.

> Projecto desenvolvido de forma independente com foco em **arquitectura limpa**, **boas práticas de engenharia de software** e **escalabilidade**, aplicando padrões usados em sistemas empresariais reais.

---

## ✅ Funcionalidades

### Gestão de Entidades
- ✅ CRUD completo de **Proprietários** com validação de NUIT moçambicano (9 algarismos)
- ✅ CRUD completo de **Veículos** com validação de número de chassis (VIN — 17 caracteres)
- ✅ CRUD completo de **Utilizadores** com controlo de acesso por função

### Fluxo Principal
- ✅ **Abertura de Pedido** com validação antecipada de matrícula activa e pedido em curso
- ✅ **Registo de Pagamento** com método (M-Pesa, e-Mola, Transferência Bancária)
- ✅ **Confirmação/Rejeição de Pagamento** pelo operador
- ✅ **Emissão Automática de Matrícula** com gerador sequencial (`AAA 000 XX → ZZZ 999 XX`)
- ✅ **Cancelamento de Pedido** com registo automático no histórico

### Rastreabilidade
- ✅ **Histórico completo** de cada pedido (registo, pagamento, cancelamento, emissão)
- ✅ **Notificações por email** ao proprietário em cada transição de estado (Gmail SMTP / Mailtrap)
- ✅ Envio assíncrono de emails com `@Async` — falha de email nunca bloqueia o fluxo principal

### Pesquisa e Relatórios
- ✅ **Paginação** em todas as listagens com filtros persistentes
- ✅ **Filtros combinados** em pedidos (ano, mês, estado) com estatísticas por período
- ✅ **Pesquisa por texto** em proprietários, veículos, matrículas e utilizadores
- ✅ **Exportação PDF** de todas as listagens com identidade visual do sistema
- ✅ **Certificado de Matrícula** em PDF

### UX e Segurança
- ✅ Interface **totalmente responsiva** com sidebar adaptável a mobile
- ✅ **Bloqueio de conta** após 3 tentativas de login falhadas (15 minutos)
- ✅ Tooltips, confirmações e mensagens de erro contextuais

---

## 🏗️ Arquitetura e Stack

```
┌─────────────────────────────────────────────┐
│             Spring MVC + Thymeleaf          │
│              (Server-Side Rendering)        │
├─────────────────────────────────────────────┤
│         Spring Security (Stateful)          │
│     BCrypt · Sessions · Role-based Access   │
├─────────────────────────────────────────────┤
│              Service Layer                  │
│        Interfaces + Implementações          │
├─────────────────────────────────────────────┤
│         Spring Data JPA + Hibernate         │
│          Flyway · Sequences · UUID          │
├─────────────────────────────────────────────┤
│              PostgreSQL 18                  │
└─────────────────────────────────────────────┘
```

### Stack Técnica

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 21 |
| Framework | Spring Boot 3.5.x |
| Persistência | Spring Data JPA + Hibernate 6 |
| Base de Dados | PostgreSQL 18 |
| Migrações | Flyway |
| Frontend | Thymeleaf + Bootstrap 5.3 |
| Segurança | Spring Security 6 (Stateful) |
| Mapeamento | MapStruct |
| Boilerplate | Lombok |
| PDF | OpenPDF 2.0.3 |
| Email | Spring Mail (Gmail SMTP / Mailtrap) |
| Build | Maven |
| IDE | Spring Tool Suite 4 |

---

## 🗃️ Modelo de Dados

```
User ──────────────────────────────────┐
                                       │ N
Owner ──────┐                          ▼
            │ N              ┌──────────────────┐
            └───────────────►│     Request      │◄──────────── History (N)
                             └──────────────────┘
Vehicle ────┘ N               │ 1          │ 1
                              ▼            ▼
                           Payment    LicensePlate
```

### Entidades Principais

| Entidade | Descrição |
|---|---|
| `User` | Operadores e administradores do sistema |
| `Owner` | Proprietários de veículos |
| `Vehicle` | Veículos a matricular |
| `Request` | Pedido de matrícula — orquestra todo o fluxo |
| `Payment` | Pagamento associado ao pedido |
| `LicensePlate` | Matrícula emitida |
| `History` | Log de eventos por pedido |
| `PlateSequence` | Sequência de matrículas por província |

### Decisões de Identificação

Todas as entidades utilizam **dual identifier strategy**:
- `id` (Long) — chave primária interna, usada em joins e performance
- `publicId` (UUID) — exposto em URLs e comunicações externas, nunca o id interno

---

## 📐 Regras de Negócio

### Formato da Matrícula
```
ABC 123 MC
│││ │││ ││
│││ │││ └─ Código da província do operador que processou
│││ └───── Sequência numérica (000–999)
└───────── Sequência de letras (AAA–ZZZ)
```

### Gerador Sequencial
- Cada província tem a sua própria sequência independente
- Começa em `AAA 000 XX` e progride até `ZZZ 999 XX`
- **Capacidade:** 26³ × 1000 = **17.576.000 matrículas por província**
- Bloqueio pessimista (`PESSIMISTIC_WRITE`) garante unicidade em ambiente concorrente

### Fluxo de Estados do Pedido
```
PENDING → PAID → ISSUED
    └──────────────→ CANCELLED
```

### Validações Implementadas
- NUIT: exactamente 9 algarismos numéricos (`\d{9}`)
- Chassis: 17 caracteres VIN (`[A-HJ-NPR-Z0-9]{17}`) — I, O, Q excluídos
- Ano de fabrico: mínimo 1900, máximo = ano actual (validação dinâmica)
- Um veículo não pode ter mais de uma matrícula activa
- Um veículo não pode ter mais de um pedido em curso (PENDING ou PAID)

---

## 🔐 Segurança

- Autenticação **stateful** com sessões HTTP
- Senhas codificadas com **BCrypt** (salt automático)
- **Controlo de acesso por role** em rotas e métodos (`@PreAuthorize`)
- `ADMIN` herda as permissões de `OPERATOR`
- **Bloqueio de conta** após 3 tentativas falhadas (15 minutos), com registo persistente na BD
- **CSRF activo** — obrigatório em autenticação stateful; o Thymeleaf injeta o token automaticamente
- Credenciais nunca expostas no código — sempre via variáveis de ambiente (`${DB_URL}`, `${MAIL_PASSWORD}`)

---

## 🚀 Como Executar

### Pré-requisitos
- Java 21+
- PostgreSQL 18
- Maven 3.9+

### Configuração

1. **Clonar o repositório**
```bash
git clone https://github.com/aderitoandala/lecatro.git
cd lecatro
```

2. **Criar a base de dados**
```sql
CREATE DATABASE db_lecatro;
```

3. **Definir as variáveis de ambiente**
```bash
# Base de dados (application-dev.yml usa valores por defeito)
# Em produção, definir:
export DB_URL=jdbc:postgresql://localhost:5432/lecatro_prod
export DB_USERNAME=postgres
export DB_PASSWORD=your_password

# Email (opcional em dev)
export MAIL_USERNAME=your_email@gmail.com
export MAIL_PASSWORD=your_app_password
```

4. **Executar**
```bash
mvn spring-boot:run
```

O Flyway cria automaticamente todas as tabelas na primeira execução.

5. **Aceder ao sistema**
```
http://localhost:8080/login
```

> Crie o primeiro utilizador ADMIN directamente na base de dados com a senha codificada em BCrypt.

---

## 📁 Estrutura do Projecto

```
com.dery.lecatro
│
├── config/          # SecurityConfig, CustomUserDetails, LoginHandlers
├── controller/      # Controllers MVC
├── service/         # Interfaces de serviço
│   └── impl/        # Implementações
├── repository/      # Interfaces Spring Data JPA
├── entity/          # Entidades JPA
│   └── enums/       # Role, Province, RequestStatus, PaymentStatus...
├── dto/
│   ├── request/     # DTOs de entrada com validações
│   └── response/    # DTOs de saída
├── mapper/          # Conversão Entity ↔ DTO (MapStruct)
├── exception/       # Excepções customizadas + GlobalExceptionHandler
└── util/            # LicensePlateGenerator, PdfGenerator
```

---

## 💡 Decisões Técnicas

| Decisão | Justificação |
|---|---|
| Long interno + UUID público | Performance em joins + segurança na exposição de IDs |
| Flyway em vez de `ddl-auto: create` | Controlo total das migrações em produção |
| Perfis dev/prod separados | Credenciais reais nunca em desenvolvimento |
| `@Async` no email | Falha de SMTP nunca bloqueia o fluxo de negócio |
| Bloqueio pessimista no gerador | Unicidade garantida em ambiente concorrente |
| MapStruct em vez de conversão manual | Geração em tempo de compilação, sem overhead em runtime |
| Spring Security stateful + CSRF | Adequado para sistema MVC com sessões — mais seguro que JWT para este contexto |
| OpenPDF 2.0.3 em vez de 3.x | API `com.lowagie` estável; 3.x mudou pacote para `org.openpdf` — migraria todo o código |
| Downgrade Boot 3.5.x | Boot 4.x com incompatibilidade activa no Flyway na altura do desenvolvimento |

---

## 👨‍💻 Autor

**Adérito Andala**  
Backend Developer · Maputo, Moçambique

[![GitHub](https://img.shields.io/badge/GitHub-aderitoandala-black?style=flat&logo=github)](https://github.com/aderitoandala)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-aderitoandala-blue?style=flat&logo=linkedin)](https://linkedin.com/in/aderitoandala)
[![Email](https://img.shields.io/badge/Email-aderitoandala@gmail.com-red?style=flat&logo=gmail)](mailto:aderitoandala@gmail.com)

---

<div align="center">

*Desenvolvido com foco em qualidade, boas práticas e contexto real moçambicano.*

</div>
