package com.github.pjozsef.markovchain.constraint

import com.github.pjozsef.markovchain.testutil.l
import io.kotlintest.IsolationMode
import io.kotlintest.data.forall
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotThrow
import io.kotlintest.shouldThrow
import io.kotlintest.specs.FreeSpec
import io.kotlintest.tables.row
import java.lang.IllegalArgumentException

class ConstraintsTest : FreeSpec({

    "parameter validation" - {

        "minLenght is negative" {
            shouldThrow<IllegalArgumentException> {
                Constraints<Any>(minLength = -5)
            }
        }

        "minLength is zero" {
            shouldThrow<IllegalArgumentException> {
                Constraints<Any>(minLength = 0)
            }
        }

        "maxLenght is negative" {
            shouldThrow<IllegalArgumentException> {
                Constraints<Any>(maxLength = -5)
            }
        }

        "maxLength is zero" {
            shouldThrow<IllegalArgumentException> {
                Constraints<Any>(maxLength = 0)
            }
        }

        "maxLenth less than minLength" {
            shouldThrow<IllegalArgumentException> {
                Constraints<Any>(minLength = 4, maxLength = 3)
            }
        }

        "maxLenth less than startsWith length" {
            shouldThrow<IllegalArgumentException> {
                Constraints(
                    startsWith = "toolong".l,
                    maxLength = 3
                )
            }
        }

        "maxLenth less than endsWith length" {
            shouldThrow<IllegalArgumentException> {
                Constraints(
                    endsWith = "toolong".l,
                    maxLength = 3
                )
            }
        }

        "maxLenth equals minimal possible word with startsWith+endsWith" {
            shouldNotThrow<IllegalArgumentException> {
                Constraints(
                    startsWith = "asdf".l,
                    endsWith = "sdfk".l,
                    maxLength = 5
                )
            }
        }

        "maxLenth less than minimal possible word with startsWith+endsWith" {
            shouldThrow<IllegalArgumentException> {
                Constraints(
                    startsWith = "asdf".l,
                    endsWith = "sdfk".l,
                    maxLength = 4
                )
            }
        }

        "startsWith and notStartsWith has common prefixes" {
            forall(
                row("asdf".l),
                row("asd".l),
                row("as".l),
                row("a".l)
            ) {
                shouldThrow<IllegalArgumentException> {
                    Constraints(
                        startsWith = "asdf".l,
                        notStartsWith = listOf(it)
                    )
                }
            }
        }

        "notStartsWith can be longer than startsWith" {
            shouldNotThrow<IllegalArgumentException> {
                Constraints(
                    startsWith = "asdf".l,
                    notStartsWith = listOf("asdfj".l)
                )
            }
        }

        "endsWith and notEndsWith has common prefixes" {
            forall(
                row("asdf".l),
                row("sdf".l),
                row("df".l),
                row("f".l)
            ) {
                shouldThrow<IllegalArgumentException> {
                    Constraints(
                        endsWith = "asdf".l,
                        notEndsWith = listOf(it)
                    )
                }
            }
        }

        "notEndsWith can be longer than endsWith" {
            shouldNotThrow<IllegalArgumentException> {
                Constraints(
                    endsWith = "asdf".l,
                    notEndsWith = listOf("aasdf".l)
                )
            }
        }
    }

    "evaluate" - {
        "empty constraint evaluates to true" {
            Constraints<String>().evaluate("".l) shouldBe true
        }
        "minLength"{
            Constraints<String>(minLength = 3) testWith mapOf(
                false to listOf("".l, "a".l, "bb".l),
                true to listOf("333".l, "???)))))______".l)
            )
        }
        "maxLength"{
            Constraints<String>(maxLength = 5) testWith mapOf(
                false to listOf("123456".l, "________________".l),
                true to listOf("0".l, "333".l, "55555".l)
            )
        }
        "startsWith" {
            Constraints(startsWith = "asdf".l) testWith mapOf(
                false to listOf("asd".l, "_".l, "jkl".l, "-asdf".l),
                true to listOf("asdf".l, "asdfasdf".l, "asdfjkl".l)
            )
        }
        "notStartsWith" {
            Constraints(
                notStartsWith = listOf(
                    "a".l,
                    "bb".l,
                    "theend".l
                )
            ) testWith mapOf(
                false to listOf("a".l, "aaaaa".l, "bb".l, "bbb".l, "bbba".l, "theend_".l),
                true to listOf("ba".l, "b".l, "end".l, "random".l)
            )
        }
        "endsWith" {
            Constraints(endsWith = "asdf".l) testWith mapOf(
                false to listOf("".l, "_".l, "_____sdf".l, "asdf-".l),
                true to listOf("asdf".l, "asdfasdf".l, "jklasdf".l)
            )
        }
        "notEndsWith" {
            Constraints(
                notEndsWith = listOf(
                    "a".l,
                    "bb".l,
                    "theend".l
                )
            ) testWith mapOf(
                false to listOf("a".l, "bb".l, "bbb".l, "bbba".l, "_theend".l),
                true to listOf("ab".l, "b".l, "end".l, "random".l)
            )
        }
        "contains" {
            Constraints(contains = listOf("asdf".l)) testWith mapOf(
                false to listOf("as_df".l, "".l, "12345678".l),
                true to listOf("123asdf456".l, "asdfasdf".l, "asdfjkl".l, "jklasdf".l)
            )
        }
        "contains multiple" {
            Constraints(
                contains = listOf(
                    "a".l,
                    "b".l,
                    "c".l
                )
            ) testWith mapOf(
                false to listOf("asd".l, "_".l, "abbaababababba".l, "-aaaacc".l, "bc".l),
                true to listOf("abc".l, "cba".l, "_a_b_c_ccc_aa_bb_".l)
            )
        }
        "notContains" {
            Constraints(notContains = listOf("asdf".l)) testWith mapOf(
                false to listOf("123asdf456".l, "asdfasdf".l, "asdfjkl".l, "jklasdf".l),
                true to listOf("as_df".l, "".l, "12345678".l)
            )
        }
        "notContains multiple" {
            Constraints(
                notContains = listOf(
                    "a".l,
                    "b".l,
                    "c".l
                )
            ) testWith mapOf(
                false to listOf("a".l, "b".l, "c".l, "abc".l, "cba".l, "_a_b_c_ccc_aa_bb_".l),
                true to listOf("sdf".l, "_".l, "".l)
            )
        }
        "excluding" {
            Constraints(
                excluding = listOf(
                    "a".l,
                    "b".l,
                    "c".l
                )
            ) testWith mapOf(
                false to listOf("a".l, "b".l, "c".l),
                true to listOf("sdf".l, "_".l, "".l, "abc".l, "cba".l, "_a_b_c_ccc_aa_bb_".l)
            )
        }
        "excluding multiple" {
            Constraints(
                excluding = listOf(
                    "as".l,
                    "df".l
                )
            ) testWith mapOf(
                false to listOf("as".l, "df".l),
                true to listOf("as_".l, "_as".l, "_as_".l, "asdf".l, "_df".l, "df_".l, "_df_".l)
            )
        }
    }

    "forWords" - {
        "returns empty constraint" {
            Constraints.forWords() shouldBe Constraints()
        }

        "fills fields correctly" {
            Constraints.forWords(
                minLength = 5,
                maxLength = 10,
                startsWith = "asd",
                notStartsWith = listOf("1", ".bc", "xy"),
                endsWith = "wqw",
                notEndsWith = listOf("555", "6"),
                contains = listOf("x", "yolo"),
                notContains = listOf("ck", "bm"),
                excluding = listOf("word1", "word2"),
                hybridPrefixPostfix = true
            ) shouldBe Constraints(
                minLength = 5,
                maxLength = 10,
                startsWith = listOf('a', 's', 'd'),
                notStartsWith = listOf(
                    listOf('1'),
                    listOf('.', 'b', 'c'),
                    listOf('x', 'y')
                ),
                endsWith = listOf('w', 'q', 'w'),
                notEndsWith = listOf(
                    listOf('5', '5', '5'),
                    listOf('6')
                ),
                contains = listOf(
                    listOf('x'),
                    listOf('y', 'o', 'l', 'o')
                ),
                notContains = listOf(
                    listOf('c', 'k'),
                    listOf('b', 'm')
                ),
                excluding = listOf(
                    listOf('w', 'o', 'r', 'd', '1'),
                    listOf('w', 'o', 'r', 'd', '2')
                ),
                hybridPrefixPostfix = true
            )
        }
    }
}) {
    override fun isolationMode() = IsolationMode.InstancePerLeaf
}

private infix fun <T> Constraints<T>.testWith(inputs: Map<Boolean, List<List<T>>>) {
    val expected = inputs.mapValues { (key, value) ->
        value.map { key }
    }
    val actual = inputs.mapValues { (_, value) ->
        value.map(this.evaluate)
    }
    actual shouldBe expected
}
