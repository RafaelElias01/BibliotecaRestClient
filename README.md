# BibliotecaRestClient

Trabalho de Programação para Web II. Sistema de biblioteca feito com dois micro serviços em Quarkus, que se comunicam por REST Client.

## Micro serviços

- **catalog** (porta 9080): cuida do acervo de livros.
- **loans** (porta 9081): cuida dos usuários, do login (JWT) e dos empréstimos. Para emprestar ou devolver, chama o catalog.

Os dados ficam em memória e são perdidos quando o serviço é parado.

## Como rodar

Precisa do Java 21 e do Maven. Em dois terminais, começando pelo catalog:

```
cd catalog
mvn quarkus:dev
```

```
cd loans
mvn quarkus:dev
```

## Endpoints

### catalog

| Método | Caminho | Descrição |
|--------|---------|-----------|
| POST | /books | Cadastra um livro |
| GET | /books | Lista os livros (filtro opcional: `?title=`) |
| GET | /books/{id} | Busca um livro |
| PUT | /books/{id}/loan | Marca o livro como emprestado |
| PUT | /books/{id}/return | Marca o livro como devolvido |

### loans

| Método | Caminho | Descrição |
|--------|---------|-----------|
| POST | /users | Cadastra um usuário |
| POST | /auth/login | Login, devolve o token JWT |
| GET | /loans/books | Lista os livros do catalog (precisa de token) |
| GET | /loans | Lista os empréstimos (filtro opcional: `?borrower=`) (precisa de token) |
| POST | /loans | Cria um empréstimo (precisa de token) |
| PUT | /loans/{id}/return | Devolve um empréstimo (precisa de token) |

O token do login deve ser enviado no cabeçalho `Authorization: Bearer <token>`.
