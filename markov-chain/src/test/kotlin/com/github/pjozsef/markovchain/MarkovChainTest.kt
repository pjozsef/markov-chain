package com.github.pjozsef.markovchain

import com.github.pjozsef.markovchain.util.WeightedDice
import com.nhaarman.mockitokotlin2.doReturnConsecutively
import com.nhaarman.mockitokotlin2.mock
import io.kotlintest.IsolationMode
import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec

class MarkovChainTest : FreeSpec({

    "Markov chain" - {

        "generates char sequence" {
            mapOf(
                "" to listOf("A"),
                "A" to listOf("B", "A", "B"),
                "B" to listOf("A", "B", "#")
            ) shouldGenerate "ABAABB"
        }

        "supports higher order transitions" {
            mapOf(
                "" to listOf("A"),
                "A" to listOf("A"),
                "AAAA" to listOf("BB"),
                "BB" to listOf("#")
            ) shouldGenerate "AAAABB"
        }

        "uses set order as maximum order" {
            mapOf(
                "" to listOf("A"),
                "A" to listOf("A"),
                "AA" to listOf("BB"),
                "B" to listOf("#"),
                "AABB" to listOf("C"),
                "C" to listOf("#")
            ) withOrder 2 shouldGenerate "AABB"
        }

        "starts generation with prefix if supplied" {
            mapOf(
                "" to listOf("A"),
                "A" to listOf("#"),
                "f" to listOf("C"),
                "C" to listOf("#")
            ) withConstraints Constraints(startsWith = "asdf") shouldGenerate "asdfC"
        }

        "uses constraints to verify generated text" {
            mapOf(
                "" to listOf("AAAAAAA", "A"),
                "A" to listOf("#")
            ) withConstraints Constraints(maxLength = 1) shouldGenerate "A"
        }
    }

}) {
    override fun isolationMode() = IsolationMode.InstancePerLeaf
}

private data class TestParameters(
    val mockTransitions: Map<String, List<String>>,
    val order: Int = Int.MAX_VALUE,
    val constraints: Constraints = Constraints()
)

private infix fun Map<String, List<String>>.withOrder(order: Int): TestParameters = TestParameters(this, order)
private infix fun Map<String, List<String>>.withConstraints(constraints: Constraints): TestParameters = TestParameters(this, constraints = constraints)
private infix fun Map<String, List<String>>.shouldGenerate(result: String) = TestParameters(this).shouldGenerate(result)
private infix fun TestParameters.shouldGenerate(result: String) {
    this.mockTransitions.mapValues { (_, returnValues) ->
        mock<WeightedDice<String>> {
            on { roll() } doReturnConsecutively returnValues
        }
    }.let { dice ->
        MarkovChain(dice, "#").generate(order = this.order, constraints = this.constraints) shouldBe result
    }
}