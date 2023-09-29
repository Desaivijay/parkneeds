package com.vijay.parkneeds

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import com.vijay.parkneeds.Models.UserModel
import com.vijay.parkneeds.databinding.ActivityRegistrationBinding

class RegistrationActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private lateinit var binding: ActivityRegistrationBinding

    private val spinnerItems = listOf("Select User Type", "Park Spot Finder", "Park Spot Owner")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference.child("users")

        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, spinnerItems)
        binding.spinner.adapter = adapter

        binding.btnRegister.setOnClickListener {
            if (binding.etName.text.equals(null)){
                binding.etName.error = "Please enter name"
            }else if (binding.etEmail.text.equals(null)){
                binding.etEmail.error = "Please enter email"
            }else if (binding.etPassword.text.equals(null)){
                binding.etPassword.error = "Please enter email"
            }else if (binding.etConfirmPassword.text.equals(null)){
                binding.etConfirmPassword.error = "Please enter email"
            }else if (binding.spinner.selectedItemId.equals(0)){
                Toast.makeText(this, "Please select user type", Toast.LENGTH_SHORT).show()
            }else{
                auth.createUserWithEmailAndPassword(binding.etEmail.text.toString(), binding.etPassword.text.toString()).addOnCompleteListener(this@RegistrationActivity){
                    if (it.isSuccessful) {
                        val id = auth.currentUser?.uid
                        database.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (id != null) {
                                    val userModel = UserModel(binding.etName.text.toString(), binding.etEmail.text.toString(), binding.etPassword.text.toString(), binding.spinner.selectedItem.toString(), id)
                                    database.child(id).setValue(userModel)
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(this@RegistrationActivity, "Failed to register", Toast.LENGTH_SHORT).show()
                            }
                        })
                        Toast.makeText(this@RegistrationActivity, "Successfully Singed Up", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@RegistrationActivity, "Singed Up Failed!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}