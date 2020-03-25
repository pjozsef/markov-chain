package com.github.pjozsef.markovchain.constraint

import kotlin.reflect.KProperty0

fun parseConstraint(stringConstraints: String) =
    stringConstraints.split(",").map {
        it.trim()
    }.fold(Constraints()) { constraints, input ->
        var newConstraints = constraints

        lengthRegex.matchEntire(input)?.let {
            it.groups["min"]?.value?.toInt()?.also {
                newConstraints = newConstraints.copy(minLength = it)
            }
            it.groups["max"]?.value?.toInt()?.also {
                newConstraints = newConstraints.copy(maxLength = it)
            }
        }

        contentRegex.matchEntire(input)?.let {
            it.groups["start"]?.value?.also {
                if (it.startsWith("!")) {
                    newConstraints = newConstraints.copy(notStartsWith = newList(it, newConstraints::notStartsWith))
                } else {
                    newConstraints = newConstraints.copy(startsWith = it)
                }
            }
            it.groups["contains"]?.value?.also {
                if (it.startsWith("!")) {
                    newConstraints = newConstraints.copy(notContains = newList(it, newConstraints::notContains))
                } else {
                    newConstraints = newConstraints.copy(contains = newList(it, newConstraints::contains))
                }
            }
            it.groups["end"]?.value?.also {
                if (it.startsWith("!")) {
                    newConstraints = newConstraints.copy(notEndsWith = newList(it, newConstraints::notEndsWith))
                } else {
                    newConstraints = newConstraints.copy(endsWith = it)
                }
            }
        }

        if (input == "!hybrid") {
            newConstraints = newConstraints.copy(hybridPrefixPostfix = false)
        }

        if (input.contains("|")) {
            input.split("|").map { it.trim() }.filter { it.isNotBlank() }.also {
                val excludingList = newConstraints.excluding?.plus(it) ?: it
                newConstraints = newConstraints.copy(excluding = excludingList)
            }
        }

        newConstraints
    }

private val lengthRegex by lazy { Regex("(?<min>\\d+)?-(?<max>\\d+)?") }
private val contentRegex by lazy { Regex("(?<start>[^*]+)?\\*(?<contains>[^*]+)?\\*(?<end>[^*]+)?") }

private fun newList(value: String, property: KProperty0<Collection<String>?>): List<String> {
    val element = if (value.startsWith("!")) value.drop(1) else value
    val elementList = listOf(element)
    return property.get()?.plus(elementList) ?: elementList
}
