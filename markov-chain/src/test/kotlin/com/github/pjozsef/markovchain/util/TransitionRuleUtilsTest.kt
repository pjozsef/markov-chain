package com.github.pjozsef.markovchain.util

import com.github.pjozsef.markovchain.testutil.l
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FreeSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe


class TransitionRuleUtilsTest : FreeSpec({
    "TransitionRuleUtils" - {
        val words = listOf(
            "%comment1".l,
            "%comment2".l,
            "foo".l,
            "bar".l,
            "baz".l,
            "qux".l,
            "quux".l,
            "quuz".l
        )

        forAll(
            row(
                1, mapOf(
                    "" to mapOf("f" to 1, "b" to 2, "q" to 3),
                    "f" to mapOf("o" to 1),
                    "o" to mapOf("o" to 1, "#" to 1),
                    "b" to mapOf("a" to 2),
                    "a" to mapOf("r" to 1, "z" to 1),
                    "r" to mapOf("#" to 1),
                    "z" to mapOf("#" to 2),
                    "q" to mapOf("u" to 3),
                    "u" to mapOf("x" to 2, "u" to 2, "z" to 1),
                    "x" to mapOf("#" to 2)
                ),
                mapOf(
                    "" to mapOf("o" to 1, "r" to 1, "z" to 2, "x" to 2),
                    "o" to mapOf("o" to 1, "f" to 1),
                    "f" to mapOf("#" to 1),
                    "r" to mapOf("a" to 1),
                    "a" to mapOf("b" to 2),
                    "b" to mapOf("#" to 2),
                    "z" to mapOf("a" to 1, "u" to 1),
                    "x" to mapOf("u" to 2),
                    "u" to mapOf("q" to 3, "u" to 2),
                    "q" to mapOf("#" to 3)
                )
            ),
            row(
                2, mapOf(
                    "" to mapOf("f" to 1, "b" to 2, "q" to 3),
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
                ),
                mapOf(
                    "" to mapOf("o" to 1, "r" to 1, "z" to 2, "x" to 2),
                    "o" to mapOf("o" to 1, "f" to 1),
                    "f" to mapOf("#" to 1),
                    "r" to mapOf("a" to 1),
                    "a" to mapOf("b" to 2),
                    "b" to mapOf("#" to 2),
                    "z" to mapOf("a" to 1, "u" to 1),
                    "x" to mapOf("u" to 2),
                    "u" to mapOf("q" to 3, "u" to 2),
                    "q" to mapOf("#" to 3),
                    "oo" to mapOf("f" to 1),
                    "of" to mapOf("#" to 1),
                    "ra" to mapOf("b" to 1),
                    "ab" to mapOf("#" to 2),
                    "za" to mapOf("b" to 1),
                    "xu" to mapOf("q" to 1, "u" to 1),
                    "uq" to mapOf("#" to 3),
                    "uu" to mapOf("q" to 2),
                    "zu" to mapOf("u" to 1)
                )
            ),
            row(
                3, mapOf(
                    "" to mapOf("f" to 1, "b" to 2, "q" to 3),
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
                ),
                mapOf(
                    "" to mapOf("o" to 1, "r" to 1, "z" to 2, "x" to 2),
                    "o" to mapOf("o" to 1, "f" to 1),
                    "f" to mapOf("#" to 1),
                    "r" to mapOf("a" to 1),
                    "a" to mapOf("b" to 2),
                    "b" to mapOf("#" to 2),
                    "z" to mapOf("a" to 1, "u" to 1),
                    "x" to mapOf("u" to 2),
                    "u" to mapOf("q" to 3, "u" to 2),
                    "q" to mapOf("#" to 3),
                    "oo" to mapOf("f" to 1),
                    "of" to mapOf("#" to 1),
                    "ra" to mapOf("b" to 1),
                    "ab" to mapOf("#" to 2),
                    "za" to mapOf("b" to 1),
                    "xu" to mapOf("q" to 1, "u" to 1),
                    "uq" to mapOf("#" to 3),
                    "uu" to mapOf("q" to 2),
                    "zu" to mapOf("u" to 1),
                    "oof" to mapOf("#" to 1),
                    "rab" to mapOf("#" to 1),
                    "zab" to mapOf("#" to 1),
                    "xuq" to mapOf("#" to 1),
                    "xuu" to mapOf("q" to 1),
                    "uuq" to mapOf("#" to 2),
                    "zuu" to mapOf("q" to 1)
                )
            ),
            row(
                4, mapOf(
                    "" to mapOf("f" to 1, "b" to 2, "q" to 3),
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
                ),
                mapOf(
                    "" to mapOf("o" to 1, "r" to 1, "z" to 2, "x" to 2),
                    "o" to mapOf("o" to 1, "f" to 1),
                    "f" to mapOf("#" to 1),
                    "r" to mapOf("a" to 1),
                    "a" to mapOf("b" to 2),
                    "b" to mapOf("#" to 2),
                    "z" to mapOf("a" to 1, "u" to 1),
                    "x" to mapOf("u" to 2),
                    "u" to mapOf("q" to 3, "u" to 2),
                    "q" to mapOf("#" to 3),
                    "oo" to mapOf("f" to 1),
                    "of" to mapOf("#" to 1),
                    "ra" to mapOf("b" to 1),
                    "ab" to mapOf("#" to 2),
                    "za" to mapOf("b" to 1),
                    "xu" to mapOf("q" to 1, "u" to 1),
                    "uq" to mapOf("#" to 3),
                    "uu" to mapOf("q" to 2),
                    "zu" to mapOf("u" to 1),
                    "oof" to mapOf("#" to 1),
                    "rab" to mapOf("#" to 1),
                    "zab" to mapOf("#" to 1),
                    "xuq" to mapOf("#" to 1),
                    "xuu" to mapOf("q" to 1),
                    "uuq" to mapOf("#" to 2),
                    "zuu" to mapOf("q" to 1),
                    "xuuq" to mapOf("#" to 1),
                    "zuuq" to mapOf("#" to 1)
                )
            )
        ) { order, expectedForward, expectedBackward ->
            "converts words into rules $order" {
                val (forward, backward) = TransitionRule.fromWords(words, order, listOf("#"), stringCommentFilter("%"))
                forward shouldBe expectedForward.transform()
                backward shouldBe expectedBackward.transform()
            }
        }
    }
}) {
    override fun isolationMode() = IsolationMode.InstancePerLeaf
}

private fun Map<String, Map<String, Int>>.transform(): Map<List<String>, Map<List<String>, Int>> =
    this.mapKeys {
        it.key.l
    }.mapValues {
        it.value.mapKeys {
            it.key.l
        }
    }
