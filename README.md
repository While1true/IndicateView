# IndicateView
简单的实现数字指示器

### 简单的方式实现右上角数字指示器


---
 实现方式：
> 重写View或者viewGroup的onDrawForegroundViewGroup可能需要setWillNotDraw(false);
> 画圆形背景，画数字ok
[github 地址](https://github.com/While1true/IndicateView)
#### 本文以Imageview为例子


```

   @Override
    public void onDrawForeground(Canvas canvas) {
        super.onDrawForeground(canvas);
        /**
         * 等于0不画
         */
        if (indicate == 0) {
            return;
        }
        if (paint == null) {
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        }
        paint.setColor(indicateColor);
        int width = getWidth();
        /**
         * 大于最大值画一个圆
         */
        if(indicate>max){
            canvas.drawCircle(width-indicateRadius,indicateRadius,indicateRadius/2,paint);
            return;
        }
        /**
         * 右上角
         * draw背景
         */

        float v = paint.measureText(indicate + "");
        rect.set(width - v - indicateRadius * 2, 0, width, indicateRadius * 2);
        canvas.drawRoundRect(rect, indicateRadius, indicateRadius, paint);
        /**
         * 画数字
         */
        paint.setColor(indicateTextColor);
        paint.setTextSize(indicatesize);
        /**
         * 计算基线位置
         */
        Paint.FontMetrics fm = paint.getFontMetrics();
        float baseLineY = indicateRadius - (fm.ascent - (fm.ascent - fm.descent) / 2);
        paint.getTextBounds(indicate+"",0,(indicate+"").length(),bounds);
        canvas.drawText(indicate + "",width - bounds.right/2 - (indicateRadius+v/2), baseLineY, paint);

    }


```
