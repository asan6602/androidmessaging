package com.example.messagingapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUp : AppCompatActivity() {

    private lateinit var edtName: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var edtPasswordConfirm: EditText
    private lateinit var btnSignUp: Button
    private lateinit var btnLogin: Button

    private lateinit var mAuth: FirebaseAuth

    private lateinit var mDbRef: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        mAuth = FirebaseAuth.getInstance()

        edtName = findViewById(R.id.edt_Name)
        edtEmail = findViewById(R.id.edt_email)
        edtPassword = findViewById(R.id.edt_password)

        edtPasswordConfirm = findViewById(R.id.edt_passwordConfirm)

        btnLogin = findViewById(R.id.btnLogin)
        btnSignUp = findViewById(R.id.btnSignUp)

        supportActionBar?.hide()

        btnSignUp.setOnClickListener {
            val name = edtName.text.toString()
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()
            val passwordConfirm = edtPasswordConfirm.text.toString()

            //checks if user left fields empty
            if(name.isNullOrEmpty() || email.isNullOrEmpty() || password.isNullOrEmpty()
                || passwordConfirm.isNullOrEmpty()) {
                Toast.makeText(this@SignUp, "Some fields are empty", Toast.LENGTH_SHORT).show()
            }

            //checks if user enters the same password
            else if(password.equals(passwordConfirm)) {
                signup(name, email, password)
            }
            else {
                Toast.makeText(this@SignUp, "The passwords you entered are not the same", Toast.LENGTH_SHORT).show()
            }


        }



        //redirect to sign up page
        btnLogin.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
    }

    private fun signup(name:String, email: String, password:String) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    //adding user to database
                    addUserToDatabase(name, email, mAuth.currentUser?.uid!!)  //!! null safe

                    //redirect
                    val intent = Intent(this@SignUp, MainActivity::class.java)
                    finish()
                    startActivity(intent)
                } else {
                    Toast.makeText(this@SignUp, "Some error occurred", Toast.LENGTH_SHORT).show()

                }
            }

    }

    private fun addUserToDatabase(name: String, email: String, uid:String) {
        mDbRef = FirebaseDatabase.getInstance().getReference()
        mDbRef.child("user").child(uid).setValue(User(name, email, uid))

    }
}