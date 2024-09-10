GMSymbolSlider : GMZZSlider {

	var thisSymbol = \circle;
	var thisSymbolRatio = 0.8;
	var thisSymbolMinSize = 24;
	var thisSymbolMaxSize = 96;

	var symbolPosition = 0;
	var thisDisplayLine = \full;

	var thisDisplayValue = true;
	var thisFontRatio = 0.3;
	var thisRoundValue = 0.01;
	var thisCenterValue = true;
	var thisDisplayFunction = nil;

	var thisDisplayHelpers = false;
	var thisHelpersRatio = 0.2;

	*new {
		^super.new.init;
	}

	init {
		super.init;
		this.drawFunc_({ this.draw; });
	}

	// Graphical settings
	symbol {
		^thisSymbol
	}

	symbol_ { |aSymbol|
		thisSymbol = aSymbol;
		this.refresh;
	}

	symbolRatio {
		^thisSymbolRatio
	}

	symbolRatio_ { |aNumber|
		thisSymbolRatio = aNumber;
		this.refresh;
	}

	symbolMinSize {
		^thisSymbolMinSize
	}

	symbolMinSize_ { |aNumber|
		thisSymbolMinSize = aNumber;
		this.refresh;
	}

	symbolMaxSize {
		^thisSymbolMaxSize
	}

	symbolMaxSize_ { |aNumber|
		thisSymbolMaxSize = aNumber;
		this.refresh;
	}

	displayLine {
		^thisDisplayLine
	}

	displayLine_ { |aSymbol|
		thisDisplayLine = aSymbol;
		this.refresh;
	}

	drawLine {
		"GMSymbolSlider: drawLine will be deprecated soon, use displayLine instead.".warn;
		^thisDisplayLine
	}

	drawLine_ { |aSymbol|
		"GMSymbolSlider: drawLine will be deprecated soon, use displayLine instead.".warn;
		thisDisplayLine = aSymbol;
		this.refresh;
	}

	fontRatio {
		^thisFontRatio
	}

	fontRatio_ { |aFloat|
		thisFontRatio = aFloat;
		if(thisDisplayValue)
		{ this.refresh; };
	}

	displayValue {
		^thisDisplayValue
	}

	displayValue_ { |aBoolean|
		thisDisplayValue = aBoolean;
		this.refresh;
	}

	roundValue {
		^thisRoundValue
	}

	roundValue_ { |aNumber|
		thisRoundValue = aNumber;
		if(thisDisplayValue)
		{ this.refresh; };
	}

	centerValue {
		^thisCenterValue
	}

	centerValue_ { |aBoolean|
		thisCenterValue = aBoolean;
		if(thisDisplayValue)
		{ this.refresh; };
	}

	displayFunction {
		^thisDisplayFunction
	}

	displayFunction_ { |aFunction|
		thisDisplayFunction = aFunction;
		if(thisDisplayValue)
		{ this.refresh; };
	}

	displayHelpers {
		^thisDisplayHelpers
	}

	displayHelpers_ { |aBoolean|
		thisDisplayHelpers = aBoolean;
		this.refresh;
	}

	drawHelpers {
		"GMSymbolSlider: drawHelpers will be deprecated soon, use displayHelpers instead.".warn;
		^thisDisplayHelpers
	}

	drawHelpers_ { |aBoolean|
		"GMSymbolSlider: drawHelpers will be deprecated soon, use displayHelpers instead.".warn;
		thisDisplayHelpers = aBoolean;
		this.refresh;
	}

	helpersRatio {
		^thisHelpersRatio
	}

	helpersRatio_ { |aNumber|
		thisHelpersRatio = aNumber;
		this.refresh;
	}

	// Custom drawFunc
	draw {
		super.drawBackground(super.backgroundColor);

		this.prDrawSlider;

		if(thisDisplayHelpers) {
			super.drawDotHelpers(
				super.polarity,
				super.orientation,
				super.helpersNumber,
				super.helperSubdivisions,
				super.helpersColor,
				thisHelpersRatio,
				thisHelpersRatio / 2
			);
		};

		if(thisDisplayValue) { this.prDrawValue; };

		// Draw the border on top of the symbol
		super.drawBorder;
	}

	prDrawSlider {
		var symbolSize = min(
			super.interactionRect.height * thisSymbolRatio,
			super.interactionRect.width * thisSymbolRatio
		);
		symbolSize = min(symbolSize, thisSymbolMaxSize);
		symbolSize = max(symbolSize, thisSymbolMinSize);

		if(super.orientation == \horizontal) {
			if(super.polarity == \uni) {
				symbolPosition =
				super.getXValueMapping(
					super.value,
					super.min,
					super.max,
					super.scale,
					super.expMin
				);
			} {
				symbolPosition =
				super.getXValueMappingBipolar(
					super.value,
					super.min,
					super.max,
					super.scale,
					super.expMin
				);
			};

			symbolPosition = Point(
				super.interactionRect.left + symbolPosition,
				super.bounds.height / 2
			);
		} {
			if(super.polarity == \uni) {
				symbolPosition =
				super.getYValueMapping(
					super.value,
					super.min,
					super.max,
					super.scale,
					super.expMin
				);
			} {
				symbolPosition =
				super.getYValueMappingBipolar(
					super.value,
					super.min,
					super.max,
					super.scale,
					super.expMin
				);
			};

			symbolPosition = Point(
				super.bounds.width / 2,
				super.interactionRect.bottom - symbolPosition
			);
		};

		Pen.strokeColor_(super.mainColor);
		Pen.width_(super.outlineSize);

		if(thisDisplayLine != \none) {
			if(thisDisplayLine == \full) {
				if(super.orientation == \horizontal) {
					Pen.line(
						Point(
							super.interactionRect.left,
							super.bounds.height / 2
						),
						Point(
							super.interactionRect.right,
							super.bounds.height / 2
						)
					);
				} {
					Pen.line(
						Point(
							super.bounds.width / 2,
							super.interactionRect.top
						),
						Point(
							super.bounds.width / 2,
							super.interactionRect.bottom
						)
					);
				};
			} {
				if(super.orientation == \horizontal) {
					if(super.polarity == \uni) {
						Pen.line(
							Point(
								super.interactionRect.left,
								super.bounds.height / 2
							),
							symbolPosition
						);
					} {
						Pen.line(
							Point(
								super.bounds.width / 2,
								super.bounds.height / 2
							),
							symbolPosition
						);
					};
				} {
					if(super.polarity == \uni) {
						Pen.line(
							Point(
								super.bounds.width / 2,
								super.interactionRect.bottom
							),
							symbolPosition
						);
					} {
						Pen.line(
							Point(
								super.bounds.width / 2,
								super.bounds.height / 2
							),
							symbolPosition
						);
					};
				};
			};
		};

		Pen.stroke;

		Pen.fillColor_(super.mainColor);
		Pen.strokeColor_(super.backgroundColor);

		if(thisSymbol == \circle)
		{ super.drawCircle(symbolPosition, symbolSize) };
		if(thisSymbol == \diamond)
		{ super.drawDiamond(symbolPosition, symbolSize) };
		if(thisSymbol == \square) {
			super.drawRect(
				symbolPosition,
				symbolSize,
				symbolSize)
		};
	}

	prDrawValue {
		var string;

		if(thisDisplayFunction.notNil) {
			string = thisDisplayFunction.value(super.value);
			string = string.asString;
		} {
			string = super.value.trunc(thisRoundValue);
			if(thisRoundValue.isKindOf(Integer))
			{ string = string.asInteger; };
			string = string.asString;
		};

		if(thisCenterValue) {
			super.stringCenteredIn(
				string,
				super.interactionRect,
				super.font.deepCopy.size_(
					min(
						super.interactionRect.width,
						super.interactionRect.height
					) * thisFontRatio
				),
				super.valueFontColor,
			)
		} {
			// /!\ DOES NOT ALLOW INVERTED SLIDERS FOR NOW
			if(super.orientation == \horizontal) {
				super.stringCenteredIn(
					string,
					Rect(
						super.interactionRect.left,
						super.interactionRect.top,
						min(
							super.interactionRect.width,
							super.interactionRect.height
						),
						min(
							super.interactionRect.width,
							super.interactionRect.height
						)
					),
					super.font.deepCopy.size_(
						min(
							super.interactionRect.width,
							super.interactionRect.height
						) * thisFontRatio
					),
					super.valueFontColor,
				);
			} {
				super.stringCenteredIn(
					string,
					Rect(
						super.interactionRect.left,
						super.interactionRect.bottom
						- min(
							super.interactionRect.width,
							super.interactionRect.height
						),
						min(
							super.interactionRect.width,
							super.interactionRect.height
						),
						min(
							super.interactionRect.width,
							super.interactionRect.height
						)
					),
					super.font.deepCopy.size_(
						min(
							super.interactionRect.width,
							super.interactionRect.height
						) * thisFontRatio
					),
					super.valueFontColor,
				);
			};
		};
	}

}