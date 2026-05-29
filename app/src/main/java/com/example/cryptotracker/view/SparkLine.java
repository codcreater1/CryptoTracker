package com.example.cryptotracker.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

/**
 * A lightweight custom View that draws a filled sparkline chart from a list of price points.
 *
 * - Green fill when the last price is higher than the first (uptrend).
 * - Red fill when the last price is lower (downtrend).
 * - No external charting library required.
 */
public class SparkLine extends View {

    private List<Double> prices;
    private Paint linePaint;
    private Paint fillPaint;
    private boolean isPositive = true;

    // Colours
    private static final int COLOR_GREEN = Color.parseColor("#00C853");
    private static final int COLOR_RED   = Color.parseColor("#FF1744");

    public SparkLine(Context context) {
        super(context);
        init();
    }

    public SparkLine(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SparkLine(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(4f);
        linePaint.setStrokeCap(Paint.Cap.ROUND);
        linePaint.setStrokeJoin(Paint.Join.ROUND);

        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setStyle(Paint.Style.FILL);
    }

    /**
     * Sets the price data and triggers a redraw.
     *
     * @param prices List of price points (oldest → newest).
     */
    public void setPrices(List<Double> prices) {
        this.prices = prices;
        if (prices != null && prices.size() >= 2) {
            isPositive = prices.get(prices.size() - 1) >= prices.get(0);
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (prices == null || prices.size() < 2) return;

        int w = getWidth();
        int h = getHeight();
        if (w == 0 || h == 0) return;

        // Find min/max for normalisation
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        for (double p : prices) {
            if (p < min) min = p;
            if (p > max) max = p;
        }
        if (max == min) max = min + 1; // avoid division by zero

        int n = prices.size();
        float padding = 8f;
        float usableH = h - padding * 2;
        float usableW = w - padding * 2;

        // Build path
        Path linePath = new Path();
        Path fillPath = new Path();

        for (int i = 0; i < n; i++) {
            float x = padding + (i / (float)(n - 1)) * usableW;
            float y = padding + (float)((max - prices.get(i)) / (max - min)) * usableH;

            if (i == 0) {
                linePath.moveTo(x, y);
                fillPath.moveTo(x, h); // start fill at bottom-left
                fillPath.lineTo(x, y);
            } else {
                linePath.lineTo(x, y);
                fillPath.lineTo(x, y);
            }
        }

        // Close fill path along the bottom
        fillPath.lineTo(padding + usableW, h);
        fillPath.close();

        // Set colours
        int lineColor = isPositive ? COLOR_GREEN : COLOR_RED;
        linePaint.setColor(lineColor);

        // Gradient fill (semi-transparent)
        int fillTop = isPositive
                ? Color.argb(80, 0, 200, 83)
                : Color.argb(80, 255, 23, 68);
        int fillBot = Color.argb(0, 0, 0, 0);

        fillPaint.setShader(new LinearGradient(0, 0, 0, h,
                fillTop, fillBot, Shader.TileMode.CLAMP));

        canvas.drawPath(fillPath, fillPaint);
        canvas.drawPath(linePath, linePaint);
    }
}
