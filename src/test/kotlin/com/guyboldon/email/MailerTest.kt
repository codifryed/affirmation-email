package com.guyboldon.email

import com.guyboldon.configuration.AppConfiguration
import com.guyboldon.models.Language
import com.icegreen.greenmail.junit5.GreenMailExtension
import com.icegreen.greenmail.util.GreenMailUtil
import com.icegreen.greenmail.util.ServerSetupTest
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import kotlinx.coroutines.runBlocking
import org.apache.http.entity.ContentType.TEXT_HTML
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.nio.charset.StandardCharsets.UTF_8
import javax.inject.Inject
import javax.mail.internet.MimeMultipart

@MicronautTest
internal class MailerTest {

    @RegisterExtension
    @JvmField
    val greenMail: GreenMailExtension = GreenMailExtension(ServerSetupTest.SMTP.setVerbose(true))

    @Inject
    lateinit var appConfiguration: AppConfiguration

    @Inject
    lateinit var mailer: Mailer

    private val htmlContent: String = """
        <!DOCTYPE html>
        <html lang="en" xmlns:th="http://www.thymeleaf.org">
        <head>
        </head>
        <body>
        Testing!!!
        </body>
        </html>
    """.trimIndent().replace("\n", "\r\n") // javax.mail uses the windows standard (probably for compat.)

    @ParameterizedTest
    @EnumSource
    fun send(language: Language) = runBlocking {
        greenMail.setUser(appConfiguration.mail.username.decrypted, appConfiguration.mail.password.decrypted)

        mailer.send(language to htmlContent)

        // wait for async mail to be received
        assertTrue(greenMail.waitForIncomingEmail(5000, language.config.subs.decrypted.size))

        val messages = greenMail.receivedMessages

        assertEquals(language.config.subs.decrypted.size, messages.size)
        messages.forEach {
            assertEquals(language.config.subject.decrypted, it.subject)
            val bodyPart = (it.content as MimeMultipart).getBodyPart(0)
            assertEquals(htmlContent, GreenMailUtil.getBody(bodyPart).trim())
            assertEquals(TEXT_HTML.withCharset(UTF_8).toString(), bodyPart.contentType)
        }
    }

    @Test
    fun testConnectionFailure() = runBlocking {
        greenMail.stop()
        val startTime = System.currentTimeMillis()

        mailer.send(Language.EN to "content")

        val totalTime = (System.currentTimeMillis() - startTime) / 1000.0
        val expectedTime = appConfiguration.mail.wait.seconds * appConfiguration.mail.retries
        assertTrue(totalTime >= expectedTime)
        // due to our async sending, all these sending shouldn't take but just a tiny bit more more time:
        assertTrue(totalTime < expectedTime + 1)
    }
}