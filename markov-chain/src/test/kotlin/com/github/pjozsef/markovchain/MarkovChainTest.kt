package com.github.pjozsef.markovchain

import com.github.pjozsef.markovchain.constraint.Constraints
import com.github.pjozsef.markovchain.testutil.l
import com.github.pjozsef.markovchain.util.*
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
            ).l shouldGenerate "ABAABB".l
        }

        "generates char sequence with multi character delimiter" {
            mapOf(
                "" to listOf("A"),
                "A" to listOf("B", "A", "B"),
                "B" to listOf("A", "B", "#"),
                "#" to listOf("#")
            ).l withOrder 2 withDelimiter listOf("#", "#") shouldGenerate "ABAABB".l
        }

        "supports higher order transition" {
            mapOf(
                "" to listOf("A"),
                "A" to listOf("A"),
                "AAAA" to listOf("BB"),
                "BB" to listOf("#")
            ).l shouldGenerate "AAAABB".l
        }

        "uses set order as maximum order" {
            mapOf(
                "" to listOf("A"),
                "A" to listOf("A"),
                "AA" to listOf("BB"),
                "B" to listOf("#"),
                "AABB" to listOf("C"),
                "C" to listOf("#")
            ).l withOrder 2 shouldGenerate "AABB".l
        }

        "starts generation with prefix if supplied" {
            mapOf(
                "" to listOf("A"),
                "A" to listOf("#"),
                "f" to listOf("C"),
                "C" to listOf("#")
            ).l withConstraints Constraints(startsWith = "asdf".l) shouldGenerate "asdfC".l
        }

        "uses constraints to verify generated text" {
            mapOf(
                "" to listOf("AAAAAAA", "A"),
                "A" to listOf("#")
            ).l withConstraints Constraints(maxLength = 1) shouldGenerate "A".l
        }

        "returns intermediate result once retry count reached" {
            mapOf(
                "" to listOf("A", "#"),
                "A" to listOf("#")
            ).l.markov().generate(
                1, 3, constraints = Constraints(
                    minLength = 1
                )
            ) shouldBe setOf("A".l)
        }

        "uses backward transition rules if 'endsWith' constraint is set" {
            mapOf(
                "" to listOf("#")
            ).l withBackwardRule mapOf(
                "A" to listOf("B"),
                "B" to listOf("B"),
                "BB" to listOf("C"),
                "BBC" to listOf("#")
            ).l withOrder 3 withConstraints Constraints(
                endsWith = "ASD".l
            ) shouldGenerate "CBBASD".l
        }

        "uses both way transitions and combines results" {
            mapOf(
                "fol" to listOf("d"),
                "d" to listOf("e"),
                "e" to listOf("r"),
                "r" to listOf("#")
            ).l withBackwardRule mapOf(
                "de" to listOf("r"),
                "r" to listOf("o"),
                "o" to listOf("l", "c"),
                "l" to listOf("o"),
                "c" to listOf("#")
            ).l withOrder 3 withConstraints Constraints(
                hybridPrefixPostfix = true,
                startsWith = "fol".l,
                endsWith = "ed".l
            ) shouldGenerate listOf(
                "folored".l,
                "folded".l,
                "foldered".l
            )
        }

        "uses both way transitions without combining them" {
            val params = mapOf(
                "fol" to listOf("d"),
                "d" to listOf("e", "#"),
                "e" to listOf("d")
            ).l withBackwardRule mapOf(
                "de" to listOf("w"),
                "w" to listOf("o"),
                "l" to listOf("l", "o"),
                "o" to listOf("l", "f"),
                "f" to listOf("#")
            ).l withOrder 3 withConstraints Constraints(
                hybridPrefixPostfix = false,
                startsWith = "fol".l,
                endsWith = "ed".l
            )
            params.markov().generate(
                order = 3,
                count = 6,
                constraints = params.constraints
            ) shouldBe setOf(
                "folded".l,
                "followed".l
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
            ).map { it.l }
            val count = 10

            val markovChain =
                MarkovChain(TransitionRule.fromWords(words, delimiter = listOf("#"), commentFilter = stringCommentFilter()).asDice(), end = "#".l)

            "returned result size is as specified" {
                markovChain
                    .generate(1, count)
                    .size shouldBe count
            }

            "returns at least as much words as specified when using hybrid strategy" {
                markovChain
                    .generate(
                        1, count, constraints = Constraints(
                            startsWith = "q".l,
                            endsWith = "x".l
                        )
                    )
                    .size shouldBeGreaterThanOrEqual count
            }
        }

        "with generalized type" - {

            "with multi delimiter" {
                mapOf(
                    listOf<Int>() to listOf(listOf(1)),
                    listOf(1) to listOf(listOf(2)),
                    listOf(2) to listOf(listOf(2)),
                    listOf(2, 2) to listOf(listOf(3)),
                    listOf(3) to listOf(listOf(0)),
                    listOf(0) to listOf(listOf(0))
                ) withOrder 2 withDelimiter listOf(0) shouldGenerate listOf(
                    listOf(1, 2, 2, 3)
                )
            }
        }
    }

}) {
    override fun isolationMode() = IsolationMode.InstancePerLeaf
}

private data class TestParameters<T>(
    val mockForwardTransitions: Map<List<T>, List<List<T>>>,
    val mockBackwardTransitions: Map<List<T>, List<List<T>>> = emptyMap(),
    val order: Int = Int.MAX_VALUE,
    val constraints: Constraints<T> = Constraints(),
    val retryCount: Int = 2,
    val delimiter: List<T>? = null
)

private infix fun <T> Map<List<T>, List<List<T>>>.withOrder(order: Int): TestParameters<T> =
    TestParameters(this, order = order)

private infix fun <T> TestParameters<T>.withOrder(order: Int): TestParameters<T> =
    this.copy(order = order)

private infix fun <T> Map<List<T>, List<List<T>>>.withConstraints(constraints: Constraints<T>): TestParameters<T> =
    TestParameters<T>(this, constraints = constraints)

private infix fun <T> TestParameters<T>.withConstraints(constraints: Constraints<T>): TestParameters<T> =
    this.copy(constraints = constraints)

private infix fun <T> TestParameters<T>.withDelimiter(delimiter: List<T>): TestParameters<T> =
    this.copy(delimiter = delimiter)

private infix fun <T> Map<List<T>, List<List<T>>>.withBackwardRule(that: Map<List<T>, List<List<T>>>): TestParameters<T> =
    TestParameters(this, that)

private infix fun <T> Map<List<T>, List<List<T>>>.shouldGenerate(result: List<T>) =
    TestParameters(this).shouldGenerate(result)

private infix fun <T> TestParameters<T>.shouldGenerate(result: List<T>) =
    this shouldGenerate setOf(result)

private infix fun <T> TestParameters<T>.shouldGenerate(result: Collection<List<T>>) {
    this.markov().generate(order = this.order, count = 1, constraints = this.constraints) shouldBe result.toSet()
}

private fun <T> Map<List<T>, List<List<T>>>.markov() = TestParameters(this).markov()

private fun <T> TestParameters<T>.markov() = MarkovChain(
    Transition(
        this.mockForwardTransitions.generateDice(),
        this.mockBackwardTransitions.generateDice()
    ),
    this.delimiter ?: listOf("#") as List<T>,
    this.retryCount
)

private fun <T> Map<List<T>, List<List<T>>>.generateDice(): MapTransition<T> = this.mapValues { (_, returnValues) ->
    mock<WeightedDice<List<T>>> {
        on { roll() } doReturnConsecutively returnValues
    }
}
