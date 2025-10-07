package com.app.thinktwice.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class Currency(
    val code: String,
    val name: String,
    val symbol: String? = null
)

@Serializable
data class Country(
    val country_name: String,
    val country_code: String,
    val country_flag_icon: String,
    val currency: Currency? = null
)

@Serializable
data class CountriesResponse(
    val countries: List<Country>,
    val total_count: Int
)

@Serializable
data class CurrenciesResponse(
    val currencies: List<Currency>,
    val total_count: Int
)
