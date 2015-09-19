package com.tjl.fuse.utils.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

import static android.renderscript.Allocation.*;

/**
 * Created by Jacob on 9/18/15.
 */
public class Blur {

  public static Bitmap blur(Context context, Bitmap bitmap) {
    Bitmap blur = bitmap.copy(bitmap.getConfig(), true);

    final RenderScript renderScript = RenderScript.create(context);
    final Allocation input =
        Allocation.createFromBitmap(renderScript, blur, MipmapControl.MIPMAP_NONE, USAGE_SCRIPT);
    final Allocation output = Allocation.createTyped(renderScript, input.getType());
    final ScriptIntrinsicBlur script =
        ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
    script.setRadius(25f);
    script.setInput(input);
    script.forEach(output);
    output.copyTo(blur);

    return blur;
  }
}
