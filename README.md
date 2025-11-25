# Java Spring Boot Mini E-Ticaret API

Spring Boot 3, Java 17 ve PostgreSQL kullanan mini e-ticaret REST API iskeleti. JWT güvenliği, Flyway migration, Testcontainers ve Docker Compose hazırdır.

## Hızlı Başlangıç
```bash
cp .env.example .env
mvn clean package
docker compose up --build
```

Lokal profil için uygulama `http://localhost:8080` adresinde ayağa kalkar, Swagger UI `http://localhost:8080/swagger-ui.html`.

Varsayılan DB bilgileri (PostgreSQL):
- Host: `localhost`
- Port: `5432`
- DB: `ecommerce_db`
- Kullanıcı: `postgres`
- Şifre: `123456789`

JWT için `.env` içinde `JWT_SECRET` en az 32 karakter olmalı (örn. `super-secret-key-change-me-32-chars-min`).

## Mimari Notlar
- Katmanlar paket bazlı: `auth`, `catalog`, `cart`, `order`, `payment`, `inventory`, `common` (ileride eklenecek).
- Veritabanı: PostgreSQL. Flyway migration klasörü `src/main/resources/db/migration`.
- Konfigürasyon: `application.yml` profilleri `local/dev/prod`.
- Build/Test: Maven, JUnit 5, Testcontainers (PostgreSQL).
- Gözlemlenebilirlik: Actuator `/health`, `/metrics`. OpenAPI springdoc.
- Güvenlik: `/api/auth/**` serbest; `/api/catalog/**` GET istekleri serbest; diğer `/api/**` JWT ile korunur. `/api/admin/**` için `ROLE_ADMIN` gerekir. BCrypt encoder ve JWT filtreleri hazır.
- CORS/Rate limit: `app.cors.*` ve `app.ratelimit.limit-per-minute` config üzerinden yönetilir; varsayılan 100 istek/dk, tüm origin’ler açık (prod için kısıtlayın).

## Faydalı Komutlar
- Derleme ve test: `mvn verify`
- Sadece derleme (test yok): `mvn package -DskipTests`
- Docker üzerinden çalıştırma: `docker compose up --build`

## Auth Akışı (JWT)
- Endpointler: `POST /api/auth/register`, `POST /api/auth/login`, `POST /api/auth/refresh`
- Varsayılan roller Flyway seed ile gelir (`USER`, `ADMIN`); başlangıçta kullanıcı yok, `register` ile oluşturulur.
- Token tipi `Bearer`. Refresh token ile yeni access token alınabilir.

## Katalog Endpointleri
- Kategoriler: `GET /api/catalog/categories`, `GET /api/catalog/categories/{slug}` (public); `POST/PUT/DELETE /api/catalog/categories` (admin).
- Ürünler: `GET /api/catalog/products` (opsiyonel `categoryId` query param), `GET /api/catalog/products/{id}`, `GET /api/catalog/products/sku/{sku}` (public); `POST/PUT/DELETE /api/catalog/products` (admin).

## Sepet, Adres, Sipariş Endpointleri
- Sepet: `GET /api/cart`, `POST /api/cart/items`, `PUT /api/cart/items/{itemId}`, `DELETE /api/cart/items/{itemId}`, `DELETE /api/cart/items` (JWT zorunlu).
- Adres: `GET /api/customer/addresses`, `POST /api/customer/addresses`, `PUT /api/customer/addresses/{id}`, `DELETE /api/customer/addresses/{id}` (JWT zorunlu, kullanıcıya ait kontrolü var).
- Sipariş: `POST /api/orders/checkout` (sepeti siparişe dönüştürür), `GET /api/orders` (kullanıcı siparişleri), `GET /api/orders/admin?status=` ve `PUT /api/orders/admin/{orderId}/status` (admin).
- Mock Ödeme: `POST /api/payments/mock/{orderId}/capture`, `POST /api/payments/mock/{orderId}/refund` (admin). Capture siparişi `PAID`, refund `REFUNDED` yapar; stok iadesi refund/cancel’da yapılır.

## Testler
- Hızlı test: `./mvnw -B test` (H2, Flyway kapalı test profili; OrderFlowIntegrationTest + AuthFlowMockMvcTest).
- Testcontainers E2E (Docker gerekli): `./mvnw -B test -Dtest=EndToEndTestcontainersTest` (Postgres container, checkout→capture→refund akışı). Docker yoksa test skip edilir (`@Testcontainers(disabledWithoutDocker = true)`).

## Konfig Notları
- Prod/Dev: `spring.jpa.hibernate.ddl-auto=validate`, `open-in-view=false`, Flyway zorunlu.
- JWT: `.env` / ortam değişkeniyle `JWT_SECRET` en az 32 karakter olmalı.
- Rate limit/CORS: `RATE_LIMIT_PER_MINUTE`, `app.cors.*` ile profil bazlı kısıtlayın (prod’da origin/metod/header’ları daraltın).
