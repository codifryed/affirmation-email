package com.guyboldon.configuration

import com.guyboldon.configuration.AppConfiguration.LanguagesConfig.LanguageConfig.EmailSubscription
import com.guyboldon.security.EncryptionProcessor.EncryptedJsonList
import com.guyboldon.security.EncryptionProcessor.EncryptedProperty
import com.guyboldon.security.EncryptionProcessor
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Value
import io.micronaut.core.convert.TypeConverter
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor
import org.jasypt.iv.RandomIvGenerator
import javax.inject.Singleton


@Factory
class BeanFactory {

    @Value("\${jasypt.encryptor.password:test}")
    lateinit var pass: String

    @Singleton
    fun encryptor(): StandardPBEStringEncryptor {
        val standardPBEStringEncryptor = StandardPBEStringEncryptor()
        standardPBEStringEncryptor.setAlgorithm("PBEWITHHMACSHA512ANDAES_256")
        standardPBEStringEncryptor.setIvGenerator(RandomIvGenerator())
        standardPBEStringEncryptor.setPassword(pass)
        // erase string in memory
        pass = ""
        return standardPBEStringEncryptor
    }

    @Singleton
    fun encryptedPropertyConverter(encryptionProcessor: EncryptionProcessor): TypeConverter<CharSequence, EncryptedProperty> =
            TypeConverter.of(
                    CharSequence::class.java,
                    EncryptedProperty::class.java
            ) { encryptionProcessor.convert(it) }


    @Singleton
    fun encryptedJsonListConverter(encryptionProcessor: EncryptionProcessor): TypeConverter<CharSequence, EncryptedJsonList<*>> =
            TypeConverter.of(
                    CharSequence::class.java,
                    EncryptedJsonList::class.java
            ) { encryptionProcessor.convertJson(it, EmailSubscription::class.java) }
}