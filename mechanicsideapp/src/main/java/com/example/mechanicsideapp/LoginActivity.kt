package com.example.mechanicsideapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loginBtn = findViewById<View>(R.id.btnLogin) as Button
        loginBtn.setOnClickListener(View.OnClickListener {
                login()
        })

        val noAccount = findViewById<TextView>(R.id.noAccount)
        noAccount.setOnClickListener{
            val intent = Intent(this,RegisterActivity::class.java)
            startActivity(intent)
        }

        val showPass = findViewById<View>(R.id.chbShowPass) as CheckBox
        val pass = findViewById<View>(R.id.inputLoginPassword) as EditText
        showPass.setOnClickListener(View.OnClickListener{
            if(showPass.isChecked) {
                pass.inputType = 1
            }else {
                pass.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
        })
    }

    override fun onStart() {
        super.onStart()

        verifyUserisLoggedIn()
    }

    private fun verifyUserisLoggedIn(){
        val currentUser = FirebaseAuth.getInstance().currentUser
        if(currentUser != null){
            val intent = Intent(this,DashboardmechanicActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private fun login(){
        val emailTxt = findViewById<View>(R.id.inputLoginEmail) as EditText
        val passTxt = findViewById<View>(R.id.inputLoginPassword) as EditText

        var email = emailTxt.text.toString()
        var pass = passTxt.text.toString()

        if(!email.isEmpty() && !pass.isEmpty()){
            mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, OnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val intent = Intent(this, DashboardmechanicActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        Toast.makeText(this, "Succesfully Logged In.", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "Error:" +task.exception!!.message.toString(), Toast.LENGTH_LONG).show()
                    }
                })
        }else{
            Toast.makeText(this, "Please fill up all credentials.", Toast.LENGTH_LONG).show()
        }
    }

}