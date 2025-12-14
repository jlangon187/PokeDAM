package com.example.pokedam;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

public class FullScreenVideoView extends VideoView {

    public FullScreenVideoView(Context context) {
        super(context);
    }

    public FullScreenVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FullScreenVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 1. Obtenemos el tamaño de la pantalla del móvil
        int screenWidth = getDefaultSize(0, widthMeasureSpec);
        int screenHeight = getDefaultSize(0, heightMeasureSpec);

        // 2. Le pedimos a Android que calcule el tamaño NATURAL del video
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int videoWidth = getMeasuredWidth();
        int videoHeight = getMeasuredHeight();

        // Si el video aún no ha cargado, usamos el tamaño de pantalla por defecto
        if (videoWidth == 0 || videoHeight == 0) {
            setMeasuredDimension(screenWidth, screenHeight);
            return;
        }

        // 3. LÓGICA "CENTER CROP" (Zoom para llenar sin deformar)
        float screenRatio = (float) screenWidth / screenHeight;
        float videoRatio = (float) videoWidth / videoHeight;

        int finalWidth = screenWidth;
        int finalHeight = screenHeight;

        // Comparamos las proporciones:
        if (screenRatio > videoRatio) {
            // La pantalla es más ancha que el video (o el video es muy alto/delgado).
            // Ajustamos el ANCHO a la pantalla y dejamos que el ALTO crezca proporcionalmente.
            // (Se recortará un poco por arriba y abajo)
            finalHeight = (int) (screenWidth / videoRatio);
        } else {
            // La pantalla es más alta que el video (o el video es panorámico).
            // Ajustamos el ALTO a la pantalla y dejamos que el ANCHO crezca proporcionalmente.
            // (Se recortará un poco por los lados)
            finalWidth = (int) (screenHeight * videoRatio);
        }

        // Aplicamos las nuevas dimensiones calculadas
        setMeasuredDimension(finalWidth, finalHeight);
    }
}