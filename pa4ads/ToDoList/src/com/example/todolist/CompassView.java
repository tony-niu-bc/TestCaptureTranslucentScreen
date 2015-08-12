package com.example.todolist;

import android.R.integer;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PointF;
import android.graphics.Shader.TileMode;
import android.graphics.Point;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class CompassView extends View 
{
	private float bearing;
	
	private Paint markerPaint;
	private Paint textPaint;
	private Paint circlePaint;
	
	private String strNorth;
	private String strSouth;
	private String strEast;
	private String strWest;
	
	private int textHeight;
	
	private enum CompassDirection {
		N, NNE, NE, ENE,
		E, ESE, SE, SSE,
		S, SSW, SW, WSW,
		W, WNW, NW, NNW
	}
	
	public CompassView(Context      context, 
					   AttributeSet attrs, 
					   int          defStyleAttr,
					   int          defStyleRes) 
	{
		super(context, attrs, defStyleAttr, defStyleRes);
		
		initCompassView();
	}

	public CompassView(Context      context, 
					   AttributeSet attrs, 
					   int          defStyleAttr) 
	{
		super(context, attrs, defStyleAttr);
		
		initCompassView();
	}

	public CompassView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);

		initCompassView();
	}

	public CompassView(Context context) 
	{
		super(context);

		initCompassView();
	}
	
	int[]   borderGradientColors;
	float[] borderGradientPositions;
	
	int[]   glassGradientColors;
	float[] glassGradientPositions;
	
	int skyHorizonColorFrom;
	int skyHorizonColorTo;
	
	int groundHorizonColorFrom;
	int groundHorizonColorTo;
	
	protected void initCompassView()
	{
		setFocusable(true);
		
		Resources r = this.getResources();
		
		circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		circlePaint.setColor(r.getColor(R.color.compass_bg));
		circlePaint.setStrokeWidth(1);
		circlePaint.setStyle(Paint.Style.STROKE);
		
		strNorth = r.getString(R.string.cardinal_north);
		strSouth = r.getString(R.string.cardinal_south);
		strWest = r.getString(R.string.cardinal_west);
		strEast = r.getString(R.string.cardinal_east);
		
		textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		textPaint.setColor(r.getColor(R.color.compass_text));
		textPaint.setFakeBoldText(true);
		textPaint.setSubpixelText(true);
		textPaint.setTextAlign(Align.LEFT);
		
		textHeight = (int)textPaint.measureText("yY");
		
		markerPaint = new Paint(Paint.ANTI_ALIAS_FLAG); 
		markerPaint.setColor(r.getColor(R.color.compass_marker));
		markerPaint.setAlpha(200);
		markerPaint.setStrokeWidth(1);
		markerPaint.setStyle(Paint.Style.STROKE);
		markerPaint.setShadowLayer(2, 1, 1, r.getColor(R.color.compass_shadow_color));
		
		borderGradientColors = new int[4];
		borderGradientColors[3] = r.getColor(R.color.compass_outer_border);
		borderGradientColors[2] = r.getColor(R.color.compass_inner_border_one);
		borderGradientColors[1] = r.getColor(R.color.compass_inner_border_two);
		borderGradientColors[0] = r.getColor(R.color.compass_inner_border);
		
		borderGradientPositions = new float[4];
		borderGradientPositions[3] = 0.0f;
		borderGradientPositions[2] = 1-0.03f;
		borderGradientPositions[1] = 1-0.06f;
		borderGradientPositions[0] = 1.0f;
		
		int glassColor = 245;
		
		glassGradientColors = new int[5];
		glassGradientColors[4] = Color.argb(65, glassColor, glassColor, glassColor);
		glassGradientColors[3] = Color.argb(100, glassColor, glassColor, glassColor);
		glassGradientColors[2] = Color.argb(50, glassColor, glassColor, glassColor);
		glassGradientColors[1] = Color.argb(0, glassColor, glassColor, glassColor);
		glassGradientColors[0] = Color.argb(0, glassColor, glassColor, glassColor);
		
		glassGradientPositions = new float[5];
		glassGradientPositions[4] = 1-0.0f;
		glassGradientPositions[3] = 1-0.06f;
		glassGradientPositions[2] = 1-0.10f;
		glassGradientPositions[1] = 1-0.20f;
		glassGradientPositions[0] = 1-1.0f;
		
		skyHorizonColorFrom = r.getColor(R.color.compass_horizon_sky_from);
		skyHorizonColorTo = r.getColor(R.color.compass_horizon_sky_to);
		
		groundHorizonColorFrom = r.getColor(R.color.compass_horizon_ground_from);
		groundHorizonColorTo = r.getColor(R.color.compass_horizon_ground_from);
	}

	private int measure(int measureSpec)
	{
		int iResult = 200;
		
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		
		if (MeasureSpec.UNSPECIFIED != specMode)
		{
			iResult = specSize;
		}
		
		return iResult;
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) 
	{
		int measuredWidth  = measure(widthMeasureSpec);
		int measuredHeight = measure(heightMeasureSpec);
		
		int d = Math.min(measuredWidth, measuredHeight);
		
		setMeasuredDimension(d, d);
	}
	
	@Override
	protected void onDraw(Canvas canvas) 
	{
		float ringWidth = textHeight + 4;
		
		int mMeasuredWidth  = getMeasuredWidth();
		int mMeasuredHeight = getMeasuredHeight();
		
		if (mMeasuredWidth > 0 && mMeasuredHeight > 0)
		{
			int px = mMeasuredWidth / 2;
			int py = mMeasuredHeight / 2;
			
			Point center = new Point(px, py);

			int radius = Math.min(px, py) - 2;
							
			RectF boundingBox = new RectF(center.x - radius, center.y - radius, 
										  center.x + radius, center.y - radius);
			
			RectF innerboundingBox = new RectF(center.x - radius + ringWidth, 
											   center.y - radius + ringWidth, 
					  					       center.x + radius - ringWidth, 
					  					       center.y + radius - ringWidth);
			
			float innerRadius = innerboundingBox.height() / 2;

			RadialGradient borderGradient = new RadialGradient(px, py, radius, 
															   borderGradientColors, 
															   borderGradientPositions, 
															   TileMode.CLAMP);
			Paint pgb = new Paint();
			pgb.setShader(borderGradient);
			
			Path outerRingPath = new Path();
			outerRingPath.addOval(boundingBox, Direction.CW);
			
			canvas.drawPath(outerRingPath, pgb);
			
			LinearGradient skyShader = new LinearGradient(center.x, innerboundingBox.top, 
														  center.x, innerboundingBox.bottom, 
														  skyHorizonColorFrom, skyHorizonColorTo, 
														  TileMode.CLAMP);
			
			Paint skyPaint = new Paint();
			skyPaint.setShader(skyShader);
			
			LinearGradient groundShader = new LinearGradient(center.x, innerboundingBox.top, 
														     center.x, innerboundingBox.bottom, 
														     groundHorizonColorFrom, groundHorizonColorTo, 
														     TileMode.CLAMP);
			
			Paint groundPaint = new Paint();
			groundPaint.setShader(groundShader);
			
			float tiltDegree = pitch;
			while (tiltDegree > 90 || tiltDegree < -90) 
			{
				if (tiltDegree > 90)
				{
					tiltDegree = -90 + (tiltDegree - 90);
				}
				
				if (tiltDegree < -90)
				{
					tiltDegree = 90 - (tiltDegree + 90);
				}
			}
			
			float rollDegree = roll;
			while (rollDegree > 180 || rollDegree < -180) 
			{
				if (rollDegree > 180)
				{
					rollDegree = -180 + (rollDegree - 180);
				}
				
				if (rollDegree < -180)
				{
					rollDegree = 180 - (rollDegree + 180);
				}
			}
			
			Path skyPath = new Path();
			skyPath.addArc(innerboundingBox, -tiltDegree, (180 + (2 * tiltDegree)));
			
			canvas.save();
			
			canvas.rotate(-rollDegree, px, py);
			canvas.drawOval(innerboundingBox, groundPaint);
			canvas.drawPath(skyPath, skyPaint);
			canvas.drawPath(skyPath, markerPaint);
			
			int markWidth = radius / 3;
			int startX = center.x - markWidth;
			int endX = center.x + markWidth;
			
			double h = innerRadius * Math.cos(Math.toRadians(90 - tiltDegree));
			double justTiltY = center.y - h;
			
			float pxPerDegree = (innerboundingBox.height() / 2) / 45f;
			
			for (int i = 90; i >= -90; i -= 10)
			{
				double ypos = justTiltY + i * pxPerDegree;
				
				if ((ypos < (innerboundingBox.top + textHeight)) || 
					(ypos > (innerboundingBox.bottom - textHeight)))
				{
					continue;
				}
				
				canvas.drawLine(startX, (float)ypos, endX, (float)ypos, markerPaint);
				
				int displayPos = (int)(tiltDegree - i);
				
				String displayString = String.valueOf(displayPos);
				
				float stringSizeWidth = textPaint.measureText(displayString);
				
				canvas.drawText(displayString, (int)(center.x - stringSizeWidth / 2), (int)(ypos) + 1, textPaint);			
			}

			markerPaint.setStrokeWidth(2);
			canvas.drawLine(center.x - radius / 2, (float)justTiltY, center.x + radius / 2, (float)justTiltY, markerPaint);
			markerPaint.setStrokeWidth(1);
			
			Path rollArrow = new Path();
			rollArrow.moveTo(center.x - 3, (int)innerboundingBox.top + 14);
			rollArrow.lineTo(center.x, (int)innerboundingBox.top + 10);
			rollArrow.moveTo(center.x + 3, (int)innerboundingBox.top + 14);
			rollArrow.lineTo(center.x, (int)innerboundingBox.top + 10);
			canvas.drawPath(rollArrow, markerPaint);
			
			String rollText = String.valueOf(rollDegree);
			double rollTextWidth = textPaint.measureText(rollText);
			canvas.drawText(rollText, (float)(center.x - rollTextWidth / 2), innerboundingBox.top + textHeight + 2, textPaint);
			
			canvas.restore();
			
			canvas.save();
			
			canvas.rotate(180, center.x, center.y);
			
			for (int i = -180; i < 180; i += 10)
			{
				if (i % 30 == 0)
				{
					String rollString = String.valueOf(i * -1);
					float rollStringWidth = textPaint.measureText(rollString);
					PointF rollStringCenter = new PointF(center.x - rollStringWidth / 2, 
														 innerboundingBox.top + 1 + textHeight);
					canvas.drawText(rollString, rollStringCenter.x, rollStringCenter.y, textPaint);
				}
				else 
				{
					canvas.drawLine(center.x, (int)innerboundingBox.top, center.x, (int)innerboundingBox.top + 5, markerPaint);
				}
				
				canvas.rotate(10, center.x, center.y);
			}
			
			canvas.restore();
			
			canvas.save();
			
			canvas.rotate(-1 * (bearing), px, py);
			
			double increment = 22.5;
			
			for (double i = 0; i < 360; i += increment)
			{
				CompassDirection cd = CompassDirection.values()[(int)(i / 22.5)];
				
				String headString = cd.toString();
				
				float headStringWidth = textPaint.measureText(headString);
				
				PointF headStringCenter = new PointF(center.x - headStringWidth / 2, 
													 boundingBox.top + 1 + textHeight);
				
				if (i % increment == 0)
				{
					canvas.drawText(headString, headStringCenter.x, headStringCenter.y, textPaint);
				}
				else 
				{
					canvas.drawLine(center.x, (int)boundingBox.top, center.x, (int)boundingBox.top + 3, markerPaint);
				}
				
				canvas.rotate((int)increment, center.x, center.y);			
			}
			
			canvas.restore();
			
			RadialGradient glassShader = new RadialGradient(px, py, (int)innerRadius, glassGradientColors, glassGradientPositions, TileMode.CLAMP);
			
			Paint glassPaint = new Paint();
			
			glassPaint.setShader(glassShader);
			
			canvas.drawOval(innerboundingBox, glassPaint);
			
			canvas.drawOval(boundingBox, circlePaint);
			
			circlePaint.setStrokeWidth(2);
			
			canvas.drawOval(innerboundingBox, circlePaint);			
		}
	}
	
