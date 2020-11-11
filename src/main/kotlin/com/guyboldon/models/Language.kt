package com.guyboldon.models

import com.guyboldon.configuration.AppConfiguration
import com.guyboldon.configuration.AppConfiguration.LanguagesConfig.LanguageConfig
import com.guyboldon.scrapers.Scraper
import com.guyboldon.scrapers.ScraperDE
import com.guyboldon.scrapers.ScraperEN
import io.micronaut.context.annotation.Context
import java.time.format.DateTimeFormatter
import java.util.*
import javax.annotation.PostConstruct
import javax.inject.Singleton

enum class Language {
    EN, DE;

    lateinit var scraper: Scraper

    lateinit var config: LanguageConfig

    lateinit var dateFormatter: DateTimeFormatter

    @Context
    @Singleton
    class LanguageConfigSelector(
            private val appConfiguration: AppConfiguration,
            private val scraperEN: ScraperEN,
            private val scraperDE: ScraperDE
    ) {

        @PostConstruct
        fun setScraper() {
            values().toList()
                    .forEach {
                        when (it) {
                            EN -> {
                                it.scraper = scraperEN
                                it.config = appConfiguration.language.en
                                it.dateFormatter = DateTimeFormatter.ofPattern("MMMM dd")
                            }
                            DE -> {
                                it.scraper = scraperDE
                                it.config = appConfiguration.language.de
                                it.dateFormatter = DateTimeFormatter.ofPattern("d. MMMM", Locale("de"))
                            }
                        }
                    }
        }
    }
}