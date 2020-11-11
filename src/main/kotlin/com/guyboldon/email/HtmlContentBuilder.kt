package com.guyboldon.email

import com.guyboldon.models.Affirmation
import com.guyboldon.models.Language
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import javax.inject.Singleton

@Singleton
class HtmlContentBuilder(
        private val templateEngine: TemplateEngine
) {

    private val log: Logger = LogManager.getLogger(HtmlContentBuilder::class.java)

    fun build(pair: Pair<Language, Affirmation>): Pair<Language, String> {
        val language = pair.first
        val affirmation = pair.second

        val date = affirmation.date.format(language.dateFormatter)
        val footer = language.config.footer.decrypted

        return Context()
                .apply {
                    setVariable("date", date)
                    setVariable("title", affirmation.title)
                    setVariable("quote", affirmation.quote)
                    setVariable("contents", affirmation.content)
                    setVariable("prayer", affirmation.prayer)
                    setVariable("pageText", affirmation.pageText)
                    setVariable("copyright", affirmation.copyright)
                    setVariable("header", affirmation.header)
                    setVariable("footer", footer)
                }.let {
                    language to templateEngine.process("affirmations", it)
                }.let {
                    log.debug("${it.first} HTML Content: \n${it.second}")
                    it
                }
    }
}