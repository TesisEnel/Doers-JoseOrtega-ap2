package edu.ucne.doers.presentation.extension

fun String?.ifNullOrBlank(default: String): String {
    return if (this.isNullOrBlank()) default else this
}