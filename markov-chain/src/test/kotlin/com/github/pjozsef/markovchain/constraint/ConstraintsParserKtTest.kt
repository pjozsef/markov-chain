package com.github.pjozsef.markovchain.constraint

import com.github.pjozsef.markovchain.testutil.l
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
                Constraints(startsWith = "prefix".l)
            ),
            row(
                "notStartsWith",
                "!prefix**",
                Constraints(notStartsWith = listOf("prefix".l))
            ),
            row(
                "multiple notStartsWith",
                "!prefix**, !p2**, !a**",
                Constraints(notStartsWith = listOf("prefix".l, "p2".l, "a".l))
            ),
            row("endsWith", "**suffix", Constraints(endsWith = "suffix".l)),
            row(
                "notEndsWith",
                "**!suffix",
                Constraints(notEndsWith = listOf("suffix".l))
            ),
            row(
                "multiple notEndsWith",
                "**!suffix, **!p2, **!a",
                Constraints(notEndsWith = listOf("suffix".l, "p2".l, "a".l))
            ),
            row(
                "contains",
                "*contains*",
                Constraints(contains = listOf("contains".l))
            ),
            row(
                "multiple contains",
                "*contains*, *a*, *b*",
                Constraints(contains = listOf("contains".l, "a".l, "b".l))
            ),
            row(
                "notContains",
                "*!contains*",
                Constraints(notContains = listOf("contains".l))
            ),
            row(
                "multiple notContains",
                "*!contains*, *!a*, *!b*",
                Constraints(notContains = listOf("contains".l, "a".l, "b".l))
            ),
            row(
                "startsWith, contains, endsWith together",
                "a*b*c",
                Constraints(startsWith = "a".l, contains = listOf("b".l), endsWith = "c".l)
            ),
            row(
                "notStartsWith, notContains, notEndsWith together",
                "!a*!b*!c",
                Constraints(notStartsWith = listOf("a".l), notContains = listOf("b".l), notEndsWith = listOf("c".l))
            ),
            row(
                "no hybrid prefix postfix strategy",
                "!hybrid",
                Constraints(hybridPrefixPostfix = false)
            ),
            row(
                "excluding words",
                "ex1|ex2|ex3",
                Constraints(excluding = listOf("ex1".l, "ex2".l, "ex3".l))
            ),
            row(
                "excluding words trimmed",
                "ex1 | ex2  |    ex3|     ||",
                Constraints(excluding = listOf("ex1".l, "ex2".l, "ex3".l))
            ),
            row(
                "multiple excluding",
                "ex1|,  ex2|ex3|",
                Constraints(excluding = listOf("ex1".l, "ex2".l, "ex3".l))
            ),
            row(
                "ignores extra separators",
                ",,, , 5-8, , a**b",
                Constraints(minLength = 5, maxLength = 8, startsWith = "a".l, endsWith = "b".l)
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
