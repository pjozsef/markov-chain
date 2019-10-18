package com.github.pjozsef.markovchain.spring

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MarkovChainApplication

fun main(args: Array<String>) {
    runApplication<MarkovChainApplication>(*args)
}
