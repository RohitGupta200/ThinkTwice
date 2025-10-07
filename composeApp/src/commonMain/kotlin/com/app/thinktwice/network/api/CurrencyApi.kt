package com.app.thinktwice.network.api

import com.app.thinktwice.network.dto.CountriesResponse
import com.app.thinktwice.network.dto.Country
import com.app.thinktwice.network.dto.CurrenciesResponse
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query

interface CurrencyApi {

    @GET("api/v1/currency/countries")
    suspend fun getAllCountries(): CountriesResponse

    @GET("api/v1/currency/countries/search")
    suspend fun searchCountries(
        @Query("q") query: String
    ): List<Country>

    @GET("api/v1/currency/currencies")
    suspend fun getAllCurrencies(): CurrenciesResponse
}
