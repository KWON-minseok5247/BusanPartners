package com.kwonminseok.busanpartners.mainScreen.profile


import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.kwonminseok.busanpartners.databinding.ActivityOnboardingFinishBinding

class OnboardingFinishActivity : AppCompatActivity() {
    private lateinit var btnStart: LinearLayout

    private lateinit var binding: ActivityOnboardingFinishBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingFinishBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        btnStart = binding.layoutStart
        btnStart.setOnClickListener {
            finish()
        }
    }
}
