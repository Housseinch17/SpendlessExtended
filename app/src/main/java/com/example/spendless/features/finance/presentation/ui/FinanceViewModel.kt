package com.example.spendless.features.finance.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendless.features.finance.domain.LifecycleObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

sealed interface FinanceEvents {
    data object PromptPin : FinanceEvents
}

@HiltViewModel
class FinanceViewModel @Inject constructor(
    private val lifecycleObserver: LifecycleObserver,
) : ViewModel() {
    private val _events = Channel<FinanceEvents>()
    val events = _events.receiveAsFlow()

    init {
        Timber.tag("MyTag").d("FinanceViewModel Started")
        appLifeCycleObserver()
    }

    override fun onCleared() {
        super.onCleared()
        Timber.tag("MyTag").d("FinanceViewModel Cleared!")
    }

    //trigger lifecycle of the app when it resume it will promptPin()
    private fun appLifeCycleObserver(){
        viewModelScope.launch {
            lifecycleObserver.isInForeground.collect { isForeground->
                if(isForeground){
                    promptPin()
                }
            }
        }
    }

    private fun promptPin() {
        viewModelScope.launch {
            Timber.tag("MyTag").d("promptPin sent")
            //here used trySend because if the collector is not available since we are using
            //default channel it will not suspend until collector collect it
            //but it will just send and forget(drop)
            _events.trySend(FinanceEvents.PromptPin)
        }
    }
}