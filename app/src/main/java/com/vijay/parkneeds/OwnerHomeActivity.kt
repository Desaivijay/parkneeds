package com.vijay.parkneeds

import android.content.Intent
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
import com.google.gson.reflect.TypeToken
import com.vijay.parkneeds.Adapter.ParkingLotItemAdapter
import com.vijay.parkneeds.Models.ParkingLot
import com.vijay.parkneeds.Models.UserModel
import com.vijay.parkneeds.databinding.ActivityOwnerHomeBinding
import java.lang.reflect.Type

class OwnerHomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private lateinit var binding: ActivityOwnerHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOwnerHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAddParkingLot.setOnClickListener {
            startActivity(Intent(this, AddParkingLotActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()

        val sp = getSharedPreferences("my_sp", MODE_PRIVATE)
        val gson = Gson()
        val json = sp.getString("user_model", null)
        val type: Type = object : TypeToken<UserModel>() {}.type

        val userModel = gson.fromJson<Any>(json, type) as UserModel

        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference.child("parking")

        database.orderByChild("id").equalTo(userModel.id).addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@OwnerHomeActivity, "Failed", Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val parkingList = ArrayList<ParkingLot>()

                for (parking in snapshot.children){
                    val parkingName = parking.child("parkingNAme").value.toString()
                    val parkingAddress = parking.child("parkingAddress").value.toString()
                    val parkingClosingTime = parking.child("parkingClosingTime").value.toString()
                    val parkingOpeningTime = parking.child("parkingOpeningTime").value.toString()
                    val parkingTotalSlots = parking.child("parkingTotalSlots").value.toString()
                    val parkingAvailableSlots = parking.child("parkingAvailableSlots").value.toString()
                    val parkingType = parking.child("parkingType").value.toString()
                    val parkingRate = parking.child("parkingRate").value.toString()
                    val userModel = parking.child("userModel").value as UserModel

                    parkingList.add(ParkingLot(userModel, parkingName, parkingAddress, parkingOpeningTime, parkingClosingTime, parkingType, parkingRate, parkingTotalSlots, parkingAvailableSlots))
                }


                val adapter = ParkingLotItemAdapter(parkingList, this@OwnerHomeActivity)
                binding.rvParkingLots.adapter = adapter
            }
        })
        val data = ArrayList<ParkingLot>()

        for (i in 1..10){
            data.add(ParkingLot(userModel,"abc", "sudbury", "10 am", "10 pm","Paid", "20", "50", "20"))
        }
    }
}