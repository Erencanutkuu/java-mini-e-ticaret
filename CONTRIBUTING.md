# Katkı Rehberi

## Nasıl başlarım?
1. Fork + branch aç (`feature/...`).
2. Kodda gerekli değişiklikleri yap.
3. Çalıştığını doğrula:
   ```bash
   ./mvnw -DskipTests package   # hızlı kontrol
   # veya
   ./mvnw test                 # H2 hızlı testler
   # Docker varsa:
   ./mvnw -Dtest=EndToEndTestcontainersTest test
   ```
4. README’ye eklediğin değişiklik varsa güncelle.
5. Pull request aç; PR şablonunu doldur.

## Stil ve kalite
- Java 17, Spring Boot 3.
- Var olan katman/paket düzenini koru (auth, catalog, cart, order, payment, common).
- Mümkünse DTO/validation ekle; anlamlı hata mesajı üret.
- Güvenlik: `/api/**` için JWT; admin uçlarında rol kontrolü.
- Migration gerektiğinde Flyway dosyası ekle (`src/main/resources/db/migration`).

## Test notları
- Hızlı testler H2 ile çalışır (test profili); Flyway disabled.
- Testcontainers E2E, Docker yoksa CI’da zaten atlanır.

## Issue/PR
- Bug için mümkünse repro adımlarını ve log’u ekle.
- Feature için kısa taslak yaz; API değişiyorsa README’yi güncelle.

Teşekkürler! 🙌
