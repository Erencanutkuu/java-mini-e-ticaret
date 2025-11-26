# Java Spring Boot Mini E-Ticaret API

Spring Boot 3 (Java 17) ile yazılmış, PostgreSQL/Flyway/JWT/Testcontainers/Docker Compose hazır gelen mini e-ticaret backend’i. Katalog, sepet, sipariş, mock ödeme akışları ve basit bir React tabanlı test paneli içerir.

## Özellikler
- JWT tabanlı kimlik doğrulama (register/login/refresh), `USER`/`ADMIN` rolleri
- Katalog (kategori/ürün), sepet, sipariş ve mock ödeme uçları
- Flyway migration (PostgreSQL), H2 profilli hızlı testler
- Testcontainers ile uçtan uca senaryo (Docker varsa)
- React test paneli (tarayıcıdan login/kategori/ürün akışı) ve Swagger UI için hazır yapı
- Dockerfile + docker-compose ile tek komutla çalıştırma

## Hızlı Başlangıç (Docker Compose)
```bash
cp .env.example .env            # SECRET ve DB şifrelerini güncelle
docker compose up --build
# API: http://localhost:8080
# React test paneli: http://localhost:8080/
# Swagger UI: http://localhost:8080/swagger-ui.html
```

## Lokal Geliştirme
```bash
cp .env.example .env    # gerçek SECRET/şifreleri yaz
./mvnw -DskipTests package
./mvnw spring-boot:run
# API: http://localhost:8080
```

## Testler
```bash
# Hızlı test (H2, Flyway kapalı test profili)
./mvnw test

# Uçtan uca Testcontainers (Docker gerekir; CI'de otomatik skip)
./mvnw -Dtest=EndToEndTestcontainersTest test
```

## Kullanım Notları
- Varsayılan DB: `ecommerce_db` / `postgres` / `change-me` (lokalde .env’den değiştir).
- JWT için `.env` içindeki `JWT_SECRET` en az 32 karakter olmalı.
- Güvenlik: `/api/auth/**` serbest; katalog GET’leri serbest; diğer uçlar JWT ister; `/api/admin/**` için ADMIN rolü gerekir.
- React test paneli: `http://localhost:8080/` üzerinden login, kategori ve ürün oluşturup listeleyebilirsin.

## Başlıca Endpointler (özet)
- Auth: `POST /api/auth/register`, `POST /api/auth/login`, `POST /api/auth/refresh`
- Katalog: `GET /api/catalog/categories|products` (public), `POST/PUT/DELETE` (ADMIN)
- Sepet: `GET/POST/PUT/DELETE /api/cart/**` (JWT)
- Sipariş: `POST /api/orders/checkout`, `GET /api/orders` (JWT), admin status güncelleme uçları
- Mock ödeme: `POST /api/payments/mock/{orderId}/capture|refund` (ADMIN)

## Yapı & Konfig
- Flyway migration: `src/main/resources/db/migration`
- Profiller: `local`, `test`, `testcontainers` (CI’de Docker yoksa E2E skip edilir)
- Rate limit/CORS: `app.ratelimit.*`, `app.cors.*` (prod’da kısıtlayın)
- OpenAPI/Swagger: `springdoc-openapi-starter-webmvc-ui`, UI `/swagger-ui.html`

## Yol Haritası (öneri)
- Lint/format ve coverage raporu (CI)
- Prod odaklı CORS/ratelimit ayarları ve refresh token revocation
- Basit sepet/checkout akışını da gösteren küçük React bileşenleri

## Lisans
Bu proje MIT lisansı ile lisanslanmıştır. Ayrıntılar için `LICENSE` dosyasına bakın.
