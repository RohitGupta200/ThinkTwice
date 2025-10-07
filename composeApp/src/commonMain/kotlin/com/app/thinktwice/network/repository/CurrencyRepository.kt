package com.app.thinktwice.network.repository

import com.app.thinktwice.network.ApiService
import com.app.thinktwice.network.dto.CountriesResponse
import com.app.thinktwice.network.dto.Country
import com.app.thinktwice.network.dto.CurrenciesResponse
import com.app.thinktwice.network.error.ErrorHandler

class CurrencyRepository(
    private val apiService: ApiService
) {

    /**
     * Get all countries with currency information
     */
    suspend fun getAllCountries(): Result<CountriesResponse> {
        return ErrorHandler.safeApiCall {
            apiService.currencyApi.getAllCountries()
        }
    }

    /**
     * Search countries by query
     */
    suspend fun searchCountries(query: String): Result<List<Country>> {
        return ErrorHandler.safeApiCall {
            apiService.currencyApi.searchCountries(query)
        }
    }

    /**
     * Get all currencies
     */
    suspend fun getAllCurrencies(): Result<CurrenciesResponse> {
        return ErrorHandler.safeApiCall {
            apiService.currencyApi.getAllCurrencies()
        }
    }
}
