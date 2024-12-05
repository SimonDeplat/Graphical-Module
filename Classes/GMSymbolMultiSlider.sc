GMSymbolMultiSlider : GMZZMultiSlider {

	var thisSymbol = \circle;
	var thisSymbolRatio = 0.5;
	var thisSymbolMinSize = 24;
	var thisSymbolMaxSize = 128;

	var symbolPosition = 0;
	var thisDisplayLine = \value;

	var thisDisplayValues = true;
	var thisFontRatio = 0.3;
	var thisRoundValue = 0.01;
	var thisCenterValues = false;
	var thisDisplayFunction = nil;

	var thisDisplayHelpers = false;
	var thisHelpersRatio = 0.2;

	var thisDisplayHiglights = false;
	var thisHighlights = nil;
	var thisHighlightRatio = 0.5;
	var thisBeat = -1;

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
		"GMSymbolMultiSlider: drawLine will be deprecated soon, use displayLine instead.".warn;
		^thisDisplayLine
	}

	drawLine_ { |aSymbol|
		"GMSymbolMultiSlider: drawLine will be deprecated soon, use displayLine instead.".warn;
		thisDisplayLine = aSymbol;
		this.refresh;
	}

	fontRatio {
		^thisFontRatio
	}

	fontRatio_ { |aFloat|
		thisFontRatio = aFloat;
		this.refresh;
	}

	displayValues {
		^thisDisplayValues
	}

	displayValues_ { |aBoolean|
		thisDisplayValues = aBoolean;
		this.refresh;
	}

	roundValue {
		^thisRoundValue
	}

	roundValue_ { |aNumber|
		thisRoundValue = aNumber;
		if(thisDisplayValues)
		{ this.refresh; };
	}

	centerValues {
		^thisCenterValues
	}

	centerValues_ { |aBoolean|
		thisCenterValues = aBoolean;
		this.refresh;
	}

	displayFunction {
		^thisDisplayFunction
	}

	displayFunction_ { |aFunction|
		thisDisplayFunction = aFunction;
		this.refresh;
	}

	displayHelpers {
		^thisDisplayHelpers
	}

	displayHelpers_ { |aBoolean|
		thisDisplayHelpers = aBoolean;
		this.refresh;
	}

	drawHelpers {
		"GMSymbolMultiSlider: drawHelpers will be deprecated soon, use displayHelpers instead.".warn;
		^thisDisplayHelpers
	}

	drawHelpers_ { |aBoolean|
		"GMSymbolMultiSlider: drawHelpers will be deprecated soon, use displayHelpers instead.".warn;
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

	beat {
		^thisBeat
	}

	beat_ { |anInteger|
		thisBeat = anInteger;
		{ this.refresh; }.defer;
	}

	displayHighlights {
		^thisDisplayHiglights
	}

	displayHighlights_ { |aBoolean|
		thisDisplayHiglights = aBoolean;
		this.refresh;
	}

	drawHighlights {
		"GMSymbolMultiSlider: drawHighlights will be deprecated soon, use displayHighlights instead.".warn;
		^thisDisplayHiglights
	}

	drawHighlights_ { |aBoolean|
		"GMSymbolMultiSlider: drawHighlights will be deprecated soon, use displayHighlights instead.".warn;
		thisDisplayHiglights = aBoolean;
		this.refresh;
	}

	highlights {
		^thisHighlights
	}

	highlights_ { |anArray|
		thisHighlights = anArray;
		this.refresh;
	}

	highlightRatio {
		^thisHighlightRatio
	}

	highlightRatio_ { |aNumber|
		thisHighlightRatio = aNumber;
		this.refresh;
	}

	// Custom drawFunc
	draw {
		super.drawBackground(super.backgroundColor);

		// Draw beat
		if(thisBeat != -1) {
			Pen.fillColor_(super.beatColor);
			if(super.orientation == \horizontal) {
				Pen.fillRect(
					Rect(
						super.interactionRect.left
						+ (thisBeat *
							(super.interactionRect.width / super.values.size)),
						super.interactionRect.top,
						super.interactionRect.width / super.values.size,
						super.interactionRect.height
					)
				);
			} {
				Pen.fillRect(
					Rect(
						super.interactionRect.left,
						super.interactionRect.top
						+ (thisBeat *
							(super.interactionRect.height / super.values.size)),
						super.interactionRect.width,
						super.interactionRect.height / super.values.size
					)
				);
			};
		};

		if(thisDisplayHiglights) { this.prDrawHighlights; };

		this.prDrawSliders;

		if(thisDisplayHelpers) {
			super.drawMultiDotHelpers(
				super.polarity,
				super.orientation,
				super.helpersNumber,
				super.helperSubdivisions,
				super.helpersColor,
				thisHelpersRatio,
				thisHelpersRatio / 2,
				super.values.size
			);
		};

		if(thisDisplayValues) { this.prDrawValues; };

		// Draw the border on top of the symbol
		super.drawBorder;
	}

	prDrawSliders {
		var caseSize, symbolPosition, symbolSize;

		if(super.orientation == \horizontal) {
			caseSize = super.interactionRect.width / super.values.size;
			symbolSize = min(
				super.interactionRect.height * thisSymbolRatio,
				caseSize * thisSymbolRatio
			);
		} {
			caseSize = super.interactionRect.height / super.values.size;
			symbolSize = min(
				caseSize * thisSymbolRatio,
				super.interactionRect.width * thisSymbolRatio,
			);
		};

		symbolSize = min(symbolSize, thisSymbolMaxSize);
		symbolSize = max(symbolSize, thisSymbolMinSize);

		super.values.do({ |value, index|
			if(super.orientation == \horizontal) {
				if(super.polarity == \uni) {
					symbolPosition =
					super.getYValueMapping(
						value,
						super.min,
						super.max,
						super.scale,
						super.expMin
					);
				} {
					symbolPosition =
					super.getYValueMappingBipolar(
						value,
						super.min,
						super.max,
						super.scale,
						super.expMin
					);
				};

				symbolPosition = Point(
					super.interactionRect.left
					+ (caseSize * index)
					+ (caseSize / 2),
					super.interactionRect.bottom - symbolPosition
				);
			} {
				if(super.polarity == \uni) {
					symbolPosition =
					super.getXValueMapping(
						value,
						super.min,
						super.max,
						super.scale,
						super.expMin
					);
				} {
					symbolPosition =
					super.getXValueMappingBipolar(
						value,
						super.min,
						super.max,
						super.scale,
						super.expMin
					);
				};

				symbolPosition = Point(
					super.interactionRect.left + symbolPosition,
					super.interactionRect.top
					+ (caseSize * index)
					+ (caseSize / 2),
				);
			};

			Pen.strokeColor_(super.mainColor);
			Pen.width_(super.outlineSize);

			if(thisDisplayLine != \none) {
				if(thisDisplayLine == \full) {
					if(super.orientation == \horizontal) {
						Pen.line(
							Point(
								super.interactionRect.left
								+ (caseSize / 2)
								+ (caseSize * index),
								super.interactionRect.top
							),
							Point(
								super.interactionRect.left
								+ (caseSize / 2)
								+ (caseSize * index),
								super.interactionRect.bottom
							)
						);
					} {
						Pen.line(
							Point(
								super.interactionRect.left,
								super.interactionRect.top
								+ (caseSize / 2)
								+ (caseSize * index)
							),
							Point(
								super.interactionRect.right,
								super.interactionRect.top
								+ (caseSize / 2)
								+ (caseSize * index)
							)
						);
					};
				} {
					if(super.orientation == \horizontal) {
						if(super.polarity == \uni) {
							Pen.line(
								Point(
									super.interactionRect.left
									+ (caseSize / 2)
									+ (caseSize * index),
									super.interactionRect.bottom
								),
								symbolPosition
							);
						} {
							Pen.line(
								Point(
									super.interactionRect.left
									+ (caseSize / 2)
									+ (caseSize * index),
									super.bounds.height / 2
								),
								symbolPosition
							);
						};
					} {
						if(super.polarity == \uni) {
							Pen.line(
								Point(
									super.interactionRect.left,
									super.interactionRect.top
									+ (caseSize / 2)
									+ (caseSize * index),
								),
								symbolPosition
							);
						} {
							Pen.line(
								Point(
									super.bounds.width / 2,
									super.interactionRect.top
									+ (caseSize / 2)
									+ (caseSize * index),
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
		});
	}

	prDrawValues {
		var string, caseSize;

		super.values.do({ |value, index|
			if(thisDisplayFunction.notNil) {
				string = thisDisplayFunction.value(value);
				string = string.asString;
			} {
				string = value.trunc(thisRoundValue);
				if(thisRoundValue.isKindOf(Integer))
				{ string = string.asInteger; };
				string = string.asString;
			};

			if(super.orientation == \horizontal)
			{ caseSize = super.interactionRect.width / super.values.size; }
			{ caseSize = super.interactionRect.height / super.values.size; };

			if(thisCenterValues) {
				if(super.orientation == \horizontal) {
					super.stringCenteredIn(
						string,
						Rect(
							super.interactionRect.left
							+ (caseSize * index),
							super.interactionRect.top,
							caseSize,
							super.interactionRect.height
						),
						super.font.deepCopy.size_(
							min(
								caseSize,
								super.interactionRect.height
							) * thisFontRatio
						),
						super.valueFontColor
					)
				} {
					super.stringCenteredIn(
						string,
						Rect(
							super.interactionRect.left,
							super.interactionRect.top
							+ (caseSize * index),
							super.interactionRect.width,
							caseSize
						),
						super.font.deepCopy.size_(
							min(
								super.interactionRect.width,
								caseSize
							) * thisFontRatio
						),
						super.valueFontColor
					)
				};
			} {
				// /!\ DOES NOT ALLOW INVERTED SLIDERS FOR NOW
				if(super.orientation == \horizontal) {
					super.stringCenteredIn(
						string,
						Rect(
							super.interactionRect.left
							+ (caseSize * index),
							super.interactionRect.bottom
							- min(
								caseSize,
								super.interactionRect.height
							),
							caseSize,
							min(
								caseSize,
								super.interactionRect.height
							)
						),
						super.font.deepCopy.size_(
							min(
								caseSize,
								super.interactionRect.height
							) * thisFontRatio
						),
						super.valueFontColor
					);
				} {
					super.stringCenteredIn(
						string,
						Rect(
							super.interactionRect.left,
							super.interactionRect.top
							+ (caseSize * index),
							min(
								super.interactionRect.width,
								caseSize
							),
							caseSize
						),
						super.font.deepCopy.size_(
							min(
								super.interactionRect.width,
								caseSize
							) * thisFontRatio
						),
						super.valueFontColor
					);
				};
			};
		});
	}

	prDrawHighlights {
		var caseSize, ratio;
		if(thisHighlights.notNil) {
			if(super.orientation == \horizontal) {
				caseSize =
				super.interactionRect.width / super.values.size;
			} {
				caseSize =
				super.interactionRect.height / super.values.size;
			};

			min(
				super.values.size,
				thisHighlights.size
			).do({ |index|
				if(index != thisBeat) {
					if(thisHighlights[index] > 0) {
						Pen.fillColor_(
							Color(
								super.highlightColor.red,
								super.highlightColor.green,
								super.highlightColor.blue,
								super.highlightColor.alpha
								* thisHighlights[index]
							)
						);

						ratio = thisHighlightRatio * thisHighlights[index];

						if(super.orientation == \horizontal) {
							Pen.fillRect(
								Rect(
									super.interactionRect.left
									+ (caseSize * index)
									+ (caseSize * ((1 - ratio) / 2)),
									super.interactionRect.top,
									caseSize * ratio,
									super.interactionRect.height
								);
							)
						} {
							Pen.fillRect(
								Rect(
									super.interactionRect.left,
									super.interactionRect.top
									+ (caseSize * index)
									+ (caseSize * ((1 - ratio) / 2)),
									super.interactionRect.width,
									caseSize * ratio
								);
							)
						};
					};
				};
			});
		};
	}

}