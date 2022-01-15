package com.github.pjozsef.markovchain.constraint

import com.github.pjozsef.markovchain.testutil.l
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FreeSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe

class ConstraintsTest : FreeSpec({

    "parameter validation" - {

        "minLength is negative" {
            shouldThrow<IllegalArgumentException> {
                Constraints<Any>(minLength = -5)
            }
        }

        "minLength is zero" {
            shouldThrow<IllegalArgumentException> {
                Constraints<Any>(minLength = 0)
            }
        }

        "maxLength is negative" {
            shouldThrow<IllegalArgumentException> {
                Constraints<Any>(maxLength = -5)
            }
        }

        "maxLength is zero" {
            shouldThrow<IllegalArgumentException> {
                Constraints<Any>(maxLength = 0)
            }
        }

        "maxLength less than minLength" {
            shouldThrow<IllegalArgumentException> {
                Constraints<Any>(minLength = 4, maxLength = 3)
            }
        }

        "maxLength less than startsWith length" {
            shouldThrow<IllegalArgumentException> {
                Constraints(
                    startsWith = listOf("toolong".l),
                    maxLength = 3
                )
            }
        }

        "maxLength less than endsWith length" {
            shouldThrow<IllegalArgumentException> {
                Constraints(
                    endsWith = listOf("toolong".l),
                    maxLength = 3
                )
            }
        }

        "maxLength equals minimal possible word with startsWith+endsWith" {
            shouldNotThrow<IllegalArgumentException> {
                Constraints(
                    startsWith = listOf("asdf".l),
                    endsWith = listOf("sdfk".l),
                    maxLength = 5
                )
            }
        }

        "maxLength less than minimal possible word with startsWith+endsWith" {
            shouldThrow<IllegalArgumentException> {
                Constraints(
                    startsWith = listOf("asdf".l),
                    endsWith = listOf("sdfk".l),
                    maxLength = 4
                )
            }
        }

        "startsWith and notStartsWith has common prefixes" {
            forAll(
                row(listOf("asdf".l)),
                row(listOf("asd".l)),
                row(listOf("as".l)),
                row(listOf("a".l)),
                row(listOf("as".l, "a".l))
            ) {
                shouldThrow<IllegalArgumentException> {
                    Constraints(
                        startsWith = listOf("asdf".l),
                        notStartsWith = it
                    )
                }
            }
        }

        "startsWith and notStartsWith has common prefixes list" {
            shouldThrow<Exception> {
                Constraints(
                    startsWith = listOf("asdf".l, "e".l),
                    notStartsWith = listOf("a".l, "asdf".l, "e".l)
                )
            }
        }

        "startsWith and notStartsWith does not fail if there is at least one valid combination" {
            shouldNotThrow<Exception> {
                Constraints(
                    startsWith = listOf("asdf".l, "e".l),
                    notStartsWith = listOf("asdf".l)
                )
            }
        }

        "notStartsWith can be longer than startsWith" {
            shouldNotThrow<IllegalArgumentException> {
                Constraints(
                    startsWith = listOf("asdf".l),
                    notStartsWith = listOf("asdfj".l, "asdfk".l)
                )
            }
        }

        "endsWith and notEndsWith has common prefixes" {
            forAll(
                row(listOf("asdf".l)),
                row(listOf("sdf".l)),
                row(listOf("df".l)),
                row(listOf("f".l)),
                row(listOf("df".l, "f".l))
            ) {
                shouldThrow<IllegalArgumentException> {
                    Constraints(
                        endsWith = listOf("asdf".l),
                        notEndsWith = it
                    )
                }
            }
        }

        "endsWith and notEndsWith has common prefixes list" {
            shouldThrow<Exception> {
                Constraints(
                    endsWith = listOf("asdf".l, "e".l),
                    notEndsWith = listOf("f".l, "asdf".l, "e".l)
                )
            }
        }

        "endsWith and notEndsWith does not fail if there is at least one valid combination" {
            shouldNotThrow<Exception> {
                Constraints(
                    endsWith = listOf("asdf".l, "e".l),
                    notEndsWith = listOf("asdf".l)
                )
            }
        }

        "notEndsWith can be longer than endsWith" {
            shouldNotThrow<IllegalArgumentException> {
                Constraints(
                    endsWith = listOf("asdf".l),
                    notEndsWith = listOf("aasdf".l, "basdf".l)
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
            Constraints(startsWith = listOf("asdf".l)) testWith mapOf(
                false to listOf("asd".l, "_".l, "jkl".l, "-asdf".l),
                true to listOf("asdf".l, "asdfasdf".l, "asdfjkl".l)
            )
        }
        "startsWith list" {
            Constraints(startsWith = listOf("www".l, "asdf".l)) testWith mapOf(
                false to listOf("wa".l, "ww".l, "asd".l, "_".l, "jkl".l, "-asdf".l),
                true to listOf("www".l, "asdf".l, "asdfasdf".l, "asdfjkl".l)
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
            Constraints(endsWith = listOf("asdf".l)) testWith mapOf(
                false to listOf("".l, "_".l, "_____sdf".l, "asdf-".l),
                true to listOf("asdf".l, "asdfasdf".l, "jklasdf".l)
            )
        }
        "endsWith List" {
            Constraints(endsWith = listOf("www".l, "asdf".l)) testWith mapOf(
                false to listOf("-ww".l, "-w".l, "".l, "_".l, "_____sdf".l, "asdf-".l),
                true to listOf("www".l, "-www".l, "asdf".l, "asdfasdf".l, "jklasdf".l)
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

        "custom constraints" {
            Constraints(
                customConstraints = listOf(
                    { input: List<String> -> input.firstOrNull() == "a" },
                    { input: List<String> -> input.lastOrNull() == "z" }
                )
            ) testWith mapOf(
                false to listOf("".l, "b".l, "a".l, "z".l, "aza".l),
                true to listOf("az".l, "asdfjklz".l)
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
                startsWith = listOf("asd", "sos"),
                notStartsWith = listOf("1", ".bc", "xy"),
                endsWith = listOf("wqw", "oiu"),
                notEndsWith = listOf("555", "6"),
                contains = listOf("x", "yolo"),
                notContains = listOf("ck", "bm"),
                excluding = listOf("word1", "word2"),
                hybridPrefixPostfix = true
            ) shouldBe Constraints(
                minLength = 5,
                maxLength = 10,
                startsWith = listOf(listOf('a', 's', 'd'), listOf('s', 'o', 's')),
                notStartsWith = listOf(
                    listOf('1'),
                    listOf('.', 'b', 'c'),
                    listOf('x', 'y')
                ),
                endsWith = listOf(listOf('w', 'q', 'w'), listOf('o', 'i', 'u')),
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
