package com.guyboldon

import io.micronaut.configuration.picocli.PicocliRunner
import io.micronaut.context.ApplicationContext
import io.micronaut.context.env.Environment
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class AffirmationEmailCommandTest {

    @Test
    fun testWithEncryptionCommandLineOption() {
        val ctx = ApplicationContext.run(Environment.CLI, Environment.TEST)
        val baos = ByteArrayOutputStream()
        System.setOut(PrintStream(baos))

        val args = arrayOf("-e", "me")
        PicocliRunner.run(AffirmationEmailCommand::class.java, ctx, *args)
        println(baos.toString())
        assertTrue(baos.toString().contains("Encrypted text:"))

        ctx.close()
    }
}
