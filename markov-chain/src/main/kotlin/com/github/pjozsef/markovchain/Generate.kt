package com.github.pjozsef.markovchain

import com.github.pjozsef.markovchain.constraint.Constraints
import com.github.pjozsef.markovchain.util.TransitionRule
import com.github.pjozsef.markovchain.util.asDice
import java.util.*

private const val RADIX_36 = 36

fun <T> generateWords(
    words: List<List<T>>,
    order: Int,
    allowedRetries: Int,
    count: Int,
    delimiter: List<T>,
    seed: Long?,
    constraints: Constraints<T>,
    commentFilter: (List<T>)->Boolean
): Iterable<List<T>> {
    val random = initializeRandom(seed)
    val chain = MarkovChain(
        TransitionRule.fromWords(
            words,
            delimiter = delimiter,
            order = order,
            commentFilter = commentFilter
        ).asDice(random),
        end = delimiter,
        allowedRetries = allowedRetries,
        random = random
    )
    return chain.generate(
        order = order,
        count = count,
        constraints = constraints
    ).shuffled(random)
        .take(count)
}

private fun initializeRandom(seed: Long?): Random {
    val randomSeed = seed ?: System.currentTimeMillis().toString(RADIX_36).takeLast(3).toLong(RADIX_36)
    println("Seed: ${randomSeed.toString(RADIX_36).toUpperCase()}")
    return Random(randomSeed)
}
