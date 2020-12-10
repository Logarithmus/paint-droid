package dev.logarithmus.paintdroid

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.min import kotlin.math.max

class DrawView(context: Context?, attrs: AttributeSet?): View(context, attrs) {
    interface Shape
    private data class Curve(var path: Path): Shape
    private data class Rectangle(var rect: RectF, val start: PointF): Shape
    private data class Oval(var rect: RectF, val start: PointF): Shape
    private data class Label(var point: PointF, var text: String): Shape

    private data class Step(var paint: Paint, var shape: Shape)
    private val steps: MutableList<Step> = ArrayList()
    private var currentStepIndex: Int = -1
    private val bgColor = (background as ColorDrawable).color
    var penWidth: Float = 3f
    var penColor: Int = Color.BLACK
    var tool: Tool = Tool.PEN
    var text: String = ""

    private fun createPaint(): Paint {
        return Paint().apply {
            strokeWidth = penWidth
            textSize = 100f
            isAntiAlias = true
            isDither = true
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            this.color = if (tool == Tool.ERASER) { bgColor } else { penColor }
        }
    }

    fun canUndo(): Boolean = currentStepIndex >= 0

    fun canRedo(): Boolean = currentStepIndex < steps.size - 1

    fun undo() {
        if (canUndo()) {
            currentStepIndex -= 1
        }
        invalidate()
    }

    fun redo() {
        if (canRedo()) {
            currentStepIndex += 1
        }
        invalidate()
    }

    fun clear() {
        steps.clear()
        currentStepIndex = -1
        invalidate()
    }

    tailrec fun Context?.getActivity(): Activity? = when (this) {
        is Activity -> this
        else -> (this as? ContextWrapper)?.baseContext?.getActivity()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                repeat(steps.size - currentStepIndex - 1) { steps.removeLast() }
                val shape: Shape = when (tool) {
                    Tool.PEN, Tool.ERASER -> Curve(Path().apply{
                        moveTo(event.x, event.y)
                    })
                    Tool.RECTANGLE -> Rectangle(
                        RectF(event.x, event.y, event.x, event.y),
                        PointF(event.x, event.y)
                    )
                    Tool.OVAL -> Oval(
                        RectF(event.x, event.y, event.x, event.y),
                        PointF(event.x, event.y)
                    )
                    Tool.LABEL -> {
                        val activity = context.getActivity() as MainActivity
                        val text = activity.onLabelDialog()
                        Label(PointF(event.x, event.y), text)
                    }
                }
                steps.add(Step(createPaint(), shape))
                currentStepIndex += 1
                if (tool == Tool.LABEL) {
                    invalidate()
                }
            }
            MotionEvent.ACTION_MOVE -> {
                when (val shape = steps.last().shape) {
                    is Curve -> shape.path.lineTo(event.x, event.y)
                    is Rectangle -> shape.rect.apply {
                        right  = max(event.x, shape.start.x)
                        left   = min(event.x, shape.start.x)
                        bottom = max(event.y, shape.start.y)
                        top    = min(event.y, shape.start.y)
                    }
                    is Oval -> shape.rect.apply {
                        right  = max(event.x, shape.start.x)
                        left   = min(event.x, shape.start.x)
                        bottom = max(event.y, shape.start.y)
                        top    = min(event.y, shape.start.y)
                    }
                }
                invalidate()
            }
        }
        return true
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        steps.take(currentStepIndex + 1).forEach{ when (val shape = it.shape) {
            is Curve     -> canvas?.drawPath(shape.path, it.paint)
            is Rectangle -> canvas?.drawRect(shape.rect, it.paint)
            is Oval      -> canvas?.drawOval(shape.rect, it.paint)
            is Label     -> canvas?.drawText(shape.text, shape.point.x, shape.point.y, it.paint)
        }}
    }
}
