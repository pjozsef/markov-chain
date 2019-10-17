package com.github.pjozsef.markovchain

import com.github.pjozsef.markovchain.util.WeightedDice
import com.github.pjozsef.markovchain.util.WordUtils
import com.github.pjozsef.markovchain.util.isTrue

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

    fun generate(
        order: Int = Int.MAX_VALUE,
        count: Int,
        constraints: Constraints = Constraints()
    ) = generate(order, constraints, 0, count, emptySet(), emptySet(), emptySet())

    private tailrec fun generate(
        order: Int = Int.MAX_VALUE,
        constraints: Constraints,
        tries: Int,
        count: Int,
        results: Set<String>,
        bufferedStarts: Set<String>,
        bufferedEnds: Set<String>
    ): Collection<String> {
        tailrec fun generateWord(current: String, transitionMap: MapTransition): String =
            when (val next = next(current.takeLast(order), transitionMap)) {
                end -> current
                else -> generateWord(current + next, transitionMap)
            }

        val isForwards = constraints.startsWith != null
        val isBackwards = constraints.endsWith != null

        return when {
            results.size >= count || tries >= allowedRetries -> results
            isForwards && isBackwards && constraints.hybridPrefixPostfix.isTrue -> {
                val newStarts = bufferedStarts + generateWord(
                    constraints.startsWith ?: "",
                    transition.forward
                ).let(::setOf)
                val newEnds = bufferedEnds + generateWord(
                    constraints.endsWith?.reversed() ?: "",
                    transition.backward
                ).reversed().let(::setOf)
                val newResults = WordUtils.combineWords(newStarts, newEnds).filter(constraints.evaluate::invoke)
                generate(order, constraints, tries + 1, count, results + newResults, newStarts, newEnds)
            }
            isForwards && isBackwards -> {
                val forward = generateWord(
                    constraints.startsWith ?: "",
                    transition.forward
                )
                val backward = generateWord(
                    constraints.endsWith?.reversed() ?: "",
                    transition.backward
                ).reversed()
                val newResult = listOf(forward, backward).filter(constraints.evaluate::invoke)
                generate(order, constraints, tries + 2, count, results + newResult, bufferedStarts, bufferedEnds)
            }
            else -> {
                val transitionMap = if (isBackwards) transition.backward else transition.forward
                val prefix = constraints.endsWith?.reversed() ?: constraints.startsWith ?: ""
                val newResult = generateWord(prefix, transitionMap).let {
                    if (isBackwards) it.reversed() else it
                }.let(::listOf).filter(constraints.evaluate::invoke)
                generate(order, constraints, tries + 1, count, results + newResult, bufferedStarts, bufferedEnds)
            }
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
