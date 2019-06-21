package com.github.pjozsef.markovchain.util

import io.kotlintest.IsolationMode
import io.kotlintest.data.suspend.forall
import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec
import io.kotlintest.tables.row


class TransitionRuleUtilsTest : FreeSpec({
    "TransitionRuleUtils" - {
        val words = listOf(
            "foo",
            "bar",
            "baz",
            "qux",
            "quux",
            "quuz"
        )

        forall(
            row(
                1, mapOf(
                    "f" to mapOf("o" to 1),
                    "o" to mapOf("o" to 1, "#" to 1),
                    "b" to mapOf("a" to 2),
                    "a" to mapOf("r" to 1, "z" to 1),
                    "r" to mapOf("#" to 1),
                    "z" to mapOf("#" to 2),
                    "q" to mapOf("u" to 3),
                    "u" to mapOf("x" to 2, "u" to 2, "z" to 1),
                    "x" to mapOf("#" to 2)
                )
            ),
            row(
                2, mapOf(
                    "f" to mapOf("o" to 1),
                    "o" to mapOf("o" to 1, "#" to 1),
                    "b" to mapOf("a" to 2),
                    "a" to mapOf("r" to 1, "z" to 1),
                    "r" to mapOf("#" to 1),
                    "z" to mapOf("#" to 2),
                    "q" to mapOf("u" to 3),
                    "u" to mapOf("x" to 2, "u" to 2, "z" to 1),
                    "x" to mapOf("#" to 2),
                    "fo" to mapOf("o" to 1),
                    "oo" to mapOf("#" to 1),
                    "ba" to mapOf("r" to 1, "z" to 1),
                    "ar" to mapOf("#" to 1),
                    "az" to mapOf("#" to 1),
                    "qu" to mapOf("x" to 1, "u" to 2),
                    "ux" to mapOf("#" to 2),
                    "uu" to mapOf("x" to 1, "z" to 1),
                    "uz" to mapOf("#" to 1)
                )
            ),
            row(
                3, mapOf(
                    "f" to mapOf("o" to 1),
                    "o" to mapOf("o" to 1, "#" to 1),
                    "b" to mapOf("a" to 2),
                    "a" to mapOf("r" to 1, "z" to 1),
                    "r" to mapOf("#" to 1),
                    "z" to mapOf("#" to 2),
                    "q" to mapOf("u" to 3),
                    "u" to mapOf("x" to 2, "u" to 2, "z" to 1),
                    "x" to mapOf("#" to 2),
                    "fo" to mapOf("o" to 1),
                    "oo" to mapOf("#" to 1),
                    "ba" to mapOf("r" to 1, "z" to 1),
                    "ar" to mapOf("#" to 1),
                    "az" to mapOf("#" to 1),
                    "qu" to mapOf("x" to 1, "u" to 2),
                    "ux" to mapOf("#" to 2),
                    "uu" to mapOf("x" to 1, "z" to 1),
                    "uz" to mapOf("#" to 1),
                    "foo" to mapOf("#" to 1),
                    "bar" to mapOf("#" to 1),
                    "baz" to mapOf("#" to 1),
                    "qux" to mapOf("#" to 1),
                    "quu" to mapOf("x" to 1, "z" to 1),
                    "uux" to mapOf("#" to 1),
                    "uuz" to mapOf("#" to 1)
                )
            ),
            row(
                4, mapOf(
                    "f" to mapOf("o" to 1),
                    "o" to mapOf("o" to 1, "#" to 1),
                    "b" to mapOf("a" to 2),
                    "a" to mapOf("r" to 1, "z" to 1),
                    "r" to mapOf("#" to 1),
                    "z" to mapOf("#" to 2),
                    "q" to mapOf("u" to 3),
                    "u" to mapOf("x" to 2, "u" to 2, "z" to 1),
                    "x" to mapOf("#" to 2),
                    "fo" to mapOf("o" to 1),
                    "oo" to mapOf("#" to 1),
                    "ba" to mapOf("r" to 1, "z" to 1),
                    "ar" to mapOf("#" to 1),
                    "az" to mapOf("#" to 1),
                    "qu" to mapOf("x" to 1, "u" to 2),
                    "ux" to mapOf("#" to 2),
                    "uu" to mapOf("x" to 1, "z" to 1),
                    "uz" to mapOf("#" to 1),
                    "foo" to mapOf("#" to 1),
                    "bar" to mapOf("#" to 1),
                    "baz" to mapOf("#" to 1),
                    "qux" to mapOf("#" to 1),
                    "quu" to mapOf("x" to 1, "z" to 1),
                    "uux" to mapOf("#" to 1),
                    "uuz" to mapOf("#" to 1),
                    "quux" to mapOf("#" to 1),
                    "quuz" to mapOf("#" to 1)
                )
            )
        ) { order, expected ->
            "converts words into rules $order" {
                TransitionRule.fromWords(words, order) shouldBe expected
            }
        }
    }
}) {
    override fun isolationMode() = IsolationMode.InstancePerLeaf
}