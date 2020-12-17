package com.github.pjozsef.markovchain

import com.github.pjozsef.markovchain.constraint.Constraints
import com.github.pjozsef.markovchain.util.WeightedDice
import com.github.pjozsef.markovchain.util.WordUtils
import com.github.pjozsef.markovchain.util.WordUtils.endsWith

internal typealias MapTransition<T> = Map<List<T>, WeightedDice<List<T>>>

data class Transition<T>(
    val forward: MapTransition<T>,
    val backward: MapTransition<T>
)

class MarkovChain<T>(
    val transition: Transition<T>,
    val end: List<T>,
    val allowedRetries: Int = 1_000_000
) {

    fun generate(
        order: Int = Int.MAX_VALUE,
        count: Int,
        constraints: Constraints<T> = Constraints()
    ) = generate(order, constraints, 0, count, emptySet(), emptySet(), emptySet())

    private tailrec fun generate(
        order: Int = Int.MAX_VALUE,
        constraints: Constraints<T>,
        tries: Int,
        count: Int,
        results: Set<List<T>>,
        bufferedStarts: Set<List<T>>,
        bufferedEnds: Set<List<T>>
    ): Collection<List<T>> {
        tailrec fun generateWord(current: List<T>, transitionMap: MapTransition<T>): List<T> {
            val next = next(current.takeLast(order), transitionMap)
            val new = current + next

            return if(new.endsWith(end)){
                new.dropLast(end.size)
            } else {
                generateWord(new, transitionMap)
            }
        }

        val isForwards = constraints.startsWith != null
        val isBackwards = constraints.endsWith != null

        return when {
            results.size >= count || tries >= allowedRetries -> results
            isForwards && isBackwards && constraints.hybridPrefixPostfix -> {
                val newStarts = bufferedStarts + generateWord(
                    constraints.startsWith ?: emptyList(),
                    transition.forward
                ).let(::setOf)
                val newEnds = bufferedEnds + generateWord(
                    constraints.endsWith?.reversed() ?: emptyList(),
                    transition.backward
                ).reversed().let(::setOf)
                val newResults = WordUtils.combineWords(newStarts, newEnds).filter(constraints.evaluate::invoke)
                generate(order, constraints, tries + 1, count, results + newResults, newStarts, newEnds)
            }
            isForwards && isBackwards -> {
                val forward = generateWord(
                    constraints.startsWith ?: emptyList(),
                    transition.forward
                )
                val backward = generateWord(
                    constraints.endsWith?.reversed() ?: emptyList(),
                    transition.backward
                ).reversed()
                val newResult = listOf(forward, backward).filter(constraints.evaluate::invoke)
                generate(order, constraints, tries + 2, count, results + newResult, bufferedStarts, bufferedEnds)
            }
            else -> {
                val transitionMap = if (isBackwards) transition.backward else transition.forward
                val prefix = constraints.endsWith?.reversed() ?: constraints.startsWith ?: emptyList()
                val newResult = generateWord(prefix, transitionMap).let {
                    if (isBackwards) it.reversed() else it
                }.let(::listOf).filter(constraints.evaluate::invoke)
                generate(order, constraints, tries + 1, count, results + newResult, bufferedStarts, bufferedEnds)
            }
        }
    }

    private tailrec fun next(
        current: List<T>,
        transitionMap: MapTransition<T>
    ): List<T> = when (val dice = transitionMap[current]) {
        null -> next(current.drop(1), transitionMap)
        else -> dice.roll()
    }
}
