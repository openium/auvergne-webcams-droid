package fr.openium.auvergnewebcams.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.fragment.OnBackPressedListener
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.toolbar.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein

/**
 * Created by Skyle on 19/02/2019.
 */

abstract class AbstractActivity : AppCompatActivity(), KodeinAware {
    override val kodein: Kodein by closestKodein()

    protected val disposables: CompositeDisposable = CompositeDisposable()
    protected var rebindDisposables: CompositeDisposable = CompositeDisposable() //Resubscribe in onstart

    protected open val handleFragmentBackPressed: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        beforeSetContentView()
        setContentView(layoutId)
        if (toolbar != null) {
            setSupportActionBar(toolbar)
        }

        setHomeAsUp(showHomeAsUp)
    }

    protected fun setHomeAsUp(enabled: Boolean = true) {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(enabled)
            setHomeButtonEnabled(enabled)
        }
    }

    override fun onStart() {
        super.onStart()
        startDisposable(rebindDisposables)
    }

    override fun onStop() {
        super.onStop()
        rebindDisposables.clear()
    }

    protected open fun startDisposable(onStartDisposables: CompositeDisposable) {

    }

    protected open fun beforeSetContentView() {

    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            onArrowPressed()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    open fun onArrowPressed() {
        onBackPressed()
    }

    override fun onBackPressed() {
        if (handleFragmentBackPressed) {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.container_framelayout)
            if (currentFragment !is OnBackPressedListener || !currentFragment.onBackPressed()) {
                super.onBackPressed()
            }
        } else {
            super.onBackPressed()
        }
    }

    protected fun showMessage(text: Int, duration: Int) {
        Snackbar.make(window.decorView.rootView, text, duration).show()
    }

    protected fun showMessage(text: String, duration: Int) {
        Snackbar.make(window.decorView.rootView, text, duration).show()
    }

    // Si true la fleche back est affichée
    protected open val showHomeAsUp: Boolean = false

    // Retourne le layout qui est associé à l'activité
    protected open val layoutId: Int = R.layout.container_toolbar
}