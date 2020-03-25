package com.github.pjozsef.markovchain.constraint

import io.kotlintest.IsolationMode
import io.kotlintest.data.suspend.forall
import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec
import io.kotlintest.tables.row

class ConstraintsParserKtTest : FreeSpec({
    "parse" - {
        forall(
            row(
                "min length",
                "2-",
                Constraints(minLength = 2)
            ),
            row(
                "min length multi digit",
                "200-",
                Constraints(minLength = 200)
            ),
            row(
                "max length",
                "-8",
                Constraints(maxLength = 8)
            ),
            row(
                "max length multi digit",
                "-86",
                Constraints(maxLength = 86)
            ),
            row(
                "min-max length",
                "2-8",
                Constraints(minLength = 2, maxLength = 8)
            ),
            row(
                "input trimmed",
                "     2-8    ",
                Constraints(minLength = 2, maxLength = 8)
            ),
            row(
                "min-max as separate",
                "2-, -8",
                Constraints(minLength = 2, maxLength = 8)
            ),
            row(
                "startsWith",
                "prefix**",
                Constraints(startsWith = "prefix")
            ),
            row(
                "notStartsWith",
                "!prefix**",
                Constraints(notStartsWith = listOf("prefix"))
            ),
            row(
                "multiple notStartsWith",
                "!prefix**, !p2**, !a**",
                Constraints(notStartsWith = listOf("prefix", "p2", "a"))
            ),
            row("endsWith", "**suffix", Constraints(endsWith = "suffix")),
            row(
                "notEndsWith",
                "**!suffix",
                Constraints(notEndsWith = listOf("suffix"))
            ),
            row(
                "multiple notEndsWith",
                "**!suffix, **!p2, **!a",
                Constraints(notEndsWith = listOf("suffix", "p2", "a"))
            ),
            row(
                "contains",
                "*contains*",
                Constraints(contains = listOf("contains"))
            ),
            row(
                "multiple contains",
                "*contains*, *a*, *b*",
                Constraints(contains = listOf("contains", "a", "b"))
            ),
            row(
                "notContains",
                "*!contains*",
                Constraints(notContains = listOf("contains"))
            ),
            row(
                "multiple notContains",
                "*!contains*, *!a*, *!b*",
                Constraints(notContains = listOf("contains", "a", "b"))
            ),
            row(
                "startsWith, contains, endsWith together",
                "a*b*c",
                Constraints(startsWith = "a", contains = listOf("b"), endsWith = "c")
            ),
            row(
                "notStartsWith, notContains, notEndsWith together",
                "!a*!b*!c",
                Constraints(notStartsWith = listOf("a"), notContains = listOf("b"), notEndsWith = listOf("c"))
            ),
            row(
                "no hybrid prefix postfix strategy",
                "!hybrid",
                Constraints(hybridPrefixPostfix = false)
            ),
            row(
                "excluding words",
                "ex1|ex2|ex3",
                Constraints(excluding = listOf("ex1", "ex2", "ex3"))
            ),
            row(
                "excluding words trimmed",
                "ex1 | ex2  |    ex3|     ||",
                Constraints(excluding = listOf("ex1", "ex2", "ex3"))
            ),
            row(
                "multiple excluding",
                "ex1|,  ex2|ex3|",
                Constraints(excluding = listOf("ex1", "ex2", "ex3"))
            )
        ) { test, input, expected ->
            test {
                parseConstraint(input) shouldBe expected
            }
        }
    }
}) {
    override fun isolationMode() = IsolationMode.InstancePerLeaf
}
