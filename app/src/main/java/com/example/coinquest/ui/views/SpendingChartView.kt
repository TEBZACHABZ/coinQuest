package com.example.coinquest.ui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.example.coinquest.data.CategorySpending

class SpendingChartView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var data: List<CategorySpending> = emptyList()
    private var minGoal: Double = 0.0
    private var maxGoal: Double = 0.0

    private val barPaint = Paint().apply {
        color = Color.parseColor("#6200EE")
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val goalPaint = Paint().apply {
        strokeWidth = 4f
        style = Paint.Style.STROKE
        pathEffect = android.graphics.DashPathEffect(floatArrayOf(10f, 10f), 0f)
        isAntiAlias = true
    }

    private val textPaint = Paint().apply {
        color = Color.GRAY
        textSize = 28f
        isAntiAlias = true
    }
    
    private val labelPaint = Paint().apply {
        color = Color.BLACK
        textSize = 32f
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
    }

    fun setData(spending: List<CategorySpending>, min: Double, max: Double) {
        this.data = spending
        this.minGoal = min
        this.maxGoal = max
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (data.isEmpty()) {
            canvas.drawText("No spending data for this period", width / 2f, height / 2f, labelPaint)
            return
        }

        val width = width.toFloat()
        val height = height.toFloat()
        val paddingLeft = 120f
        val paddingRight = 40f
        val paddingTop = 60f
        val paddingBottom = 100f
        
        val chartWidth = width - paddingLeft - paddingRight
        val chartHeight = height - paddingTop - paddingBottom

        val maxAmount = maxOf(data.maxOf { it.totalAmount }, maxGoal, 100.0)

        val barSpacing = 40f
        val barWidth = (chartWidth - (data.size - 1) * barSpacing) / data.size
        
        data.forEachIndexed { index, item ->
            val barHeight = (item.totalAmount / maxAmount * chartHeight).toFloat()
            val left = paddingLeft + index * (barWidth + barSpacing)
            val top = height - paddingBottom - barHeight
            val right = left + barWidth
            val bottom = height - paddingBottom
            
            // Draw Rounded Bar
            canvas.drawRoundRect(left, top, right, bottom, 12f, 12f, barPaint)
            
            // Draw category name
            val label = if (item.categoryName.length > 6) item.categoryName.substring(0, 4) + ".." else item.categoryName
            canvas.drawText(label, left + barWidth / 2, height - paddingBottom + 40f, textPaint)
        }

        // Draw Axes
        canvas.drawLine(paddingLeft, paddingTop, paddingLeft, height - paddingBottom, textPaint) // Y
        canvas.drawLine(paddingLeft, height - paddingBottom, width - paddingRight, height - paddingBottom, textPaint) // X

        // Draw Min Goal Line
        if (minGoal > 0) {
            goalPaint.color = Color.parseColor("#2196F3") // Blue
            val minLineY = (height - paddingBottom - (minGoal / maxAmount * chartHeight)).toFloat()
            canvas.drawLine(paddingLeft, minLineY, width - paddingRight, minLineY, goalPaint)
            canvas.drawText("Min", paddingLeft - 60f, minLineY + 10f, textPaint)
        }

        // Draw Max Goal Line
        if (maxGoal > 0) {
            goalPaint.color = Color.parseColor("#F44336") // Red
            val maxLineY = (height - paddingBottom - (maxGoal / maxAmount * chartHeight)).toFloat()
            canvas.drawLine(paddingLeft, maxLineY, width - paddingRight, maxLineY, goalPaint)
            canvas.drawText("Max", paddingLeft - 60f, maxLineY + 10f, textPaint)
        }
    }
}
