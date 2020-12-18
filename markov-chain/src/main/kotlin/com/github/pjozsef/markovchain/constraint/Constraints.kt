package com.github.pjozsef.markovchain.constraint

import com.github.pjozsef.markovchain.util.WordUtils
import com.github.pjozsef.markovchain.util.WordUtils.containsList
import com.github.pjozsef.markovchain.util.WordUtils.endsWith
import com.github.pjozsef.markovchain.util.WordUtils.startsWith
import com.github.pjozsef.markovchain.util.WordUtils.toListOfChar

data class Constraints<T>(
    val minLength: Int? = null,
    val maxLength: Int? = null,
    val startsWith: List<T>? = null,
    val notStartsWith: Collection<List<T>>? = null,
    val endsWith: List<T>? = null,
    val notEndsWith: Collection<List<T>>? = null,
    val contains: Collection<List<T>>? = null,
    val notContains: Collection<List<T>>? = null,
    val excluding: Collection<List<T>>? = null,
    val hybridPrefixPostfix: Boolean = true
) {

    companion object{
        fun forWords(
            minLength: Int? = null,
            maxLength: Int? = null,
            startsWith: String? = null,
            notStartsWith: Collection<String>? = null,
            endsWith: String? = null,
            notEndsWith: Collection<String>? = null,
            contains: Collection<String>? = null,
            notContains: Collection<String>? = null,
            excluding: Collection<String>? = null,
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
            require(maxLength >= startsWith?.size ?: 0)
            require(maxLength >= endsWith?.size ?: 0)
            if (startsWith != null && endsWith != null) {
                val commonLength = WordUtils.commonPostfixPrefixLength(startsWith, endsWith)
                val startLength = startsWith.size - commonLength
                val endLength = endsWith.size - commonLength
                val totalMinimumLength = startLength + commonLength + endLength
                require(maxLength >= totalMinimumLength)
            }
            if (minLength != null) {
                require(minLength <= maxLength)
            }
        }
        if (startsWith != null && notStartsWith != null) {
            require(notStartsWith.none { startsWith.startsWith(it) })
        }
        if (endsWith != null && notEndsWith != null) {
            require(notEndsWith.none { endsWith.endsWith(it) })
        }
    }

    val evaluate: (List<T>) -> Boolean =
        listOfNotNull(
            minLength?.let { { input: List<T> -> it <= input.size } },
            maxLength?.let { { input: List<T> -> input.size <= it } },
            startsWith?.let { { input: List<T> -> input.startsWith(it) } },
            notStartsWith?.let { { input: List<T> -> it.none{ input.startsWith(it) }} },
            endsWith?.let { { input: List<T> -> input.endsWith(it) } },
            notEndsWith?.let { { input: List<T> -> it.none{ input.endsWith(it) }} },
            contains?.let { { input: List<T> -> it.all { input.containsList(it) } } },
            notContains?.let { { input: List<T> -> it.none { input.containsList(it) } } },
            excluding?.let { { input: List<T> -> !it.contains(input) } }
        ).let { predicates ->
            { input -> predicates.all { it(input) } }
        }
}
