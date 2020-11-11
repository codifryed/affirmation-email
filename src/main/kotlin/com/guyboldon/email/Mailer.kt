package com.guyboldon.email

import com.guyboldon.configuration.AppConfiguration
import com.guyboldon.models.Language
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import org.apache.commons.mail.EmailException
import org.apache.commons.mail.HtmlEmail
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.nio.charset.StandardCharsets
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Singleton
import javax.mail.internet.InternetAddress

@Singleton
class Mailer(
        private val appConfiguration: AppConfiguration
) {

    private val log: Logger = LogManager.getLogger(Mailer::class.java)

    suspend fun send(emailInfo: Pair<Language, String>) = supervisorScope {
        val startTime = System.currentTimeMillis()
        val language = emailInfo.first
        val emailContent = emailInfo.second
        log.info("Beginning Mail Sending for Language: $language")

        val handler = CoroutineExceptionHandler { _, throwable ->
            log.error("mailing to a recipient threw exception:", throwable)
        }
        language.config.subs.decrypted
                .map {
                    launch(handler) {
                        val emailTo = listOf(InternetAddress(it.email, it.name))
                        val retryCount = AtomicInteger(0)
                        while (retryCount.get() < appConfiguration.mail.retries) {
                            try {
                                HtmlEmail()
                                        .apply {
                                            setCharset(StandardCharsets.UTF_8.name())
                                            setHtmlMsg(emailContent)
                                            setTo(emailTo)

                                            hostName = appConfiguration.mail.host.decrypted
                                            setSmtpPort(appConfiguration.mail.port)
                                            setAuthentication(appConfiguration.mail.username.decrypted, appConfiguration.mail.password.decrypted)
                                            socketConnectionTimeout = appConfiguration.mail.connectionTimeout.toMillis().toInt()
                                            socketTimeout = appConfiguration.mail.timeout.toMillis().toInt()
                                            isSSLOnConnect = appConfiguration.mail.ssl
                                            setDebug(appConfiguration.mail.debug)

                                            setFrom(appConfiguration.mail.fromEmail.decrypted, appConfiguration.mail.fromName.decrypted)
                                            subject = language.config.subject.decrypted

                                            send()
                                            log.info("Mail successfully sent.")
                                            retryCount.set(appConfiguration.mail.retries)
                                        }
                            } catch (e: EmailException) {
                                log.warn("Mail Sending error: ${e.message}, retry#${retryCount.get() + 1}, retrying...")
                                delay(appConfiguration.mail.wait.toMillis())
                                if (retryCount.incrementAndGet() >= appConfiguration.mail.retries)
                                    throw e
                            }
                        }
                    }
                }.map { it.join() }


        val sendTimeSeconds = (System.currentTimeMillis() - startTime) / 1000.0
        log.info("Mail Sending finished in $sendTimeSeconds seconds")
    }
}