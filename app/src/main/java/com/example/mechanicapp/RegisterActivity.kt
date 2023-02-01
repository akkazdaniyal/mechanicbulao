package com.example.mechanicapp

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.*
import com.example.mechanicapp.databinding.ActivityMainBinding
import com.example.mechanicapp.databinding.ActivityRegisterBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    val mAuth = FirebaseAuth.getInstance()
    private lateinit var mDatabase : DatabaseReference
    private val FirebaseUserID: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val regBtn = findViewById<View>(R.id.btnRegister) as Button

        mDatabase = FirebaseDatabase.getInstance().getReference("Users")

        regBtn.setOnClickListener(View.OnClickListener {
            view -> register()
        })

        val alreadyAccount = findViewById<TextView>(R.id.alreadyHaveAccount)
        alreadyAccount.setOnClickListener{
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }
        val showPass = findViewById<View>(R.id.chbShowPassRegister) as CheckBox
        val pass = findViewById<View>(R.id.inputPassword) as EditText
        val confirmPass = findViewById<View>(R.id.inputConfirmPassword) as EditText
        showPass.setOnClickListener(View.OnClickListener{
            if(showPass.isChecked) {
                pass.inputType = 1
                confirmPass.inputType = 1
            }else {
                pass.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                confirmPass.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
        })


    }


    private fun register(){
            val usernameTxt = findViewById<View>(R.id.inputUsername) as EditText
            val emailTxt = findViewById<View>(R.id.inputEmail) as EditText
            val passwordTxt = findViewById<View>(R.id.inputPassword) as EditText
            val cpasswordTxt = findViewById<View>(R.id.inputConfirmPassword) as EditText

        var username = usernameTxt.text.toString()
        var email = emailTxt.text.toString()
        var password = passwordTxt.text.toString()
        var cpassword = cpasswordTxt.text.toString()

            if(!username.isEmpty() && !email.isEmpty() && !password.isEmpty() && !cpassword.isEmpty()) {
                if(password == cpassword) {
                    mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this,
                        OnCompleteListener{ task ->
                        if(task.isSuccessful) {
                            val user = mAuth.currentUser
                            val FirebaseUserID = user!!.uid
                            val userData = Users(FirebaseUserID,username,"","Happy User","","","",0)
                            mDatabase.child(FirebaseUserID).setValue(userData)
                            Toast.makeText(this, "Successfully Registered!", Toast.LENGTH_LONG).show()
                        }else {
                            Toast.makeText(this,"Error:" + task.exception!!.message.toString(), Toast.LENGTH_LONG).show()
                        }
                    })
                }else{
                    Toast.makeText(this, "Password does not match!", Toast.LENGTH_LONG).show()
                }
            }
            else{
                Toast.makeText(this, "Please fill up all the credentials!", Toast.LENGTH_LONG).show()
            }
        }
    }
