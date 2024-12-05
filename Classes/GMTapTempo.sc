GMTapTempo : GMUserView {
	var thisAction;
	var thisTempo = nil;
	var thisTimeBetweenClicks = nil;

	var thisString = "-";
	var thisStringRatio = 0.4;
	var thisCurrentBackColor;
	var thisBlinkColor;
	var thisCurrentFontColor;
	var thisBlinkFontColor;

	var thisFrameRate = 60;
	var thisBlinkTime = 1;
	var timer = 0;
	var timeStamps;

	*new {
		^super.new.init;
	}

	*initClass {
		// Those classes need to be compiled
		// BEFORE we can compile this particular class
		Class.initClassTree(SystemClock);
		Class.initClassTree(Date);
	}

	init {
		super.init;

		this.setEventHandler(
			QObject.mouseUpEvent, \mouseDownEvent, true);
		this.setEventHandler(
			QObject.mouseUpEvent, \mouseUpEvent, false);
		this.setEventHandler(
			QObject.mouseDblClickEvent, \mouseDownEvent, false);

		this.drawFunc_({ this.draw; });
		thisAction = {};

		this.mouseDownAction_({
			var reset, time, times;
			// This will only happen on first click
			if(timeStamps.isNil) {
				timeStamps = List(0);
				timeStamps.add([
					Date().hourStamp,
					SystemClock.seconds
				]);
				thisAction.value(nil, timeStamps);
				timer = 1;
				thisString = "-";
				this.animate_(true);
			} { // On subsequent clicks:
				reset = false;
				// Check if we need to reset tap tempo
				// -> this click not coherent with current tempo
				if(thisTimeBetweenClicks.notNil) {
					if(
						(
							SystemClock.seconds -
							timeStamps[timeStamps.size - 1][1]
						) >
						(thisTimeBetweenClicks * 2)
					) { reset = true; };
				};

				if(reset) {
					time = [
						Date().hourStamp,
						SystemClock.seconds
					];
					timeStamps.do({ |array| array.clear; });
					timeStamps.clear;
					timeStamps.add(time);

					thisAction.value(nil, timeStamps);

					thisTempo = nil;
					thisTimeBetweenClicks = nil;
					thisString = "-";

					thisBlinkTime = 1.0;
					timer = 1;
					this.animate_(true);
				} {
					// If not reset
					times = List(0);
					timeStamps.add([
						Date().hourStamp,
						SystemClock.seconds
					]);

					// Mean current tempo
					(timeStamps.size - 1).do({ |index|
						times.add(
							timeStamps[index + 1][1]
							- timeStamps[index][1]
						);
					});
					thisTimeBetweenClicks = times.mean;

					// Update tempo
					thisTempo = 60 / thisTimeBetweenClicks;

					thisAction.value(thisTempo, timeStamps);

					thisString = thisTempo.asInteger.asString;
					thisBlinkTime = thisTimeBetweenClicks;
					timer = 1;
					this.animate_(true);
				};
			};
		});

		this.frameRate_(thisFrameRate);

		thisBlinkColor = Color.white;
		thisBlinkFontColor = Color.black;

		thisCurrentBackColor = super.mainColor;
		thisCurrentFontColor = super.fontColor;

		timeStamps = nil;
	}

	// Setters & Getters - we're not using <>string,
	// because we want to refresh the view
	// when a value has been updated

	// Interaction
	action_ { |aFunction|
		thisAction = aFunction;
	}

	// Graphical settings
	stringRatio {
		^thisStringRatio
	}

	stringRatio_ { |aFloat|
		thisStringRatio = aFloat;
		this.refresh;
	}

	blinkColor {
		^thisBlinkColor
	}

	blinkColor_ { |aColor|
		thisBlinkColor = aColor;
	}

	blinkFontColor {
		^thisBlinkFontColor
	}

	blinkFontColor_ { |aColor|
		thisBlinkFontColor = aColor;
	}

	blink {
		timer = 1;
		{ this.animate_(true); }.defer;
	}



	bpm {
		^thisTempo
	}

	bpm_ { |newBPM|
		thisTempo = newBPM;
		thisString = thisTempo.asInteger.asString;
		thisBlinkTime = 60 / newBPM;
		this.refresh;
	}

	// Custom drawFunc
	draw {
		var buttonSize = min(
			this.bounds.width,
			this.bounds.height
		);

		if(timer > 0)
		{ timer = timer - ((1 / thisFrameRate) / thisBlinkTime) };

		if(timer <= 0) {
			timer = 0;
			thisCurrentBackColor = super.backColor;
			thisCurrentFontColor = super.fontColor;
			this.animate_(false);
		} {
			thisCurrentBackColor = Color(
				super.backColor.red +
				((thisBlinkColor.red - super.backColor.red) * timer),
				super.backColor.green +
				((thisBlinkColor.green - super.backColor.green) * timer),
				super.backColor.blue +
				((thisBlinkColor.blue - super.backColor.blue) * timer),
				super.backColor.alpha +
				((thisBlinkColor.alpha - super.backColor.alpha) * timer)
			);

			thisCurrentFontColor = Color(
				super.fontColor.red +
				((thisBlinkFontColor.red - super.fontColor.red) * timer),
				super.fontColor.green +
				((thisBlinkFontColor.green - super.fontColor.green) * timer),
				super.fontColor.blue +
				((thisBlinkFontColor.blue - super.fontColor.blue) * timer),
				super.fontColor.alpha +
				((thisBlinkFontColor.alpha - super.fontColor.alpha) * timer)
			);
		};

		if(super.displayBorder) {
			if(super.borderSize > 0) {
				Pen.strokeColor_(super.borderColor);
				Pen.width_(super.borderSize);
				Pen.addArc(
					Point(
						super.bounds.width / 2,
						super.bounds.height / 2
					),
					(buttonSize / 2) - (super.borderSize / 2),
					0, 2pi
				);
				Pen.stroke;
				buttonSize =
				buttonSize - (super.borderSize * 2);
			};

			if(super.secondBorderSize > 0) {
				Pen.strokeColor_(super.secondBorderColor);
				Pen.width_(super.secondBorderSize);
				Pen.addArc(
					Point(
						super.bounds.width / 2,
						super.bounds.height / 2
					),
					(buttonSize / 2) - (super.secondBorderSize / 2),
					0, 2pi
				);
				Pen.stroke;
				buttonSize =
				buttonSize - (super.secondBorderSize * 2);
			};

			if(super.thirdBorderSize > 0) {
				Pen.strokeColor_(super.thirdBorderColor);
				Pen.width_(super.thirdBorderSize);
				Pen.addArc(
					Point(
						super.bounds.width / 2,
						super.bounds.height / 2
					),
					(buttonSize / 2) - (super.thirdBorderSize / 2),
					0, 2pi
				);
				Pen.stroke;
				buttonSize =
				buttonSize - (super.thirdBorderSize * 2);
			};
		};

		Pen.fillColor_(thisCurrentBackColor);
		Pen.addArc(
			Point(
				super.bounds.width / 2,
				super.bounds.height / 2
			),
			buttonSize / 2,
			0, 2pi
		);
		Pen.fill;

		super.stringCenteredIn(
			thisString,
			super.interactionRect,
			super.font.deepCopy.size_(
				buttonSize * thisStringRatio
			),
			thisCurrentFontColor
		);
	}
}