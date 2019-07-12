package com.github.pjozsef.markovchain

import com.github.pjozsef.markovchain.util.WeightedDice
import com.github.pjozsef.markovchain.util.WordUtils
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
    ) = generate(order, constraints, 0, emptyList(), emptyList())

    private tailrec fun generate(
        order: Int = Int.MAX_VALUE,
        constraints: Constraints,
        tries: Int,
        bufferedStarts: List<String>,
        bufferedEnds: List<String>
    ): Collection<String> {
        tailrec fun generateWord(current: String, transitionMap: MapTransition): String =
            when (val next = next(current.takeLast(order), transitionMap)) {
                end -> current
                else -> generateWord(current + next, transitionMap)
            }
        if(tries >= allowedRetries){
            throw RetryCountReached(allowedRetries)
        }
        val isForwards = constraints.startsWith!=null
        val isBackwards = constraints.endsWith!=null

        return when{
            isForwards&&isBackwards -> {
                val startWord = generateWord(constraints.startsWith ?: "", transition.forward)
                val newStarts = bufferedStarts + listOf(startWord)
                val endWord = generateWord(constraints.endsWith?.reversed() ?: "", transition.backward).reversed()
                val newEnds = bufferedEnds + listOf(endWord)
                val results = WordUtils.combineWords(newStarts, newEnds).filter(constraints.evaluate::invoke)
                if(results.isNotEmpty()){
                    println(tries)
                    results
                } else {
                    generate(order, constraints, tries + 1, newStarts, newEnds)
                }
            }
            else -> {
                val transitionMap = if(isBackwards) transition.backward else transition.forward
                val prefix = constraints.endsWith?.reversed() ?: constraints.startsWith ?: ""
                val result = generateWord(prefix, transitionMap).let {
                    if(isBackwards) it.reversed() else it
                }
                if(constraints.evaluate(result)){
                    listOf(result)
                } else {
                    generate(order, constraints, tries + 1, bufferedStarts, bufferedEnds)
                }
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