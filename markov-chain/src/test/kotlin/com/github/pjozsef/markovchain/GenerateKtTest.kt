package com.github.pjozsef.markovchain

import com.github.pjozsef.markovchain.constraint.Constraints
import io.kotlintest.IsolationMode
import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec
import java.io.InputStreamReader

class GenerateKtTest: FreeSpec({
    "matches snapshot" {
        val baseWords = read("/snapshot/female_names_input.txt")
        val seed = 45L

        val generatedWords = generateWords(
            baseWords,
            order = 3,
            allowedRetries = 1_000_000,
            count = 70,
            seed = seed,
            constraints = Constraints(
                minLength = 4,
                maxLength = 10,
                excluding = baseWords
            )
        )

        val snapshotWords = read("/snapshot/female_names_output.txt")

        generatedWords.toList() shouldBe snapshotWords
    }
}){
    override fun isolationMode() =  IsolationMode.InstancePerLeaf
}

private fun Any.read(resource: String) = InputStreamReader(this::class.java.getResourceAsStream(resource)).readLines()
