package com.example.patrimoin_dz;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import androidx.appcompat.widget.AppCompatImageView;

public class ZoomableImageView extends AppCompatImageView {

    private Matrix matrix = new Matrix();
    private float[] matrixValues = new float[9];
    private float scale = 1f;
    private float minScale = 1f;
    private float maxScale = 5f;
    private float saveScale = 1f;
    private float origWidth, origHeight;
    private float viewWidth, viewHeight;
    private float lastFocusX, lastFocusY;

    private ScaleGestureDetector scaleDetector;
    private GestureDetector gestureDetector;

    public ZoomableImageView(Context context) {
        super(context);
        initialize(context);
    }

    public ZoomableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public ZoomableImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize(context);
    }

    private void initialize(Context context) {
        setScaleType(ScaleType.MATRIX);
        matrix = new Matrix();
        setImageMatrix(matrix);

        // Détecteur de gestes pour le zoom par pincement
        scaleDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                float scaleFactor = detector.getScaleFactor();
                float newScale = saveScale * scaleFactor;

                if (newScale < maxScale && newScale > minScale) {
                    saveScale = newScale;
                    float focusX = detector.getFocusX();
                    float focusY = detector.getFocusY();
                    matrix.postScale(scaleFactor, scaleFactor, focusX, focusY);
                    fixTranslation();
                    setImageMatrix(matrix);
                }
                return true;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                // Rien à faire ici
            }
        });

        // Détecteur de gestes pour le double-clic
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                float targetScale = (saveScale == minScale) ? 3f : minScale;
                saveScale = targetScale;
                matrix.setScale(targetScale, targetScale, e.getX(), e.getY());
                fixTranslation();
                setImageMatrix(matrix);
                return true;
            }
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth = w;
        viewHeight = h;
        if (origWidth == 0 && origHeight == 0 && getDrawable() != null) {
            origWidth = getDrawable().getIntrinsicWidth();
            origHeight = getDrawable().getIntrinsicHeight();
            centerImage();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);

        // Gérer le déplacement (pan)
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastFocusX = event.getX();
                lastFocusY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (saveScale > minScale) {
                    float deltaX = event.getX() - lastFocusX;
                    float deltaY = event.getY() - lastFocusY;
                    matrix.postTranslate(deltaX, deltaY);
                    fixTranslation();
                    setImageMatrix(matrix);
                }
                lastFocusX = event.getX();
                lastFocusY = event.getY();
                break;
        }
        return true;
    }

    private void centerImage() {
        float scaleX = viewWidth / origWidth;
        float scaleY = viewHeight / origHeight;
        scale = Math.min(scaleX, scaleY);
        saveScale = scale;
        matrix.setScale(scale, scale);
        float transX = (viewWidth - origWidth * scale) / 2;
        float transY = (viewHeight - origHeight * scale) / 2;
        matrix.postTranslate(transX, transY);
        setImageMatrix(matrix);
    }

    private void fixTranslation() {
        matrix.getValues(matrixValues);
        float transX = matrixValues[Matrix.MTRANS_X];
        float transY = matrixValues[Matrix.MTRANS_Y];
        float scaledWidth = origWidth * saveScale;
        float scaledHeight = origHeight * saveScale;

        // Limiter la translation pour que l'image reste dans les limites
        if (scaledWidth < viewWidth) {
            transX = (viewWidth - scaledWidth) / 2;
        } else {
            if (transX > 0) transX = 0;
            if (transX < viewWidth - scaledWidth) transX = viewWidth - scaledWidth;
        }

        if (scaledHeight < viewHeight) {
            transY = (viewHeight - scaledHeight) / 2;
        } else {
            if (transY > 0) transY = 0;
            if (transY < viewHeight - scaledHeight) transY = viewHeight - scaledHeight;
        }

        matrix.setScale(saveScale, saveScale);
        matrix.postTranslate(transX, transY);
    }
}