GMRoundButton : GMUserView {
	var thisString = "";
	var thisStringRatio = 0.4;
	var thisOrientation = \right;
	var thisSVG;
	var thisSVGPath = "";
	var thisSVGRatio = 0.8;
	var thisSVGSize;

	var thisCurrentBackColor;
	var thisBlinkColor;

	var thisCurrentFontColor;
	var thisBlinkFontColor;

	var thisFrameRate = 60;
	var thisBlinkTime = 0.5;
	var timer = 0;

	*new {
		^super.new.init;
	}

	init {
		super.init;

		this.setEventHandler(
			QObject.mouseUpEvent, \mouseDownEvent, true);
		this.setEventHandler(
			QObject.mouseUpEvent, \mouseUpEvent, false);
		this.setEventHandler(
			QObject.mouseDblClickEvent, \mouseDownEvent, false);

		this.drawFunc_({ this.draw });
		this.mouseDownAction_({
			timer = 1;
			this.animate_(true);
		});

		this.onResize_({
			if((thisSVGPath == "").not)
			{ this.resizeSVG; };
		});

		this.frameRate_(thisFrameRate);

		thisBlinkColor = Color.white;
		thisBlinkFontColor = Color.black;

		thisCurrentBackColor = super.mainColor;
		thisCurrentFontColor = super.fontColor;
	}

	// Setters & Getters - we're not using <>string,
	// because we want to refresh the view
	// when a value has been updated

	// Interaction
	action_ { |aFunction|
		mouseDownAction = {
			aFunction.value;
			timer = 1;
			this.animate_(true);
		};
	}

	mouseDownAction_ { |aFunction|
		mouseDownAction = {
			this.animate_(true);
			timer = 1;
			aFunction.value;
		};
	}

	// Graphical settings
	string {
		^thisString
	}

	string_ { |aString|
		thisString = aString;
		this.refresh;
	}

	stringRatio {
		^thisStringRatio
	}

	stringRatio_ { |aFloat|
		thisStringRatio = aFloat;
		this.refresh;
	}

	orientation {
		^thisOrientation
	}

	orientation_ { |aSymbol|
		thisOrientation = aSymbol;
		this.refresh;
	}

	svgRatio {
		^thisSVGRatio
	}

	svgRatio_ { |aFloat|
		thisSVGRatio = aFloat;
		if((thisSVGPath == "").not)
		{ this.refresh; };
	}

	svg {
		^thisSVGPath
	}

	svg_ { |aPath|
		if((thisSVGPath == "").not)
		{ thisSVG.free; };
		thisSVGPath = aPath;
		thisSVG = Image.openSVG(thisSVGPath);
		thisSVGSize = Point(thisSVG.width, thisSVG.height);
		this.resizeSVG;
	}

	resizeSVG  {
		var buttonSize = min(
			super.interactionRect.width,
			super.interactionRect.height
		);
		var hRatio = buttonSize / thisSVGSize.x;
		var vRatio = buttonSize / thisSVGSize.y;

		var width;
		var height;

		thisSVG.free;

		if(hRatio < vRatio) {
			width = (buttonSize * thisSVGRatio);
			height = (thisSVGSize.y * (width / thisSVGSize.x));
			thisSVG = Image.openSVG(
				thisSVGPath,
				Size(width, height));
		} {
			height = (buttonSize * thisSVGRatio);
			width = (thisSVGSize.x * (height / thisSVGSize.y));
			thisSVG = Image.openSVG(
				thisSVGPath,
				Size(width, height));
		};

		this.refresh;
	}

	blinkTime {
		^thisBlinkTime
	}

	blinkTime_ { |aNumber|
		thisBlinkTime = aNumber;
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

		if((thisSVGPath == "").not) {

			Pen.fillColor_(super.fontColor);

			thisSVG.drawInRect(
				Rect(
					(this.bounds.width / 2) - (thisSVG.width / 2),
					(this.bounds.height / 2) - (thisSVG.height / 2),
					thisSVG.width,
					thisSVG.height
				);
			);
		};

		super.stringCenteredIn(
			thisString,
			super.interactionRect,
			super.font.deepCopy.size_(
				buttonSize * thisStringRatio
			),
			super.fontColor,
			thisOrientation
		);
	}
}