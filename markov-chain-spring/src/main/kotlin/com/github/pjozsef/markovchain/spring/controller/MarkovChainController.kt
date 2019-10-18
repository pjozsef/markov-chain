package com.github.pjozsef.markovchain.spring.controller

import com.github.pjozsef.markovchain.generateWords
import com.github.pjozsef.markovchain.spring.dto.MarkovChainRequestDto
import com.github.pjozsef.markovchain.spring.dto.toConstraints
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class MarkovChainController {

    @PostMapping("/api/generate")
    fun generate(@RequestBody requestDto: MarkovChainRequestDto): Iterable<String> =
        with(requestDto) {
            generateWords(
                words,
                order,
                1000,
                15,
                seed,
                toConstraints()
            )
        }

}
