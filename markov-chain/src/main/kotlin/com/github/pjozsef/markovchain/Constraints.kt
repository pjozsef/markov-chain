package com.github.pjozsef.markovchain

import com.github.pjozsef.markovchain.util.WordUtils

data class Constraints(
    val minLength: Int? = null,
    val maxLength: Int? = null,
    val startsWith: String? = null,
    val endsWith: String? = null,
    val contains: Collection<String>? = null,
    val notContains: Collection<String>? = null,
    val excluding: Collection<String>? = null
) {
    init {
        if (minLength != null) {
            require(minLength > 0)
        }
        if (maxLength != null) {
            require(maxLength > 0)
            require(maxLength>=startsWith?.length?:0)
            require(maxLength>=endsWith?.length?:0)
            if(startsWith!=null && endsWith!=null){
                val commonStringLength = WordUtils.commonPostfixPrefixLength(startsWith, endsWith)
                val startLength = startsWith.length-commonStringLength
                val endLength = endsWith.length-commonStringLength
                val totalMinimumLength = startLength + commonStringLength + endLength
                require(maxLength>=totalMinimumLength)
            }
            if (minLength != null) {
                require(minLength <= maxLength)
            }
        }
    }

    val evaluate: (String) -> Boolean =
        listOfNotNull(
            minLength?.let { { str: String -> it <= str.length } },
            maxLength?.let { { str: String -> str.length <= it } },
            startsWith?.let { { str: String -> str.startsWith(it) } },
            endsWith?.let { { str: String -> str.endsWith(it) } },
            contains?.let { { str: String -> it.all { str.contains(it) } } },
            notContains?.let { { str: String -> it.none { str.contains(it) } } },
            excluding?.let { { str: String -> !it.contains(str) } }
        ).let { predicates ->
            { input -> predicates.all { it(input) } }
        }
}