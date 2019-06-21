package com.github.pjozsef.markovchain

import com.github.pjozsef.markovchain.util.WeightedDice

class MarkovChain(val transitions: Map<String, WeightedDice<String>>, val end: String) {

    operator fun invoke(current: String): String {
        return transitions.getValue(current).roll()
    }

    tailrec fun generate(
        order: Int = Int.MAX_VALUE,
        constraints: Constraints = Constraints()
    ): String {
        tailrec fun generate(current: String): String = when (val next = next(current.takeLast(order))) {
            end -> current
            else -> generate(current + next)
        }

        val result = generate(constraints.startsWith ?: "")
        return if(constraints.evaluate(result)) result else generate(order, constraints)
    }

    private tailrec fun next(current: String): String = when (val dice = transitions[current]) {
        null -> next(current.drop(1))
        else -> dice.roll()
    }
}