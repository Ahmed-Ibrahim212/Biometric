package com.example.biometricauthentication

import android.content.Intent
import android.hardware.biometrics.BiometricManager
import android.hardware.biometrics.BiometricPrompt
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.core.content.ContextCompat
import com.example.biometricauthentication.databinding.ActivityMainBinding
import java.util.concurrent.Executor

class MainActivity : AppCompatActivity() {
    private var _binding : ActivityMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: androidx.biometric.BiometricPrompt.PromptInfo


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imgFInger.setOnClickListener {
            checkDeviceHasBiometric()
        }

        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor, object: BiometricPrompt.AuthenticationCallback(){
        override fun onAuthenticationError(errorCode: Int, errString: CharSequence){
            super.onAuthenticationError(errorCode, errString)
            Toast.makeText(this@MainActivity,"Authentication error: $errString", Toast.LENGTH_LONG).show()
        }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                super.onAuthenticationSucceeded(result)

                Toast.makeText(this@MainActivity,"Authentication succeeded!", Toast.LENGTH_LONG).show()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()

                Toast.makeText(this@MainActivity,"Authentication failed!", Toast.LENGTH_LONG).show()
            }
        })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Sample Title")
            .setSubtitle("Sample Subtitle")
            .setNegativeButtonText("Sample NegativeButtonText")
            .build()

        binding.btnLogin.setOnClickListener{
            biometricPrompt.authenticate(promptInfo)
        }
    }

    fun checkDeviceHasBiometric(){
        val biometricManager = androidx.biometric.BiometricManager.from(this)
        when(biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)){
            BiometricManager.BIOMETRIC_SUCCESS ->{
                Log.d("MY_APP_TAG", "App can authenticate using biometric")
                binding.txtMsg.text = "App can authenticate using biometric"
                binding.btnLogin.isEnabled = true
            }

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE->{
                Log.d("MY_APP_TAG", "Biometric features are currently not available")
                binding.txtMsg.text = "Biometric features are currently not available"
                binding.btnLogin.isEnabled = false
            }

             BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED->{
                 val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                     putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED, BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
                 }
                 binding.btnLogin.isEnabled = false

                 startActivityForResult(enrollIntent, 100)

             }
        }
    }
}