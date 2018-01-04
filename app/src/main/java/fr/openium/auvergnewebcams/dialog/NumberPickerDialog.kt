package fr.openium.auvergnewebcams.dialog

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.widget.Button
import android.widget.NumberPicker
import fr.openium.auvergnewebcams.Constants
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.event.Events
import fr.openium.auvergnewebcams.utils.PreferencesAW

/**
 * Created by laura on 04/01/2018.
 */
class NumberPickerDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(activity)
        dialog.setContentView(R.layout.dialog_picker)

        val numberPicker = dialog.findViewById<NumberPicker>(R.id.numberPicker)

        val value = arguments?.getInt(Constants.ARG_DELAY_VALUE, PreferencesAW.DEFAUT_TIME_DELAY) ?: PreferencesAW.DEFAUT_TIME_DELAY
        numberPicker.postDelayed(
                {
                    numberPicker.value = value
                }
                , 0)


        numberPicker.minValue = MIN_VALUE
        numberPicker.maxValue = MAX_VALUE

        val buttonCancel = dialog.findViewById<Button>(R.id.buttonCancel)
        buttonCancel.setOnClickListener { dismiss() }

        val buttonOk = dialog.findViewById<Button>(R.id.buttonOk)
        buttonOk.setOnClickListener {
            Events.eventNewValueDelay.set(numberPicker.value)
            dismiss()
        }

        return dialog
    }


    companion object {

        private const val MIN_VALUE = 1
        private const val MAX_VALUE = 120

        fun newInstance(currentValue: Int): NumberPickerDialog {
            val dialog = NumberPickerDialog()
            val args = Bundle()
            args.putInt(Constants.ARG_DELAY_VALUE, currentValue)
            dialog.arguments = args
            return dialog
        }

    }

}