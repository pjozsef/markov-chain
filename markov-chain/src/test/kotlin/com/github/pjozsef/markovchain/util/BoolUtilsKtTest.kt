package com.github.pjozsef.markovchain.util

import io.kotlintest.IsolationMode
import io.kotlintest.data.forall
import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec
import io.kotlintest.tables.row

class BoolUtilsKtTest: FreeSpec({
    forall(
        row(true, true),
        row(false, false),
        row(null, false)
    ){ input, expected ->
        "$input.isTrue == $expected"{
            input.isTrue shouldBe expected
        }
    }
}){
    override fun isolationMode() = IsolationMode.InstancePerLeaf
}