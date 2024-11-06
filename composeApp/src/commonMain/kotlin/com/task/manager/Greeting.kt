package com.task.manager

class Greeting {
    private val platform = getPlatform()

    fun greet(): String {
        return "Hello Juan on ${platform.name}!"
    }
}