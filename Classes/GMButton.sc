GMButton : GMUserView {

	var thisString = "";
	var thisStringRatio = 0.8;
	var thisOrientation = \right;
	var thisSVG;
	var thisSVGPath = "";
	var thisSVGRatio = 0.8;
	var thisSVGSize;

	*new {
		^super.new.init;
	}

	init {
		super.init;

		this.setEventHandler(
			QObject.mouseUpEvent, \mouseDownEvent, true);
		this.setEventHandler(
			QObject.mouseUpEvent, \mouseUpEvent, true);
		this.setEventHandler(
			QObject.mouseDblClickEvent, \mouseDownEvent, false);

		this.drawFunc_({ this.draw });

		this.onResize_({
			if((thisSVGPath == "").not)
			{ this.resizeSVG; };
		});
	}

	// Setters & Getters - we're not using <>string,
	// because we want to refresh the view
	// when a value has been updated

	// Interaction
	mouseDownAction_ { |aFunction|
		mouseDownAction = aFunction;
	}

	mouseUpAction_ { |aFunction|
		mouseUpAction = aFunction;
	}

	action_ { |aFunction|
		this.mouseDownAction_(aFunction);
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
		{ this.resizeSVG; };
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
		} {
			height = (super.interactionRect.height * thisSVGRatio);
			width = (thisSVGSize.x * (height / thisSVGSize.y));
		};
		thisSVG = Image.openSVG(
			thisSVGPath,
			Size(width, height));

		this.refresh;
	}

	draw {
		super.drawFrame(super.backColor);

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