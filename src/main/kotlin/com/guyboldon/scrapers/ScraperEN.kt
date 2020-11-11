package com.guyboldon.scrapers

import com.guyboldon.configuration.AffirmationException
import com.guyboldon.models.Affirmation
import com.guyboldon.models.Language
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Singleton

@Singleton
class ScraperEN : Scraper {

    private val log: Logger = LogManager.getLogger(ScraperEN::class.java)

    private val language = Language.EN

    override fun getAffirmation(): Pair<Language, Affirmation> {
        val startTime = System.currentTimeMillis()
        log.info("$language Website scraping started")
        try {
            val copyright = language.config.copyright.decrypted ?: ""
            val header = language.config.header.decrypted ?: ""
            val document = Jsoup.connect(language.config.website.decrypted).get()
            val meditationElement = document.getElementById("hwg_meditation_canvas")

            val date = getDateFrom(meditationElement.select(".date_text").text())
            val title = meditationElement.select(".hwg-meditation-title").text()

            val paragraphs = meditationElement.selectFirst(".paragraph_text").select("p")
            val quote = getQuote(paragraphs)
            val content = getContent(paragraphs)
            val prayer = getPrayer(paragraphs)

            val pageNumber = meditationElement.select(".page_number span").text()
            val pageText = "Page - $pageNumber"

            val affirmation = Affirmation(
                    date, title, quote, content, prayer, pageText, copyright, header)
            logAffirmation(log, language, affirmation, startTime)
            return language to affirmation
        } catch (statusEx: HttpStatusException) {
            throw AffirmationException("Scraping Website: ${language.config.website} not found.", statusEx)
        } catch (e: IOException) {
            throw AffirmationException("Failure by scraping website: ${language.config.website}", e)
        }
    }

    private fun getQuote(paragraphs: Elements?): String {
        val quoteElement =
                paragraphs?.first()?.select("em strong")
                        ?: paragraphs?.first()?.select("strong em")
        return quoteElement?.text() ?: throw AffirmationException("Can't find the daily quotes")
    }

    private fun getContent(paragraphs: Elements?): List<String> {
        if (paragraphs.isNullOrEmpty() || paragraphs.size < 2)
            throw AffirmationException("Can't find enough content.")

        return paragraphs
                .subList(1, paragraphs.size - 1)
                .map { it.text() }
                .toCollection(mutableListOf())
    }

    private fun getPrayer(paragraphs: Elements?): String {
        val prayer = paragraphs?.last()?.select("em")?.text()
                ?: throw AffirmationException("Prayer Text not found")

        // if there's no italicized text, that just take the last paragraph
        return if (prayer.isBlank()) paragraphs.last().text() else prayer
    }

    private fun getDateFrom(monthAndDayText: String?): LocalDate {
        try {
            val year = LocalDate.now().year
            return LocalDate.parse(
                    "$monthAndDayText $year",
                    DateTimeFormatter.ofPattern("MMMM dd yyyy"))
        } catch (e: Exception) {
            throw AffirmationException("Date Formatting problem", e)
        }
    }
}