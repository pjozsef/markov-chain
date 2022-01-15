package com.github.pjozsef.markovchain

import com.github.pjozsef.markovchain.constraint.Constraints
import com.github.pjozsef.markovchain.util.WordUtils.toListOfChar
import com.github.pjozsef.markovchain.util.charCommentFilter
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import java.io.InputStreamReader

class GenerateKtTest: FreeSpec({
    "matches snapshot" {
        val baseWords = read("/snapshot/female_names_input.txt").toListOfChar() ?: error("")
        val seed = 45L

        val generatedWords = generateWords(
            baseWords,
            order = 3,
            allowedRetries = 1_000_000,
            delimiter = listOf('#'),
            count = 70,
            seed = seed,
            constraints = Constraints(
                minLength = 4,
                maxLength = 10,
                excluding = baseWords
            ),
            commentFilter = charCommentFilter()
        ).map { it.joinToString("") }.sorted()

        val snapshotWords = read("/snapshot/female_names_output.txt")

        generatedWords.toList() shouldBe snapshotWords
    }
}){
    override fun isolationMode() =  IsolationMode.InstancePerLeaf
}

private fun Any.read(resource: String) = InputStreamReader(this::class.java.getResourceAsStream(resource)).readLines()
