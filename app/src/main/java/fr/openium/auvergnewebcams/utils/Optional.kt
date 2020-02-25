package fr.openium.auvergnewebcams.utils

sealed class Optional<out T> {
    class Some<out T>(val element: T) : Optional<T>()
    object None : Optional<Nothing>()

    val value: T?
        get() = if (this is Some) element else null

    companion object {
        inline fun <reified T> of(element: T?): Optional<T> {
            return if (element != null) {
                Some(element)
            } else {
                None
            }
        }
    }
}