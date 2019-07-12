package com.github.pjozsef.markovchain.util

import io.kotlintest.IsolationMode
import io.kotlintest.data.suspend.forall
import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec
import io.kotlintest.tables.row


class WordUtilsTest : FreeSpec({
    "combine single words" - {
        forall(
            row(
                "returns all combinations of words at matching letters",
                "red",
                "dimension",
                listOf(
                    "rension",
                    "redimension"
                )
            ),
            row(
                "returns words without duplication",
                "folder",
                "colored",
                listOf(
                    "folored",
                    "fored",
                    "fold",
                    "folded",
                    "foldered"
                )
            ),
            row(
                "returns words without duplication #2",
                "endure",
                "red",
                listOf(
                    "ed",
                    "end",
                    "endured"
                )
            )
        ) { test, first, second, expected ->
            test {
                WordUtils.combineWords(first, second) shouldBe expected
            }
        }
    }

    "combine multiple words" - {
        "should return the union of all combinations" {
            val startWords = listOf(
                "follow",
                "folder",
                "red"
            )
            val endWords = listOf(
                "endure",
                "dimension",
                "cinema"
            )
            val expected = listOf(
                "fon",
                "follon",
                "foldure",
                "foldendure",
                "folde",
                "foldere",
                "foldimension",
                "foldension",
                "foldema",
                "re",
                "rendure",
                "redure",
                "rension",
                "redimension",
                "rema"
            )
            WordUtils.combineWords(startWords, endWords) shouldBe expected
        }
    }

    "commonPostfixPrefixLength" - {
        "calculates the shared length of first word's postfix and second word's prefix" {
            WordUtils.commonPostfixPrefixLength(
                "accomodate",
                "datenbank"
            ) shouldBe 4
        }
        "calculates the larges shared substring's length" {
            WordUtils.commonPostfixPrefixLength(
                "ccccada",
                "adaqqqq"
            ) shouldBe 3
        }

        "returns 0 if the second word does not start with the first's end" {
            WordUtils.commonPostfixPrefixLength(
                "nothing",
                "common"
            ) shouldBe 0
        }
    }
}) {
    override fun isolationMode() = IsolationMode.InstancePerLeaf
}
