package com.weelgo.core;

import java.util.List;

public class Bound
{
	private int top;
	private int left;
	private int bottom;
	private int right;
	private int width;
	private int height;
	private String backgroundColor;
	private String borderColor;
	private int level;
	private boolean empty;
	private String text;
	private String fontSizeString;
	private float fontSize;
	private boolean fontBold = false;
	private String uuid;

	public int getTop()
	{
		return top;
	}

	public void setTop(int top)
	{
		this.top = top;
	}

	public int getLeft()
	{
		return left;
	}

	public void setLeft(int left)
	{
		this.left = left;
	}

	public int getBottom()
	{
		return bottom;
	}

	public void setBottom(int bottom)
	{
		this.bottom = bottom;
	}

	public int getRight()
	{
		return right;
	}

	public void setRight(int right)
	{
		this.right = right;
	}

	public int getWidth()
	{
		return width;
	}

	public void setWidth(int width)
	{
		this.width = width;
	}

	public int getHeight()
	{
		return height;
	}

	public void setHeight(int height)
	{
		this.height = height;
	}

	public String getBackgroundColor()
	{
		return backgroundColor;
	}

	public void setBackgroundColor(String backgroundColor)
	{
		this.backgroundColor = backgroundColor;
	}

	public String getBorderColor()
	{
		return borderColor;
	}

	public void setBorderColor(String borderColor)
	{
		this.borderColor = borderColor;
	}

	public int getLevel()
	{
		return level;
	}

	public void setLevel(int level)
	{
		this.level = level;
	}

	public boolean isEmpty()
	{
		return empty;
	}

	public void setEmpty(boolean empty)
	{
		this.empty = empty;
	}

	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		this.text = text;
	}

	public String getFontSizeString()
	{
		return fontSizeString;
	}

	public void setFontSizeString(String fontSizeString)
	{
		this.fontSizeString = fontSizeString;
	}

	public float getFontSize()
	{
		return fontSize;
	}

	public void setFontSize(float fontSize)
	{
		this.fontSize = fontSize;
	}

	public String getUuid()
	{
		return uuid;
	}

	public void setUuid(String uuid)
	{
		this.uuid = uuid;
	}

	public boolean isFontBold()
	{
		return fontBold;
	}

	public void setFontBold(boolean fontBold)
	{
		this.fontBold = fontBold;
	}

	public static Bound calculateBound(List<Bound> arl,
			boolean invertedXAxis,
			boolean invertedYAxis)
	{
		int top = 0;
		boolean topInit = false;
		int right = 0;
		boolean rightInit = false;
		int bottom = 0;
		boolean bottomInit = false;
		int left = 0;
		boolean leftInit = false;
		if (arl != null && arl.size() > 0)
		{

			for (Bound a : arl)
			{
				if (a != null)
				{
					if (invertedYAxis)
					{
						if (topInit == false || top > a.getTop())
						{
							top = a.getTop();
							topInit = true;
						}
						if (bottomInit == false || bottom < a.getBottom())
						{
							bottom = a.getBottom();
							bottomInit = true;
						}
					} else
					{
						if (topInit == false || top < a.getTop())
						{
							top = a.getTop();
							topInit = true;
						}
						if (bottomInit == false || bottom > a.getBottom())
						{
							bottom = a.getBottom();
							bottomInit = true;
						}
					}
					if (invertedXAxis)
					{
						if (rightInit == false || right > a.getRight())
						{
							right = a.getRight();
							rightInit = true;
						}

						if (leftInit == false || left < a.getLeft())
						{
							left = a.getLeft();
							leftInit = true;
						}
					} else
					{
						if (rightInit == false || right < a.getRight())
						{
							right = a.getRight();
							rightInit = true;
						}

						if (leftInit == false || left > a.getLeft())
						{
							left = a.getLeft();
							leftInit = true;
						}
					}

				}
			}

		}

		Bound b = new Bound();
		b.setBottom(bottom);
		b.setLeft(left);
		b.setRight(right);
		b.setTop(top);
		b.setHeight(Math.abs(top - bottom));
		b.setWidth(Math.abs(right - left));
		return b;
	}
}