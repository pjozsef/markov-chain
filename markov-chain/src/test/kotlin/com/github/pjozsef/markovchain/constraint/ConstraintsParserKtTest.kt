package com.github.pjozsef.markovchain.constraint

import com.github.pjozsef.markovchain.testutil.c
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
                Constraints(startsWith = listOf("prefix".c))
            ),
            row(
                "notStartsWith",
                "!prefix**",
                Constraints(notStartsWith = listOf("prefix".c))
            ),
            row(
                "multiple notStartsWith",
                "!prefix**, !p2**, !a**",
                Constraints(notStartsWith = listOf("prefix".c, "p2".c, "a".c))
            ),
            row(
                "endsWith",
                "**suffix",
                Constraints(endsWith = listOf("suffix".c))
            ),
            row(
                "notEndsWith",
                "**!suffix",
                Constraints(notEndsWith = listOf("suffix".c))
            ),
            row(
                "multiple notEndsWith",
                "**!suffix, **!p2, **!a",
                Constraints(notEndsWith = listOf("suffix".c, "p2".c, "a".c))
            ),
            row(
                "contains",
                "*contains*",
                Constraints(contains = listOf("contains".c))
            ),
            row(
                "multiple contains",
                "*contains*, *a*, *b*",
                Constraints(contains = listOf("contains".c, "a".c, "b".c))
            ),
            row(
                "notContains",
                "*!contains*",
                Constraints(notContains = listOf("contains".c))
            ),
            row(
                "multiple notContains",
                "*!contains*, *!a*, *!b*",
                Constraints(notContains = listOf("contains".c, "a".c, "b".c))
            ),
            row(
                "startsWith, contains, endsWith together",
                "a*b*c",
                Constraints(startsWith = listOf("a".c), contains = listOf("b".c), endsWith = listOf("c".c))
            ),
            row(
                "notStartsWith, notContains, notEndsWith together",
                "!a*!b*!c",
                Constraints(notStartsWith = listOf("a".c), notContains = listOf("b".c), notEndsWith = listOf("c".c))
            ),
            row(
                "no hybrid prefix postfix strategy",
                "!hybrid",
                Constraints(hybridPrefixPostfix = false)
            ),
            row(
                "excluding words",
                "ex1|ex2|ex3",
                Constraints(excluding = listOf("ex1".c, "ex2".c, "ex3".c))
            ),
            row(
                "excluding words trimmed",
                "ex1 | ex2  |    ex3|     ||",
                Constraints(excluding = listOf("ex1".c, "ex2".c, "ex3".c))
            ),
            row(
                "multiple excluding",
                "ex1|,  ex2|ex3|",
                Constraints(excluding = listOf("ex1".c, "ex2".c, "ex3".c))
            ),
            row(
                "ignores extra separators",
                ",,, , 5-8, , a**b",
                Constraints(minLength = 5, maxLength = 8, startsWith = listOf("a".c), endsWith = listOf("b".c))
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
