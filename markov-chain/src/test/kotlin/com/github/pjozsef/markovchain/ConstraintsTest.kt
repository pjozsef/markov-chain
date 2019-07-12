package com.github.pjozsef.markovchain

import io.kotlintest.IsolationMode
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotThrow
import io.kotlintest.shouldThrow
import io.kotlintest.specs.FreeSpec
import java.lang.IllegalArgumentException

class ConstraintsTest : FreeSpec({

    "parameter validation" - {

        "minLenght is negative" {
            shouldThrow<IllegalArgumentException> {
                Constraints(minLength = -5)
            }
        }

        "minLength is zero" {
            shouldThrow<IllegalArgumentException> {
                Constraints(minLength = 0)
            }
        }

        "maxLenght is negative" {
            shouldThrow<IllegalArgumentException> {
                Constraints(maxLength = -5)
            }
        }

        "maxLength is zero" {
            shouldThrow<IllegalArgumentException> {
                Constraints(maxLength = 0)
            }
        }

        "maxLenth less than minLength" {
            shouldThrow<IllegalArgumentException> {
                Constraints(minLength = 4, maxLength = 3)
            }
        }

        "maxLenth less than startsWith length" {
            shouldThrow<IllegalArgumentException> {
                Constraints(startsWith = "toolong", maxLength = 3)
            }
        }

        "maxLenth less than endsWith length" {
            shouldThrow<IllegalArgumentException> {
                Constraints(endsWith = "toolong", maxLength = 3)
            }
        }

        "maxLenth equals minimal possible word with startsWith+endsWith" {
            shouldNotThrow<IllegalArgumentException> {
                Constraints(startsWith = "asdf", endsWith = "sdfk", maxLength = 5)
            }
        }

        "maxLenth less than minimal possible word with startsWith+endsWith" {
            shouldThrow<IllegalArgumentException> {
                Constraints(startsWith = "asdf", endsWith = "sdfk", maxLength = 4)
            }
        }
    }

    "evaluate" - {
        "empty constraint evaluates to true" {
            Constraints().evaluate("") shouldBe true
        }
        "minLength"{
            Constraints(minLength = 3) testWith mapOf(
                false to listOf("", "a", "bb"),
                true to listOf("333", "???)))))______")
            )
        }
        "maxLength"{
            Constraints(maxLength = 5) testWith mapOf(
                false to listOf("123456", "________________"),
                true to listOf("0", "333", "55555")
            )
        }
        "startsWith" {
            Constraints(startsWith = "asdf") testWith mapOf(
                false to listOf("asd", "_", "jkl", "-asdf"),
                true to listOf("asdf", "asdfasdf", "asdfjkl")
            )
        }
        "endsWith" {
            Constraints(endsWith = "asdf") testWith mapOf(
                false to listOf("", "_", "_____sdf", "asdf-"),
                true to listOf("asdf", "asdfasdf", "jklasdf")
            )
        }
        "contains" {
            Constraints(contains = listOf("asdf")) testWith mapOf(
                false to listOf("as_df", "", "12345678"),
                true to listOf("123asdf456", "asdfasdf", "asdfjkl", "jklasdf")
            )
        }
        "contains multiple" {
            Constraints(contains = listOf("a", "b", "c")) testWith mapOf(
                false to listOf("asd", "_", "abbaababababba", "-aaaacc", "bc"),
                true to listOf("abc", "cba", "_a_b_c_ccc_aa_bb_")
            )
        }
        "notContains" {
            Constraints(notContains = listOf("asdf")) testWith mapOf(
                false to listOf("123asdf456", "asdfasdf", "asdfjkl", "jklasdf"),
                true to listOf("as_df", "", "12345678")
            )
        }
        "notContains multiple" {
            Constraints(notContains = listOf("a", "b", "c")) testWith mapOf(
                false to listOf("a", "b", "c", "abc", "cba", "_a_b_c_ccc_aa_bb_"),
                true to listOf("sdf", "_", "")
            )
        }
        "excluding" {
            Constraints(excluding = listOf("a", "b", "c")) testWith mapOf(
                false to listOf("a", "b", "c"),
                true to listOf("sdf", "_", "", "abc", "cba", "_a_b_c_ccc_aa_bb_")
            )
        }
        "excluding multiple" {
            Constraints(excluding = listOf("as", "df")) testWith mapOf(
                false to listOf("as", "df"),
                true to listOf("as_", "_as", "_as_", "asdf", "_df", "df_", "_df_")
            )
        }
    }
}) {
    override fun isolationMode() = IsolationMode.InstancePerLeaf
}

private infix fun Constraints.testWith(inputs: Map<Boolean, List<String>>) {
    val expected = inputs.mapValues { (key, value) ->
        value.map { key }
    }
    val actual = inputs.mapValues { (_, value) ->
        value.map(this.evaluate)
    }
    actual shouldBe expected
}