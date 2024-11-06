package com.task.manager

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform