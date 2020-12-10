package dev.logarithmus.paintdroid.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import dev.logarithmus.paintdroid.R
import dev.logarithmus.paintdroid.Tool
import dev.logarithmus.paintdroid.databinding.ToolDialogBinding

class ToolDialogFragment: DialogFragment() {
    private lateinit var view: ToolDialogBinding
    private lateinit var listener: ToolDialogListener
   
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        view = ToolDialogBinding.inflate(LayoutInflater.from(context))
        view.toolsRadioGroup.check( when (listener.tool) {
            Tool.PEN       -> view.penRadioButton.id
            Tool.RECTANGLE -> view.rectangleRadioButton.id
            Tool.OVAL      -> view.ovalRadioButton.id
            Tool.LABEL     -> view.labelRadioButton.id
            Tool.ERASER    -> view.eraserRadioButton.id
        })
        return activity?.let {
            AlertDialog.Builder(it)
                .setTitle(R.string.select_tool)
                .setView(view.root)
                .setPositiveButton(R.string.ok) { _, _ ->
                    listener.tool = when (view.toolsRadioGroup.checkedRadioButtonId) {
                        view.penRadioButton.id       -> Tool.PEN
                        view.rectangleRadioButton.id -> Tool.RECTANGLE
                        view.ovalRadioButton.id      -> Tool.OVAL
                        view.labelRadioButton.id     -> Tool.LABEL
                        view.eraserRadioButton.id    -> Tool.ERASER
                        else                         -> Tool.PEN
                    }
                }
                .setNegativeButton(R.string.cancel) { _, _ -> }
                .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as ToolDialogListener
    }

    interface ToolDialogListener {
        var tool: Tool
    }
}