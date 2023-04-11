package com.soojin.jinstargram

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.appsearch.SetSchemaRequest.READ_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.soojin.jinstargram.databinding.ActivityLoginBinding
import com.soojin.jinstargram.databinding.ActivityMainBinding
import com.soojin.jinstargram.navigation.*
import kotlinx.android.synthetic.main.activity_main.*



class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1)

        binding.bottomNavigation.run {
            setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.action_home -> {
                        var detailViewFragment = DetailViewFragment()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.main_content, detailViewFragment).commit()
                        true
                    }
                    R.id.action_search -> {
                        var gridFragment = GridFragment()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.main_content, gridFragment).commit()
                        true
                    }
                    R.id.action_add_photo -> {
                        //권한 체크
                        if (ContextCompat.checkSelfPermission(this@MainActivity, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED){
                            startActivity(Intent(this@MainActivity,AddPhotoActivity::class.java))
                        }

                        true
                    }
                    R.id.action_favorite -> {
                        var alarmFragment = AlarmFragment()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.main_content, alarmFragment).commit()
                        true
                    }
                    R.id.action_account-> {
                        var userFragment = UserFragment()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.main_content, userFragment).commit()
                        true
                    }
                    else -> {
                       false
                    }
                }


            }

        }

    }
}