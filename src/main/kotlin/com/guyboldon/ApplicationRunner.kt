package com.guyboldon

import com.guyboldon.configuration.AffirmationException
import com.guyboldon.email.HtmlContentBuilder
import com.guyboldon.email.Mailer
import com.guyboldon.models.Language
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import javax.inject.Singleton

@Singleton
class ApplicationRunner(
        private val htmlContentBuilder: HtmlContentBuilder,
        private val mailer: Mailer
) {

    private val log: Logger = LogManager.getLogger(ApplicationRunner::javaClass)

    fun start() = runBlocking {
        val appStartTime = System.currentTimeMillis()
        log.info("Application initialized.")

        Language.values().toList()
                .map { language ->
                    launch(Dispatchers.Default) {
                        val languageStartTime = System.currentTimeMillis()
                        try {
                            language.scraper
                                    .getAffirmation()
                                    .let {
                                        htmlContentBuilder.build(it)
                                    }.also {
                                        mailer.send(it)
                                    }
                        } catch (e: AffirmationException) {
                            log.error(e.message, e)
                        }
                        val totalTime = (System.currentTimeMillis() - languageStartTime) / 1000.0
                        log.info("$language Affirmation completed in $totalTime seconds")
                    }
                }.map { it.join() }


        val totalTime = (System.currentTimeMillis() - appStartTime) / 1000.0
        log.info("All Affirmations completed in $totalTime seconds")
        log.info("Application finished.")
    }
}