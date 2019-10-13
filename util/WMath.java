package de.stylextv.bits.util;

import net.minecraft.util.math.MathHelper;

public final class WMath
{
	public static int clamp(int num, int min, int max)
	{
		return num < min ? min : num > max ? max : num;
	}
	
	public static float clamp(float num, float min, float max)
	{
		return num < min ? min : num > max ? max : num;
	}
	
	public static double clamp(double num, double min, double max)
	{
		return num < min ? min : num > max ? max : num;
	}
	
	public static float sin(float value)
	{
		return MathHelper.sin(value);
	}
	
	public static float cos(float value)
	{
		return MathHelper.cos(value);
	}
	
	public static float wrapDegrees(float value)
	{
		return MathHelper.wrapDegrees(value);
	}
	
	public static double wrapDegrees(double value)
	{
		return MathHelper.wrapDegrees(value);
	}
}