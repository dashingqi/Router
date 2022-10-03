package com.dashingqi.applicationa

import android.content.ComponentName
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.dashingqi.router.annotation.Route

@Route(path = "home/main", description = "HomePage")
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.btnA).setOnClickListener {
            Intent().apply {
                component = ComponentName(
                    "com.dashingqi.applicationb",
                    "com.dashingqi.applicationb.ActionActivity"
                )
                startActivity(this)
            }
        }
    }
}