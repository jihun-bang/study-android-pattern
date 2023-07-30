package com.example.mvi

data class TermsState(
    val termsAgreementStatus: TermsAgreementStatus,
    val terms: List<Terms>,
    val outputMessage: String
)

enum class TermsAgreementStatus(val kr: String) {
    NONE("선택 없음"), ALL_AGREED("모든 약간 동의"), PARTIALLY_AGREED("일부 약간 동의"), NONE_AGREED("모든 약간 미동의"),
}

data class Terms(
    val id: Int,
    val isAgreed: Boolean
)