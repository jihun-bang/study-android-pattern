package com.example.mvi

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.Stack

class MainViewModel : ViewModel() {
    private val _state = MutableLiveData<TermsState>()
    val state: LiveData<TermsState> = _state

    private val _previousStates = Stack<TermsState>()

    init {
        _state.value = TermsState(
            termsAgreementStatus = TermsAgreementStatus.NONE,
            terms = listOf(
                Terms(1, false),
                Terms(2, false),
                Terms(3, false),
            ),
            outputMessage = ""
        )
    }

    fun processIntent(intent: TermsIntent) {
        when (intent) {
            is TermsIntent.AgreeToTermsGroup -> {
                val status = intent.status
                _state.value = _state.value?.copy(
                    termsAgreementStatus = status,
                    terms = when (status) {
                        TermsAgreementStatus.NONE -> _state.value?.terms
                        TermsAgreementStatus.ALL_AGREED -> listOf(
                            Terms(1, true),
                            Terms(2, true),
                            Terms(3, true),
                        )

                        TermsAgreementStatus.PARTIALLY_AGREED -> listOf(
                            Terms(1, true),
                            Terms(2, false),
                            Terms(3, false),
                        )

                        TermsAgreementStatus.NONE_AGREED -> listOf(
                            Terms(1, false),
                            Terms(2, false),
                            Terms(3, false),
                        )
                    } ?: emptyList(),
                    outputMessage = "${status.kr} 클릭",
                )
                _previousStates.push(_state.value)
            }

            is TermsIntent.AgreeToTerms -> {
                var checked = ""
                val oldState = _state.value
                val newTerms = oldState?.terms?.map { terms ->
                    if (terms.id == intent.termsId) {
                        val isAgreed = terms.isAgreed
                        checked = if (isAgreed) "해제" else "체크"
                        terms.copy(isAgreed = !isAgreed)
                    } else {
                        terms
                    }
                }
                _state.value = oldState?.copy(
                    terms = newTerms ?: emptyList(),
                    outputMessage = "약관${intent.termsId} 동의 $checked",
                )
                _previousStates.push(_state.value)
            }

            is TermsIntent.ClickPlay -> {
                _state.value =
                    _state.value?.copy(outputMessage = "PLAY 클릭")
            }

            is TermsIntent.ClickRewind -> {
                val outputMessage = "REWIND 클릭"
                _state.value = if (_previousStates.size > 1) {
                    _previousStates.pop()
                    _previousStates.lastElement()
                        .copy(outputMessage = "$outputMessage (롤백O)")
                } else _state.value?.copy(
                    outputMessage = "$outputMessage (롤백X)"
                )
            }
        }
    }
}