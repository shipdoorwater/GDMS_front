package com.example.gdms_front

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gdms_front.myPage.MyPageActivity
import com.example.gdms_front.navigation_frag.ProfitFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<ImageView>(R.id.myPageBtn).setOnClickListener {
            val intent = Intent(this, MyPageActivity::class.java)
            startActivity(intent)
        }
        
        
        // 다른 엑티비티에서 나와서 지울 때
        val fragmentToLoad = intent.getStringExtra("FragmentToLoad")
        Log.d("FragmentToLoad", "FragmentToLoad: $fragmentToLoad")

        if (fragmentToLoad=="ProfitFragment") {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, ProfitFragment())
            .commit()
            intent.removeExtra("FragmentToLoad")
        }
    }
}