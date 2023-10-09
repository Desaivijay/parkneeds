package com.vijay.parkneeds

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
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

    lateinit var parkingList: ArrayList<ParkingLot>
    lateinit var userModel: UserModel
    lateinit var adapter: ParkingLotItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOwnerHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)
        linearLayoutManager.stackFromEnd = true
        binding.rvParkingLots.layoutManager = linearLayoutManager

        val sp = getSharedPreferences("my_sp", MODE_PRIVATE)
        val gson = Gson()
        val json = sp.getString("user_model", null)
        val type: Type = object : TypeToken<UserModel>() {}.type

        userModel = gson.fromJson<Any>(json, type) as UserModel

        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference.child("parking")

        parkingList = ArrayList<ParkingLot>()
        adapter = ParkingLotItemAdapter(parkingList, this@OwnerHomeActivity)
        binding.rvParkingLots.adapter = adapter

        binding.btnAddParkingLot.setOnClickListener {
            startActivity(Intent(this, AddParkingLotActivity::class.java))
        }
    }


    override fun onResume() {
        super.onResume()

        getParkingList()
    }

    private fun getParkingList() {
        database.orderByChild("userModel/id").equalTo(userModel.id)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@OwnerHomeActivity, "Failed", Toast.LENGTH_SHORT).show()
                }

                override fun onDataChange(snapshot: DataSnapshot) {

                    parkingList.clear()
                    for (parking in snapshot.children) {
                        val parkingLot = parking.getValue(ParkingLot::class.java)
                        parkingLot?.let { parkingList.add(parkingLot) }

                        adapter.notifyDataSetChanged()
                    }
                }
            })
    }
}