package com.example.gdms_front.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.gdms_front.R
import com.example.gdms_front.databinding.ActivityJoin1Binding

class JoinActivity1 : AppCompatActivity() {

    private lateinit var binding: ActivityJoin1Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_join1)
        binding = ActivityJoin1Binding.inflate(layoutInflater)
        setContentView(binding.root)

//        var isGotoJoin = true
//        if (pw1 != pw2) {
//            isGotoJoin = false
//        }
//        if (isGotoJoin == true) {
//    }
    }
}