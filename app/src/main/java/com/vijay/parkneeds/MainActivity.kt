package com.vijay.parkneeds

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.vijay.parkneeds.Models.UserModel
import com.vijay.parkneeds.databinding.ActivityMainBinding
import com.vijay.parkneeds.databinding.ActivitySplashBinding

class MainActivity : AppCompatActivity() {

    // View Binding
    private lateinit var binding: ActivityMainBinding

    private lateinit var userModel: UserModel

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private lateinit var sp: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference.child("users")

        sp = getSharedPreferences("my_sp", MODE_PRIVATE)
        editor = sp.edit()

        binding.btnLogin.setOnClickListener {
            if (binding.etEmail.text.equals(null)){
                binding.etEmail.error = "PLease enter email"
            }else if (binding.etPassword.text.equals(null)){
                binding.etPassword.error = "Please enter password"
            }else{
                auth.signInWithEmailAndPassword(binding.etEmail.text.toString(), binding.etPassword.text.toString()).addOnCompleteListener(this) {
                    if (it.isSuccessful) {
                        val id = auth.currentUser?.uid
                        if (id != null){
                            database.addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val value = snapshot.child(id).getValue(UserModel::class.java)
                                    if (value != null) {

                                        val gson = Gson()
                                        val json = gson.toJson(value)
                                        editor.putString("user_model", json)
                                        editor.apply()

                                        if (value.userType == "Park Spot Finder"){
                                            startActivity(Intent(this@MainActivity, FinderHomeActivity::class.java))
                                        }else{
                                            startActivity(Intent(this@MainActivity, OwnerHomeActivity::class.java))
                                        }
                                        finish()
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Toast.makeText(this@MainActivity, "Failed to register", Toast.LENGTH_SHORT).show()
                                }
                            })
                        }
                        Toast.makeText(this, "Successfully LoggedIn", Toast.LENGTH_SHORT).show()
                    } else
                        Toast.makeText(this, "Log In failed ", Toast.LENGTH_SHORT).show()
                }
            }

//            if (userModel.userType.equals("Park Spot Finder", true)) {
//                startActivity(Intent(this, FinderHomeActivity::class.java))
//            }else{
//                startActivity(Intent(this, OwnerHomeActivity::class.java))
//            }
//            finish()
        }

        binding.tvSignUpHere.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }
    }
}