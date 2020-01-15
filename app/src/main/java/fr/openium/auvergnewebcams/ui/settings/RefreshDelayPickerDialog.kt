package fr.openium.auvergnewebcams.ui.settings

import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.NumberPicker
import fr.openium.auvergnewebcams.Constants
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.base.AbstractDialog
import fr.openium.auvergnewebcams.event.eventNewRefreshDelayValue
import fr.openium.auvergnewebcams.utils.PreferencesAW

/**
 * Created by Openium on 19/02/2019.
 */
class RefreshDelayPickerDialog : AbstractDialog() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_refresh_delay_picker)

        val numberPicker = dialog.findViewById<NumberPicker>(R.id.numberPickerDialogRefreshDelay)
        val value = arguments?.getInt(Constants.ARG_DELAY_VALUE, PreferencesAW.DEFAULT_TIME_DELAY) ?: PreferencesAW.DEFAULT_TIME_DELAY

        numberPicker.postDelayed({
            numberPicker.value = value
        }, 0)

        numberPicker.minValue = MIN_VALUE
        numberPicker.maxValue = MAX_VALUE

        val buttonCancel = dialog.findViewById<Button>(R.id.buttonCancelDialogRefreshDelay)
        buttonCancel.setOnClickListener { dismiss() }

        val buttonOk = dialog.findViewById<Button>(R.id.buttonOkDialogRefreshDelay)
        buttonOk.setOnClickListener {
            eventNewRefreshDelayValue.accept(numberPicker.value)
            dismiss()
        }

        return dialog
    }

    companion object {

        private const val MIN_VALUE = 1
        private const val MAX_VALUE = 120

        fun newInstance(currentValue: Int): RefreshDelayPickerDialog {
            return RefreshDelayPickerDialog().apply {
                arguments = Bundle().apply {
                    putInt(Constants.ARG_DELAY_VALUE, currentValue)
                }
            }
        }
    }
}