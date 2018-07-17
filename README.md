# design decisions

- prefered usage of primitives over Objects (`long` vs `Long`), because of cheap null safety (not because of less memory consumption)
- TimeService interface could implement `now()` as default method and override in tests, went for clear separation of interface and impl  