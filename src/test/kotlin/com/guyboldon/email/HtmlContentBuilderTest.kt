package com.guyboldon.email

import com.guyboldon.models.Affirmation
import com.guyboldon.models.Language
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.time.LocalDate
import javax.inject.Inject

@MicronautTest
internal class HtmlContentBuilderTest {

    @Inject
    lateinit var htmlContentBuilder: HtmlContentBuilder

    private val affirmation: Affirmation = Affirmation(
            LocalDate.of(2017, 11, 1),
            "TITLE!",
            "QUOTE!",
            listOf("PARAGRAPH1", "PARAGRAPH2", "PARAGRAPH3"),
            "PRAYER!",
            "PAGE_NUMBER!",
            "COPYRIGHT!",
            "HEADER!"
    )

    @ParameterizedTest
    @EnumSource
    fun checkHtml(language: Language) {

        val htmlContentPair = htmlContentBuilder.build(language to affirmation)

        val htmlContent = htmlContentPair.second

        assertTrue(htmlContent.contains("<!DOCTYPE html>"))
        assertTrue(htmlContent.contains(language.config.footer.decrypted.toString()))
        assertTrue(htmlContent.contains(affirmation.date.format(language.dateFormatter)))
        assertTrue(htmlContent.contains(affirmation.title))
        assertTrue(htmlContent.contains(affirmation.quote))
        assertTrue(affirmation.content.all { htmlContent.contains(it) })
        assertTrue(htmlContent.contains(affirmation.prayer))
        assertTrue(htmlContent.contains(affirmation.pageText))
        assertTrue(htmlContent.contains(affirmation.copyright))
        assertTrue(htmlContent.contains(affirmation.header))
    }
}