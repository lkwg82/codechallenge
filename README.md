# code challenge
We​ ​ would​ ​ like​ ​ to​ ​ have​ ​ a ​ ​ restful​ ​ API​ ​ for​ ​ our​ ​ statistics.​ ​ The​ ​ main​ ​ use​ ​ case​ ​ for​ ​ our​ ​ API​ ​ is​ ​ to
calculate​ ​ realtime​ ​ statistic​ ​ from​ ​ the​ ​ last​ ​ 60​ ​ seconds.​ ​ There​ ​ will​ ​ be​ ​ two​ ​ APIs,​ ​ one​ ​ of​ ​ them​ ​ is
called​ ​ every​ ​ time​ ​ a ​ ​ transaction​ ​ is​ ​ made.​ ​ It​ ​ is​ ​ also​ ​ the​ ​ sole​ ​ input​ ​ of​ ​ this​ ​ rest​ ​ API.​ ​ The​ ​ other​ ​ one
returns​ ​ the​ ​ statistic​ ​ based​ ​ of​ ​ the​ ​ transactions​ ​ of​ ​ the​ ​ last​ ​ 60​ ​ seconds.

# Limitation in Impl
- I timeboxed the impl and therefore I expect there are some edge cases which not handled sufficiently
- O(1) in terms of memory is reached except the peaks before garbage collection for intermediate objects (inherent problem)
- threadsafety is achieved by using `synchronized` blocks, could be improved but was fast for 1_000_000 transactions in 2s see `StressIT`

# design decisions

- prefered usage of primitives over Objects (`long` vs `Long`), because of cheap null safety (not because of less memory consumption)
- TimeService interface could implement `now()` as default method and override in tests, went for clear separation of interface and impl
- Clean code on a moderate level (consistent naming, plausable abstraction and readable code with emphasis on less comments)  
