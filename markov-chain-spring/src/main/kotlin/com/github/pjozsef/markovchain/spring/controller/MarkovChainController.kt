package com.github.pjozsef.markovchain.spring.controller

import com.github.pjozsef.markovchain.generateWords
import com.github.pjozsef.markovchain.spring.dto.MarkovChainRequestDto
import com.github.pjozsef.markovchain.spring.dto.toConstraints
import com.github.pjozsef.markovchain.util.WordUtils.toListOfChar
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class MarkovChainController {

    @PostMapping("/api/generate")
    fun generate(@RequestBody requestDto: MarkovChainRequestDto): Iterable<String> =
        with(requestDto) {
            if (words.isEmpty()) {
                emptyList()
            } else {
                generateWords(
                    words.toListOfChar() ?: error("empty input"),
                    order,
                    1000,
                    15,
                    listOf('#'),
                    seed,
                    toConstraints()
                ).map { it.joinToString("") }
            }
        }

}
