# Bazer API

Bazer é uma plataforma de marketplace B2B no modelo atacado. Lojistas (VENDEDORs) cadastram produtos e o Bazer funciona como intermediário: coleta os pedidos, repassa ao vendedor e cobra uma comissão por pedido. O comprador faz o pedido pelo app, paga via PIX e acompanha a entrega com rastreio gerado pelo Melhor Envio.

---

## Stack

- Java 21 + Spring Boot 4.0.5
- Spring Security 7 com JWT + hierarquia de roles
- JPA/Hibernate + MySQL
- Maven
- SDK Mercado Pago 2.1.24 (PIX)
- Melhor Envio REST API (frete + etiqueta, OAuth2)
- Spring Mail (notificações por email, Gmail SMTP)
- SpringDoc OpenAPI — Swagger UI em `/swagger-ui.html`

---

## Setup — passo a passo

### 1. Pré-requisitos

- [Java 21](https://adoptium.net)
- [Maven](https://maven.apache.org/download.cgi)
- [MySQL 8+](https://dev.mysql.com/downloads/mysql/)

### 2. Criar o banco de dados

Conecte no MySQL e execute:

```sql
CREATE DATABASE bdbazer
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```

O Hibernate cria todas as tabelas automaticamente na primeira vez que a aplicação sobe (`ddl-auto=update`).

### 3. Criar o `application.properties`

O arquivo não está no git por segurança. Crie em `src/main/resources/application.properties` com o conteúdo da seção **Configuração** abaixo.

### 4. (Opcional) Configurar ngrok para webhooks

Se for testar com Mercado Pago ou Melhor Envio reais (não mock), você precisa de uma URL pública para receber webhooks. Instale o [ngrok](https://ngrok.com) e rode em um terminal separado:

```bash
ngrok http 8080
```

Copie a URL gerada (ex: `https://abc123.ngrok-free.app`) e coloque nas propriedades:
- `mercadopago.notification-url=https://abc123.ngrok-free.app/payments/webhook/mercadopago`
- `melhorenvio.redirect-uri=https://abc123.ngrok-free.app/auth/callback`

> A URL muda a cada reinício do ngrok (plano free). Atualize o `application.properties` e o painel do Melhor Envio sempre que reiniciar.

### 5. Subir a aplicação

```bash
mvn spring-boot:run
```

### 6. Acessar o Swagger

```
http://localhost:8080/swagger-ui.html
```

O Swagger lista todos os endpoints com exemplos de request/response. Para testar endpoints autenticados:

1. Crie um perfil via `POST /profiles` (público, sem auth)
2. Faça login via `POST /usuarios/login` — você receberá um `{ "token": "eyJ..." }`
3. Clique em **Authorize** (canto superior direito do Swagger)
4. Digite: `Bearer eyJ...` (com o prefixo "Bearer ")
5. Confirme — todos os cadeados fecham e as chamadas passam a incluir o JWT automaticamente

> Dica: crie um perfil ADMIN primeiro para ter acesso total na exploração inicial.

---

## Roles e hierarquia

```
ADMIN > VENDEDOR
ADMIN > COMPRADOR
```

O ADMIN herda todas as permissões de VENDEDOR e COMPRADOR. Ao criar um perfil, escolha o `role` correspondente.

---

## Configuração

O arquivo `application.properties` não está no git (`.gitignore` por segurança). Crie em `src/main/resources/application.properties`:

```properties
# ─── Banco de dados ───────────────────────────────────────────────────────────
# Crie o banco antes: CREATE DATABASE bdbazer DEFAULT CHARACTER SET utf8mb4;
spring.datasource.url=jdbc:mysql://localhost:3306/bdbazer
spring.datasource.username=root
spring.datasource.password=SUA_SENHA_MYSQL

# Cria/atualiza tabelas automaticamente — não usar 'create-drop' em produção
spring.jpa.hibernate.ddl-auto=update

# ─── JWT ──────────────────────────────────────────────────────────────────────
# Qualquer string longa e aleatória serve como secret
api.security.token.secret=mude-para-uma-string-longa-e-aleatoria-aqui

# ─── Upload de imagens ────────────────────────────────────────────────────────
# Pasta onde as fotos de perfil e imagens de produto são salvas
file.upload-dir=uploads/

# ─── Mercado Pago ─────────────────────────────────────────────────────────────
# Obtenha em: mercadopago.com.br/developers → Suas integrações → Credenciais
# Use credenciais TEST-... para sandbox, APP_USR-... para produção
mercadopago.access-token=TEST-SEU_ACCESS_TOKEN
mercadopago.public-key=TEST-SUA_PUBLIC_KEY

# URL pública que o MP chama quando o pagamento é confirmado (requer ngrok em dev)
mercadopago.notification-url=https://SEU_NGROK/payments/webhook/mercadopago

# true = PIX simulado, sem chamar o MP (recomendado em desenvolvimento)
mercadopago.mock-enabled=true

# ─── Melhor Envio ─────────────────────────────────────────────────────────────
# Obtenha em: melhorenvio.com.br → Gerenciar → Tokens de acesso → Criar aplicação
melhorenvio.client-id=SEU_CLIENT_ID
melhorenvio.client-secret=SEU_CLIENT_SECRET

# URL de callback do OAuth2 — deve ser cadastrada na aplicação dentro do painel ME
melhorenvio.redirect-uri=https://SEU_NGROK/auth/callback

# sandbox para testes, https://melhorenvio.com.br para produção
melhorenvio.base-url=https://sandbox.melhorenvio.com.br

# true = retorna MOCK-TRACK-{id}, sem chamar o ME (recomendado em desenvolvimento)
melhorenvio.mock-enabled=true

# ─── Email (Gmail SMTP) ───────────────────────────────────────────────────────
# Usado para notificar admins quando um pagamento é aprovado
# Para gerar uma App Password no Gmail: myaccount.google.com/apppasswords
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=SEU_EMAIL@gmail.com
spring.mail.password=SUA_APP_PASSWORD_16_DIGITOS
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

### Mock mode (desenvolvimento)

Com ambos os mocks ativos, o fluxo completo funciona sem nenhuma conta externa:
- **Mercado Pago** (`mock-enabled=true`): `POST /payments/pix/{id}` retorna um PIX estático simulado sem chamar a API do MP
- **Melhor Envio** (`mock-enabled=true`): `POST /orders/{id}/ship` retorna `MOCK-TRACK-{orderId}` sem chamar a API ME

**Para produção:** setar ambos para `false`, usar credenciais `APP_USR-...` do MP e `base-url=https://melhorenvio.com.br`.

---

## Configuração do Melhor Envio (OAuth2)

O Melhor Envio usa OAuth2. Após subir a aplicação:

1. Faça login com um ADMIN no Swagger e copie o JWT
2. Chame `GET /melhorenvio/auth-url` para obter a URL de autorização
3. Acesse a URL no browser e autorize o app "Bazer"
4. O callback é tratado automaticamente em `/auth/callback` e o token é salvo no banco

O access token dura 30 dias, o refresh token 45 dias. Renovação automática quando o token está prestes a expirar.

---

## Endpoints

### Autenticação

| Método | Endpoint | Auth | Descrição |
|--------|----------|------|-----------|
| POST | `/usuarios/login` | público | Retorna JWT. Body: `{ "username": "email", "password": "senha" }` |

---

### Perfil

| Método | Endpoint | Auth | Descrição |
|--------|----------|------|-----------|
| POST | `/profiles` | público | Cadastra usuário + perfil. `multipart/form-data`. Campos: `name`, `document`, `phone`, `role` (COMPRADOR/VENDEDOR/ADMIN), `photo` (arquivo opcional). Cria automaticamente um endereço padrão. |
| GET | `/profiles/{id}` | JWT | Busca perfil por ID |
| GET | `/profiles/user/{userId}` | JWT | Busca perfil pelo ID do usuário (útil para redirecionar após login) |
| GET | `/profiles/searchStore?name=` | público | Lista lojas (VENDEDOR) filtrando por nome. Sem parâmetro retorna todas. |
| PUT | `/profiles/me` | JWT | Atualiza `name`, `document` e/ou `phone` do perfil autenticado (campos opcionais — só envia o que quiser mudar) |
| PATCH | `/profiles/{id}/commission/{commissionId}` | ADMIN | Atribui comissão a uma loja |

---

### Endereço

Todos os endpoints operam sobre o perfil autenticado — o `profileId` é resolvido pelo JWT, não precisa ser enviado no body.

| Método | Endpoint | Auth | Descrição |
|--------|----------|------|-----------|
| POST | `/addresses` | JWT | Cria endereço. Body: `{ "street", "number", "complement", "district", "city", "state", "postalCode", "country" }` |
| GET | `/addresses` | JWT | Lista endereços do perfil autenticado |
| PUT | `/addresses/{id}` | JWT | Atualiza (valida que pertence ao perfil autenticado) |
| DELETE | `/addresses/{id}` | JWT | Remove (valida que pertence ao perfil autenticado) |

---

### Produto

| Método | Endpoint | Auth | Descrição |
|--------|----------|------|-----------|
| GET | `/products` | público | Lista todos os produtos |
| GET | `/products/{id}` | público | Busca produto por ID |
| GET | `/products/search?name=` | público | Busca por nome |
| GET | `/products/category/{categoryId}` | público | Lista por categoria |
| GET | `/products/store/{storeId}` | público | Lista todos os produtos de uma loja |
| GET | `/products/most-purchased` | público | Ranking geral por quantidade vendida |
| GET | `/products/most-purchased/store/{storeId}` | público | Ranking por loja |
| POST | `/products` | JWT | Cria produto. Inclua `weight`, `width`, `height`, `length` — sem eles o checkout é bloqueado. |
| POST | `/products/{id}/images` | JWT | Adiciona imagem ao produto. `multipart/form-data`, campo `image`. |
| PUT | `/products/{id}` | JWT | Atualiza produto completo |
| DELETE | `/products/{id}` | JWT | Remove produto |

---

### Categoria

| Método | Endpoint | Auth | Descrição |
|--------|----------|------|-----------|
| GET | `/categories` | público | Lista todas as categorias |
| GET | `/categories/{id}` | público | Busca por ID |
| POST | `/categories` | ADMIN | Cria categoria. Body: `{ "name": "Eletrônicos" }` |
| PUT | `/categories/{id}` | ADMIN | Atualiza |
| DELETE | `/categories/{id}` | ADMIN | Remove |

---

### Comissão

| Método | Endpoint | Auth | Descrição |
|--------|----------|------|-----------|
| POST | `/commissions` | ADMIN | Cria tabela de comissão. Body: `{ "name": "Padrão", "rate": 0.10 }`. `rate` decimal: `0.1` = 10%. |
| GET | `/commissions` | ADMIN | Lista todas as comissões |

Após criar, atribuir à loja via `PATCH /profiles/{id}/commission/{commissionId}`.

---

### Carrinho e Pedido

#### Fluxo do comprador

| Método | Endpoint | Auth | Descrição |
|--------|----------|------|-----------|
| GET | `/cart` | JWT | Retorna carrinho ativo (PENDING). Cria automaticamente se não existir. |
| POST | `/cart/items` | JWT | Adiciona item ao carrinho. Body: `{ "productId": 1, "quantity": 2 }`. Cria carrinho se não existir. |
| DELETE | `/cart/items/{itemId}` | JWT | Remove item do carrinho |
| POST | `/cart/checkout` | JWT | Finaliza carrinho: calcula frete + comissão e transita para AGUARDANDO_PAGAMENTO. Body: `{ "postalCodeDestination": "01310100" }` |
| GET | `/orders` | JWT | Lista pedidos do comprador (excluindo PENDING) |
| GET | `/orders/{id}` | JWT | Detalhe de um pedido |
| POST | `/orders/{id}/cancel` | JWT | Comprador cancela pedido (só funciona em AGUARDANDO_PAGAMENTO) |

#### Fluxo do vendedor

| Método | Endpoint | Auth | Descrição |
|--------|----------|------|-----------|
| GET | `/orders/store` | VENDEDOR | Lista pedidos da loja do vendedor autenticado |
| POST | `/orders/{id}/process` | VENDEDOR | Inicia processamento (CONFIRMED → PROCESSING) |
| POST | `/orders/{id}/ship` | VENDEDOR | Envia pedido (PROCESSING → SHIPPED). Gera etiqueta ME e salva tracking automaticamente. |
| POST | `/orders/{id}/cancel` | VENDEDOR | Cancela pedido (funciona em AGUARDANDO_PAGAMENTO, CONFIRMED ou PROCESSING) |

#### Fluxo do admin

| Método | Endpoint | Auth | Descrição |
|--------|----------|------|-----------|
| GET | `/orders/admin/confirmed` | ADMIN | Fila de pedidos pagos aguardando processamento (CONFIRMED), ordem FIFO |
| POST | `/orders/{id}/confirm-payment` | ADMIN | Confirma pagamento manualmente (fallback — o fluxo real é via webhook MP) |

---

### Pagamento

| Método | Endpoint | Auth | Descrição |
|--------|----------|------|-----------|
| POST | `/payments/pix/{orderId}` | JWT | Gera cobrança PIX. Retorna `pixCode` (copia-e-cola), `qrCode` (base64), `ticketUrl`, `expiresAt` (30 min). Pode ser chamado novamente se o pedido estiver em PAGAMENTO_EXPIRADO. |
| GET | `/payments/pix/{orderId}` | JWT | Consulta status e dados do PIX gerado |
| POST | `/payments/webhook/mercadopago` | público | Webhook do MP. `approved` → confirma pagamento + email para admins. Configurar no painel MP. |

---

### Entrega

| Método | Endpoint | Auth | Descrição |
|--------|----------|------|-----------|
| GET | `/deliveries/order/{orderId}` | JWT | Consulta entrega: `trackingCode`, `trackingUrl`, `status`, `shippingDate`, `deliveredAt` |

---

### Melhor Envio (admin)

| Método | Endpoint | Auth | Descrição |
|--------|----------|------|-----------|
| GET | `/melhorenvio/auth-url` | ADMIN | Retorna URL OAuth2 como JSON para abrir no browser |
| GET | `/melhorenvio/authorize` | ADMIN | Redireciona diretamente para a tela OAuth2 do ME |
| GET | `/auth/callback` | público | Callback OAuth2 — troca code por token (chamado automaticamente pelo ME) |
| POST | `/melhorenvio/refresh` | ADMIN | Força renovação manual do token |
| POST | `/melhorenvio/webhook` | público | Recebe status de entrega do ME. Configurar URL no painel ME. |

---

### Analytics

| Método | Endpoint | Auth | Descrição |
|--------|----------|------|-----------|
| GET | `/analytics/store` | VENDEDOR | Dashboard da loja: faturamento total/mês, ticket médio, compradores únicos, comissão paga, pedidos por status, top 5 produtos, evolução dos últimos 6 meses |
| GET | `/analytics/admin?year=2026` | ADMIN | Dashboard da plataforma: faturamento, comissão, top 10 lojas e compradores. `year` opcional. |
| GET | `/analytics/admin/store/{profileId}` | ADMIN | Dashboard de uma loja específica (visão admin) |

---

### Avaliações de Produto

| Método | Endpoint | Auth | Descrição |
|--------|----------|------|-----------|
| POST | `/assessment-products` | JWT | Cria avaliação. Body: `{ "starQuantity": 5, "comment": "Ótimo!", "productId": 1 }` |
| GET | `/assessment-products/product/{productId}` | público | Lista avaliações de um produto |
| GET | `/assessment-products/{id}` | público | Busca por ID |
| PUT | `/assessment-products/{id}` | JWT | Atualiza avaliação completa |
| DELETE | `/assessment-products/{id}` | JWT | Remove |

---

### Avaliações de Perfil/Loja

| Método | Endpoint | Auth | Descrição |
|--------|----------|------|-----------|
| POST | `/assessment-profiles` | JWT | Cria avaliação. Body: `{ "starQuantity": 4, "comment": "Boa loja", "profileId": 2 }` |
| GET | `/assessment-profiles/profile/{profileId}` | público | Lista avaliações de um perfil |
| GET | `/assessment-profiles/{id}` | público | Busca por ID |
| PUT | `/assessment-profiles/{id}` | JWT | Atualiza **só a nota** (`starQuantity`). O comentário não pode ser editado. |
| DELETE | `/assessment-profiles/{id}` | JWT | Remove |

---

## Máquina de estados do pedido

```
PENDING (carrinho)
  └─ checkout ──────────────→ AGUARDANDO_PAGAMENTO
                                  ├─ webhook MP "approved" ───→ CONFIRMED
                                  ├─ PIX expirou (30 min) ────→ PAGAMENTO_EXPIRADO
                                  │                                └─ novo PIX ──→ AGUARDANDO_PAGAMENTO
                                  └─ cancel ──────────────────→ CANCELLED (comprador ou vendedor)
                              CONFIRMED
                                  ├─ process ─────────────────→ PROCESSING
                                  └─ cancel ──────────────────→ CANCELLED (vendedor)
                              PROCESSING
                                  ├─ ship ────────────────────→ SHIPPED
                                  └─ cancel ──────────────────→ CANCELLED (vendedor)
                              SHIPPED
                                  └─ webhook ME "delivered" ──→ DELIVERED
```

---

## Regras de negócio

### Carrinho — uma loja por carrinho
O primeiro produto adicionado define a loja do carrinho. Tentar adicionar produto de outra loja retorna erro 400 com a mensagem:
> "Seu carrinho já tem itens da loja X. Finalize ou esvazie o carrinho antes de comprar de outra loja."

O carrinho (PENDING) não aparece em `GET /orders`.

### Checkout — dimensões obrigatórias
Todos os produtos do carrinho precisam ter `weight`, `width`, `height` e `length` cadastrados. Sem eles, o Melhor Envio não consegue calcular o frete e o checkout é bloqueado com erro 400.

### Checkout — cálculo do total
- Frete calculado pelo ME, serviço mais barato disponível
- Comissão = subtotal × `commission.rate`
- Total = subtotal + frete + comissão
- Lojas sem comissão atribuída: `commissionAmount = 0`

### PIX — expiração em 30 minutos
Um job verifica a cada 60 segundos pagamentos PENDING vencidos. Ao expirar: `Payment.status = REJECTED`, `Order.status = PAGAMENTO_EXPIRADO`. O comprador pode então chamar `POST /payments/pix/{orderId}` novamente para gerar um novo PIX.

### Confirmação de pagamento — email automático
Ao confirmar via webhook MP (`status: approved`), o sistema dispara um email assíncrono para **todos os admins cadastrados** com detalhes do pedido (comprador, loja, itens, frete, comissão, total, CEP destino).

### Envio — etiqueta gerada automaticamente
Ao chamar `POST /orders/{id}/ship`, o sistema executa em sequência:
1. ViaCEP lookup do CEP destino (para montar endereço completo do comprador)
2. Adiciona ao carrinho ME: remetente (loja), destinatário (comprador), produtos, volumes, serviço escolhido no checkout
3. Checkout da etiqueta (débita saldo ME)
4. Gera etiqueta e salva código de rastreio

Se o saldo ME for insuficiente, retorna erro 400 e a transição de status não acontece.

### Entrega — só o webhook ME marca como DELIVERED
O vendedor **não pode marcar a entrega como concluída** manualmente. Apenas o webhook `POST /melhorenvio/webhook` com `"status": "delivered"` transita o pedido para DELIVERED. Configurar a URL do webhook no painel do Melhor Envio: `https://SEU_NGROK/melhorenvio/webhook`.

### Cancelamento por papel
| Status do pedido | Comprador | Vendedor |
|-----------------|-----------|---------|
| AGUARDANDO_PAGAMENTO | ✅ pode cancelar | ✅ pode cancelar |
| CONFIRMED | ❌ | ✅ pode cancelar |
| PROCESSING | ❌ | ✅ pode cancelar |
| SHIPPED | ❌ | ❌ |
| DELIVERED | ❌ | ❌ |

### Pré-requisitos para gerar etiqueta ME
- A loja precisa ter **telefone** cadastrado (`PUT /profiles/me` com campo `phone`)
- O comprador precisa ter **CPF** cadastrado (campo `document`, 11 dígitos)
- A loja precisa ter **saldo** na carteira do Melhor Envio

---

## Cenário de teste end-to-end

Fluxo completo com mock ativo para MP e ME (padrão de desenvolvimento).

### 1. Criar perfis

```bash
# Admin
POST /profiles   (multipart/form-data)
name=Admin Bazer  document=11144477735  phone=11999990000  role=ADMIN

# Loja
POST /profiles   (multipart/form-data)
name=Loja Teste  document=11144477736  phone=11988880000  role=VENDEDOR

# Comprador
POST /profiles   (multipart/form-data)
name=João Comprador  document=11144477737  phone=11977770000  role=COMPRADOR
```

### 2. Obter JWT de cada perfil

```bash
POST /usuarios/login
{ "username": "admin@email.com", "password": "senha123" }
# → { "token": "eyJ..." }   ← guarda como TOKEN_ADMIN

POST /usuarios/login
{ "username": "loja@email.com", "password": "senha123" }
# → guarda como TOKEN_LOJA

POST /usuarios/login
{ "username": "joao@email.com", "password": "senha123" }
# → guarda como TOKEN_COMPRADOR
```

### 3. Loja: conferir endereço (necessário para calcular frete de origem)

```bash
# Authorization: Bearer TOKEN_LOJA
GET /addresses
# Se não tiver endereço com CEP, criar:
POST /addresses
{ "street": "Rua das Flores", "number": "100", "district": "Centro", "city": "São Paulo", "state": "SP", "postalCode": "01310100", "country": "Brasil" }
```

### 4. Admin: criar comissão e atribuir à loja

```bash
# Authorization: Bearer TOKEN_ADMIN
POST /commissions
{ "name": "Comissão Padrão", "rate": 0.10 }
# → { "id": 1 }

# profileId da loja (veja na resposta do POST /profiles ou GET /profiles/user/{userId})
PATCH /profiles/2/commission/1
```

### 5. Admin: criar categoria

```bash
# Authorization: Bearer TOKEN_ADMIN
POST /categories
{ "name": "Eletrônicos" }
# → { "id": 1 }
```

### 6. Loja: cadastrar produto com dimensões

```bash
# Authorization: Bearer TOKEN_LOJA
POST /products
{
  "name": "Cabo USB-C",
  "description": "Cabo 1m",
  "price": 29.90,
  "stock": 100,
  "categoryId": 1,
  "weight": 0.1,
  "width": 5,
  "height": 2,
  "length": 15
}
# → { "id": 1 }
```

### 7. Comprador: montar carrinho e fazer checkout

```bash
# Authorization: Bearer TOKEN_COMPRADOR
POST /cart/items
{ "productId": 1, "quantity": 2 }

GET /cart
# → { "status": "PENDING", "price": 59.80, ... }

POST /cart/checkout
{ "postalCodeDestination": "20040020" }
# → { "id": 1, "status": "AGUARDANDO_PAGAMENTO", "shippingCost": X, "commissionAmount": Y, "price": total }
# Guarda o orderId (ex: 1)
```

### 8. Comprador: gerar PIX

```bash
# Authorization: Bearer TOKEN_COMPRADOR
POST /payments/pix/1
# → { "pixCode": "00020126...", "qrCode": "iVBOR...", "expiresAt": "2026-05-04T12:30:00", "status": "PENDING" }

# Consultar status a qualquer momento:
GET /payments/pix/1
```

### 9. Admin: confirmar pagamento (substitui webhook MP em dev)

```bash
# Authorization: Bearer TOKEN_ADMIN
POST /orders/1/confirm-payment
# → { "status": "CONFIRMED" }
# Um email é enviado para todos os admins automaticamente
```

### 10. Vendedor: processar e enviar

```bash
# Authorization: Bearer TOKEN_LOJA
GET /orders/store
# Confirma que o pedido 1 aparece com status CONFIRMED

POST /orders/1/process
# → { "status": "PROCESSING" }

POST /orders/1/ship
# → { "status": "SHIPPED" }
# Com mock ativo: tracking = "MOCK-TRACK-1"
```

### 11. Consultar entrega

```bash
# Authorization: Bearer TOKEN_COMPRADOR (ou qualquer JWT)
GET /deliveries/order/1
# → { "trackingCode": "MOCK-TRACK-1", "trackingUrl": "https://melhorenvio.com.br/rastreamento/MOCK-TRACK-1", "status": "IN_TRANSIT" }
```

### 12. Simular entrega concluída (webhook ME)

```bash
# Sem Authorization — endpoint público
POST /melhorenvio/webhook
{ "tracking": "MOCK-TRACK-1", "status": "delivered" }
# → 200 OK

# Verificar:
GET /orders/1
# → { "status": "DELIVERED" }

GET /deliveries/order/1
# → { "status": "DELIVERED", "deliveredAt": "2026-05-04" }
```

### 13. (Opcional) Testar expiração do PIX

```bash
# Gerar um novo pedido até o passo 8
# Aguardar 30 minutos (ou reduzir PIX_EXPIRATION_MINUTES no PaymentService para teste)
# O job @Scheduled detecta e muda:  Payment → REJECTED, Order → PAGAMENTO_EXPIRADO

GET /orders/{id}
# → { "status": "PAGAMENTO_EXPIRADO" }

# Gerar novo PIX:
POST /payments/pix/{id}
# → novo pixCode, novo expiresAt, Order volta para AGUARDANDO_PAGAMENTO
```

---

## Estrutura de pastas

```
src/main/java/bazer/
├── configuration/
│   ├── Exception/   — GlobalExceptionHandler, BusinessRuleException
│   ├── Security/    — JWT filter, AuthService, SecurityConfiguration
│   ├── Swagger/     — SwaggerConfig
│   └── Web/         — CORS config
├── domain/
│   ├── user/               — User, login
│   ├── profile/            — Profile, foto, busca de lojas, update
│   ├── address/            — CRUD de endereço
│   ├── category/           — Categoria de produto
│   ├── product/            — Produto, imagens, dimensões
│   ├── commission/         — Tabela de comissão
│   ├── order/              — Carrinho, checkout, máquina de estados
│   ├── payment/            — PIX, webhook MP, expiração automática
│   ├── delivery/           — Etiqueta ME, rastreio, webhook
│   ├── notification/       — Email assíncrono para admins
│   ├── analytics/          — Dashboard loja e admin
│   ├── assessment_product/ — Avaliação de produto (nota + comentário)
│   └── assessment_profile/ — Avaliação de loja (nota + comentário)
└── integration/
    ├── melhorenvio/ — OAuth2, cálculo de frete, geração de etiqueta, ViaCEP
    └── mercadopago/ — SDK, gateway PIX, mock mode
```