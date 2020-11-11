package com.guyboldon.models

import java.time.LocalDate

data class Affirmation(
        val date: LocalDate,
        val title: String,
        val quote: String,
        val content: List<String>,
        val prayer: String,
        val pageText: String,
        val copyright: String,
        val header: String,
)

