package com.guyboldon.models

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.util.*

internal class LanguageTest {

    @Test
    fun checkNumberOfLanguages() {
        assertEquals(2, Language.values().size)
    }

    @ParameterizedTest
    @EnumSource
    fun checkExistingEnums(language: Language) {
        assertTrue(EnumSet.of(Language.EN, Language.DE).contains(language))
    }

    @ParameterizedTest
    @EnumSource
    fun checkScraper(language: Language) {
        assertNotNull(language.scraper)
    }

    @ParameterizedTest
    @EnumSource
    fun checkConfig(language: Language) {
        assertNotNull(language.config)
    }

    @ParameterizedTest
    @EnumSource
    fun checkFormatter(language: Language) {
        assertNotNull(language.dateFormatter)
    }
}