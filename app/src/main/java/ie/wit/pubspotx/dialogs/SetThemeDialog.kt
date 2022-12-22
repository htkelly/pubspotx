package ie.wit.pubspotx.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import ie.wit.pubspotx.R

class SetThemeDialog : DialogFragment() {
    internal lateinit var listener: SetThemeDialogListener

    interface SetThemeDialogListener {
        fun onSelectLightMode(dialog: DialogFragment)
        fun onSelectDarkMode(dialog: DialogFragment)
        fun onSelectSystemDefault(dialog: DialogFragment)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as SetThemeDialogListener
        } catch (e: java.lang.ClassCastException) {
            throw ClassCastException((context.toString() + "does not implement SetThemeDialogListener"))
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(R.string.dialog_set_theme)
                .setCancelable(true)
                .setItems(R.array.theme_choices,
                    DialogInterface.OnClickListener { dialog, which ->
                        if (which == 0) listener.onSelectLightMode(this)
                        if (which == 1) listener.onSelectDarkMode(this)
                        if (which == 2) listener.onSelectSystemDefault(this)
                    })
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}