GMTextView : GMUserView {

	var thisString = "";
	var thisFontRatio = 0.8;
	var thisTextOrientation = \right;

	*new {
		^super.new.init;
	}

	init {
		super.init;

		this.setEventHandler(
			QObject.mouseUpEvent, \mouseDownEvent, false);
		this.setEventHandler(
			QObject.mouseUpEvent, \mouseUpEvent, false);
		this.setEventHandler(
			QObject.mouseDblClickEvent, \mouseDownEvent, false);

		this.drawFunc_({ this.draw; });
		super.displayBorder_(false);
		super.displayBackground_(false);
	}

	// Graphical settings
	string {
		^thisString
	}

	string_ { |aString|
		thisString = aString;
		this.refresh;
	}

	fontRatio {
		^thisFontRatio
	}

	fontRatio_ { |aFloat|
		thisFontRatio = aFloat;
		this.refresh;
	}

	orientation {
		^thisTextOrientation
	}

	orientation_ { |aSymbol|
		thisTextOrientation = aSymbol;
		this.refresh;
	}

	draw {
		var font, stringRect;

		super.drawFrame(super.backColor);

		super.stringCenteredIn(
			thisString,
			super.interactionRect,
			super.font.deepCopy.size_(
				min(
					super.interactionRect.width * thisFontRatio,
					super.interactionRect.height * thisFontRatio
				)
			),
			super.fontColor,
			thisTextOrientation
		);
	}

}