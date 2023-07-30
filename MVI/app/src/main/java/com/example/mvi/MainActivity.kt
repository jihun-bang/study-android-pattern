package com.example.mvi

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.ScrollingMovementMethod
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.mvi.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        viewModel.state.observe(this) { state ->
            renderState(state)
        }
        setUpIntents()
    }

    override fun onResume() {
        super.onResume()

        binding.textLogger.apply {
            movementMethod = ScrollingMovementMethod()
            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    post {
                        val scrollAmount = layout.getLineTop(lineCount) - height
                        if (scrollAmount > 0)
                            scrollTo(0, scrollAmount)
                        else
                            scrollTo(0, 0)
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })
        }
    }

    private fun renderState(state: TermsState) {
        binding.apply {
            radio1.isChecked = state.termsAgreementStatus == TermsAgreementStatus.ALL_AGREED
            radio2.isChecked = state.termsAgreementStatus == TermsAgreementStatus.PARTIALLY_AGREED
            radio3.isChecked = state.termsAgreementStatus == TermsAgreementStatus.NONE_AGREED
            checkBox1.isChecked = state.terms[0].isAgreed
            checkBox2.isChecked = state.terms[1].isAgreed
            checkBox3.isChecked = state.terms[2].isAgreed
            textLogger.text = "${textLogger.text}\n${state.outputMessage}"
        }
    }

    private fun setUpIntents() {
        with(binding) {
            radio1.setOnClickListener {
                viewModel.processIntent(TermsIntent.AgreeToTermsGroup(TermsAgreementStatus.ALL_AGREED))
            }
            radio2.setOnClickListener {
                viewModel.processIntent(TermsIntent.AgreeToTermsGroup(TermsAgreementStatus.PARTIALLY_AGREED))
            }
            radio3.setOnClickListener {
                viewModel.processIntent(TermsIntent.AgreeToTermsGroup(TermsAgreementStatus.NONE_AGREED))
            }
            checkBox1.setOnClickListener {
                viewModel.processIntent(TermsIntent.AgreeToTerms(termsId = 1))
            }
            checkBox2.setOnClickListener {
                viewModel.processIntent(TermsIntent.AgreeToTerms(termsId = 2))
            }
            checkBox3.setOnClickListener {
                viewModel.processIntent(TermsIntent.AgreeToTerms(termsId = 3))
            }
            playButton.setOnClickListener {
                val intent = Intent(this@MainActivity, SecondActivity::class.java)
                startActivity(intent)
                viewModel.processIntent(TermsIntent.ClickPlay)
            }
            rewindButton.setOnClickListener {
                viewModel.processIntent(TermsIntent.ClickRewind)
            }
        }
    }
}
