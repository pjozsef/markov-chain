package com.github.pjozsef.markovchain.util

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FreeSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import java.util.*

class RandomUtilsTest: FreeSpec({
    val random = mock<Random>()
    "weightedCoin" - {
        lateinit var coin: WeightedCoin
        "with invalid constructor parameters" - {
            "trueProbability less than 0" {
                shouldThrow<IllegalArgumentException> {
                    WeightedCoin(-0.0001)
                }
            }
            "trueProbability more than 1" {
                shouldThrow<IllegalArgumentException> {
                    WeightedCoin(1.0001)
                }
            }
        }
        "with 80% trueProbability" - {
            coin = WeightedCoin(0.8, random)

            "should return true when random number below 0.8" {
                whenever(random.nextDouble()).thenReturn(0.8)

                coin.flip() shouldBe true
            }
            "should return true when random number above 0.8" {
                whenever(random.nextDouble()).thenReturn(0.8+0.01)

                coin.flip() shouldBe false
            }
        }
        "equals is implemented" {
            val coin1 = WeightedCoin(0.3, random)
            val coin2 = WeightedCoin(0.3, random)

            coin1 shouldBe coin2
        }
        "statistics should reflect trueProbability" - {
            forAll(
                row(0.5),
                row(0.2),
                row(0.1),
                row(1.0),
                row(0.8),
                row(0.0)
            ){ trueProbability ->
                coin = WeightedCoin(trueProbability)
                val flips = 1_000_000
                val results = (1..flips).map { coin.flip() }.groupBy { it }.mapValues { it.value.size }
                val truePercentage = results.getOrDefault(true, 0).toDouble()/flips
                val falsePercentage = results.getOrDefault(false, 0).toDouble()/flips

                truePercentage  shouldBe (trueProbability plusOrMinus(0.01))
                falsePercentage  shouldBe (1-trueProbability plusOrMinus(0.01))
            }
        }
    }

    "flipCoin" - {
        val seed = 4235235L
        val flipCoinRandom = Random(seed)
        val weightedCoinRandom = Random(seed)

        "should work the same way as WeightedCoin" {
            val trueProbability = 0.3542
            (0..100_000).asSequence().map {
                flipCoin(trueProbability, flipCoinRandom) to WeightedCoin(trueProbability, weightedCoinRandom).flip()
            }.forEach { (result, actual) ->
                result shouldBe actual
            }
        }
    }

    "weightedDice" - {
        lateinit var dice: WeightedDice<String>
        "with invalid constructor parameters" - {
            "contains negative probability" {
                shouldThrow<IllegalArgumentException> {
                    WeightedDice(mapOf(
                        "A" to 1.0,
                        "B" to 1.0,
                        "C" to -1.0
                    ))
                }
            }
        }

        "normalizes inputs" {
            val percentages = listOf(80, 10, 10)
            val values = listOf("A", "B", "C")
            val probabilities = values.zip(percentages).toMap()
            dice = WeightedDice(probabilities)
            val rolls = 1_000_000
            val results = (1..rolls).map { dice.roll() }.groupBy { it }.mapValues { it.value.size }

            values.map {
                val expected = probabilities.getValue(it).toDouble()/100.0
                val actual = results.getOrDefault(it, 0).toDouble()/rolls
                actual to expected
            }.forEach { (actual, expected) ->
                actual  shouldBe (expected plusOrMinus(0.01))
            }
        }
        "equals is implemented" {
            val probability = mapOf(
                "A" to 1.0,
                "B" to 1.0,
                "C" to 3.0
            )
            val die1 = WeightedDice(probability, random)
            val die2 = WeightedDice(probability, random)

            die1 shouldBe die2
        }
        "statistics should reflect probabilities" {
            forAll(
                row(listOf(0.5, 0.3, 0.2)),
                row(listOf(0.8, 0.1, 0.1)),
                row(listOf(0.0, 0.6, 0.4)),
                row(listOf(1.0, 0.0, 0.0))
            ){ percentages ->
                val values = listOf("A", "B", "C")
                val probabilities = values.zip(percentages).toMap()
                dice = WeightedDice(probabilities)
                val rolls = 1_000_000
                val results = (1..rolls).map { dice.roll() }.groupBy { it }.mapValues { it.value.size }

                values.map {
                    val expected = probabilities.getOrDefault(it, 0.0)
                    val actual = results.getOrDefault(it, 0).toDouble()/rolls
                    actual to expected
                }.forEach { (actual, expected) ->
                    actual  shouldBe (expected plusOrMinus(0.01))
                }
            }
        }
    }

    "intWeightedDice" - {
        val seed = 4235235L
        val intWeightedRandom = Random(seed)
        val weightedRandom = Random(seed)

        "should work the same way as WeightedDice" {
            val probabilities = listOf(0.55, 0.25, 0.2)
            val probabilityList = (0..2).zip(probabilities)
            (0..100_000).asSequence().map {
                intWeightedDice(probabilities, intWeightedRandom).roll() to WeightedDice(probabilityList, weightedRandom).roll()
            }.forEach { (result, actual) ->
                result shouldBe actual
            }
        }
    }
}){
    override fun isolationMode() = IsolationMode.InstancePerLeaf
}
