GMXYUserView : GMUserView {

	*new {
		^super.new.init;
	}

	init {
		super.init;
	}

	///////////////////////
	//    INTERACTION    //
	///////////////////////

	// Returns the relative X position of the cursor
	getXMousePos { |mousePos, invert = false|
		mousePos = mousePos.linlin(
			super.interactionRect.left,
			super.interactionRect.right,
			0, 1
		);
		if(invert) { mousePos = 1 - mousePos; };
		^mousePos
	}

	// Returns the relative Y position of the cursor
	getYMousePos { |mousePos, invert = true|
		mousePos = mousePos.linlin(
			super.interactionRect.top,
			super.interactionRect.bottom,
			0, 1
		);
		if(invert) { mousePos = 1 - mousePos; };
		^mousePos
	}

	// Maps the X position of the cursor to a scale
	getXMouseMapping { |mousePos, min, max,
		scale, invert = false, expMin = 0.001|

		mousePos = this.getXMousePos(mousePos, invert);

		// Special cases first
		if(mousePos == 0) { mousePos = min; } {
			if(mousePos == 1) { mousePos = max; } {
				if(scale.isKindOf(Symbol)) {

					if(scale == \lin) {
						mousePos = mousePos.linlin(
							0, 1,
							min, max
						);
					};

					if(scale == \exp) {
						if(min == 0) { min = expMin; };
						mousePos = mousePos.linexp(
							0, 1,
							min, max
						);
					};

					if(scale == \log) {
						if(min == 0) { min = expMin; };
						mousePos = (1 - mousePos).linexp(
							0, 1,
							min, max
						);

						mousePos = max - mousePos;
					};
				} {
					mousePos = mousePos.lincurve(
						0, 1, min, max, scale);
				};
			};
		};

		^mousePos
	}

	// Maps the Y position of the cursor to a scale
	getYMouseMapping { |mousePos, min, max,
		scale, invert = true, expMin = 0.001|

		mousePos = this.getYMousePos(mousePos, invert);

		// Special cases first
		if(mousePos == 0) { mousePos = min; } {
			if(mousePos == 1) { mousePos = max; } {
				if(scale.isKindOf(Symbol)) {
					if(scale == \lin) {
						mousePos = mousePos.linlin(
							0, 1,
							min, max
						);
					};

					if(scale == \exp) {
						if(min == 0) { min = expMin; };
						mousePos = mousePos.linexp(
							0, 1,
							min, max
						);
					};

					if(scale == \log) {
						if(min == 0) { min = expMin; };
						mousePos = (1 - mousePos).linexp(
							0, 1,
							min, max
						);

						mousePos = max - mousePos;
					};
				} {
					mousePos = mousePos.lincurve(
						0, 1, min, max, scale);
				};
			};
		};

		^mousePos
	}

	// Maps the X position of the cursor to a bipolar scale
	getXMouseMappingBipolar { |mousePos, min, max,
		scale, invert = false, expMin = 0.001|

		mousePos = this.getXMousePos(mousePos, invert);

		// Special cases first
		if(mousePos == 0) { mousePos = max.neg; } {
			if(mousePos == 0.5) { mousePos = min; } {
				if(mousePos == 1) { mousePos = max; } {

					if(scale.isKindOf(Symbol)) {
						if(scale == \lin) {
							if(mousePos < 0.5) {
								mousePos = 0.5 - mousePos;
								mousePos = mousePos.linlin(
									0,
									0.5,
									min.neg,
									max.neg
								);
							} {
								mousePos = mousePos.linlin(
									0.5,
									1,
									min,
									max
								);
							};
						};

						if(scale == \exp) {
							if(min == 0) { min = expMin; };
							if(mousePos < 0.5) {
								mousePos = 0.5 - mousePos;
								mousePos = mousePos.linexp(
									0,
									0.5,
									min.neg,
									max.neg
								);
							} {
								mousePos = mousePos.linexp(
									0.5,
									1,
									min,
									max
								);
							};
						};

						if(scale == \log) {
							if(min == 0) { min = expMin; };
							if(mousePos < 0.5) {
								mousePos = mousePos.linexp(
									0, 0.5,
									min.neg, max.neg
								);
								mousePos = max.neg - mousePos;
							} {
								mousePos = 0.5 - (mousePos - 0.5);
								mousePos = mousePos.linexp(
									0, 0.5,
									min, max
								);
								mousePos = max - mousePos;
							};
						};

					} {
						if(mousePos < 0.5) {
							mousePos = (0.5 - mousePos) * 2;
							mousePos = mousePos.lincurve(
								0,
								1,
								min,
								max,
								scale
							).neg;
						} {
							mousePos = (mousePos - 0.5) * 2;
							mousePos = mousePos.lincurve(
								0,
								1,
								min,
								max,
								scale
							);
						};
					};

				};
			};
		};

		^mousePos
	}

	// Maps the Y position of the cursor to a bipolar scale
	getYMouseMappingBipolar { |mousePos, min, max,
		scale, invert = true, expMin = 0.001|

		mousePos = this.getYMousePos(mousePos, invert);

		// Special cases first
		if(mousePos == 0) { mousePos = max.neg; } {
			if(mousePos == 0.5) { mousePos = min; } {
				if(mousePos == 1) { mousePos = max; } {
					if(scale.isKindOf(Symbol)) {
						if(scale == \lin) {
							if(mousePos < 0.5) {
								mousePos = 0.5 - mousePos;
								mousePos = mousePos.linlin(
									0,
									0.5,
									min.neg,
									max.neg
								);
							} {
								mousePos = mousePos.linlin(
									0.5,
									1,
									min,
									max
								);
							};
						};

						if(scale == \exp) {
							if(min == 0) { min = expMin; };
							if(mousePos < 0.5) {
								mousePos = 0.5 - mousePos;
								mousePos = mousePos.linexp(
									0,
									0.5,
									min.neg,
									max.neg
								);
							} {
								mousePos = mousePos.linexp(
									0.5,
									1,
									min,
									max
								);
							};
						};

						if(scale == \log) {
							if(min == 0) { min = expMin; };
							if(mousePos < 0.5) {
								mousePos = mousePos.linexp(
									0, 0.5,
									min.neg, max.neg
								);
								mousePos = max.neg - mousePos;
							} {
								mousePos = 0.5 - (mousePos - 0.5);
								mousePos = mousePos.linexp(
									0, 0.5,
									min, max
								);
								mousePos = max - mousePos;
							};
						};

					} {
						if(mousePos < 0.5) {
							mousePos = (0.5 - mousePos) * 2;
							mousePos = mousePos.lincurve(
								0,
								1,
								min,
								max,
								scale
							).neg;
						} {
							mousePos = (mousePos - 0.5) * 2;
							mousePos = mousePos.lincurve(
								0,
								1,
								min,
								max,
								scale
							);
						};
					};
				};
			};
		};

		^mousePos
	}

	// Maps the X position of the cursor to an index,
	// usually used to detect which case has been clicked
	getXIndex { |mousePos, maxIndex, invert = false|
		var index = 0;
		var caseSize = super.interactionRect.width / maxIndex;

		mousePos = mousePos - super.interactionRect.left;

		while { (mousePos - caseSize) > 0 } {
			index = index + 1;
			mousePos = mousePos - caseSize
		};

		if(index > (maxIndex - 1)) { index = maxIndex - 1; };
		if(invert) { index = maxIndex - index - 1; };

		^index
	}

	// Maps the Y position of the cursor to an index,
	// usually used to detect which case has been clicked
	getYIndex { |mousePos, maxIndex, invert = true|
		var index = 0;
		var caseSize = super.interactionRect.height / maxIndex;

		mousePos = mousePos - super.interactionRect.top;

		while { (mousePos - caseSize) > 0 } {
			index = index + 1;
			mousePos = mousePos - caseSize
		};

		if(index > (maxIndex - 1)) { index = maxIndex - 1; };
		if(invert) { index = maxIndex - index - 1; };

		^index
	}

	// Maps the cursor position to a 2D index,
	// usually used to detect which case has been clicked
	getCase { |mousePosX, mousePosY, xMaxIndex,
		yMaxIndex, xInvert = false, yInvert = true|

		var case = Point(
			this.getXIndex(mousePosX, xMaxIndex, xInvert),
			this.getYIndex(mousePosY, yMaxIndex, yInvert)
		);

		^case
	}

	////////////////////
	//    GRAPHICS    //
	////////////////////

	// Maps a given value to an horizontal position on the widget
	// The returned value doesn't take border into account
	getXValueMapping { |value, min, max, scale, expMin = 0.001|

		if(scale.isKindOf(Symbol)) {
			if(scale == \lin) { value =
				value.linlin(
					min,
					max,
					0,
					super.interactionRect.width);
			};

			if(scale == \exp) {
				if(value == min) { value = 0; } {
					if(min == 0) { min = expMin; };
					value = value.explin(
						min,
						max,
						0,
						super.interactionRect.width
				); };
			};

			if(scale == \log) {
				if(value == min) { value = 0; } {
					if(min == 0) { min = expMin; };
					value = max - value;
					value = value.explin(
						min,
						max,
						super.interactionRect.width,
						0
					);
				};
			};
		} { // elif value is a number
			value = value.curvelin(
				min,
				max,
				0,
				super.interactionRect.width,
				scale
			);
		};

		^value;
	}

	// Maps a given value to a vertical position on the widget
	// The returned value doesn't take border into account
	getYValueMapping { |value, min, max, scale, expMin = 0.001|

		if(scale.isKindOf(Symbol)) {
			if(scale == \lin) {
				value = value.linlin(
					min,
					max,
					0,
					super.interactionRect.height
			); };

			if(scale == \exp) {
				if(value == min)
				{ value = 0; } {
					if(min == 0) { min = expMin; };
					value = value.explin(
						min,
						max,
						0,
						super.interactionRect.height
				); };
			};

			if(scale == \log) {
				if(value == min) { value = 0; } {
					if(min == 0) { min = expMin; };
					value = max - value;
					value = value.explin(
						min,
						max,
						super.interactionRect.height,
						0
					);
				};
			};
		} { // elif value is a number
			value = value.curvelin(
				min,
				max,
				0,
				super.interactionRect.height,
				scale
			);
		};

		^value;
	}

	// Maps a given bipolar value to an horizontal position on the widget
	// The returned value doesn't take border into account
	getXValueMappingBipolar { |value, min, max, scale, expMin = 0.001|

		// Special cases first
		if(value == max.neg) { value = 0; } {
			if((value == min) or: { value == min.neg })
			{ value = super.interactionRect.width / 2; } {
				if(value == max)
				{ value = super.interactionRect.width; } {

					if(scale.isKindOf(Symbol)) {
						if(scale == \lin) {
							if(value < min) {
								value = value.abs.linlin(
									min,
									max,
									0,
									super.interactionRect.width / 2;
								);
								value = (super.interactionRect.width / 2) - value;
							} {
								value = value.linlin(
									min,
									max,
									0,
									super.interactionRect.width / 2;
								);
								value =
								(super.interactionRect.width / 2) + value;
							};
						};

						if(scale == \exp) {
							if(min == 0) { min = expMin; };
							if(value < 0) {
								value = value.abs;
								value = value.explin(
									min,
									max,
									0,
									super.interactionRect.width / 2
								);
								value =
								(super.interactionRect.width / 2) - value;
							} {
								value = value.explin(
									min,
									max,
									super.interactionRect.width / 2,
									super.interactionRect.width
								);
							};
						};

						if(scale == \log) {
							if(min == 0) { min = expMin; };
							// Redondancy here ?
							if(value < 0) {
								value = value.abs;
								value = max - value;
								value = value.explin(
									min,
									max,
									0,
									super.interactionRect.width / 2
								);
								value =
								(super.interactionRect.width / 2
									- value);
								value =
								(super.interactionRect.width / 2)
								- value;
							} {
								value = (max - value).explin(
									min,
									max,
									0,
									super.interactionRect.width / 2
								);
								value =
								(super.interactionRect.width / 2 - value);
								value =
								(super.interactionRect.width / 2) + value;
							};
						};
					} { // elif value is a number
						if(value < min) {
							value = value.abs.curvelin(
								min,
								max,
								0,
								super.interactionRect.width / 2,
								scale
							);

							value =
							(super.interactionRect.width / 2) - value;
						} {
							value = value.abs.curvelin(
								min,
								max,
								super.interactionRect.width / 2,
								super.interactionRect.width,
								scale
							);
						};
					};
				};
			};
		};

		^value
	}

	// Maps a given bipolar value to a vertical position on the widget
	// The returned value doesn't take border into account
	getYValueMappingBipolar { |value, min, max, scale, expMin = 0.001|

		// Special cases first
		if(value == max.neg) { value = 0; } {
			if((value == min) or: { value == min.neg })
			{ value = super.interactionRect.height / 2; } {
				if(value == max)
				{ value = super.interactionRect.height; } {

					if(scale.isKindOf(Symbol)) {
						if(scale == \lin) {
							if(value < min) {
								value = value.abs.linlin(
									min,
									max,
									0,
									super.interactionRect.height / 2;
								);
								value =
								(super.interactionRect.height / 2) - value;
							} {
								value = value.linlin(
									min,
									max,
									0,
									super.interactionRect.height / 2;
								);
								value =
								(super.interactionRect.height / 2) + value;
							};
						};

						if(scale == \exp) {
							if(min == 0) { min = expMin; };
							if(value < 0) {
								value = value.abs;
								value = value.explin(
									min,
									max,
									0,
									super.interactionRect.height / 2
								);
								value =
								(super.interactionRect.height / 2) - value;
							} {
								value = value.explin(
									min,
									max,
									super.interactionRect.height / 2,
									super.interactionRect.height
								);
							};
						};

						if(scale == \log) {
							if(min == 0) { min = expMin; };
							// Redondancy here ?
							if(value < 0) {
								value = value.abs;
								value = max - value;
								value = value.explin(
									min,
									max,
									0,
									super.interactionRect.height / 2
								);
								value =
								(super.interactionRect.height / 2
									- value);
								value =
								(super.interactionRect.height / 2)
								- value;
							} {
								value = (max - value).explin(
									min,
									max,
									0,
									super.interactionRect.height / 2
								);
								value =
								(super.interactionRect.height / 2 - value);
								value =
								(super.interactionRect.height / 2) + value;
							};
						};
					} { // elif value is a number
						if(value < min) {
							value = value.abs.curvelin(
								min,
								max,
								0,
								super.interactionRect.height / 2,
								scale
							);

							value =
							(super.interactionRect.height / 2) - value;
						} {
							value = value.abs.curvelin(
								min,
								max,
								super.interactionRect.height / 2,
								super.interactionRect.height,
								scale
							);
						};
					};
				};
			};
		};

		^value
	}

	/////////////////////////////
	//    GRAPHIC FUNCTIONS    //
	/////////////////////////////

	drawCircle { |position, symbolSize|
		Pen.addArc(
			position,
			(symbolSize / 2),
			0, 2pi
		);
		Pen.fill;

		if(super.outlineSize > 0) {
			Pen.addArc(
				position,
				(symbolSize / 2) -
				(super.outlineSize * 2),
				0, 2pi
			);
			Pen.stroke;
		};
	}

	drawRect { |position, symbolWidth, symbolHeight|
		Pen.fillRect(
			Rect(
				position.x - (symbolWidth / 2),
				position.y - (symbolHeight / 2),
				symbolWidth, symbolHeight
			)
		);

		Pen.addRect(
			Rect(
				position.x - (symbolWidth / 2) + (super.outlineSize * 2),
				position.y - (symbolHeight / 2) + (super.outlineSize * 2),
				symbolWidth - (super.outlineSize * 4),
				symbolHeight - (super.outlineSize * 4)
			)
		);
		Pen.stroke;
	}

	drawDiamond { |position, symbolSize|
		Pen.moveTo(position - Point(symbolSize / 2, 0));
		Pen.lineTo(position - Point(0, symbolSize / 2));
		Pen.lineTo(position + Point(symbolSize / 2, 0));
		Pen.lineTo(position + Point(0, symbolSize / 2));
		Pen.fill;

		Pen.moveTo(position -
			Point(symbolSize / 2 - (super.outlineSize * 2), 0));
		Pen.lineTo(position -
			Point(0, symbolSize / 2 - (super.outlineSize * 2)));
		Pen.lineTo(position +
			Point(symbolSize / 2 - (super.outlineSize * 2), 0));
		Pen.lineTo(position +
			Point(0, symbolSize / 2 - (super.outlineSize * 2)));
		Pen.lineTo(position -
			Point(symbolSize / 2 - (super.outlineSize * 2), 0));
		Pen.stroke;
	}

	drawLineHelpers { |polarity = \uni, orientation = \horizontal,
		helpersNumber, helperSubdivisions,
		color = nil, center = true,
		helperRatio = 1, subRatio = 1|

		var nHelpers =
		((helpersNumber - 1) * (helperSubdivisions + 1)) + 1;

		if(polarity == \bi)
		{ nHelpers = (nHelpers * 2) - 1; };

		if(color.isNil) { color = Color(1, 1, 1, 0.5); };
		Pen.strokeColor_(color);

		if(orientation == \horizontal) {
			var caseSize = super.interactionRect.width / (nHelpers - 1);
			nHelpers.do({ |index|
				var helperSize;

				if((index % (helperSubdivisions + 1) == 0)) {
					helperSize =
					super.interactionRect.height * helperRatio;
					Pen.width_(super.outlineSize);
				} {
					helperSize = super.interactionRect.height * subRatio;
					Pen.width_(
						max(
							(super.outlineSize / 2),
							1
						);
					);
				};

				if(center) {
					Pen.line(
						Point(
							super.interactionRect.left +
							(caseSize * index),
							(super.bounds.height / 2)
							- (helperSize / 2)
						),
						Point(
							super.interactionRect.left +
							(caseSize * index),
							(super.bounds.height / 2)
							+ (helperSize / 2)
						)
					);
				} {
					Pen.line(
						Point(
							super.interactionRect.left +
							(caseSize * index),
							super.interactionRect.bottom
							- helperSize
						),
						Point(
							super.interactionRect.left +
							(caseSize * index),
							super.interactionRect.bottom
						)
					);
				};

				Pen.stroke;
			});
		} {
			var caseSize = super.interactionRect.height / (nHelpers - 1);
			nHelpers.do({ |index|
				var helperSize;

				if((index % (helperSubdivisions + 1) == 0)) {
					helperSize = super.interactionRect.width * helperRatio;
					Pen.width_(super.outlineSize);
				} {
					helperSize = super.interactionRect.width * subRatio;
					Pen.width_(
						max(
							(super.outlineSize / 2),
							1
						);
					);
				};

				if(center) {
					Pen.line(
						Point(
							(super.bounds.width / 2)
							- (helperSize / 2),
							super.interactionRect.top +
							(caseSize * index)

						),
						Point(
							(super.bounds.width / 2)
							+ (helperSize / 2),
							super.interactionRect.top +
							(caseSize * index)
						)
					);
				} {
					Pen.line(
						Point(
							super.interactionRect.left,
							super.interactionRect.top +
							(caseSize * index)
						),
						Point(
							super.interactionRect.left
							+ helperSize,
							super.interactionRect.top +
							(caseSize * index)
						)
					);
				};

				Pen.stroke;
			});

		};

	}

	drawDotHelpers { |polarity = \uni, orientation = \horizontal,
		helpersNumber, helperSubdivisions,
		color = nil, helperRatio = 1, subRatio = 1|

		var nHelpers =
		((helpersNumber - 1) * (helperSubdivisions + 1)) + 1;

		if(polarity == \bi)
		{ nHelpers = (nHelpers * 2) - 1; };

		if(color.isNil) { color = Color(1, 1, 1, 0.5); };
		Pen.fillColor_(color);

		if(orientation == \horizontal) {
			var caseSize = super.interactionRect.width / (nHelpers - 1);
			nHelpers.do({ |index|
				var helperSize;

				if((index % (helperSubdivisions + 1) == 0)) {
					helperSize = min(
						super.interactionRect.height * helperRatio,
						super.interactionRect.width / (nHelpers + 2)
					);

				} {
					helperSize = min(
						super.interactionRect.height * subRatio,
						(super.interactionRect.width / (nHelpers + 2))
						* (helperRatio / subRatio).reciprocal
					);
				};

				Pen.addArc(
					Point(
						super.interactionRect.left +
						(caseSize * index),
						(super.bounds.height / 2)
					),
					helperSize / 2,
					0, 2pi
				);

				Pen.fill;
			});
		} {
			var caseSize = super.interactionRect.height / (nHelpers - 1);
			nHelpers.do({ |index|
				var helperSize;

				if((index % (helperSubdivisions + 1) == 0)) {
					helperSize = min(
						super.interactionRect.width * helperRatio,
						super.interactionRect.height / (nHelpers + 2)
					);

				} {
					helperSize = min(
						super.interactionRect.width * subRatio,
						(super.interactionRect.height / (nHelpers + 2))
						* (helperRatio / subRatio).reciprocal
					);
				};

				Pen.addArc(
					Point(
						(super.bounds.width / 2),
						super.interactionRect.top +
						(caseSize * index)
					),
					helperSize / 2,
					0, 2pi
				);

				Pen.fill;
			});
		};
	}

	drawMultiDotHelpers { |polarity = \uni, orientation = \horizontal,
		helpersNumber, helperSubdivisions,
		color = nil, helperRatio = 1, subRatio = 1, nCases = 1|

		var nHelpers =
		((helpersNumber - 1) * (helperSubdivisions + 1)) + 1;

		if(polarity == \bi)
		{ nHelpers = (nHelpers * 2) - 1; };

		if(color.isNil) { color = Color(1, 1, 1, 0.5); };
		Pen.fillColor_(color);

		if(orientation == \vertical) {
			var caseSize = super.interactionRect.width / (nHelpers - 1);
			var caseSize2 = super.interactionRect.height / nCases;

			nHelpers.do({ |index|
				var helperSize;

				if((index % (helperSubdivisions + 1) == 0)) {
					helperSize = min(
						(super.interactionRect.height / nCases) * helperRatio,
						super.interactionRect.width / (nHelpers + 2)
					);

				} {
					helperSize = min(
						(super.interactionRect.height / nCases) * subRatio,
						(super.interactionRect.width / (nHelpers + 2))
						* (helperRatio / subRatio).reciprocal
					);
				};

				nCases.do({ |index2|
					Pen.addArc(
						Point(
							super.interactionRect.left +
							(caseSize * index),
							super.interactionRect.top
							+ (caseSize2 / 2)
							+ (index2 * caseSize2)
						),
						(helperSize / 2),
						0, 2pi
					);
				});

				Pen.fill;
			});
		} {
			var caseSize = super.interactionRect.height / (nHelpers - 1);
			var caseSize2 = super.interactionRect.width / nCases;

			nHelpers.do({ |index|
				var helperSize;

				if((index % (helperSubdivisions + 1) == 0)) {
					helperSize = min(
						(super.interactionRect.width / nCases) * helperRatio,
						super.interactionRect.height / (nHelpers + 2)
					);

				} {
					helperSize = min(
						(super.interactionRect.width / nCases) * subRatio,
						(super.interactionRect.height / (nHelpers + 2))
						* (helperRatio / subRatio).reciprocal
					);
				};

				nCases.do({ |index2|
					Pen.addArc(
						Point(
							super.interactionRect.left
							+ (caseSize2 / 2)
							+ (index2 * caseSize2),
							super.interactionRect.top +
							(caseSize * index)
						),
						helperSize / 2,
						0, 2pi
					);
				});

				Pen.fill;
			});
		};
	}

}