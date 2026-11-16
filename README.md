# BibliotecaRestClient
Sistema de biblioteca feito com dois micro serviços em Quarkus, que se comunicam por REST Client.

- **catalog** (porta 9080): cuida dos livros.
- **loans** (porta 9081): cuida dos usuários, do login com JWT e dos empréstimos. Para emprestar e devolver, chama o catalog.

Os dados ficam em memória e se perdem quando o serviço é parado.

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

## Funcionalidades

No catalog: cadastrar, listar, buscar livros por título, emprestar e devolver.

No loans: cadastrar usuário, fazer login, criar e devolver empréstimo e listar empréstimos de uma pessoa. As operações de empréstimo exigem o token do login, enviado no cabeçalho `Authorization: Bearer <token>`.
