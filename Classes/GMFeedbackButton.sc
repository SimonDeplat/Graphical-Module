GMFeedbackButton : GMUserView {
	var thisString = "";
	var thisStringRatio = 0.8;
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

	free {
		if(thisSVGPath != "") {
			thisSVG.free;
			thisSVGPath = "";
			this.refresh;
		};
	}

	resizeSVG  {
		var hRatio = super.interactionRect.width / thisSVGSize.x;
		var vRatio = super.interactionRect.height / thisSVGSize.y;

		var width;
		var height;

		thisSVG.free;

		if(hRatio < vRatio) {
			width = (super.interactionRect.width * thisSVGRatio);
			height = (thisSVGSize.y * (width / thisSVGSize.x));
			thisSVG = Image.openSVG(
				thisSVGPath,
				Size(width, height));
		} {
			height = (super.interactionRect.height * thisSVGRatio);
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
				((thisBlinkColor.blue - super.backColor.blue) * timer)
			);

			thisCurrentFontColor = Color(
				super.fontColor.red +
				((thisBlinkFontColor.red - super.fontColor.red) * timer),
				super.fontColor.green +
				((thisBlinkFontColor.green - super.fontColor.green) * timer),
				super.fontColor.blue +
				((thisBlinkFontColor.blue - super.fontColor.blue) * timer)
			);
		};

		super.drawFrame(thisCurrentBackColor);

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
				min(
					super.interactionRect.width * thisStringRatio,
					super.interactionRect.height * thisStringRatio
				)
			),
			super.fontColor,
			thisOrientation
		);
	}
}