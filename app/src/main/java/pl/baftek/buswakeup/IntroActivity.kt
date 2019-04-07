package pl.baftek.buswakeup

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.github.paolorotolo.appintro.AppIntro
import com.github.paolorotolo.appintro.AppIntroFragment
import com.github.paolorotolo.appintro.model.SliderPage

class IntroActivity : AppIntro() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val page1 = SliderPage().apply {
            val color = R.color.colorPrimary
            title = getString(R.string.hi)
            description = getString(R.string.intro_description_1)
            imageDrawable = R.drawable.ic_bus_172dp
            bgColor = resources.getColor(color)
        }

        val page2 = SliderPage().apply {
            val color = R.color.colorPrimaryDark
            title = getString(R.string.set_destination)
            description = getString(R.string.intro_description_2)
            imageDrawable = R.drawable.ic_my_location_172dp
            bgColor = resources.getColor(color)
        }

        val page3 = SliderPage().apply {
            val color = R.color.colorAccent
            title = getString(R.string.have_nice_rest)
            description = getString(R.string.intro_description_3)
            imageDrawable = R.drawable.ic_moon
            bgColor = resources.getColor(color)
        }

        addSlide(AppIntroFragment.newInstance(page1))
        addSlide(AppIntroFragment.newInstance(page2))
        addSlide(AppIntroFragment.newInstance(page3))

        showSkipButton(false)
        setFadeAnimation()
    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        startActivity(Intent(this, MapsActivity::class.java))
        finish()
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        startActivity(Intent(this, MapsActivity::class.java))
        finish()
    }
}