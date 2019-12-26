package com.revolut.converter.core.di

internal object CoreDependenciesHolder {
    lateinit var coreDependencies: CoreDependencies
}

val coreDependencies: CoreDependencies
    get() {
        return CoreDependenciesHolder.coreDependencies
    }
