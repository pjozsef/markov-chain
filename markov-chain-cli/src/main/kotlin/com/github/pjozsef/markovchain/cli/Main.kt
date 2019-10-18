@file:JvmName("Main")

package com.github.pjozsef.markovchain.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.int
import com.github.pjozsef.markovchain.Constraints
import com.github.pjozsef.markovchain.generateWords
import java.io.File

class MainCommand : CliktCommand() {

    companion object {
        private const val RADIX_36 = 36
    }

    val words: File by argument().file(exists = true, folderOkay = false, readable = true)

    val seed: String? by option("--seed")
    val count: Int by option("--count").int().default(10)
    val order: Int by option("--order").int().default(3)
    val allowedRetries: Int by option("--retries").int().default(1000000)

    val minLength: Int by option("--min").int().default(3)
    val maxLength: Int by option("--max").int().default(8)
    val startsWith: String? by option("--starts")
    val endsWith: String? by option("--ends")
    val contains: Collection<String>? by option("--contains").split(",")
    val notContains: Collection<String>? by option("--not-contains").split(",")
    val excluding: Collection<String>? by option("--excluding").split(",")
    val hybridPrefixPostfix: Boolean by option("--useHybridPrefixPostfix").flag(default = true)

    override fun run() {
        println(words)

        generateWords(
            words.readLines(),
            order,
            allowedRetries,
            count,
            seed?.toLong(RADIX_36),
            getConstraints()
        ).forEach(::println)
    }

    private fun getConstraints() = Constraints(
        minLength,
        maxLength,
        startsWith,
        endsWith,
        contains,
        notContains,
        excluding,
        hybridPrefixPostfix
    )

}

fun main(args: Array<String>) = MainCommand().main(args)
