GMColorPicker : GMUserView {

	var color;
	var hsv;
	var margin = 12;
	var input = nil;
	var helperRatio = 0.25;
	var svRatio = 0.6;
	var helperSize = 12;

	*new {
		^super.new.init;
	}

	init {
		super.init;

		// Allowed events
		this.setEventHandler(
			QObject.mouseUpEvent,
			\mouseDownEvent, true);
		this.setEventHandler(
			QObject.mouseUpEvent,
			\mouseUpEvent, true);
		this.setEventHandler(
			QObject.mouseMoveEvent,
			\mouseMoveEvent, true);

		// Forbidden events
		this.setEventHandler(
			QObject.mouseDblClickEvent,
			\mouseDownEvent, false);

		hsv = [0, 1, 1];
		color = Color(1, 0, 0);

		this.drawFunc_({ this.draw });
		this.action_({});
		this.mouseUpAction_({ input = nil; });
	}

	action_ { |aFunction|
		this.mouseDownAction_({ |view, x, y|

			input = this.prGetInput(x, y);

			if(input == "sv") {
				hsv[1] = x.linlin(
					super.interactionRect.left + margin,
					super.interactionRect.left +
					(super.interactionRect.width * 0.5) - margin,
					0, 1);
				hsv[2] = y.linlin(
					super.interactionRect.top +
					(super.interactionRect.height * 0.25) + margin,
					super.interactionRect.bottom - margin,
					0, 1);
				color = this.hsvToColor(hsv);
				aFunction.value(color);
				this.refresh;
			};

			if(input == "h") {
				hsv[0] = y.linlin(
					super.interactionRect.top +
					(super.interactionRect.height * 0.25) + margin,
					super.interactionRect.bottom - margin,
					0, 359);
				color = this.hsvToColor(hsv);
				aFunction.value(color);
				this.refresh;
			};

			if(input == "a") {
				color.alpha = y.linlin(
					super.interactionRect.top +
					(super.interactionRect.height * 0.25) + margin,
					super.interactionRect.bottom - margin,
					1, 0);
				aFunction.value(color);
				this.refresh;
			};
		});

		this.mouseMoveAction_({ |view, x, y|
			if(input == "sv") {
				hsv[1] = x.linlin(
					super.interactionRect.left + margin,
					super.interactionRect.left +
					(super.interactionRect.width * 0.5) - margin,
					0, 1);
				hsv[2] = y.linlin(
					super.interactionRect.top +
					(super.interactionRect.height * 0.25) + margin,
					super.interactionRect.bottom - margin,
					0, 1);
				color = this.hsvToColor(hsv);
				aFunction.value(color);
				this.refresh;
			};

			if(input == "h") {
				hsv[0] = y.linlin(
					super.interactionRect.top +
					(super.interactionRect.height * 0.25) + margin,
					super.interactionRect.bottom - margin,
					0, 359);
				color = this.hsvToColor(hsv);
				aFunction.value(color);
				this.refresh;
			};

			if(input == "a") {
				color.alpha = y.linlin(
					super.interactionRect.top +
					(super.interactionRect.height * 0.25) + margin,
					super.interactionRect.bottom - margin,
					1, 0);
				aFunction.value(color);
				this.refresh;
			};
		});
	}

	prGetInput { |x, y|
		var input = nil;
		var inBounds = y >= (super.interactionRect.top + (super.interactionRect.height * 0.25) + margin);

		if(inBounds) {
			inBounds = (y <= (super.interactionRect.bottom - margin));
		};

		if(inBounds) {
			if(x >= (super.interactionRect.left + margin)) {
				if(x <= (super.interactionRect.left + (super.interactionRect.width * 0.5) - margin)) {
					input = "sv";
				};
			};

			if(input.isNil) {
				if(x >= (super.interactionRect.left + (super.interactionRect.width * 0.5) + margin)) {
					if(x <= (super.interactionRect.left + (super.interactionRect.width * 0.75) - margin)) {
						input = "h";
					};
				};
			};

			if(input.isNil) {
				if(x >= (super.interactionRect.left + (super.interactionRect.width * 0.75) + margin)) {
					if(x <= (super.interactionRect.right - margin)) {
						input = "a";
					};
				};
			};
		};

		^input
	}

	hsvToColor { |hsv|
		var newColor = Color(0, 0, 0);

		var h = hsv[0];
		var s = hsv[1];
		var v = hsv[2];

		var c = s * v;
		var x = c * (1 - (((h / 60.0)%2.0) - 1).abs);
		var m = v - c;

		if((h >= 0)&&(h < 60), {
			newColor.red = c;
			newColor.green = x;
			newColor.blue = 0;
		});

		if((h >= 60)&&(h < 120), {
			newColor.red = x;
			newColor.green = c;
			newColor.blue = 0;
		});

		if((h >= 120)&&(h < 180), {
			newColor.red = 0;
			newColor.green = c;
			newColor.blue = x;
		});

		if((h >= 180)&&(h < 240), {
			newColor.red = 0;
			newColor.green = x;
			newColor.blue = c;
		});

		if((h >= 240)&&(h < 300), {
			newColor.red = x;
			newColor.green = 0;
			newColor.blue = c;
		});

		if((h >= 300)&&(h < 360), {
			newColor.red = c;
			newColor.green = 0;
			newColor.blue = x;
		});

		newColor.red = newColor.red + m;
		newColor.green = newColor.green + m;
		newColor.blue = newColor.blue + m;
		newColor.alpha = color.alpha;

		^newColor
	}

	setColor_ { |aColor|
		var r = aColor.red;
		var g = aColor.green;
		var b = aColor.blue;

		var cmax = max(
			r,
			max(g, b)
		);
		var cmin = min(
			r,
			min(g, b)
		);
		var diff = cmax - cmin;

		if(cmax == cmin) {
			hsv[0] = 0;
		} {
			if(cmax == r) {
				hsv[0] =
				((60 * ((g - b) / diff)) + 360) % 360;
			};
			if(cmax == g) {
				hsv[0] =
				((60 * ((b - r) / diff)) + 120) % 360;
			};
			if(cmax == b) {
				hsv[0] =
				((60 * ((r - g) / diff)) + 240) % 360;
			};
		};

		if(cmax == 0)
		{ hsv[1] = 0; }
		{ hsv[1] = (diff / cmax); };

		hsv[2] = cmax;

		color = aColor;

		this.refresh;
	}

	draw {
		var saturationHelperPoint;

		super.drawFrame;

		this.prDrawSVGradient;
		this.prDrawSaturationHelper;
		this.prDrawHuePicker;
		this.prDrawHueHelper;
		this.prDrawColorHelper;
		this.prDrawAlphaPicker;
		this.prDrawAlphaHelper;
	}

	prDrawSVGradient {
		// Draw the sv gradient
		((super.interactionRect.width * 0.5) - (margin * 2)).do({ |xIndex|
			Pen.addRect(
				Rect(
					super.interactionRect.left + margin + xIndex,
					super.interactionRect.top
					+ (super.interactionRect.height * 0.25)
					+ margin,
					1,
					(super.interactionRect.height * 0.75) - (margin * 2)
				);
			);
			Pen.fillAxialGradient(
				Point(
					0,
					super.interactionRect.top
					+ (super.interactionRect.height * 0.25)
					+ margin
				),
				Point(
					0,
					super.interactionRect.bottom - margin
				),
				Color.black,
				this.hsvToColor(
					[
						hsv[0],
						xIndex.linlin(
							0,
							(super.interactionRect.width * 0.5)
							- (margin * 2),
							0, 1 ),
						1
					]
				)
			);
		});
	}

	prDrawColorHelper {
		Pen.fillColor_(color);
		Pen.fillRect(
			Rect(
				super.interactionRect.left + margin,
				super.interactionRect.top + margin,
				super.interactionRect.width - (margin * 2),
				(super.interactionRect.height * 0.25)
				- (margin * 2)
				- helperSize
			)
		);
	}

	prDrawHueHelper {
		Pen.addRect(
			Rect(
				super.interactionRect.left
				+ (super.interactionRect.width * 0.5),

				super.interactionRect.top +
				(super.interactionRect.height * 0.25)
				+ margin
				+ (((super.interactionRect.height * 0.75) - (margin * 2)) *
					hsv[0].linlin(0, 360, 0, 1))
				- (helperSize * 2),

				super.interactionRect.width * 0.25,

				helperSize
			)
		);

		Pen.fillAxialGradient(
			Point(
				0,

				super.interactionRect.top +
				(super.interactionRect.height * 0.25)
				+ margin
				+ (((super.interactionRect.height * 0.75) - (margin * 2)) *
					hsv[0].linlin(0, 360, 0, 1))
				- (helperSize * 2),
			),
			Point(
				0,

				super.interactionRect.top +
				(super.interactionRect.height * 0.25)
				+ margin
				+ (((super.interactionRect.height * 0.75) - (margin * 2)) *
					hsv[0].linlin(0, 360, 0, 1))
				- helperSize,
			),
			Color(0, 0, 0, 0),
			Color(0, 0, 0, 1)
		);

		Pen.addRect(
			Rect(
				super.interactionRect.left
				+ (super.interactionRect.width * 0.5),

				super.interactionRect.top +
				(super.interactionRect.height * 0.25)
				+ margin
				+ (((super.interactionRect.height * 0.75) - (margin * 2)) *
					hsv[0].linlin(0, 360, 0, 1))
				+ helperSize,

				super.interactionRect.width * 0.25,

				helperSize
			)
		);

		Pen.fillAxialGradient(
			Point(
				0,

				super.interactionRect.top +
				(super.interactionRect.height * 0.25)
				+ margin
				+ (((super.interactionRect.height * 0.75) - (margin * 2)) *
					hsv[0].linlin(0, 360, 0, 1))
				+ helperSize,
			),
			Point(
				0,

				super.interactionRect.top +
				(super.interactionRect.height * 0.25)
				+ margin
				+ (((super.interactionRect.height * 0.75) - (margin * 2)) *
					hsv[0].linlin(0, 360, 0, 1))
				+ (helperSize * 2),
			),
			Color(0, 0, 0, 1),
			Color(0, 0, 0, 0)
		);

		Pen.fillColor_(color);
		Pen.fillRect(
			Rect(
				super.interactionRect.left
				+ (super.interactionRect.width * 0.5),

				super.interactionRect.top +
				(super.interactionRect.height * 0.25)
				+ margin
				+ (((super.interactionRect.height * 0.75) - (margin * 2)) *
					hsv[0].linlin(0, 360, 0, 1))
				- helperSize,

				super.interactionRect.width * 0.25,

				helperSize * 2
			)
		);
	}

	prDrawSaturationHelper {
		var saturationHelperPoint = Point(
			super.interactionRect.left + margin +
			(((super.interactionRect.width * 0.5) - (margin * 2)) * hsv[1]),

			super.interactionRect.top +
			(super.interactionRect.height * 0.25)
			+ margin
			+ ((super.interactionRect.height * 0.75
				- (margin * 2)) * hsv[2])
		);

		Pen.addWedge(
			saturationHelperPoint,
			helperSize * 1.5,
			0, 2pi
		);

		Pen.fillRadialGradient(
			saturationHelperPoint,
			saturationHelperPoint,
			helperSize,
			helperSize * 1.5,
			Color(0, 0, 0, 1),
			Color(0, 0, 0, 0)
		);

		Pen.fillColor_(color);
		Pen.addWedge(
			saturationHelperPoint,
			helperSize,
			0, 2pi
		);
		Pen.fill;
	}

	prDrawHuePicker {
		Pen.width_(1);
		(super.interactionRect.height * 0.75 - (margin * 2)).do({ |yIndex|
			Pen.strokeColor_(
				this.hsvToColor(
					[
						yIndex.linlin(

							0,
							(super.interactionRect.height * 0.75)
							- (margin * 2),
							0,
							360
						),
						hsv[1],
						hsv[2]
					];
				);
			);

			Pen.moveTo(
				Point(
					super.interactionRect.left
					+ (super.interactionRect.width * 0.5) + margin,

					super.interactionRect.top +
					(super.interactionRect.height * 0.25)
					+ margin + yIndex)
			);

			Pen.lineTo(
				Point(
					super.interactionRect.left
					+ (super.interactionRect.width * 0.75) - margin,

					super.interactionRect.top +
					(super.interactionRect.height * 0.25)
					+ margin + yIndex)
			);

			Pen.stroke;
		});
	}

	prDrawAlphaPicker {
		Pen.addRect(
			Rect(
				super.interactionRect.left
				+ (super.interactionRect.width * 0.75) + margin,

				super.interactionRect.top +
				(super.interactionRect.height * 0.25)
				+ margin,

				(super.interactionRect.width * 0.25)
				- (margin * 2),

				(super.interactionRect.height * 0.75)
				- (margin * 2)
			)
		);

		Pen.fillAxialGradient(
			Point(
				0,
				super.interactionRect.top +
				(super.interactionRect.height * 0.25)
				+ margin
			),
			Point(
				0,
				super.interactionRect.bottom
				- margin
			),
			Color(color.red, color.green, color.blue, 1),
			Color(color.red, color.green, color.blue, 0)
		);
	}

	prDrawAlphaHelper {
		Pen.addRect(
			Rect(
				super.interactionRect.left
				+ (super.interactionRect.width * 0.75),

				super.interactionRect.top +
				(super.interactionRect.height * 0.25)
				+ margin
				+ (((super.interactionRect.height * 0.75) - (margin * 2)) *
					(1 - color.alpha))
				- (helperSize * 2),

				super.interactionRect.width * 0.25,

				helperSize
			)
		);

		Pen.fillAxialGradient(
			Point(
				0,

				super.interactionRect.top +
				(super.interactionRect.height * 0.25)
				+ margin
				+ (((super.interactionRect.height * 0.75) - (margin * 2)) *
					(1 - color.alpha))
				- (helperSize * 2),
			),
			Point(
				0,

				super.interactionRect.top +
				(super.interactionRect.height * 0.25)
				+ margin
				+ (((super.interactionRect.height * 0.75) - (margin * 2)) *
					(1 - color.alpha))
				- helperSize,
			),
			Color(0, 0, 0, 0),
			Color(0, 0, 0, 1)
		);

		Pen.addRect(
			Rect(
				super.interactionRect.left
				+ (super.interactionRect.width * 0.75),

				super.interactionRect.top +
				(super.interactionRect.height * 0.25)
				+ margin
				+ (((super.interactionRect.height * 0.75) - (margin * 2)) *
					(1 - color.alpha))
				+ helperSize,

				super.interactionRect.width * 0.25,

				helperSize
			)
		);

		Pen.fillAxialGradient(
			Point(
				0,

				super.interactionRect.top +
				(super.interactionRect.height * 0.25)
				+ margin
				+ (((super.interactionRect.height * 0.75) - (margin * 2)) *
					(1 - color.alpha))
				+ helperSize,
			),
			Point(
				0,

				super.interactionRect.top +
				(super.interactionRect.height * 0.25)
				+ margin
				+ (((super.interactionRect.height * 0.75) - (margin * 2)) *
					(1 - color.alpha))
				+ (helperSize * 2),
			),
			Color(0, 0, 0, 1),
			Color(0, 0, 0, 0)
		);

		Pen.fillColor_(color);
		Pen.fillRect(
			Rect(
				super.interactionRect.left
				+ (super.interactionRect.width * 0.75),

				super.interactionRect.top +
				(super.interactionRect.height * 0.25)
				+ margin
				+ (((super.interactionRect.height * 0.75) - (margin * 2)) *
					(1 - color.alpha))
				- helperSize,

				super.interactionRect.width * 0.25,

				helperSize * 2
			)
		);
	}
}