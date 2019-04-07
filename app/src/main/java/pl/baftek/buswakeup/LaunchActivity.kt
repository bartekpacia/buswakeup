package pl.baftek.buswakeup

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.edit

class LaunchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        if (preferences.getBoolean(PREFS_KEY_INTRO, false)) {
            startActivity(Intent(this, MapsActivity::class.java))
        } else {
            preferences.edit {
                putBoolean(PREFS_KEY_INTRO, true)
            }
            startActivity(Intent(this, IntroActivity::class.java))
        }

        //startActivity(Intent(this, IntroActivity::class.java))

        finish()
    }
}