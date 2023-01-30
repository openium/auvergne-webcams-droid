package fr.openium.auvergnewebcams.ui.settings

import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.NumberPicker
import fr.openium.auvergnewebcams.KEY_DELAY_VALUE
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.base.AbstractDialog
import fr.openium.auvergnewebcams.event.eventNewRefreshDelayValue
import fr.openium.auvergnewebcams.utils.PreferencesUtils

/**
 * Created by Openium on 19/02/2019.
 */
class RefreshDelayPickerDialog : AbstractDialog() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        Dialog(requireContext()).apply {
            setContentView(R.layout.dialog_refresh_delay_picker)

            val delayValue = arguments?.getInt(KEY_DELAY_VALUE, PreferencesUtils.DEFAULT_REFRESH_DELAY)
                ?: PreferencesUtils.DEFAULT_REFRESH_DELAY

            val numberPicker = findViewById<NumberPicker>(R.id.numberPickerDialogRefreshDelay).apply {
                minValue = MIN_VALUE
                maxValue = MAX_VALUE
                value = delayValue
            }

            findViewById<Button>(R.id.buttonCancelDialogRefreshDelay).setOnClickListener { dismiss() }

            findViewById<Button>(R.id.buttonOkDialogRefreshDelay).setOnClickListener {
                eventNewRefreshDelayValue.accept(numberPicker.value)
                dismiss()
            }
        }

    companion object {

        private const val MIN_VALUE = 1
        private const val MAX_VALUE = 120

        fun newInstance(currentValue: Int): RefreshDelayPickerDialog =
            RefreshDelayPickerDialog().apply {
                arguments = Bundle().apply {
                    putInt(KEY_DELAY_VALUE, currentValue)
                }
            }
    }
}