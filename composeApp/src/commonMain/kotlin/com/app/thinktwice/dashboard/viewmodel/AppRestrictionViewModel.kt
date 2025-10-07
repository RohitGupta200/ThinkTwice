package com.app.thinktwice.dashboard.viewmodel

import com.app.thinktwice.applocking.logic.AppMonitoringCoordinator
import com.app.thinktwice.applocking.models.RestrictedApp
import com.app.thinktwice.applocking.platform.AppInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel for managing app restrictions
 */
class AppRestrictionViewModel(
    private val coordinator: AppMonitoringCoordinator
) {

    private val viewModelScope = CoroutineScope(Dispatchers.Main)

    // State flows
    val restrictedApps: StateFlow<List<RestrictedApp>> = coordinator
        .getRestrictedApps()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val enabledRestrictedApps: StateFlow<List<RestrictedApp>> = coordinator
        .getEnabledRestrictedApps()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val isMonitoring: StateFlow<Boolean> = coordinator.isMonitoring

    private val _hasPermissions = MutableStateFlow(false)
    val hasPermissions: StateFlow<Boolean> = _hasPermissions.asStateFlow()

    private val _installedApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val installedApps: StateFlow<List<AppInfo>> = _installedApps.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        checkPermissions()
    }

    /**
     * Check if permissions are granted
     */
    fun checkPermissions() {
        viewModelScope.launch {
            _hasPermissions.value = coordinator.hasPermissions()
        }
    }

    /**
     * Request required permissions
     */
    fun requestPermissions() {
        viewModelScope.launch {
            coordinator.requestPermissions()
            checkPermissions()
        }
    }

    /**
     * Load installed apps
     */
    fun loadInstalledApps() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val apps = coordinator.getInstalledApps()
                _installedApps.value = apps
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Add app to restricted list
     */
    fun addRestrictedApp(appInfo: AppInfo) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = coordinator.addRestrictedApp(
                    appId = appInfo.packageName,
                    appName = appInfo.appName,
                    packageName = appInfo.packageName,
                    iconPath = appInfo.iconPath
                )
                if (result.isFailure) {
                    _error.value = result.exceptionOrNull()?.message
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Remove app from restricted list
     */
    fun removeRestrictedApp(appId: Long) {
        viewModelScope.launch {
            try {
                val result = coordinator.removeRestrictedApp(appId)
                if (result.isFailure) {
                    _error.value = result.exceptionOrNull()?.message
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    /**
     * Toggle app restriction on/off
     */
    fun toggleAppRestriction(appId: Long, isEnabled: Boolean) {
        viewModelScope.launch {
            try {
                val result = coordinator.toggleAppRestriction(appId, isEnabled)
                if (result.isFailure) {
                    _error.value = result.exceptionOrNull()?.message
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    /**
     * Start monitoring
     */
    fun startMonitoring() {
        viewModelScope.launch {
            try {
                coordinator.startMonitoring()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    /**
     * Stop monitoring
     */
    fun stopMonitoring() {
        viewModelScope.launch {
            try {
                coordinator.stopMonitoring()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    /**
     * Toggle monitoring on/off
     */
    fun toggleMonitoring() {
        if (isMonitoring.value) {
            stopMonitoring()
        } else {
            startMonitoring()
        }
    }

    /**
     * Clear error
     */
    fun clearError() {
        _error.value = null
    }

    /**
     * Dispose resources
     */
    fun dispose() {
        coordinator.dispose()
    }
}
