package questbot

import com.google.inject.Key
import com.google.inject.TypeLiteral
import com.google.inject.binder.LinkedBindingBuilder
import com.google.inject.binder.ScopedBindingBuilder

/** These extensions exist because kotlin's [Pair] extension [Pair.to] is really annoying */
fun <T> LinkedBindingBuilder<T>.toType(clazz: Class<out T>): ScopedBindingBuilder {
    return this.to(clazz)
}

fun <T> LinkedBindingBuilder<T>.toType(clazz: TypeLiteral<T>): ScopedBindingBuilder {
    return this.to(clazz)
}

fun <T> LinkedBindingBuilder<T>.toType(clazz: Key<T>): ScopedBindingBuilder {
    return this.to(clazz)
}