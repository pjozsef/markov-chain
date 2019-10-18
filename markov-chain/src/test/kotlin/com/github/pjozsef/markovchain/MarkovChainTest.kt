package com.github.pjozsef.markovchain

import com.github.pjozsef.markovchain.util.TransitionRule
import com.github.pjozsef.markovchain.util.WeightedDice
import com.github.pjozsef.markovchain.util.asDice
import com.nhaarman.mockitokotlin2.doReturnConsecutively
import com.nhaarman.mockitokotlin2.mock
import io.kotlintest.IsolationMode
import io.kotlintest.matchers.numerics.shouldBeGreaterThanOrEqual
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

        "supports higher order transition" {
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

        "returns intermediate result once retry count reached" {
            mapOf(
                "" to listOf("A", "#"),
                "A" to listOf("#")
            ).markov().generate(1, 3, constraints = Constraints(minLength = 1)) shouldBe setOf("A")
        }

        "uses backward transition rules if 'endsWith' constraint is set" {
            mapOf(
                "" to listOf("#")
            ) withBackwardRule mapOf(
                "A" to listOf("B"),
                "B" to listOf("B"),
                "BB" to listOf("C"),
                "BBC" to listOf("#")
            ) withOrder 3 withConstraints Constraints(endsWith = "ASD") shouldGenerate "CBBASD"
        }

        "uses both way transitions and combines results" {
            mapOf(
                "fol" to listOf("d"),
                "d" to listOf("e"),
                "e" to listOf("r"),
                "r" to listOf("#")
            ) withBackwardRule mapOf(
                "de" to listOf("r"),
                "r" to listOf("o"),
                "o" to listOf("l", "c"),
                "l" to listOf("o"),
                "c" to listOf("#")
            ) withOrder 3 withConstraints Constraints(
                hybridPrefixPostfix = true,
                startsWith = "fol",
                endsWith = "ed"
            ) shouldGenerate listOf(
                "folored",
                "folded",
                "foldered"
            )
        }

        "uses both way transitions without combining them" {
            val params = mapOf(
                "fol" to listOf("d"),
                "d" to listOf("e", "#"),
                "e" to listOf("d")
            ) withBackwardRule mapOf(
                "de" to listOf("w"),
                "w" to listOf("o"),
                "l" to listOf("l", "o"),
                "o" to listOf("l", "f"),
                "f" to listOf("#")
            ) withOrder 3 withConstraints Constraints(
                hybridPrefixPostfix = false,
                startsWith = "fol",
                endsWith = "ed"
            )
            params.markov().generate(
                order = 3,
                count = 6,
                constraints = params.constraints
            ) shouldBe setOf(
                "folded",
                "followed"
            )
        }

        "with real transition rules" - {
            val words = listOf(
                "foo",
                "bar",
                "baz",
                "qux",
                "quux",
                "quuz"
            )
            val count = 10

            val markovChain = MarkovChain(TransitionRule.fromWords(words).asDice())

            "returned result size is as specified" {
                markovChain
                    .generate(1, count)
                    .size shouldBe count
            }

            "returns at least as much words as specified when using hybrid strategy" {
                markovChain
                    .generate(1, count, constraints = Constraints(startsWith = "q", endsWith = "x"))
                    .size shouldBeGreaterThanOrEqual count
            }
        }
    }

}) {
    override fun isolationMode() = IsolationMode.InstancePerLeaf
}

private data class TestParameters(
    val mockForwardTransitions: Map<String, List<String>>,
    val mockBackwardTransitions: Map<String, List<String>> = emptyMap(),
    val order: Int = Int.MAX_VALUE,
    val constraints: Constraints = Constraints(),
    val retryCount: Int = 2
)

private infix fun Map<String, List<String>>.withOrder(order: Int): TestParameters =
    TestParameters(this, order = order)

private infix fun TestParameters.withOrder(order: Int): TestParameters =
    this.copy(order = order)

private infix fun Map<String, List<String>>.withConstraints(constraints: Constraints): TestParameters =
    TestParameters(this, constraints = constraints)

private infix fun TestParameters.withConstraints(constraints: Constraints): TestParameters =
    this.copy(constraints = constraints)

private infix fun Map<String, List<String>>.withBackwardRule(that: Map<String, List<String>>): TestParameters =
    TestParameters(this, that)

private infix fun Map<String, List<String>>.shouldGenerate(result: String) =
    TestParameters(this).shouldGenerate(result)

private infix fun TestParameters.shouldGenerate(result: String) =
    this shouldGenerate setOf(result)

private infix fun TestParameters.shouldGenerate(result: Collection<String>) {
    this.markov().generate(order = this.order, count = 1, constraints = this.constraints) shouldBe result.toSet()
}

private fun Map<String, List<String>>.markov() = TestParameters(this).markov()

private fun TestParameters.markov() = MarkovChain(
    Transition(
        this.mockForwardTransitions.generateDice(),
        this.mockBackwardTransitions.generateDice()
    ),
    "#",
    this.retryCount
)

private fun Map<String, List<String>>.generateDice(): MapTransition = this.mapValues { (_, returnValues) ->
    mock<WeightedDice<String>> {
        on { roll() } doReturnConsecutively returnValues
    }
}
