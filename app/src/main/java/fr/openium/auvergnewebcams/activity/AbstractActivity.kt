package fr.openium.auvergnewebcams.activity

import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.android.appKodein
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.fragment.OnBackPressedListener
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.toolbar.*

abstract class AbstractActivity : AppCompatActivity() {
    protected val subscriptions: CompositeDisposable = CompositeDisposable()
    protected open val handleFragmentBackPressed: Boolean = true
    protected val kodeinInjector = KodeinInjector()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        kodeinInjector.inject(appKodein())

        beforeSetContentView()
        setContentView(layoutId)
        if (toolbar != null) {
            setSupportActionBar(toolbar)
        }

        if (showHomeAsUp) {
            if (supportActionBar != null) {
                (supportActionBar as ActionBar).setDisplayHomeAsUpEnabled(true)
                (supportActionBar as ActionBar).setHomeButtonEnabled(true)
            }
        }
    }

    open protected fun beforeSetContentView() {

    }

    override fun onDestroy() {
        super.onDestroy()
        subscriptions.clear()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.home) {
            onArrowPressed()
            return true
        } else {
            return super.onOptionsItemSelected(item)
        }
    }


    open fun onArrowPressed() {
        onBackPressed()
    }


    override fun onBackPressed() {
        if (handleFragmentBackPressed) {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.container_framelayout)
            if (!(currentFragment is OnBackPressedListener) || !currentFragment.onBackPressed()) {
                super.onBackPressed()
            }
        } else {
            super.onBackPressed()
        }
    }

    // Si true la fleche back est affichée
    protected open val showHomeAsUp: Boolean = false

    // Retourne le layout qui est associé à l'activité
    protected open val layoutId: Int = R.layout.container
}
