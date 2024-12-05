GMLineMultiSlider : GMZZMultiSlider {

	var thisDotsRatio = 0.2;
	var thisDrawDots = true;
	var thisJoinExtremities = false;
	var thisFillShape = true;

	var symbolPosition = 0;

	var thisDisplayValues = true;
	var thisFontRatio = 0.3;
	var thisRoundValue = 0.01;
	var thisCenterValues = false;
	var thisDisplayFunction = nil;

	var thisDrawHelpers = false;
	var thisHelpersRatio = 0.5;
	var thisHelpersStyle = \dot;
	var thisCenterHelpers = true;

	var thisDrawHighlights = false;
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
	joinExtremities {
		^thisJoinExtremities
	}

	joinExtremities_ { |aBoolean|
		thisJoinExtremities = aBoolean;
		this.refresh;
	}

	dotsRatio {
		^thisDotsRatio
	}

	dotsRatio_ { |aNumber|
		thisDotsRatio = aNumber;
		this.refresh;
	}

	drawDots {
		^thisDrawDots
	}

	drawDots_ { |aBoolean|
		^thisDrawDots
	}

	fillShape {
		^thisFillShape
	}

	fillShape_ { |aBoolean|
		thisFillShape = aBoolean;
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

	drawHelpers {
		^thisDrawHelpers
	}

	drawHelpers_ { |aBoolean|
		thisDrawHelpers = aBoolean;
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

	drawHighlights {
		^thisDrawHighlights
	}

	drawHighlights_ { |aBoolean|
		thisDrawHighlights = aBoolean;
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

		if(thisFillShape) { this.prFillShape; };

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

		if(thisDrawHighlights) { this.prDrawHighlights; };

		this.prDrawSliders;

		if(thisDrawHelpers) {
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

		// Draw the border on top of the symbol
		super.drawBorder;
	}

	prDrawSliders {
		var caseSize;
		var points = List(0);
		var dotSize;
		if(super.orientation == \horizontal)
		{ caseSize = super.interactionRect.width / super.values.size; }
		{ caseSize = super.interactionRect.height / super.values.size; };

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

				points.add(symbolPosition);
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

				points.add(symbolPosition);
			};

		});

		Pen.strokeColor_(super.mainColor);
		Pen.width_(super.outlineSize);

		if(thisJoinExtremities) {
			if(super.orientation == \horizontal) {
				Pen.line(
					Point(
						super.interactionRect.left - (caseSize / 2),
						points[points.size - 1].y
					),
					points[0]
				);
				Pen.line(
					Point(
						super.interactionRect.right + (caseSize / 2),
						points[0].y
					),
					points[points.size - 1]
				);
			} {
				Pen.line(
					Point(
						points[points.size - 1].x,
						super.interactionRect.top - (caseSize / 2),
					),
					points[0]
				);
				Pen.line(
					Point(
						points[0].x,
						super.interactionRect.bottom + (caseSize / 2),
					),
					points[points.size - 1]
				);
			};
		};

		Pen.moveTo(points[0]);
		(points.size - 1).do({ |index|
			Pen.lineTo(points[index + 1]);
		});

		Pen.stroke;

		if(thisDrawDots) {
			if(super.orientation == \horizontal) {
				dotSize = min(
					caseSize * thisDotsRatio,
					super.interactionRect.height * thisDotsRatio
				);
			} {
				dotSize = min(
					super.interactionRect.width * thisDotsRatio,
					caseSize * thisDotsRatio
				);
			};

			Pen.fillColor_(super.mainColor);

			points.do({ |point|
				Pen.addArc(
					point,
					dotSize / 2,
					0, 2pi
				);
			});

			Pen.fill;
		};
	}

	prFillShape {
		var origin = Point(0, 0);
		var end = Point(0, 0);
		var caseSize;
		var point;
		var points = List(0);

		if(super.orientation == \horizontal)
		{ caseSize = super.interactionRect.width / super.values.size; }
		{ caseSize = super.interactionRect.height / super.values.size; };

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

				points.add(symbolPosition);
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

				points.add(symbolPosition);
			};

		});

		Pen.fillColor_(super.secondColor);

		if(super.orientation == \horizontal) {
			if(thisJoinExtremities) {
				origin.x = super.interactionRect.left - (caseSize / 2);
				end.x = super.interactionRect.right + (caseSize / 2);
			} {
				origin.x = super.interactionRect.left + (caseSize / 2);
				end.x = super.interactionRect.right - (caseSize / 2);
			};

			if(super.polarity == \uni) {
				origin.y = super.interactionRect.bottom;
				end.y = super.interactionRect.bottom;
			} {
				origin.y = super.bounds.height / 2;
				end.y = super.bounds.height / 2;
			};
		} {
			if(thisJoinExtremities) {
				origin.y = super.interactionRect.top - (caseSize / 2);
				end.y = super.interactionRect.bottom + (caseSize / 2);
			} {
				origin.y = super.interactionRect.top + (caseSize / 2);
				end.y = super.interactionRect.bottom - (caseSize / 2);
			};

			if(super.polarity == \uni) {
				origin.x = super.interactionRect.left;
				end.x = super.interactionRect.left;
			} {
				origin.x = super.bounds.width / 2;
				end.x = super.bounds.width / 2;
			};
		};

		Pen.moveTo(origin);

		if(thisJoinExtremities) {
			point = Point(0, 0);
			point.x = origin.x;
			point.y = origin.y;
			if(this.orientation == \horizontal)
			{ point.y = points[points.size - 1].y; }
			{ point.x = points[points.size - 1].x; };
			Pen.lineTo(point);
		};

		points.do({ |point| Pen.lineTo(point); });

		if(thisJoinExtremities) {
			point = Point(0, 0);
			point.x = end.x;
			point.y = end.y;
			if(this.orientation == \horizontal)
			{ point.y = points[0].y; }
			{ point.x = points[0].x; };
			Pen.lineTo(point);
		};

		Pen.lineTo(end);


		Pen.fill;
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