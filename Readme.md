## Matrix Home Server load\stress test

This app is another try to make loading\stress tests for Matrix Home Server.

**PLEASE DON'T USE IT ON PRODUCTION SERVER!!!**

For examples please check `src/main/kotlin/Main.kt`

Don't forget to change limitations in config like below:

```yaml
rc_message:
 per_second: 10000
 burst_count: 10000

rc_registration:
 per_second: 10000
 burst_count: 10000

rc_login:
 address:
  per_second: 10000
  burst_count: 10000
 account:
   per_second: 10000
   burst_count: 10000
 failed_attempts:
   per_second: 10000
   burst_count: 10000

rc_admin_redaction:
 per_second: 10000
 burst_count: 10000

rc_joins:
 local:
   per_second: 10000
   burst_count: 10000
 remote:
   per_second: 10000
   burst_count: 10000
```


### Attention!

This app working properly on `Linux`, if you want to use it on `Windows` or `MAC` please replace `launch` with `thread`. 
Soon I will add a branch using threads.



### Thanks

Thank you [maxidorius](https://github.com/maxidorius) for [sdk](https://github.com/kamax-matrix/matrix-java-sdk).

Pull requests are welcome!