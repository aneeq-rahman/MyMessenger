package com.aneeq.messenger.overlay

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.Log
import com.google.mlkit.vision.text.Text

class TextGraphic internal constructor(
    overlay: GraphicOverlay?,
    private val element: Text.Element?
) :
    GraphicOverlay.Graphic(overlay) {
    private val rectPaint: Paint = Paint()
    private val textPaint: Paint

    /**
     * Draws the text block annotations for position, size, and raw value on the supplied canvas.
     */


    companion object {
        private const val TAG = "TextGraphic"
        private const val TEXT_COLOR = Color.RED
        private const val TEXT_SIZE = 54.0f
        private const val STROKE_WIDTH = 4.0f
    }

    init {
        rectPaint.color = TextGraphic.Companion.TEXT_COLOR
        rectPaint.style = Paint.Style.STROKE
        rectPaint.strokeWidth = TextGraphic.Companion.STROKE_WIDTH
        textPaint = Paint()
        textPaint.color = TextGraphic.Companion.TEXT_COLOR
        textPaint.textSize = TextGraphic.Companion.TEXT_SIZE
        // Redraw the overlay, as this graphic has been added.
        postInvalidate()
    }

    override fun draw(canvas: Canvas?) {
        Log.d(TextGraphic.Companion.TAG, "on draw text graphic")
        checkNotNull(element) { "Attempting to draw a null text." }

        // Draws the bounding box around the TextBlock.
        val rect = RectF(element.boundingBox)
        canvas!!.drawRect(rect, rectPaint)

        // Renders the text at the bottom of the box.
        canvas.drawText(element.text, rect.left, rect.bottom, textPaint)
    }
}
