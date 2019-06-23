package com.github.pjozsef.markovchain

data class Constraints(
    val minLength: Int? = null,
    val maxLength: Int? = null,
    val startsWith: String? = null,
    val endsWith: String? = null,
    val contains: List<String>? = null,
    val notContains: List<String>? = null,
    val excluding: List<String>? = null
) {
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