# affirmation-email
A [Micronaut](https://micronaut.io) application written in [Kotlin](https://kotlinlang.org/) with:
 - [Coroutines](https://github.com/Kotlin/kotlinx.coroutines) used for robustness and concurrency
 - [HtmlUnit](https://htmlunit.sourceforge.io/) and [JSoup](https://jsoup.org/) used for HTML and Javascript web scraping 
 - [Jasypt](http://www.jasypt.org/) used for hand spun property encryption
 - [Thymeleaf](https://www.thymeleaf.org/) used for email templating
 - [Apache Commons Email](https://commons.apache.org/proper/commons-email/) used for mailing sending

## To encrypt properties:

encrypt with env password
```
./gradlew run --args="-e 'text'"

```

decrypt with env password:
```
./gradlew run --args="-d 'text'"
```

***or Debug an EncryptedProperty*** model, both encrypted and decrypted forms are contained within

***Note:*** jasypt.encryptor.password must be correctly set for encryption to work

set property value as: 
```
app:
    sensitive:
        password: ENC(encrypted value)
 ```       

---

## To check for dependency updates:
```
./gradlew dependencyUpdates
```