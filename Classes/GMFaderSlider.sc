GMFaderSlider : GMZZSlider {

	var thisMinAlpha = 0.2;
	var thisAlpha = 0;
	var thisSliderRatio = 0.8;

	var thisDisplayValue = true;
	var thisFontRatio = 0.3;
	var thisRoundValue = 0.01;
	var thisCenterValue = true;
	var thisDisplayFunction = nil;

	var thisDisplayHelpers = false;
	var thisHelpersRatio = 0.5;
	var thisHelpersStyle = \line;
	var thisCenterHelpers = false;

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

	sliderRatio {
		^thisSliderRatio
	}

	sliderRatio_ { |aNumber|
		thisSliderRatio = aNumber;
		this.refresh;
	}

	fontRatio {
		^thisFontRatio
	}

	fontRatio_ { |aFloat|
		thisFontRatio = aFloat;
		this.refresh;
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
		"GMFaderSlider: drawHelpers will be deprecated soon, use displayHelpers instead.".warn;
		^thisDisplayHelpers
	}

	drawHelpers_ { |aBoolean|
		"GMFaderSlider: drawHelpers will be deprecated soon, use displayHelpers instead.".warn;
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

	prDrawSlider {
		if(super.orientation == \horizontal) {
			if(super.polarity == \uni) {
				var thisRectSize = super.getXValueMapping(
					super.value,
					super.min,
					super.max,
					super.scale,
					super.expMin
				);

				Pen.fillRect(
					Rect(
						super.interactionRect.left,
						super.interactionRect.top +
						((super.interactionRect.height
							* (1 - thisSliderRatio)) / 2),
						thisRectSize,
						this.interactionRect.height * thisSliderRatio
					)
				);
			} {
				var thisRectPos = super.getXValueMappingBipolar(
					super.value,
					super.min,
					super.max,
					super.scale,
					super.expMin
				);

				// Only draw if value != min
				if(super.value < super.min) {
					Pen.fillRect(
						Rect(
							super.interactionRect.left + thisRectPos,
							super.interactionRect.top +
							((super.interactionRect.height
								* (1 - thisSliderRatio)) / 2),
							(this.interactionRect.width / 2)
							- thisRectPos,
							this.interactionRect.height * thisSliderRatio
						)
					);
				};
				if(super.value > super.min) {
					Pen.fillRect(
						Rect(
							super.interactionRect.left
							+ (this.interactionRect.width / 2),
							super.interactionRect.top +
							((super.interactionRect.height
								* (1 - thisSliderRatio)) / 2),
							thisRectPos
							- (this.interactionRect.width / 2),
							this.interactionRect.height * thisSliderRatio
						)
					);
				};
			};
		} {
			if(super.polarity == \uni) {
				var thisRectSize = super.getYValueMapping(
					super.value,
					super.min,
					super.max,
					super.scale,
					super.expMin
				);

				Pen.fillRect(
					Rect(
						super.interactionRect.left +
						((super.interactionRect.width
							* (1 - thisSliderRatio)) / 2),
						super.interactionRect.bottom - thisRectSize,
						super.interactionRect.width * thisSliderRatio,
						thisRectSize
					)
				);
			} {
				// /!\ Value is negated to invert axis /!\
				var thisRectPos = super.getYValueMappingBipolar(
					super.value.neg,
					super.min,
					super.max,
					super.scale,
					super.expMin
				);

				// Only draw if value != min
				if(super.value < super.min) {
					Pen.fillRect(
						Rect(
							super.interactionRect.left +
							((super.interactionRect.width
								* (1 - thisSliderRatio)) / 2),
							super.interactionRect.top + thisRectPos,
							this.interactionRect.width * thisSliderRatio,
							(this.interactionRect.height / 2)
							- thisRectPos
						)
					);
				};
				if(super.value > super.min) {
					Pen.fillRect(
						Rect(
							super.interactionRect.left +
							((super.interactionRect.width
								* (1 - thisSliderRatio)) / 2),
							super.interactionRect.top + thisRectPos,
							this.interactionRect.width * thisSliderRatio,
							(this.interactionRect.height / 2)
							- thisRectPos

						)
					);
				};
			};
		};
	}

	// Custom drawFunc
	draw {
		super.drawFrame(super.backgroundColor);

		if(super.polarity == \uni) {
			thisAlpha = super.value.linlin(
				super.min,
				super.max,
				thisMinAlpha,
				1
			);
		} {
			if(super.value.abs == super.min)
			{ thisAlpha = thisMinAlpha; } {
				if(super.value.abs == super.max)
				{ thisAlpha = 1; } {
					thisAlpha = super.value.abs.linlin(
						super.min,
						super.max,
						thisMinAlpha,
						1
					);
				};
			};
		};

		Pen.fillColor_(
			Color(
				super.mainColor.red,
				super.mainColor.green,
				super.mainColor.blue,
				thisAlpha
			);
		);

		this.prDrawSlider;

		if(thisDisplayHelpers) {
			if(thisHelpersStyle == \line) {
				super.drawLineHelpers(
					super.polarity,
					super.orientation,
					super.helpersNumber,
					super.helperSubdivisions,
					super.helpersColor,
					thisCenterHelpers,
					thisHelpersRatio,
					thisHelpersRatio.pow(2)
				);
			} {
				if(thisHelpersStyle == \dot) {
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
			};
		};

		if(thisDisplayValue) { this.prDrawValue; };
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