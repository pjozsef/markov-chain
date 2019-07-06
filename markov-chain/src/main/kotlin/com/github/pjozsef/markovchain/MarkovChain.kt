package com.github.pjozsef.markovchain

import com.github.pjozsef.markovchain.util.TransitionRule
import com.github.pjozsef.markovchain.util.WeightedDice
import com.github.pjozsef.markovchain.util.asDice
import java.lang.RuntimeException

class MarkovChain(
    val transitions: Map<String, WeightedDice<String>>,
    val end: String = "#",
    val allowedRetries: Int = 1_000_000
) {
    class RetryCountReached(val count: Int) : RuntimeException("Passed allowed retry count of $count")

    operator fun invoke(current: String): String {
        return transitions.getValue(current).roll()
    }

    fun generate(
        order: Int = Int.MAX_VALUE,
        constraints: Constraints = Constraints()
    ) = generate(order, constraints, 0)

    private tailrec fun generate(
        order: Int = Int.MAX_VALUE,
        constraints: Constraints = Constraints(),
        tries: Int
    ): String {
        tailrec fun generateWord(current: String): String = when (val next = next(current.takeLast(order))) {
            end -> current
            else -> generateWord(current + next)
        }
        return when {
            tries >= allowedRetries -> throw RetryCountReached(allowedRetries)
            else -> {
                val result = generateWord(constraints.startsWith ?: "")
                if (constraints.evaluate(result)) result else generate(order, constraints, tries + 1)
            }
        }
    }

    private tailrec fun next(current: String): String = when (val dice = transitions[current]) {
        null -> next(current.drop(1))
        else -> dice.roll()
    }
}