package com.example.spendless.features.finance.data.datasource

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.spendless.features.finance.domain.LifecycleObserver
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber

class AndroidAppLifecycleObserver @Inject constructor(): LifecycleObserver {
    override val isInForeground: Flow<Boolean> = callbackFlow {
        val lifecycle = ProcessLifecycleOwner.get().lifecycle

        //here i have to trigger if it's already in background and later resumed
        //since lifecycle is
        var hasBeenBackgrounded = false

//        //Emit the current state immediately
//        trySend(lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED))

        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> {
                    hasBeenBackgrounded = true
                }
                //here only send when onResume
                Lifecycle.Event.ON_RESUME -> {
                    if(hasBeenBackgrounded) {
                        Timber.tag("MyTag").d("triggered")
                        trySend(true)
                    }
                }
                else -> Unit
            }
        }

        lifecycle.addObserver(observer)
        awaitClose { lifecycle.removeObserver(observer) }
    }.flowOn(Dispatchers.Main)
}