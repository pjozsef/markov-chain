package com.github.pjozsef.markovchain

import com.github.pjozsef.markovchain.util.WeightedDice
import java.lang.RuntimeException

internal typealias MapTransition = Map<String, WeightedDice<String>>

data class Transition(
    val forward: MapTransition,
    val backward: MapTransition
)

class MarkovChain(
    val transition: Transition,
    val end: String = "#",
    val allowedRetries: Int = 1_000_000
) {
    class RetryCountReached(val count: Int) : RuntimeException("Passed allowed retry count of $count")

    fun generate(
        order: Int = Int.MAX_VALUE,
        constraints: Constraints = Constraints()
    ) = generate(order, constraints, 0)

    private tailrec fun generate(
        order: Int = Int.MAX_VALUE,
        constraints: Constraints,
        tries: Int
    ): String {
        tailrec fun generateWord(current: String, transitionMap: MapTransition): String =
            when (val next = next(current.takeLast(order), transitionMap)) {
                end -> current
                else -> generateWord(current + next, transitionMap)
            }
        if(tries >= allowedRetries){
            throw RetryCountReached(allowedRetries)
        }
        val isBackwards = constraints.endsWith!=null
        val transitionMap = if(isBackwards) transition.backward else transition.forward
        val prefix = constraints.endsWith ?: constraints.startsWith ?: ""
        val result = generateWord(prefix, transitionMap).let {
            if(isBackwards) it.reversed() else it
        }
        return if(constraints.evaluate(result)){
            result
        } else {
            generate(order, constraints, tries + 1)
        }
    }

    private tailrec fun next(
        current: String,
        transitionMap: MapTransition
    ): String = when (val dice = transitionMap[current]) {
        null -> next(current.drop(1), transitionMap)
        else -> dice.roll()
    }
}