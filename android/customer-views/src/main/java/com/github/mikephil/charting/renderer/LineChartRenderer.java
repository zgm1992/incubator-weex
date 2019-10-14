package com.github.mikephil.charting.renderer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointD;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class LineChartRenderer extends LineRadarRenderer {

    protected LineDataProvider mChart;

    /**
     * paint for the inner circle of the value indicators
     */
    protected Paint mCirclePaintInner;

    /**
     * Bitmap object used for drawing the paths (otherwise they are too long if
     * rendered directly on the canvas)
     */
    protected WeakReference<Bitmap> mDrawBitmap;

    /**
     * on this canvas, the paths are rendered, it is initialized with the
     * pathBitmap
     */
    protected Canvas mBitmapCanvas;

    /**
     * the bitmap configuration to be used
     */
    protected Bitmap.Config mBitmapConfig = Bitmap.Config.ARGB_8888;

    protected Path cubicPath = new Path();
    protected Path cubicFillPath = new Path();
    protected Path mGenerateFilledPathBuffer = new Path();
    int lastGroupIndex = 0;
    int lastBarIndex = 0;
    private float[] mLineBuffer = new float[4];
    /**
     * cache for the circle bitmaps of all datasets
     */
    private HashMap<IDataSet, DataSetImageCache> mImageCaches = new HashMap<>();
    /**
     * buffer for drawing the circles
     */
    private float[] mCirclesBuffer = new float[2];

    public LineChartRenderer(LineDataProvider chart, ChartAnimator animator,
                             ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
        mChart = chart;

        mCirclePaintInner = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaintInner.setStyle(Paint.Style.FILL);
        mCirclePaintInner.setColor(Color.WHITE);
    }

    @Override
    public void initBuffers() {
    }

    @Override
    public void drawData(Canvas c) {

        int width = (int) mViewPortHandler.getChartWidth();
        int height = (int) mViewPortHandler.getChartHeight();

        Bitmap drawBitmap = mDrawBitmap == null ? null : mDrawBitmap.get();

        if (drawBitmap == null
                || (drawBitmap.getWidth() != width)
                || (drawBitmap.getHeight() != height)) {

            if (width > 0 && height > 0) {
                drawBitmap = Bitmap.createBitmap(width, height, mBitmapConfig);
                mDrawBitmap = new WeakReference<>(drawBitmap);
                mBitmapCanvas = new Canvas(drawBitmap);
            } else
                return;
        }

        drawBitmap.eraseColor(Color.TRANSPARENT);

        LineData lineData = mChart.getLineData();

        for (ILineDataSet set : lineData.getDataSets()) {

            if (set.isVisible())
                drawDataSet(c, set);
        }

        c.drawBitmap(drawBitmap, 0, 0, mRenderPaint);
    }

    protected void drawDataSet(Canvas c, ILineDataSet dataSet) {

        if (dataSet.getEntryCount() < 1)
            return;

        mRenderPaint.setStrokeWidth(dataSet.getLineWidth());
        mRenderPaint.setPathEffect(dataSet.getDashPathEffect());


        mRenderPaint.setStyle(Paint.Style.STROKE);

        switch (dataSet.getMode()) {
            default:
            case LINEAR:
            case STEPPED:
                drawLinear(c, dataSet);
                break;

            case CUBIC_BEZIER:
                drawCubicBezier(dataSet);
                break;

            case HORIZONTAL_BEZIER:
                drawHorizontalBezier(dataSet);
                break;
        }

        mRenderPaint.setPathEffect(null);
    }

    protected void drawHorizontalBezier(ILineDataSet dataSet) {

        float phaseY = mAnimator.getPhaseY();

        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

        mXBounds.set(mChart, dataSet);

        cubicPath.reset();

        if (mXBounds.range >= 1) {

            Entry prev = dataSet.getEntryForIndex(mXBounds.min);
            Entry cur = prev;

            // let the spline start
            cubicPath.moveTo(cur.getX(), cur.getY() * phaseY);

            for (int j = mXBounds.min + 1; j <= mXBounds.range + mXBounds.min; j++) {

                prev = cur;
                cur = dataSet.getEntryForIndex(j);

                final float cpx = (prev.getX())
                        + (cur.getX() - prev.getX()) / 2.0f;

                cubicPath.cubicTo(
                        cpx, prev.getY() * phaseY,
                        cpx, cur.getY() * phaseY,
                        cur.getX(), cur.getY() * phaseY);
            }
        }

        // if filled is enabled, close the path
        if (dataSet.isDrawFilledEnabled()) {

            cubicFillPath.reset();
            cubicFillPath.addPath(cubicPath);
            // create a new path, this is bad for performance
            drawCubicFill(mBitmapCanvas, dataSet, cubicFillPath, trans, mXBounds);
        }

        mRenderPaint.setColor(dataSet.getColor());


        trans.pathValueToPixel(cubicPath);

        mBitmapCanvas.drawPath(cubicPath, mRenderPaint);

        mRenderPaint.setPathEffect(null);
    }

    protected void drawCubicBezier(ILineDataSet dataSet) {

        float phaseX = Math.max(0.f, Math.min(1.f, mAnimator.getPhaseX()));
        float phaseY = mAnimator.getPhaseY();

        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

        mXBounds.set(mChart, dataSet);

        float intensity = dataSet.getCubicIntensity();

        cubicPath.reset();

        if (mXBounds.range >= 1) {

            float prevDx = 0f;
            float prevDy = 0f;
            float curDx = 0f;
            float curDy = 0f;

            // Take an extra point from the left, and an extra from the right.
            // That's because we need 4 points for a cubic bezier (cubic=4), otherwise we get lines moving and doing weird stuff on the edges of the chart.
            // So in the starting `prev` and `cur`, go -2, -1
            // And in the `lastIndex`, add +1

            final int firstIndex = mXBounds.min + 1;
            final int lastIndex = mXBounds.min + mXBounds.range;

            Entry prevPrev;
            Entry prev = dataSet.getEntryForIndex(Math.max(firstIndex - 2, 0));
            Entry cur = dataSet.getEntryForIndex(Math.max(firstIndex - 1, 0));
            Entry next = cur;
            int nextIndex = -1;

            if (cur == null) return;

            // let the spline start
            cubicPath.moveTo(cur.getX(), cur.getY() * phaseY);
            for (int j = mXBounds.min + 1; j <= mXBounds.range + mXBounds.min; j++) {

                prevPrev = prev;
                prev = cur;
                cur = nextIndex == j ? next : dataSet.getEntryForIndex(j);

                float preX = prevPrev.getX();
                float curX = cur.getX();
                float nexX = next.getX();


                nextIndex = j + 1 < dataSet.getEntryCount() ? j + 1 : j;
                next = dataSet.getEntryForIndex(nextIndex);

                prevDx = (curX - preX) * intensity;
                prevDy = (cur.getY() - prevPrev.getY()) * intensity;
                curDx = (float) ((nexX - prev.getX()) * intensity * 2);
                curDy = (next.getY() - prev.getY()) * intensity;


                cubicPath.cubicTo(prev.getX() + prevDx, (prev.getY() + prevDy) * phaseY,
                        curX - curDx,
                        (cur.getY() - curDy) * phaseY, curX, cur.getY() * phaseY);
            }
        }

        // if filled is enabled, close the path
        if (dataSet.isDrawFilledEnabled()) {

            cubicFillPath.reset();
            cubicFillPath.addPath(cubicPath);

            drawCubicFill(mBitmapCanvas, dataSet, cubicFillPath, trans, mXBounds);
        }


        trans.pathValueToPixel(cubicPath);

        int color = dataSet.getColor();
        if (color == Color.TRANSPARENT || dataSet.getLineWidth()==0) {
            // TODO: 2018/11/23 如果是透明色，就不设置到画笔上，也不绘制，否则透明色后面的色值属性都出不来
            mRenderPaint.setPathEffect(null);
            return;
        }

//        if (dataSet.getLineWidth() == 0) { //如果线的厚度为0，那么就意味着不显示曲线，这里则将曲线隐藏处理
//            color = Color.TRANSPARENT;
//        }

        mRenderPaint.setColor(color);
        mBitmapCanvas.drawPath(cubicPath, mRenderPaint);

        mRenderPaint.setPathEffect(null);
    }

    protected void drawCubicFill(Canvas c, ILineDataSet dataSet, Path spline, Transformer trans, XBounds bounds) {

        float fillMin = dataSet.getFillFormatter()
                .getFillLinePosition(dataSet, mChart);

        spline.lineTo(dataSet.getEntryForIndex(bounds.min + bounds.range).getX(), fillMin);
        spline.lineTo(dataSet.getEntryForIndex(bounds.min).getX(), fillMin);
        spline.close();

        trans.pathValueToPixel(spline);

        final Drawable drawable = dataSet.getFillDrawable();
        if (drawable != null) {

            drawFilledPath(c, spline, drawable);
        } else {

            drawFilledPath(c, spline, dataSet.getFillColor(), dataSet.getFillAlpha());
        }
    }

    /**
     * Draws a normal line.
     *
     * @param c
     * @param dataSet
     */
    protected void drawLinear(Canvas c, ILineDataSet dataSet) {

        int entryCount = dataSet.getEntryCount();

        final boolean isDrawSteppedEnabled = dataSet.isDrawSteppedEnabled();
        final int pointsPerEntryPair = isDrawSteppedEnabled ? 4 : 2;

        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

        float phaseY = mAnimator.getPhaseY();


        Canvas canvas = null;

        // if the data-set is dashed, draw on bitmap-canvas
        if (dataSet.isDashedLineEnabled()) {
            canvas = mBitmapCanvas;
        } else {
            canvas = c;
        }

        mXBounds.set(mChart, dataSet);

        // if drawing filled is enabled
        if (dataSet.isDrawFilledEnabled() && entryCount > 0) {
            drawLinearFill(c, dataSet, trans, mXBounds);
        }

        // more than 1 color
        if (dataSet.getColors().size() > 1) {

            if (mLineBuffer.length <= pointsPerEntryPair * 2)
                mLineBuffer = new float[pointsPerEntryPair * 4];

            for (int j = mXBounds.min; j <= mXBounds.range + mXBounds.min; j++) {

                Entry e = dataSet.getEntryForIndex(j);
                if (e == null) continue;

                mLineBuffer[0] = e.getX();
                mLineBuffer[1] = e.getY() * phaseY;

                if (j < mXBounds.max) {

                    e = dataSet.getEntryForIndex(j + 1);

                    if (e == null) break;

                    if (isDrawSteppedEnabled) {
                        mLineBuffer[2] = e.getX();
                        mLineBuffer[3] = mLineBuffer[1];
                        mLineBuffer[4] = mLineBuffer[2];
                        mLineBuffer[5] = mLineBuffer[3];
                        mLineBuffer[6] = e.getX();
                        mLineBuffer[7] = e.getY() * phaseY;
                    } else {
                        mLineBuffer[2] = e.getX();
                        mLineBuffer[3] = e.getY() * phaseY;
                    }

                } else {
                    mLineBuffer[2] = mLineBuffer[0];
                    mLineBuffer[3] = mLineBuffer[1];
                }

                trans.pointValuesToPixel(mLineBuffer);

                if (!mViewPortHandler.isInBoundsRight(mLineBuffer[0]))
                    break;

                // make sure the lines don't do shitty things outside
                // bounds
                if (!mViewPortHandler.isInBoundsLeft(mLineBuffer[2])
                        || (!mViewPortHandler.isInBoundsTop(mLineBuffer[1]) && !mViewPortHandler
                        .isInBoundsBottom(mLineBuffer[3])))
                    continue;

                // get the color that is set for this line-segment
                int color = dataSet.getColor();
                if (color != Color.TRANSPARENT) {
                    mRenderPaint.setColor(dataSet.getColor(j));
                    canvas.drawLines(mLineBuffer, 0, pointsPerEntryPair * 2, mRenderPaint);
                }
            }

        } else { // only one color per dataset

            if (mLineBuffer.length < Math.max((entryCount) * pointsPerEntryPair, pointsPerEntryPair) * 2)
                mLineBuffer = new float[Math.max((entryCount) * pointsPerEntryPair, pointsPerEntryPair) * 4];

            Entry e1, e2;

            e1 = dataSet.getEntryForIndex(mXBounds.min);

            if (e1 != null) {

                int j = 0;
                Map<Integer, Float> xMap = new TreeMap<>();  // TODO: 2018/10/17 作为x轴的非均匀映射转换使用
                for (int x = mXBounds.min; x <= mXBounds.range + mXBounds.min; x++) {

                    e1 = dataSet.getEntryForIndex(x == 0 ? 0 : (x - 1));
                    e2 = dataSet.getEntryForIndex(x);


                    if (e1 == null || e2 == null) continue;

                    xMap.put(j, e1.getX());
                    mLineBuffer[j++] = e1.getX();
                    mLineBuffer[j++] = e1.getY() * phaseY;

                    if (isDrawSteppedEnabled) {
                        xMap.put(j, e2.getX());
                        mLineBuffer[j++] = e2.getX();
                        mLineBuffer[j++] = e1.getY() * phaseY;
                        xMap.put(j, e2.getX());
                        mLineBuffer[j++] = e2.getX();
                        mLineBuffer[j++] = e1.getY() * phaseY;
                    }

                    xMap.put(j, e2.getX());
                    mLineBuffer[j++] = e2.getX();
                    mLineBuffer[j++] = e2.getY() * phaseY;
                }

                if (j > 0) {
                    trans.pointValuesToPixel(mLineBuffer);

                    final int size = Math.max((mXBounds.range + 1) * pointsPerEntryPair, pointsPerEntryPair) * 2;

//                    List<Float> realXValueList = mChart.getValue();
//                    if (realXValueList != null) {
//                        Log.i("jarvanTest", "start count");
//                        Float max = 1f, min = 1f;
//                        Float maxValue = -1f, minValue = -1f;
//                        for (Integer key : xMap.keySet()) {
//                            // 获取最大最小边界数值，为非均匀映射做准备
////                            Log.i("jarvanTest", "  key is ->" + key + " orginal value ->" + xMap.get(key) + " map by view value is ->" + mLineBuffer[key]);
//                            if (xMap.get(key) < min) {
//                                min = xMap.get(key);
//                                minValue = mLineBuffer[key];
//                            } else if (xMap.get(key) > max) {
//                                max = xMap.get(key);
//                                maxValue = mLineBuffer[key];
//                            }
//                        }
////                        Log.i("jarvanTest"," min key is ->"+min+" min values ->"+minValue+"  max key ->"+max+" max values is ->"+maxValue);
//
//                        for (Integer key : xMap.keySet()) {
//                            // 将均匀数值，按照x 轴上的内容，映射成非均匀的数值坐标体系
//                            if (mLineBuffer[key] == minValue || mLineBuffer[key] == maxValue) {
//                                continue;
//                            }
//                            float x1 = xMap.get(key);
//                            if (realXValueList.size() > ((int) x1)) {
//                                float x2 = realXValueList.get((int) x1);
//                                mLineBuffer[key] = minValue + (maxValue - minValue) * (x2 / realXValueList.get(realXValueList.size() - 1));
//                            }
//                        }
//                    }
                    int color = dataSet.getColor();
                    if (color != Color.TRANSPARENT &&  dataSet.getLineWidth()!=0) {
                        mRenderPaint.setColor(color);
//                    if (mLineBuffer[62] == 64 && mLineBuffer[63] == 441) {
                        canvas.drawLines(mLineBuffer, 0, size, mRenderPaint);
                    }
//                    }
//                    Log.i("jarvanTest", "draw  just one color lines is ->" + mLineBuffer + Log.getStackTraceString(new Throwable()));
                }
            }
        }

        mRenderPaint.setPathEffect(null);
    }

    /**
     * Draws a filled linear path on the canvas.
     *
     * @param c
     * @param dataSet
     * @param trans
     * @param bounds
     */
    protected void drawLinearFill(Canvas c, ILineDataSet dataSet, Transformer trans, XBounds bounds) {

        final Path filled = mGenerateFilledPathBuffer;

        final int startingIndex = bounds.min;
        final int endingIndex = bounds.range + bounds.min;
        final int indexInterval = 128;

        int currentStartIndex = 0;
        int currentEndIndex = indexInterval;
        int iterations = 0;

        // Doing this iteratively in order to avoid OutOfMemory errors that can happen on large bounds sets.
        do {
            currentStartIndex = startingIndex + (iterations * indexInterval);
            currentEndIndex = currentStartIndex + indexInterval;
            currentEndIndex = currentEndIndex > endingIndex ? endingIndex : currentEndIndex;

            if (currentStartIndex <= currentEndIndex) {
                generateFilledPath(dataSet, currentStartIndex, currentEndIndex, filled);

                trans.pathValueToPixel(filled);

                final Drawable drawable = dataSet.getFillDrawable();
                if (drawable != null) {

                    drawFilledPath(c, filled, drawable);
                } else {

                    drawFilledPath(c, filled, dataSet.getFillColor(), dataSet.getFillAlpha());
                }
            }

            iterations++;

        } while (currentStartIndex <= currentEndIndex);

    }

    /**
     * Generates a path that is used for filled drawing.
     *
     * @param dataSet    The dataset from which to read the entries.
     * @param startIndex The index from which to start reading the dataset
     * @param endIndex   The index from which to stop reading the dataset
     * @param outputPath The path object that will be assigned the chart data.
     * @return
     */
    private void generateFilledPath(final ILineDataSet dataSet, final int startIndex, final int endIndex, final Path outputPath) {

        final float fillMin = dataSet.getFillFormatter().getFillLinePosition(dataSet, mChart);
        final float phaseY = mAnimator.getPhaseY();
        final boolean isDrawSteppedEnabled = dataSet.getMode() == LineDataSet.Mode.STEPPED;

        final Path filled = outputPath;
        filled.reset();

        final Entry entry = dataSet.getEntryForIndex(startIndex);

        filled.moveTo(entry.getX(), fillMin);
        filled.lineTo(entry.getX(), entry.getY() * phaseY);

        // create a new path
        Entry currentEntry = null;
        Entry previousEntry = null;
        for (int x = startIndex + 1; x <= endIndex; x++) {

            currentEntry = dataSet.getEntryForIndex(x);

            if (isDrawSteppedEnabled && previousEntry != null) {
                filled.lineTo(currentEntry.getX(), previousEntry.getY() * phaseY);
            }

            filled.lineTo(currentEntry.getX(), currentEntry.getY() * phaseY);

            previousEntry = currentEntry;
        }

        // close up
        if (currentEntry != null) {
            filled.lineTo(currentEntry.getX(), fillMin);
        }

        filled.close();
    }

    @Override
    public void drawValues(Canvas c) {

        if (isDrawingValuesAllowed(mChart)) {

            List<ILineDataSet> dataSets = mChart.getLineData().getDataSets();

            for (int i = 0, count = dataSets.size() - 1; i <= count; i++) {

                ILineDataSet dataSet = dataSets.get(i);

                if (!shouldDrawValues(dataSet))
                    continue;

                // apply the text-styling defined by the DataSet
                applyValueTextStyle(dataSet);

                Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

                // make sure the values do not interfear with the circles
                int valOffset = (int) (dataSet.getCircleRadius() * 1.75f);

                if (!dataSet.isDrawCirclesEnabled()) {
                    valOffset = valOffset / 2;
                }

                mXBounds.set(mChart, dataSet);

                float[] positions = trans.generateTransformedValuesLine(dataSet, mAnimator.getPhaseX(), mAnimator
                        .getPhaseY(), mXBounds.min, mXBounds.max);

                MPPointF iconsOffset = MPPointF.getInstance(dataSet.getIconsOffset());
                iconsOffset.x = Utils.convertDpToPixel(iconsOffset.x);
                iconsOffset.y = Utils.convertDpToPixel(iconsOffset.y);

                for (int j = 0; j < positions.length; j += 2) {

                    float x = positions[j];
                    float y = positions[j + 1];

                    if (!mViewPortHandler.isInBoundsRight(x))
                        break;

                    if (!mViewPortHandler.isInBoundsLeft(x) || !mViewPortHandler.isInBoundsY(y))
                        continue;

                    Entry entry = dataSet.getEntryForIndex(j / 2 + mXBounds.min);

                    if (dataSet.isDrawValuesEnabled()) {
                        drawValue(c, dataSet.getValueFormatter(), entry.getY(), entry, i, x,
                                y - valOffset, dataSet.getValueTextColor(j / 2));
                    }

                    if (entry.getIcon() != null && dataSet.isDrawIconsEnabled()) {

                        Drawable icon = entry.getIcon();

                        Utils.drawImage(
                                c,
                                icon,
                                (int) (x + iconsOffset.x),
                                (int) (y + iconsOffset.y),
                                icon.getIntrinsicWidth(),
                                icon.getIntrinsicHeight());
                    }
                }

                MPPointF.recycleInstance(iconsOffset);
            }
        }
    }

    @Override
    public void drawExtras(Canvas c) {
        drawCircles(c);
    }

    protected void drawCircles(Canvas c) {

        mRenderPaint.setStyle(Paint.Style.FILL);

        float phaseY = mAnimator.getPhaseY();

        mCirclesBuffer[0] = 0;
        mCirclesBuffer[1] = 0;

        List<ILineDataSet> dataSets = mChart.getLineData().getDataSets();


//        Entry max = dataSet.getEntryForIndex(boundsRangeCount -1);
//        mCirclesBuffer[0] = max.getX();
//        trans.pointValuesToPixel(mCirclesBuffer);
//        float maxRight = mCirclesBuffer[0];
//        Log.i("jarvan2", " max right is ->"+maxRight);


        for (int i = 0; i < dataSets.size(); i++) {

            ILineDataSet dataSet = dataSets.get(i);

            if (!dataSet.isVisible() ||
                    !(dataSet.isDrawCirclesEnabled()
                            || dataSet.isLineSidesPointAble() ||
                            ( mViewPortHandler.getSignPostRender()!=null &&-1 != mViewPortHandler.getSignPostRender().getCurrentClickBar())
                    )
                    ||
                    dataSet.getEntryCount() == 0)
                continue;

            mCirclePaintInner.setColor(dataSet.getCircleHoleColor());

            Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

            mXBounds.set(mChart, dataSet);

            float circleRadius = dataSet.getCircleRadius();
            float circleHoleRadius = dataSet.getCircleHoleRadius();
            boolean drawCircleHole = dataSet.isDrawCircleHoleEnabled() &&
                    circleHoleRadius < circleRadius &&
                    circleHoleRadius > 0.f;
            boolean drawTransparentCircleHole = drawCircleHole &&
                    dataSet.getCircleHoleColor() == ColorTemplate.COLOR_NONE;

            DataSetImageCache imageCache;

            if (mImageCaches.containsKey(dataSet)) {
                imageCache = mImageCaches.get(dataSet);
            } else {
                imageCache = new DataSetImageCache();
                mImageCaches.put(dataSet, imageCache);
            }

            boolean changeRequired = imageCache.init(dataSet);
            // only fill the cache with new bitmaps if a change is required
            if (changeRequired) {
                imageCache.fill(dataSet, drawCircleHole, drawTransparentCircleHole);
            }

            int boundsRangeCount = mXBounds.range + mXBounds.min;

            for (int j = mXBounds.min; j <= boundsRangeCount; j++) {

                Entry e = dataSet.getEntryForIndex(j);

                if (e == null) break;

                mCirclesBuffer[0] = e.getX();
                mCirclesBuffer[1] = e.getY() * phaseY;

                trans.pointValuesToPixel(mCirclesBuffer);

                if (!mViewPortHandler.isInBoundsRight(mCirclesBuffer[0]))
                    break;

                if (!mViewPortHandler.isInBoundsLeft(mCirclesBuffer[0]) ||
                        !mViewPortHandler.isInBoundsY(mCirclesBuffer[1]))
                    continue;

//                if ()
//                if (mChart.getValue() != null && j!=mXBounds.min && j!=boundsRangeCount) {
//                    mCirclesBuffer[0] = maxRight * (mChart.getValue().get(j) / mChart.getValue().get(mChart.getValue().size() - 1));
//
//                    Log.i("jarvan2", "j is ->"+j+"  reset point left is ->" + mCirclesBuffer[0]);
//                }

                Bitmap circleBitmap = imageCache.getBitmap(j);
                Bitmap highLightcircleBitmap = imageCache.getHighLightBitmap();
                if (circleBitmap != null) {
                    if (dataSet.isDrawCirclesEnabled() || //所有端点都描述的情况
                            (dataSet.isLineSidesPointAble() && (j == mXBounds.min || j == boundsRangeCount))) { //只描述首尾端点的情况
                        if (highLightcircleBitmap != null && mViewPortHandler.getSignPostRender()!=null &&  j == mViewPortHandler.getSignPostRender().getCurrentClickBar()) {
                            c.drawBitmap(highLightcircleBitmap, mCirclesBuffer[0] - imageCache.getHighLightRadius(), mCirclesBuffer[1] - imageCache.getHighLightRadius(), null);

                        } else {
                            c.drawBitmap(circleBitmap, mCirclesBuffer[0] - circleRadius, mCirclesBuffer[1] - circleRadius, null);
                        }
                    }
                }

                if (mViewPortHandler.getSignPostRender()!=null &&j == mViewPortHandler.getSignPostRender().getCurrentClickBar() ){
                    if (mViewPortHandler.getSignPostRender().getEvent() == null || mViewPortHandler.getSignPostRender().getEvent().getAction() != MotionEvent.ACTION_MOVE) {
                        mViewPortHandler.getSignPostRender().setCurtSorX(mCirclesBuffer[0]);
                        mViewPortHandler.getSignPostRender().drawCursor(c, mCirclesBuffer[0], 10, mCursorPaint);
                    }
                }
            }
        }
    }

    @Override
    public void drawHighlighted(Canvas c, Highlight[] indices) {
        LineData lineData = mChart.getLineData();

        for (Highlight high : indices) {

            ILineDataSet set = lineData.getDataSetByIndex(high.getDataSetIndex());

            if (set == null || !set.isHighlightEnabled())
                continue;

            Entry e = set.getEntryForXValue(high.getX(), high.getY());

            if (!isInBoundsX(e, set))
                continue;

            MPPointD pix = mChart.getTransformer(set.getAxisDependency()).getPixelForValues(e.getX(), e.getY() * mAnimator
                    .getPhaseY());

            high.setDraw((float) pix.x, (float) pix.y);

            // draw the lines
            drawHighlightLines(c, (float) pix.x, (float) pix.y, set);
            int barIndex = (int) high.getX();
            if (high.getX()-barIndex >= 0.99999f){ //兼容异常数据，传奇四舍五入
                barIndex++;
            }
            mViewPortHandler.getSignPostRender().setCurrentClickBar(barIndex);
            MotionEvent event = mViewPortHandler.getSignPostRender().getEvent();
            if (event != null && event.getAction() == event.ACTION_MOVE) { //是否正在滑动
                mViewPortHandler.getSignPostRender().setCurtSorX(event.getX());
                mViewPortHandler.getSignPostRender().drawCursor(c, event.getX(), 10, mCursorPaint);
            } else { //是否已经松手了
//                float cursorX = mBarRect.left + barWidth / 2;
                float cursorX = (float) pix.x;
                mViewPortHandler.getSignPostRender().setCurtSorX(cursorX);
                mViewPortHandler.getSignPostRender().drawCursor(c, cursorX, 10, mCursorPaint);
            }

            SignPostRender.OnBarClickListener onBarClickListener = mViewPortHandler.getSignPostRender().getOnBarClickListener(mViewPortHandler);
            mViewPortHandler.getSignPostRender().setCurrentClickBar(barIndex);
            if (onBarClickListener != null && (lastGroupIndex != high.getDataSetIndex() || lastBarIndex != barIndex)) {
                onBarClickListener.onBarClick(high.getDataSetIndex(), barIndex);
                lastGroupIndex = high.getDataSetIndex();
                lastBarIndex = barIndex;
            }
        }

    }

    /**
     * Returns the Bitmap.Config that is used by this renderer.
     *
     * @return
     */
    public Bitmap.Config getBitmapConfig() {
        return mBitmapConfig;
    }

    /**
     * Sets the Bitmap.Config to be used by this renderer.
     * Default: Bitmap.Config.ARGB_8888
     * Use Bitmap.Config.ARGB_4444 to consume less memory.
     *
     * @param config
     */
    public void setBitmapConfig(Bitmap.Config config) {
        mBitmapConfig = config;
        releaseBitmap();
    }

    /**
     * Releases the drawing bitmap. This should be called when {@link LineChart#onDetachedFromWindow()}.
     */
    public void releaseBitmap() {
        if (mBitmapCanvas != null) {
            mBitmapCanvas.setBitmap(null);
            mBitmapCanvas = null;
        }
        if (mDrawBitmap != null) {
            Bitmap drawBitmap = mDrawBitmap.get();
            if (drawBitmap != null) {
                drawBitmap.recycle();
            }
            mDrawBitmap.clear();
            mDrawBitmap = null;
        }
    }

    private class DataSetImageCache {

        int highLightRadius = 0;
        private Path mCirclePathBuffer = new Path();
        private Bitmap[] circleBitmaps;
        private Bitmap circleHighLightBitmap;

        /**
         * Sets up the cache, returns true if a change of cache was required.
         *
         * @param set
         * @return
         */
        protected boolean init(ILineDataSet set) {

            int size = set.getCircleColorCount();
            boolean changeRequired = false;

            if (circleBitmaps == null) {
                circleBitmaps = new Bitmap[size];
                changeRequired = true;
            } else if (circleBitmaps.length != size) {
                circleBitmaps = new Bitmap[size];
                changeRequired = true;
            }

            return changeRequired;
        }

        protected int getHighLightRadius() {
            return highLightRadius;
        }

        void createHighLightBitmap(LineDataSet set) {
            if (!set.isDrawCirclesEnabled() || !set.isDrawHighlightCircles()) {
                return;
            }
            float highLightCircleRadius = set.getHighLightCircleRadius();
            float highLightCircleHoleRadius = set.getHighLightCircleHoleRadius();
            float highLightCircleOutsideWidth = set.getHighLightCircleOutsideWidth();
            highLightRadius = (int) (highLightCircleRadius + highLightCircleOutsideWidth);
            int size = (int) ((highLightCircleRadius + highLightCircleOutsideWidth) * 2.1);
            if (size<0){
                return;
            }
            Bitmap.Config conf = Bitmap.Config.ARGB_4444;
            Bitmap circleBitmap = Bitmap.createBitmap(size, size, conf);
            Canvas canvas = new Canvas(circleBitmap);
            int tempColor;  //临时暂存
            tempColor = mCirclePaintInner.getColor();
            mCirclePaintInner.setColor(set.getHighLightCircleOutsideColor());
            canvas.drawCircle(
                    highLightRadius,
                    highLightRadius,
                    highLightRadius,
                    mCirclePaintInner);
            mCirclePaintInner.setColor(tempColor);

            if (highLightCircleRadius>0) {
                tempColor = mRenderPaint.getColor();
                mRenderPaint.setColor(set.getHighLightCircleColor());
                canvas.drawCircle(
                        highLightRadius,
                        highLightRadius,
                        highLightCircleRadius,
                        mRenderPaint);
                mRenderPaint.setColor(tempColor);
            }

            if (highLightCircleHoleRadius>0) {
                tempColor = mCirclePaintInner.getColor();
                mCirclePaintInner.setColor(set.getHighLightCircleHoleColor());
                canvas.drawCircle(
                        highLightRadius,
                        highLightRadius,
                        highLightCircleHoleRadius,
                        mCirclePaintInner);
                mCirclePaintInner.setColor(tempColor);
            }

            circleHighLightBitmap = circleBitmap;

        }


        /**
         * Fills the cache with bitmaps for the given dataset.
         *
         * @param set
         * @param drawCircleHole
         * @param drawTransparentCircleHole
         */
        protected void fill(ILineDataSet set, boolean drawCircleHole, boolean drawTransparentCircleHole) {

            int colorCount = set.getCircleColorCount();
            float circleRadius = set.getCircleRadius();
            float circleHoleRadius = set.getCircleHoleRadius();

            if (set instanceof LineDataSet) {
                createHighLightBitmap((LineDataSet) set);
            }

            for (int i = 0; i < colorCount; i++) {

                Bitmap.Config conf = Bitmap.Config.ARGB_4444;
                Bitmap circleBitmap = Bitmap.createBitmap((int) (circleRadius * 2.1), (int) (circleRadius * 2.1), conf);

                Canvas canvas = new Canvas(circleBitmap);
                circleBitmaps[i] = circleBitmap;
                mRenderPaint.setColor(set.getCircleColor(i));

                if (drawTransparentCircleHole) {
                    // Begin path for circle with hole
                    mCirclePathBuffer.reset();

                    mCirclePathBuffer.addCircle(
                            circleRadius,
                            circleRadius,
                            circleRadius,
                            Path.Direction.CW);

                    // Cut hole in path
                    mCirclePathBuffer.addCircle(
                            circleRadius,
                            circleRadius,
                            circleHoleRadius,
                            Path.Direction.CCW);

                    // Fill in-between
                    canvas.drawPath(mCirclePathBuffer, mRenderPaint);
                } else {

                    canvas.drawCircle(
                            circleRadius,
                            circleRadius,
                            circleRadius,
                            mRenderPaint);

                    if (drawCircleHole) {
                        canvas.drawCircle(
                                circleRadius,
                                circleRadius,
                                circleHoleRadius,
                                mCirclePaintInner);
                    }
                }
            }
        }

        /**
         * Returns the cached Bitmap at the given index.
         *
         * @param index
         * @return
         */
        protected Bitmap getBitmap(int index) {
            return circleBitmaps[index % circleBitmaps.length];
        }

        protected Bitmap getHighLightBitmap() {
            return circleHighLightBitmap;
        }

    }
}
