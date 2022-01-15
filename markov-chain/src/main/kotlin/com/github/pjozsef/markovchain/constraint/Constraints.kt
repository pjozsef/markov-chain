package com.github.pjozsef.markovchain.constraint

import com.github.pjozsef.markovchain.util.WordUtils
import com.github.pjozsef.markovchain.util.WordUtils.containsList
import com.github.pjozsef.markovchain.util.WordUtils.endsWith
import com.github.pjozsef.markovchain.util.WordUtils.startsWith
import com.github.pjozsef.markovchain.util.WordUtils.toListOfChar

data class Constraints<T>(
    val minLength: Int? = null,
    val maxLength: Int? = null,
    val startsWith: Collection<List<T>>? = null,
    val notStartsWith: Collection<List<T>>? = null,
    val endsWith: Collection<List<T>>? = null,
    val notEndsWith: Collection<List<T>>? = null,
    val contains: Collection<List<T>>? = null,
    val notContains: Collection<List<T>>? = null,
    val excluding: Collection<List<T>>? = null,
    val customConstraints: Collection<(List<T>) -> Boolean>? = null,
    val hybridPrefixPostfix: Boolean = true
) {

    companion object {
        fun forWords(
            minLength: Int? = null,
            maxLength: Int? = null,
            startsWith: Collection<String>? = null,
            notStartsWith: Collection<String>? = null,
            endsWith: Collection<String>? = null,
            notEndsWith: Collection<String>? = null,
            contains: Collection<String>? = null,
            notContains: Collection<String>? = null,
            excluding: Collection<String>? = null,
            customConstraints: Collection<(String) -> Boolean>? = null,
            hybridPrefixPostfix: Boolean = true
        ): Constraints<Char> {
            return Constraints(
                minLength,
                maxLength,
                startsWith.toListOfChar(),
                notStartsWith.toListOfChar(),
                endsWith.toListOfChar(),
                notEndsWith.toListOfChar(),
                contains.toListOfChar(),
                notContains.toListOfChar(),
                excluding.toListOfChar(),
                customConstraints?.map {
                    { word: List<Char> ->
                        it(word.toString())
                    }
                },
                hybridPrefixPostfix
            )
        }
    }

    init {
        if (minLength != null) {
            require(minLength > 0)
        }
        if (maxLength != null) {
            require(maxLength > 0)
            require(maxLength >= startsWith.innerMaxSize())
            require(maxLength >= endsWith.innerMaxSize())
            if (startsWith != null && endsWith != null) {
                val commonLength = WordUtils.commonPostfixPrefixMultiLength(startsWith, endsWith)
                val startLength = startsWith.innerMaxSize() - commonLength
                val endLength = endsWith.innerMaxSize() - commonLength
                val totalMinimumLength = startLength + commonLength + endLength
                require(maxLength >= totalMinimumLength)
            }
            if (minLength != null) {
                require(minLength <= maxLength)
            }
        }
        if (startsWith != null && notStartsWith != null) {
            require(startsWith.any { start -> notStartsWith.none { start.startsWith(it) }})
        }
        if (endsWith != null && notEndsWith != null) {
            require(endsWith.any { end -> notEndsWith.none { end.endsWith(it) }})
        }
    }

    val evaluate: (List<T>) -> Boolean =
        listOfNotNull(
            minLength?.let { { input: List<T> -> it <= input.size } },
            maxLength?.let { { input: List<T> -> input.size <= it } },
            startsWith?.let { { input: List<T> -> it.any { input.startsWith(it) } } },
            notStartsWith?.let { { input: List<T> -> it.none { input.startsWith(it) } } },
            endsWith?.let { { input: List<T> -> it.any{ input.endsWith(it) } } },
            notEndsWith?.let { { input: List<T> -> it.none { input.endsWith(it) } } },
            contains?.let { { input: List<T> -> it.all { input.containsList(it) } } },
            notContains?.let { { input: List<T> -> it.none { input.containsList(it) } } },
            excluding?.let { { input: List<T> -> !it.contains(input) } },
            customConstraints?.let { { input: List<T> -> it.all { it(input) } } }
        ).let { predicates ->
            { input -> predicates.all { it(input) } }
        }
}

private fun <T> Collection<List<T>>?.innerMaxSize() =
    this?.map { it.size }?.maxOrNull() ?: 0
