package dev.logarithmus.paintdroid.dialogs

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import dev.logarithmus.paintdroid.R
import dev.logarithmus.paintdroid.databinding.LabelDialogBinding


class LabelDialogFragment: DialogFragment() {
    private lateinit var view: LabelDialogBinding
    private lateinit var listener: LabelDialogListener
   
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        view = LabelDialogBinding.inflate(LayoutInflater.from(context))
        return activity?.let {
            AlertDialog.Builder(it)
                .setTitle(R.string.enter_text)
                .setView(view.root)
                .setPositiveButton(R.string.ok) { _, _ ->
                    listener.text = view.labelText.text.toString()
                }
                .setNegativeButton(R.string.cancel) { _, _ -> }
                .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as LabelDialogListener
    }

    interface LabelDialogListener {
        var text: String
    }
}