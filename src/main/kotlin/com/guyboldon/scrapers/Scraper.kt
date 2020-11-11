package com.guyboldon.scrapers

import com.guyboldon.models.Affirmation
import com.guyboldon.models.Language
import org.apache.logging.log4j.Logger

interface Scraper {

    fun getAffirmation(): Pair<Language, Affirmation>

    fun logAffirmation(log: Logger, language: Language, affirmation: Affirmation, startTime: Long) {
        log.debug("$language Scraped Affirmation: \n$affirmation")
        val scrapeTimeSeconds = (System.currentTimeMillis() - startTime) / 1000.0
        log.info("$language Website scrape complete in $scrapeTimeSeconds seconds")
    }
}
