package com.queatz.fantasydating.ui

import coil.annotation.ExperimentalCoilApi
import coil.decode.DataSource
import coil.drawable.CrossfadeDrawable
import coil.request.ImageResult
import coil.request.SuccessResult
import coil.transition.CrossfadeTransition
import coil.transition.Transition
import coil.transition.TransitionTarget

class AlwaysCrossfade(durationMillis: Int = CrossfadeDrawable.DEFAULT_DURATION) : Transition {

    @ExperimentalCoilApi
    private val transition = CrossfadeTransition(durationMillis, true)

    override suspend fun transition(target: TransitionTarget, result: ImageResult) {
        var imageResult =
            if (result is SuccessResult && result.metadata.dataSource == DataSource.MEMORY_CACHE) {
                result.copy(metadata = result.metadata.copy(dataSource = DataSource.NETWORK))
            } else result

        transition.transition(target, imageResult)
    }

    override fun equals(other: Any?) = transition.equals(other)
    override fun hashCode() = transition.hashCode()
    override fun toString() = transition.toString()
}
