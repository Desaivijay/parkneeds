package com.vijay.parkneeds

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vijay.parkneeds.Adapter.ParkingLotItemAdapter
import com.vijay.parkneeds.Models.ParkingLot
import com.vijay.parkneeds.Models.UserModel
import com.vijay.parkneeds.databinding.ActivityFinderHomeBinding
import java.lang.reflect.Type

class FinderHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFinderHomeBinding

    lateinit var parkingLotList: ArrayList<ParkingLot>
    private lateinit var userModel: UserModel
    lateinit var adapter: ParkingLotItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinderHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)
        linearLayoutManager.stackFromEnd = true
        binding.rvParkingLots.layoutManager = linearLayoutManager

        val sp = getSharedPreferences("my_sp", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sp?.getString("user_model", null)
        val type: Type = object : TypeToken<UserModel>() {}.type

        userModel = UserModel()
        userModel = gson.fromJson<Any>(json, type) as UserModel

        parkingLotList = ArrayList()
        adapter = ParkingLotItemAdapter(parkingLotList, this)
        binding.rvParkingLots.adapter = adapter

        getParkingLotList()

        binding.btnSearch.setOnClickListener {
            val query = binding.etCitySearch.text.toString().toLowerCase()
            val filteredList : ArrayList<ParkingLot> = parkingLotList.filter { item ->
                item.parkingAddress.toLowerCase().contains(query)
            } as ArrayList<ParkingLot>
            adapter.updateData(filteredList)
        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.menu_clock -> {
                startActivity(Intent(this, ClockActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun getParkingLotList() {
        val parkingLotRef = Firebase.database.reference.child("parking")

        parkingLotRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                parkingLotList.clear()
                for (childSnapshot in snapshot.children) {
                    val parkingLot = childSnapshot.getValue(ParkingLot::class.java)
                    parkingLot?.let { parkingLotList.add(parkingLot) }

                    adapter.notifyDataSetChanged()
                }
            }
        })
    }
}