package com.app.thinktwice

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform