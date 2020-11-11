package com.guyboldon.scrapers

import com.guyboldon.models.Affirmation
import com.guyboldon.models.Language
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.time.LocalDate
import javax.inject.Inject

@MicronautTest
internal class ScrapersTest {

    @Inject
    lateinit var scraperEN: ScraperEN

    @Inject
    lateinit var scraperDE: ScraperDE

    @Test
    fun getEnAffirmation() {
        val affirmationPair = scraperEN.getAffirmation()
        assertEquals(Language.EN, affirmationPair.first)
        checkAffirmation(affirmationPair.second)
    }

    @Test
    fun getDeAffirmation() {
        val affirmationPair = scraperDE.getAffirmation()
        assertEquals(Language.DE, affirmationPair.first)
        checkAffirmation(affirmationPair.second)
    }

    private fun checkAffirmation(affirmation: Affirmation) {
        assertTrue(affirmation.content.isNotEmpty())
        assertTrue(affirmation.content.all { it.isNotBlank() })
        assertTrue(affirmation.copyright.isNotBlank())
        assertTrue(affirmation.header.isNotBlank())
        assertTrue(affirmation.pageText.isNotBlank())
        assertTrue(affirmation.prayer.isNotBlank())
        assertTrue(affirmation.quote.isNotBlank())
        assertTrue(affirmation.title.isNotBlank())
        assertEquals(affirmation.date, LocalDate.now())
    }
}