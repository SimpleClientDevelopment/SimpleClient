package simpleclient.util;

import simpleclient.render.RenderContext;

public class DrawUtil {

	/*
	 * https://en.wikipedia.org/wiki/Stadium_(geometry)
	 */
	public static void stadium(final float wX1, final float wY1, final float wX2,
			final float wY2, final int color) {
		final float width = wX2 - wX1;
		final float height = wY2 - wY1;

		if (width <= height) {
			throw new RuntimeException("Width must be higher than height.");
		}

		final float circleRadius = height / 2;

		DrawUtil.rectangle(wX1 + circleRadius, wY1, wX2 - circleRadius, wY2, color);

		RenderContext.triangleFan(context -> {
			// Left
			context.circle(wX1 + circleRadius, wY2 - circleRadius, circleRadius, 180.F, 360.F, 1.f, color);

			// Right
			context.circle(wX2 - circleRadius, wY2 - circleRadius, circleRadius, 0.F, 180.F, 1.F, color);
		});
	}

	public static void circle(final float x, final float y, final float radius,
			final int color) {
		DrawUtil.circle(x, y, radius, 0.F, 360.F, 1.F, color);
	}

	public static void circle(final float x, final float y, final float radius,
			final float startAngle, final float endAngle, final int color) {
		DrawUtil.circle(x, y, radius, startAngle, endAngle, 1.F, color);
	}

	public static void circle(final float x, final float y, final float radius,
			final float startAngle, final float endAngle, final float step, final int color) {
		RenderContext.triangleFan(
				context -> context.circle(x, y, radius, startAngle, endAngle, step, color));
	}

	public static void rectangle(float left, float top, float right, float bottom,
			final int color) {
		RenderContext.quads(context -> context.rectangle(left, top, right, bottom, color));
	}

	public static void border(final float left, final float top, final float right,
			final float bottom, final float borderWidth, final float borderHeight, final int color) {
		RenderContext.quads(context -> {
			// Top
			context.rectangle(left + borderWidth, top, right - borderWidth, top + borderHeight, color);

			// Right
			context.rectangle(right - borderWidth, top, right, bottom, color);

			// Bottom
			context.rectangle(left + borderWidth, bottom - borderHeight, right - borderWidth, bottom, color);

			// Left
			context.rectangle(left, top, left + borderWidth, bottom, color);
		});
	}

	public static void roundedRectangle(final float left, final float top,
			final float right, final float bottom, final float circleRadius, final int color) {
		RenderContext.triangleFan(context -> {
			// Top-Left
			context.circle(left + circleRadius, top + circleRadius, circleRadius, 180.F, 270.F, 1.f,
					color);

			// Top-Right
			context.circle(right - circleRadius, top + circleRadius, circleRadius, 90.F, 180.F, 1.f,
					color);

			// Bottom-Left
			context.circle(left + circleRadius, bottom - circleRadius, circleRadius, 270.F, 360.F,
					1.f, color);

			// Bottom-Right
			context.circle(right - circleRadius, bottom - circleRadius, circleRadius, 0.F, 90.F,
					1.f, color);
		});

		RenderContext.quads(context -> {
			// Middle
			context.rectangle(left + circleRadius, top + circleRadius, right - circleRadius,
					bottom - circleRadius, color);

			// Top
			context.rectangle(left + circleRadius, top, right - circleRadius, top + circleRadius,
					color);

			// Right
			context.rectangle(right - circleRadius, top + circleRadius, right,
					bottom - circleRadius, color);

			// Bottom
			context.rectangle(left + circleRadius, bottom - circleRadius, right - circleRadius,
					bottom, color);

			// Left
			context.rectangle(left, top + circleRadius, left + circleRadius, bottom - circleRadius,
					color);
		});
	}
}