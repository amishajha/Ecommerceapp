package com.example.shopnest

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton

class SNbutton (context: Context, attrs: AttributeSet):AppCompatButton(context, attrs) {
    init {
        // Call the function to apply the font to the components.
        applyFont()
    }

    /**
     * Applies a font to a Button.
     */
    private fun applyFont() {

        // This is used to get the file from the assets folder and set it to the title textView.
        val typeface: Typeface =
                Typeface.createFromAsset(context.assets, "Montserrat-Bold.ttf")
        setTypeface(typeface)

    }

}