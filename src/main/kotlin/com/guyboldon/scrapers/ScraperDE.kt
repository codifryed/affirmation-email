package com.guyboldon.scrapers

import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.DomNode
import com.gargoylesoftware.htmlunit.html.DomNodeList
import com.gargoylesoftware.htmlunit.html.HtmlPage
import com.guyboldon.configuration.AffirmationException
import com.guyboldon.models.Affirmation
import com.guyboldon.models.Language
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.jsoup.HttpStatusException
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Singleton

@Singleton
class ScraperDE : Scraper {

    private val log: Logger = LogManager.getLogger(ScraperDE::class.java)

    private val language = Language.DE

    override fun getAffirmation(): Pair<Language, Affirmation> {
        val startTime = System.currentTimeMillis()
        log.info("$language Website scraping started")
        try {
            WebClient().use {
                it.options.isCssEnabled = false
                it.options.isJavaScriptEnabled = true
                val copyright = language.config.copyright.decrypted ?: ""
                val header = language.config.header.decrypted ?: ""

                val document: HtmlPage = it.getPage(language.config.website.decrypted)
                val affirmationNode = (document.querySelector("#hwg-meditation") as DomNode?)
                        ?: throw AffirmationException("Meditation Main Node not found")

                val date = getDateFrom(affirmationNode)

                val title = getTitle(affirmationNode)

                val paragraphs = getParagraphs(affirmationNode)
                checkWeHaveEnoughContent(paragraphs)
                val quote = getQuote(paragraphs)
                val content = getContent(paragraphs)
                val prayer = getPrayer(paragraphs)

                val pageText = getPageNumberText(affirmationNode)

                val affirmation = Affirmation(
                        date, title, quote, content, prayer, pageText, copyright, header)
                logAffirmation(log, language, affirmation, startTime)
                return language to affirmation
            }
        } catch (statusEx: HttpStatusException) {
            throw AffirmationException("Scraping Website: ${language.config.website} not found.", statusEx)
        } catch (e: IOException) {
            throw AffirmationException("Failure by scraping website: ${language.config.website}", e)
        }
    }

    private fun getDateFrom(affirmationNode: DomNode): LocalDate {
        val dayAndMonthText = (affirmationNode.querySelector("#meditation-date") as DomNode?)?.asText()
                ?: throw AffirmationException("Meditation Date not found")
        try {
            val year = LocalDate.now().year
            return LocalDate.parse(
                    "$dayAndMonthText $year",
                    DateTimeFormatter.ofPattern("d. MMMM yyyy", Locale("de")))
        } catch (e: Exception) {
            throw AffirmationException("Date formatting problem", e)
        }
    }

    private fun getTitle(affirmationNode: DomNode): String =
            ((affirmationNode.querySelector("#hwg-meditation-title") as DomNode?)?.asText()
                    ?: throw AffirmationException("Title not found"))

    private fun getParagraphs(affirmationNode: DomNode): DomNodeList<DomNode> =
            ((affirmationNode.querySelector("#quote-text") as DomNode?)?.querySelectorAll("p")
                    ?: throw AffirmationException("Meditation Paragraphs not found"))

    private fun checkWeHaveEnoughContent(paragraphs: DomNodeList<DomNode>) {
        if (paragraphs.size < 3)
            throw AffirmationException("Not enough paragraphs scraped for a meditation")
    }

    private fun getQuote(paragraphs: DomNodeList<DomNode>): String =
            (paragraphs[0]?.querySelector("em strong, strong em, strong i, i strong") as DomNode?)
                    ?.asText()
                    ?: throw AffirmationException("No Quote Found")

    private fun getContent(paragraphs: DomNodeList<DomNode>): List<String> =
            paragraphs
                    .subList(1, paragraphs.size - 1)
                    .map { it.asText() }
                    .toCollection(mutableListOf())

    private fun getPrayer(paragraphs: DomNodeList<DomNode>): String =
            paragraphs[paragraphs.size - 1]?.asText()
                    ?: throw AffirmationException("No Prayer found")

    private fun getPageNumberText(affirmationNode: DomNode): String =
            ((affirmationNode.querySelector("#page-number") as DomNode?)?.asText()
                    ?: throw AffirmationException("No Page Number Text found"))
}