# BibliotecaRestClient
Sistema de biblioteca feito com dois micro serviços em Quarkus, que se comunicam por REST Client.

- **catalog** (porta 9080): cuida dos livros.
- **loans** (porta 9081): cuida dos usuários, do login com JWT e dos empréstimos. Para emprestar e devolver, chama o catalog.

Os dados ficam em memória e se perdem quando o serviço é parado.

## Endpoints

catalog:

- POST /books: cadastra um livro
- GET /books: lista os livros (pode filtrar com `?title=`)
- GET /books/{id}: busca um livro
- PUT /books/{id}/loan: marca como emprestado
- PUT /books/{id}/return: marca como devolvido

loans:

- POST /users: cadastra um usuário
- POST /auth/login: faz login e devolve o token
- GET /loans/books: lista os livros do catalog
- GET /loans: lista os empréstimos (pode filtrar com `?borrower=`)
- POST /loans: cria um empréstimo
- PUT /loans/{id}/return: devolve um empréstimo

Os endpoints de `/loans` exigem o token do login, enviado no cabeçalho `Authorization: Bearer <token>`.
