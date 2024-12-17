package fr.openium.auvergnewebcams.base

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.custom.OnBackPressedListener
import fr.openium.auvergnewebcams.utils.DateUtils
import fr.openium.auvergnewebcams.utils.PreferencesUtils
import io.reactivex.disposables.CompositeDisposable
import org.koin.android.ext.android.inject

/**
 * Created by Openium on 19/02/2019.
 */

abstract class AbstractActivity<T : ViewBinding> : AppCompatActivity() {

    protected val disposables: CompositeDisposable = CompositeDisposable()

    protected val prefUtils by inject<PreferencesUtils>()
    protected val dateUtils by inject<DateUtils>()

    protected open val handleFragmentBackPressed: Boolean = true

    private var _binding: T? = null
    protected val binding get() = _binding!!

    abstract fun provideViewBinding(): T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = provideViewBinding()
        setContentView(binding.root)

        binding.root.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)?.also {
            setSupportActionBar(it)
        }
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
        _binding = null
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
            val currentFragment = supportFragmentManager.findFragmentById(R.id.container_framelayout)
            if (currentFragment !is OnBackPressedListener || !currentFragment.onBackPressed()) {
                super.onBackPressed()
            }
        } else {
            super.onBackPressed()
        }
    }

    // If true, the back arrow is displayed
    protected open val showHomeAsUp: Boolean = false

    // Returns the layout associated with the activity
    protected open val layoutId: Int = R.layout.container_toolbar
}