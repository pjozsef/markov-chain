package com.github.pjozsef.markovchain.util

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FreeSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import java.util.*

class RandomUtilsKtTest : FreeSpec({
    "randomElement" - {
        "returns element randomly" {
            val list = listOf(30, 542, 998)
            val random = mock<Random>()
            forAll(
                row(0, 30),
                row(2, 998)
            ) { mockIndex, expected ->
                whenever(random.nextInt(list.size)) doReturn mockIndex
                list.randomElement(random) shouldBe expected
            }
        }

        "throws exception for empty list" {
            shouldThrow<IllegalArgumentException> {
                emptyList<String>().randomElement(Random())
            }.message shouldBe "List must not be empty!"
        }
    }
}) {
    override fun isolationMode() = IsolationMode.InstancePerLeaf
}
