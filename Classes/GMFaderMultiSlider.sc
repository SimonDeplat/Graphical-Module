GMFaderMultiSlider : GMZZMultiSlider {

	var thisMinAlpha = 0.2;
	var thisAlpha = 0;
	var thisSlidersRatio = 0.8;
	var thisSlidersColors = nil;

	var thisDisplayValues = true;
	var thisFontRatio = 0.3;
	var thisRoundValue = 0.01;
	var thisCenterValues = true;
	var thisDisplayFunction = nil;

	var thisDisplayHelpers = false;
	var thisHelpersRatio = 1;
	var thisHelpersStyle = \line;
	var thisCenterHelpers = true;

	var thisDisplayHighlights = false;
	var thisHighlights = nil;
	var thisHighlightRatio = 0.25;
	var thisBeat = -1;

	*new {
		^super.new.init;
	}

	init {
		super.init;
		this.drawFunc_({ this.draw; });
	}

	// Graphical settings
	minAlpha {
		^thisMinAlpha
	}

	minAlpha_ { |aNumber|
		thisMinAlpha = aNumber;
		this.refresh;
	}

	slidersRatio {
		^thisSlidersRatio
	}

	slidersRatio_ { |aNumber|
		thisSlidersRatio = aNumber;
		this.refresh;
	}

	slidersColors_ { |colors|
		if(colors.isKindOf(Color)) {
			colors = [colors];
		};
		thisSlidersColors = colors;
		this.refresh;
	}

	slidersColors {
		^thisSlidersColors
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
		"GMFaderMultiSlider: drawHelpers will be deprecated soon, use displayHelpers instead.".warn;
		^thisDisplayHelpers
	}

	drawHelpers_ { |aBoolean|
		"GMFaderMultiSlider: drawHelpers will be deprecated soon, use displayHelpers instead.".warn;
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

	helpersStyle {
		^thisHelpersStyle
	}

	helpersStyle_ { |aSymbol|
		thisHelpersStyle = aSymbol;
		this.refresh;
	}

	centerHelpers {
		^thisCenterHelpers
	}

	centerHelpers_ { |aBoolean|
		thisCenterHelpers = aBoolean;
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
		^thisDisplayHighlights
	}

	displayHighlights_ { |aBoolean|
		thisDisplayHighlights = aBoolean;
		this.refresh;
	}

	drawHighlights {
		"GMFaderMultiSlider: drawHighlights will be deprecated soon, use displayHighlights instead.".warn;
		^thisDisplayHighlights
	}

	drawHighlights_ { |aBoolean|
		"GMFaderMultiSlider: drawHighlights will be deprecated soon, use displayHighlights instead.".warn;
		thisDisplayHighlights = aBoolean;
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
		var invertOrientation = \horizontal;
		if(super.orientation == \horizontal)
		{ invertOrientation = \vertical; };

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

		this.prDrawSliders;

		if(thisDisplayHighlights) { this.prDrawHighlights; };

		if(thisDisplayHelpers) {
			if(thisHelpersStyle == \line) {
				super.drawLineHelpers(
					super.polarity,
					invertOrientation,
					super.helpersNumber,
					super.helperSubdivisions,
					super.helpersColor,
					thisCenterHelpers,
					thisHelpersRatio,
					thisHelpersRatio.pow(2)
				);
			} {
				if(thisHelpersStyle == \dot) {
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
			};
		};

		if(thisDisplayValues) { this.prDrawValues; };

		super.drawBorder;
	}

	prDrawSliders {
		var caseSize;
		var thisRectSize, thisRectPos;
		if(super.orientation == \horizontal)
		{ caseSize = super.interactionRect.width / super.values.size; }
		{ caseSize = super.interactionRect.height / super.values.size; };

		super.values.do({ |value, index|

			if(super.polarity == \uni) {
				thisAlpha = value.linlin(
					super.min,
					super.max,
					thisMinAlpha,
					1
				);
			} {
				if(value.abs == super.min)
				{ thisAlpha = thisMinAlpha; } {
					if(value.abs == super.max)
					{ thisAlpha = 1; } {
						thisAlpha = value.abs.linlin(
							super.min,
							super.max,
							thisMinAlpha,
							1
						);
					};
				};
			};

			if(thisSlidersColors.isNil) {
				Pen.fillColor_(
					Color(
						super.mainColor.red,
						super.mainColor.green,
						super.mainColor.blue,
						thisAlpha
					);
				);
			} {
				Pen.fillColor_(
					Color(
						thisSlidersColors[index % thisSlidersColors.size].red,
						thisSlidersColors[index % thisSlidersColors.size].green,
						thisSlidersColors[index % thisSlidersColors.size].blue,
						thisAlpha
					);
				);
			};

			if(super.orientation == \horizontal) {
				if(super.polarity == \uni) {
					thisRectSize = super.getYValueMapping(
						value,
						super.min,
						super.max,
						super.scale,
						super.expMin
					);

					Pen.fillRect(
						Rect(
							super.interactionRect.left
							+ (caseSize * index)
							+ (caseSize * ((1 - thisSlidersRatio) / 2)),
							super.interactionRect.bottom
							- thisRectSize,
							caseSize * thisSlidersRatio,
							thisRectSize
						)
					);
				} {
					thisRectPos = super.getYValueMappingBipolar(
						value,
						super.min,
						super.max,
						super.scale,
						super.expMin
					);
					thisRectPos = super.interactionRect.height - thisRectPos;

					// Only draw if value != min
					if(value < super.min) {
						Pen.fillRect(
							Rect(
								super.interactionRect.left
								+ (caseSize * index)
								+ (caseSize * ((1 - thisSlidersRatio) / 2)),
								super.bounds.height / 2,
								caseSize * thisSlidersRatio,
								thisRectPos - (super.bounds.height / 2)
								+ super.interactionRect.top
							)
						);
					};
					if(value > super.min) {
						Pen.fillRect(
							Rect(
								super.interactionRect.left
								+ (caseSize * index)
								+ (caseSize * ((1 - thisSlidersRatio) / 2)),
								super.interactionRect.top + thisRectPos,
								caseSize * thisSlidersRatio,
								(super.bounds.height / 2)
								- (super.interactionRect.top + thisRectPos)
							)
						);
					};
				};
			} {
				if(super.polarity == \uni) {
					thisRectSize = super.getXValueMapping(
						value,
						super.min,
						super.max,
						super.scale,
						super.expMin
					);

					Pen.fillRect(
						Rect(
							super.interactionRect.left,
							super.interactionRect.top
							+ (caseSize * index)
							+ (caseSize * ((1 - thisSlidersRatio) / 2)),
							thisRectSize,
							caseSize * thisSlidersRatio
						)
					);
				} {
					thisRectPos = super.getXValueMappingBipolar(
						value,
						super.min,
						super.max,
						super.scale,
						super.expMin
					);

					// Only draw if value != min
					if(value < super.min) {
						Pen.fillRect(
							Rect(
								super.bounds.width / 2,
								super.interactionRect.top
								+ (caseSize * index)
								+ (caseSize * ((1 - thisSlidersRatio) / 2)),
								thisRectPos - (super.bounds.width / 2)
								+ super.interactionRect.top,
								caseSize * thisSlidersRatio
							)
						);
					};
					if(value > super.min) {
						Pen.fillRect(
							Rect(
								super.bounds.width / 2,
								super.interactionRect.top
								+ (caseSize * index)
								+ (caseSize * ((1 - thisSlidersRatio) / 2)),
								thisRectPos
								- (super.bounds.width / 2)
								+ super.interactionRect.left,
								caseSize * thisSlidersRatio
							)
						);
					};
				};
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
