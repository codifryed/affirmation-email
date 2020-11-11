package com.guyboldon.security

import com.fasterxml.jackson.databind.ObjectMapper
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor
import javax.inject.Singleton

@Singleton
class EncryptionProcessor(
        private val encryptor: StandardPBEStringEncryptor,
        private val objectMapper: ObjectMapper
) {
    private val prefix: String = "ENC("

    fun isEncryptionUtilUsed(options: EncryptionOptions): Boolean =
            options.textToEncrypt.isNotBlank() || options.textToDecrypt.isNotBlank()

    fun processArguments(options: EncryptionOptions) {

        if (options.textToEncrypt.isNotBlank()) {
            val encryptedText = encryptor.encrypt(options.textToEncrypt)
            println("""
                Text to encrypt:
                ${options.textToEncrypt}
                Encrypted text:
                $encryptedText
            """.trimIndent())
        } else if (options.textToDecrypt.isNotBlank()) {
            val decryptedText = encryptor.decrypt(options.textToDecrypt)
            println("""
                Text to decrypt:
                ${options.textToDecrypt}
                Decrypted text:
                $decryptedText
            """.trimIndent())
        }
    }

    fun convert(property: CharSequence?): EncryptedProperty? {
        return if (!property.isNullOrBlank() && property.startsWith(prefix)) {
            val encryptedProperty = property.substring(prefix.length, property.length - 1)
            EncryptedProperty(encryptor.decrypt(encryptedProperty), encryptedProperty)
        } else
            EncryptedProperty(property?.toString(), encryptor.encrypt(property?.toString()))
    }

    fun <T> convertJson(property: CharSequence?, classType: Class<T>): EncryptedJsonList<T> {
        return if (!property.isNullOrBlank() && property.startsWith(prefix)) {

            val encryptedProperty = property.substring(prefix.length, property.length - 1)
            val decryptedJson = encryptor.decrypt(encryptedProperty)
            val emailList: List<T> = objectMapper.readValue(
                    decryptedJson,
                    objectMapper.typeFactory.constructCollectionType(List::class.java, classType))
            EncryptedJsonList(emailList, encryptedProperty, decryptedJson)
        } else {

            val decryptedList: List<T> =
                    if (property.isNullOrBlank())
                        emptyList()
                    else
                        objectMapper.readValue(
                                property.toString(),
                                objectMapper.typeFactory.constructCollectionType(List::class.java, classType))
            val encryptedJson = encryptor.encrypt(property?.toString())
            EncryptedJsonList(decryptedList, encryptedJson, property?.toString())
        }
    }

    data class EncryptionOptions(
            val textToEncrypt: String,
            val textToDecrypt: String
    )

    data class EncryptedProperty(
            val decrypted: String?,
            val encrypted: String?
    )

    data class EncryptedJsonList<T>(
            val decrypted: List<T>,
            val encrypted: String?,
            val json: String?
    )
}
