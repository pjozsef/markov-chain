package com.github.pjozsef.markovchain

import com.github.pjozsef.markovchain.constraint.Constraints
import com.github.pjozsef.markovchain.util.TransitionRule
import com.github.pjozsef.markovchain.util.asDice
import java.util.*

private const val RADIX_36 = 36

fun generateWords(
    words: List<String>,
    order: Int,
    allowedRetries: Int,
    count: Int,
    seed: Long?,
    constraints: Constraints
): Iterable<String> {
    val random = initializeRandom(seed)

    val chain = MarkovChain(
        TransitionRule.fromWords(
            words,
            order = order
        ).asDice(random),
        allowedRetries = allowedRetries
    )
    return chain.generate(
        order = order,
        count = count,
        constraints = constraints
    ).shuffled(random)
        .take(count)
        .toSortedSet()
}

private fun initializeRandom(seed: Long?): Random {
    val randomSeed = seed ?: System.currentTimeMillis().toString(RADIX_36).takeLast(3).toLong(RADIX_36)
    println("Seed: ${randomSeed.toString(RADIX_36).toUpperCase()}")
    return Random(randomSeed)
}
