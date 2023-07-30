package com.example.mvi

sealed class TermsIntent {
    data class AgreeToTermsGroup(
        val status: TermsAgreementStatus,
    ) : TermsIntent()
    data class AgreeToTerms(
        val termsId: Int,
    ) : TermsIntent()

    object ClickPlay : TermsIntent()
    object ClickRewind : TermsIntent()
}