//	@Override
//	protected void onDraw(Canvas canvas) 
//	{
//		int mMeasuredWidth  = getMeasuredWidth();
//		int mMeasuredHeight = getMeasuredHeight();
//		
//		int px = mMeasuredWidth / 2;
//		int py = mMeasuredHeight / 2;
//		
//		int radius = Math.min(px, py);
//		
//		canvas.drawCircle(px, py, radius, circlePaint);
//				
//		canvas.save();
//				
//		canvas.rotate(-bearing, px, py);
//		
//		int textWidth = (int)textPaint.measureText("W");
//		
//		int cardinalX = px - textWidth / 2;
//		int cardinalY = py - radius + textHeight;
//
//		for (int i = 0; i < 24; ++i)
//		{
//			canvas.drawLine(px, py-radius, px, py-radius+10, markerPaint);
//			
//			canvas.save();
//			
//			canvas.translate(0, textHeight);
//			
//			if (0 == i % 6)
//			{
//				String dirString = "";
//				
//				switch(i)
//				{
//				case 0:
//					dirString = strNorth;
//					
//					canvas.drawLine(px, 2 * textHeight, px-5, 3 * textHeight, markerPaint);
//					canvas.drawLine(px, 2 * textHeight, px+5, 3 * textHeight, markerPaint);
//					break;
//					
//				case 6:
//					dirString = strEast;
//					break;
//				case 12:
//					dirString = strSouth;
//					break;
//				case 18:
//					dirString = strWest;
//					break;					
//				}
//				
//				canvas.drawText(dirString, cardinalX, cardinalY, textPaint);
//			}
//			else if (0 == i % 3)
//			{
//				String strAngle = String.valueOf(i * 15);
//				
//				float fAngleTextWidth = textPaint.measureText(strAngle);
//				
//				int angleTextX = (int)(px - fAngleTextWidth / 2);
//				int angleTextY = py - radius + textHeight;
//				
//				canvas.drawText(strAngle, angleTextX, angleTextY, textPaint);
//			}
//			
//			canvas.restore();
//			
//			canvas.rotate(15, px, py);
//		}
//				
//		canvas.restore();
//	}

	public void setBearing(float _bearing)
	{
		bearing = _bearing;
	}

	public float getBearing()
	{
		return bearing;
	}

	private float pitch;
	public void setPitch(float _pitch)
	{
		pitch = _pitch;
	}

	public float getPitch()
	{
		return pitch;
	}

	private float roll;
	public void setRoll(float _pitch)
	{
		pitch = _pitch;
	}

	public float getRoll()
	{
		return pitch;
	}
	
}
