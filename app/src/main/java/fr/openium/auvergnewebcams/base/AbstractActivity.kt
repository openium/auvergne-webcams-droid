package fr.openium.auvergnewebcams.base

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.custom.OnBackPressedListener
import fr.openium.auvergnewebcams.utils.DateUtils
import fr.openium.auvergnewebcams.utils.PreferencesUtils
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.toolbar.toolbar
import org.koin.android.ext.android.inject

/**
 * Created by Openium on 19/02/2019.
 */

abstract class AbstractActivity : AppCompatActivity() {
    protected val disposables: CompositeDisposable = CompositeDisposable()

    protected val prefUtils by inject<PreferencesUtils>()
    protected val dateUtils by inject<DateUtils>()

    protected open val handleFragmentBackPressed: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(layoutId)

        toolbar?.also { setSupportActionBar(it) }
        setHomeAsUp(showHomeAsUp)
    }

    protected fun setHomeAsUp(enabled: Boolean = true) {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(enabled)
            setHomeButtonEnabled(enabled)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            onBackPressed()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (handleFragmentBackPressed) {
            val currentFragment =
                supportFragmentManager.findFragmentById(R.id.container_framelayout)
            if (currentFragment !is OnBackPressedListener || !currentFragment.onBackPressed()) {
                super.onBackPressed()
            }
        } else {
            super.onBackPressed()
        }
    }

    // Si true la fleche back est affichée
    protected open val showHomeAsUp: Boolean = false

    // Retourne le layout qui est associé à l'activité
    protected open val layoutId: Int = R.layout.container_toolbar
}