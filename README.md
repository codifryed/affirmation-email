# affirmation-email
A web scraper used for automatic for daily affirmation emails

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