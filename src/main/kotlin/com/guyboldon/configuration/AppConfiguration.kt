package com.guyboldon.configuration

import com.guyboldon.security.EncryptionProcessor.EncryptedJsonList
import com.guyboldon.security.EncryptionProcessor.EncryptedProperty
import io.micronaut.context.annotation.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties("app")
class AppConfiguration {

    var language: LanguagesConfig = LanguagesConfig()

    var mail: MailConfig = MailConfig()

    @ConfigurationProperties("language")
    class LanguagesConfig {
        var en: LanguageConfigEN = LanguageConfigEN()
        var de: LanguageConfigDE = LanguageConfigDE()


        @ConfigurationProperties("en")
        class LanguageConfigEN : LanguageConfig()

        @ConfigurationProperties("de")
        class LanguageConfigDE : LanguageConfig()

        open class LanguageConfig {

            lateinit var website: EncryptedProperty

            lateinit var header: EncryptedProperty

            lateinit var copyright: EncryptedProperty

            lateinit var subject: EncryptedProperty

            lateinit var footer: EncryptedProperty

            lateinit var subs: EncryptedJsonList<EmailSubscription>

            data class EmailSubscription(
                    var name: String? = null,
                    var email: String? = null
            )
        }
    }

    @ConfigurationProperties("mail")
    class MailConfig {

        lateinit var host: EncryptedProperty

        var port: Int = 25

        var ssl: Boolean = false

        var connectionTimeout: Duration = Duration.ofSeconds(30)

        var timeout: Duration = Duration.ofSeconds(30)

        lateinit var username: EncryptedProperty

        lateinit var password: EncryptedProperty

        var wait: Duration = Duration.ofSeconds(0)

        var retries: Int = 0

        lateinit var fromName: EncryptedProperty

        lateinit var fromEmail: EncryptedProperty

        val debug: Boolean = false
    }
}