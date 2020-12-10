package dev.logarithmus.paintdroid

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.DrawableCompat
import dev.logarithmus.paintdroid.databinding.ActivityMainBinding
import dev.logarithmus.paintdroid.dialogs.*

class MainActivity: AppCompatActivity(),
    ToolDialogFragment.ToolDialogListener,
    LabelDialogFragment.LabelDialogListener,
    PenWidthDialogFragment.PenWidthDialogListener,
    PenColorDialogFragment.PenColorDialogListener,
    ClearScreenDialogFragment.ClearScreenDialogListener {

    private lateinit var activity: ActivityMainBinding
    private lateinit var amvMenu: Menu
    private lateinit var toolItem: MenuItem
    private lateinit var undoItem: MenuItem
    private lateinit var redoItem: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activity.root)
        setSupportActionBar(activity.toolbar.root)
        activity.toolbar.menu.setOnMenuItemClickListener { onOptionsItemSelected(it) }
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        amvMenu = activity.toolbar.menu.menu
        menuInflater.inflate(R.menu.menu_main, amvMenu)
        toolItem = amvMenu.findItem(R.id.tool)
        undoItem = amvMenu.findItem(R.id.undo)
        redoItem = amvMenu.findItem(R.id.redo)
        DrawableCompat.setTint(undoItem.icon, Color.GRAY)
        DrawableCompat.setTint(redoItem.icon, Color.GRAY)
        return true
    }

    fun onLabelDialog(): String {
        LabelDialogFragment()
            .show(supportFragmentManager, "LabelDialog")
        return text
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        updateUndoRedo()
        return super.dispatchTouchEvent(event)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.tool -> ToolDialogFragment()
                .show(supportFragmentManager, "ToolDialog")
            R.id.pen_width -> PenWidthDialogFragment()
                .show(supportFragmentManager, "PenWidthDialog")
            R.id.pen_color -> PenColorDialogFragment()
                .show(supportFragmentManager, "PenColorDialog")
            R.id.undo -> {
                activity.drawView.undo()
                updateUndoRedo()
            }
            R.id.redo -> {
                activity.drawView.redo()
                updateUndoRedo()
            }
            R.id.clear_screen -> ClearScreenDialogFragment()
                .show(supportFragmentManager, "ClearScreenDialog")
            else -> super.onOptionsItemSelected(item)
        }
        return when (item.itemId) {
            R.id.pen_width, R.id.pen_color, R.id.undo,
            R.id.redo, R.id.clear_screen -> true
            else -> false
        }
    }

    override var tool: Tool
        get() = activity.drawView.tool
        set(tool) {
            activity.drawView.tool = tool
            toolItem.icon = Drawable.createFromXml(resources, resources.getXml(
                when (tool) {
                    Tool.PEN       -> R.drawable.ic_twotone_create_24
                    Tool.RECTANGLE -> R.drawable.ic_rectangle
                    Tool.OVAL      -> R.drawable.ic_oval
                    Tool.LABEL     -> R.drawable.ic_twotone_text_fields_24
                    Tool.ERASER    -> R.drawable.ic_eraser
                })
            )
        }

    override var penWidth: Float
        get() = activity.drawView.penWidth
        set(width) { activity.drawView.penWidth = width }

    override var penColor: Int
        get() = activity.drawView.penColor
        set(color) { activity.drawView.penColor = color }

    override var text: String
        get() = activity.drawView.text
        set(text) { activity.drawView.text = text }

    override fun clearScreen() {
        activity.drawView.clear()
        updateUndoRedo()
    }
    
    private fun updateUndoRedo() {
        DrawableCompat.setTint(undoItem.icon,
            if (activity.drawView.canUndo()) { Color.BLACK } else { Color.GRAY }
        )
        DrawableCompat.setTint(redoItem.icon,
            if (activity.drawView.canRedo()) { Color.BLACK } else { Color.GRAY }
        )
    }
}