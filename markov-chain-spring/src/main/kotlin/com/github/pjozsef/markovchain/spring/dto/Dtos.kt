package com.github.pjozsef.markovchain.spring.dto

import com.github.pjozsef.markovchain.Constraints

data class MarkovChainRequestDto(
    val words: List<String>,
    val count: Int = 15,
    val order: Int = 3,
    val seed: Long?,
    val constraints: ConstraintsDto = ConstraintsDto()
)

data class ConstraintsDto(
    val minLength: Int? = null,
    val maxLength: Int? = null,
    val startsWith: String? = null,
    val endsWith: String? = null,
    val contains: Collection<String>? = null,
    val notContains: Collection<String>? = null,
    val excluding: Collection<String>? = null,
    val hybridPrefixPostfix: Boolean = true
)

fun MarkovChainRequestDto.toConstraints() = Constraints(
    constraints.minLength,
    constraints.maxLength,
    constraints.startsWith,
    constraints.endsWith,
    constraints.contains,
    constraints.notContains,
    constraints.excluding ?: words,
    constraints.hybridPrefixPostfix
)
