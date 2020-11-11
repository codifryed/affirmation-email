package com.guyboldon

import com.guyboldon.security.EncryptionProcessor
import com.guyboldon.security.EncryptionProcessor.EncryptionOptions
import io.micronaut.configuration.picocli.PicocliRunner
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import javax.inject.Inject

@Command(name = "affirmation-email",
        description = ["Runs several web-scrapers and sends the contents to multiple emails"],
        mixinStandardHelpOptions = true)
class AffirmationEmailCommand : Runnable {

    private val log: Logger = LogManager.getLogger(AffirmationEmailCommand::javaClass)

    @Option(names = ["-e", "--encrypt"], description = ["The text to encrypt"])
    var textToEncrypt: String = ""

    @Option(names = ["-d", "--decrypt"], description = ["The text to decrypt"])
    var textToDecrypt: String = ""

    @Inject
    lateinit var applicationRunner: ApplicationRunner

    @Inject
    lateinit var encryptionProcessor: EncryptionProcessor

    override fun run() {

        val startBanner = """
            _______ _____________________                             _____ _____                               _______                   
___    |___  __/___  __/___(_)_______________ ___ ______ ___  /____(_)______ _______ ________       ___    |________ ________ 
__  /| |__  /_  __  /_  __  / __  ___/__  __ `__ \_  __ `/_  __/__  / _  __ \__  __ \__  ___/       __  /| |___  __ \___  __ \
_  ___ |_  __/  _  __/  _  /  _  /    _  / / / / // /_/ / / /_  _  /  / /_/ /_  / / /_(__  )        _  ___ |__  /_/ /__  /_/ /
/_/  |_|/_/     /_/     /_/   /_/     /_/ /_/ /_/ \__,_/  \__/  /_/   \____/ /_/ /_/ /____/         /_/  |_|_  .___/ _  .___/ 
                                                                                                            /_/      /_/      
        """.trimIndent()

        log.info("\n$startBanner")

        val options = EncryptionOptions(textToEncrypt, textToDecrypt)

        if (encryptionProcessor.isEncryptionUtilUsed(options))
            encryptionProcessor.processArguments(options)
        else
            applicationRunner.start()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            PicocliRunner.run(AffirmationEmailCommand::class.java, *args)
        }
    }
}
