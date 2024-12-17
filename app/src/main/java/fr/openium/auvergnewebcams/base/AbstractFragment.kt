package fr.openium.auvergnewebcams.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import fr.openium.auvergnewebcams.utils.DateUtils
import fr.openium.auvergnewebcams.utils.PreferencesUtils
import io.reactivex.disposables.CompositeDisposable
import org.koin.android.ext.android.inject

/**
 * Created by Openium on 19/02/2019.
 */

abstract class AbstractFragment<T : ViewBinding> : Fragment() {

    protected val disposables: CompositeDisposable = CompositeDisposable()

    protected val prefUtils by inject<PreferencesUtils>()
    protected val dateUtils by inject<DateUtils>()

    private var _binding: T? = null
    protected val binding get() = _binding!!

    protected abstract fun provideViewBinding(): T

    // --- Life Cycle ---------------------------------------------------

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = provideViewBinding()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposables.clear()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }
}
