package com.github.pjozsef.markovchain.util

import com.github.pjozsef.markovchain.testutil.l
import com.github.pjozsef.markovchain.util.WordUtils.containsList
import com.github.pjozsef.markovchain.util.WordUtils.endsWith
import com.github.pjozsef.markovchain.util.WordUtils.startsWith
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FreeSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe


class WordUtilsTest : FreeSpec({
    "combine single words" - {
        forAll(
            row(
                "returns all combinations of words at matching letters",
                "red".l,
                "dimension".l,
                setOf(
                    "rension".l,
                    "redimension".l
                )
            ),
            row(
                "returns words without duplication",
                "folder".l,
                "colored".l,
                setOf(
                    "folored".l,
                    "fored".l,
                    "fold".l,
                    "folded".l,
                    "foldered".l
                )
            ),
            row(
                "returns words without duplication #2",
                "endure".l,
                "red".l,
                setOf(
                    "ed".l,
                    "end".l,
                    "endured".l
                )
            )
        ) { test, first, second, expected ->
            test {
                WordUtils.combineSingleWords(first, second) shouldBe expected
            }
        }
    }

    "combine multiple words" - {
        "should return the union of all combinations" {
            val startWords = listOf(
                "follow".l,
                "folder".l,
                "red".l
            )
            val endWords = listOf(
                "endure".l,
                "dimension".l,
                "cinema".l
            )
            val expected = setOf(
                "fon".l,
                "follon".l,
                "foldure".l,
                "foldendure".l,
                "folde".l,
                "foldere".l,
                "foldimension".l,
                "foldension".l,
                "foldema".l,
                "re".l,
                "rendure".l,
                "redure".l,
                "rension".l,
                "redimension".l,
                "rema".l
            )
            WordUtils.combineWords(startWords, endWords) shouldBe expected
        }
    }

    "commonPostfixPrefixLength List" - {
        "calculates the minimum from each pairing" {
            WordUtils.commonPostfixPrefixMultiLength(
                listOf("accomodate".l),
                listOf("datenbank".l, "eta".l)
            ) shouldBe 1
        }
    }

    "commonPostfixPrefixLength" - {
        "calculates the shared length of first word's postfix and second word's prefix" {
            WordUtils.commonPostfixPrefixLength(
                "accomodate".l,
                "datenbank".l
            ) shouldBe 4
        }
        "calculates the largest shared substring's length" {
            WordUtils.commonPostfixPrefixLength(
                "ccccada".l,
                "adaqqqq".l
            ) shouldBe 3
        }

        "returns 0 if the second word does not start with the first's end" {
            WordUtils.commonPostfixPrefixLength(
                "nothing".l,
                "common".l
            ) shouldBe 0
        }
    }

    "startsWith" {
        forAll(
            row(listOf(), listOf(), true),
            row(listOf("a", "b"), listOf("c"), false),
            row(listOf("a"), listOf("a"), true),
            row(listOf("a", "a", "c"), listOf("a", "a"), true),
            row(listOf("a", "b"), listOf("a", "b", "c"), false)
        ) { list, prefix, expected ->
            list.startsWith(prefix) shouldBe expected
        }
    }

    "endsWith" {
        forAll(
            row(listOf(), listOf(), true),
            row(listOf("a", "b"), listOf("c"), false),
            row(listOf("a"), listOf("a"), true),
            row(listOf("a", "a"), listOf("a"), true),
            row(listOf("b", "a", "c"), listOf("a", "c"), true),
            row(listOf("a", "b"), listOf("a", "b", "c"), false)
        ) { list, suffix, expected ->
            list.endsWith(suffix) shouldBe expected
        }
    }

    "containsList" {
        forAll(
            row(listOf(), listOf(), true),
            row(listOf("a", "b"), listOf("c"), false),
            row(listOf("a"), listOf("a"), true),
            row(listOf("a", "a"), listOf("a"), true),
            row(listOf("a", "a"), listOf("a", "a"), true),
            row(listOf("b", "a"), listOf("a"), true),
            row(listOf("a", "a", "b", "a", "a"), listOf("a", "b", "a"), true),
            row(listOf("a", "b"), listOf("a", "b", "c"), false)
        ) { list, content, expected ->
            list.containsList(content) shouldBe expected
        }
    }
}) {
    override fun isolationMode() = IsolationMode.InstancePerLeaf
}
