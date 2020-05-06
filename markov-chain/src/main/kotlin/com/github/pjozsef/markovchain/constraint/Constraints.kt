package com.github.pjozsef.markovchain.constraint

import com.github.pjozsef.markovchain.util.WordUtils

data class Constraints(
    val minLength: Int? = null,
    val maxLength: Int? = null,
    val startsWith: String? = null,
    val notStartsWith: Collection<String>? = null,
    val endsWith: String? = null,
    val notEndsWith: Collection<String>? = null,
    val contains: Collection<String>? = null,
    val notContains: Collection<String>? = null,
    val excluding: Collection<String>? = null,
    val hybridPrefixPostfix: Boolean = true
) {
    init {
        if (minLength != null) {
            require(minLength > 0)
        }
        if (maxLength != null) {
            require(maxLength > 0)
            require(maxLength >= startsWith?.length ?: 0)
            require(maxLength >= endsWith?.length ?: 0)
            if (startsWith != null && endsWith != null) {
                val commonStringLength = WordUtils.commonPostfixPrefixLength(startsWith, endsWith)
                val startLength = startsWith.length - commonStringLength
                val endLength = endsWith.length - commonStringLength
                val totalMinimumLength = startLength + commonStringLength + endLength
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

    val evaluate: (String) -> Boolean =
        listOfNotNull(
            minLength?.let { { str: String -> it <= str.length } },
            maxLength?.let { { str: String -> str.length <= it } },
            startsWith?.let { { str: String -> str.startsWith(it) } },
            notStartsWith?.let { { str: String -> it.none{ str.startsWith(it) }} },
            endsWith?.let { { str: String -> str.endsWith(it) } },
            notEndsWith?.let { { str: String -> it.none{ str.endsWith(it) }} },
            contains?.let { { str: String -> it.all { str.contains(it) } } },
            notContains?.let { { str: String -> it.none { str.contains(it) } } },
            excluding?.let { { str: String -> !it.contains(str) } }
        ).let { predicates ->
            { input -> predicates.all { it(input) } }
        }
}