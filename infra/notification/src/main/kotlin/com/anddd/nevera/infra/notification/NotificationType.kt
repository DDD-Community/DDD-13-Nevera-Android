package com.anddd.nevera.infra.notification

internal enum class NotificationType(val value: String) {
    DEFAULT("default"),
    UNKNOWN("unknown");

    companion object {
        fun from(value: String?): NotificationType =
            entries.find { it.value == value } ?: UNKNOWN
    }
}