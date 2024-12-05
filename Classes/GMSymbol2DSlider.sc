GMSymbol2DSlider : GMZZ2DSlider {
	var thisSymbol = \circle;
	var thisSymbolRatio = 0.1;
	var thisSymbolMinSize = 24;
	var thisSymbolMaxSize = 96;

	var thisDisplayLine = false;

	var thisDisplayValues = true;
	var thisFontRatio = 0.1;
	var thisRoundValues;
	var thisXDisplayFunction = nil;
	var thisYDisplayFunction = nil;

	var thisHelpersStyle = \line;
	var thisDisplayHelpers = false;
	var thisHelpersRatio = 0.2;
	var thisCenterHelpers;

	var thisSVGPath = "";
	var thisSVGSize = nil;
	var thisSVGRatio = 0.8;
	var thisIMG = nil;

	*new {
		^super.new.init;
	}

	init {
		super.init;
		this.drawFunc_({ this.draw; });
		thisCenterHelpers = [false, false];
		thisRoundValues = [0.01, 0.01];

		this.onResize_(
			FunctionList()
			.addFunc({ this.prResizeSVG; })
		);

		this.onClose_(
			FunctionList()
			.addFunc({ this.free; })
		);
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

	displayLine_ { |aBoolean|
		thisDisplayLine = aBoolean;
		this.refresh;
	}

	drawLine {
		"GMSymbol2DSlider: drawLine will be deprecated soon, use displayLine instead.".warn;
		^thisDisplayLine
	}

	drawLine_ { |aBoolean|
		"GMSymbol2DSlider: drawLine will be deprecated soon, use displayLine instead.".warn;
		thisDisplayLine = aBoolean;
		this.refresh;
	}

	fontRatio {
		^thisFontRatio
	}

	fontRatio_ { |aFloat|
		thisFontRatio = aFloat;
		if(thisDisplayValues)
		{ this.refresh; };
	}

	displayValues {
		^thisDisplayValues
	}

	displayValues_ { |aBoolean|
		thisDisplayValues = aBoolean;
		this.refresh;
	}

	roundValues {
		^thisRoundValues
	}

	roundValues_ { |anArray|
		thisRoundValues = anArray;
		if(thisDisplayValues)
		{ this.refresh; };
	}

	xDisplayFunction {
		^thisXDisplayFunction
	}

	xDisplayFunction_ { |aFunction|
		thisXDisplayFunction = aFunction;
		if(thisDisplayValues)
		{ this.refresh; };
	}

	yDisplayFunction {
		^thisYDisplayFunction
	}

	yDisplayFunction_ { |aFunction|
		thisYDisplayFunction = aFunction;
		if(thisDisplayValues)
		{ this.refresh; };
	}

	helpersStyle {
		^thisHelpersStyle
	}

	helpersStyle_ { |aBoolean|
		thisHelpersStyle = aBoolean;
		if(thisDisplayHelpers)
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
		"GMSymbol2DSlider: drawHelpers will be deprecated soon, use displayHelpers instead.".warn;
		^thisDisplayHelpers
	}

	drawHelpers_ { |aBoolean|
		"GMSymbol2DSlider: drawHelpers will be deprecated soon, use displayHelpers instead.".warn;
		thisDisplayHelpers = aBoolean;
		this.refresh;
	}

	helpersRatio {
		^thisHelpersRatio
	}

	helpersRatio_ { |aNumber|
		thisHelpersRatio = aNumber;
		if(thisDisplayHelpers)
		{ this.refresh; };
	}

	centerHelpers {
		^thisCenterHelpers
	}

	centerHelpers_ { |anArray|
		thisCenterHelpers = anArray;
		if(thisDisplayHelpers)
		{ this.refresh; };
	}

	svg_ { |aPath|
		if(thisIMG.notNil)
		{ this.free; };

		thisSVGPath = aPath;
		thisIMG = Image.openSVG(aPath);
		thisSVGSize = Point(
			thisIMG.width,
			thisIMG.height
		);

		this.prResizeSVG;
	}

	svgRatio {
		^thisSVGRatio
	}

	svgRatio_ { |aFloat|
		thisSVGRatio = aFloat;
		this.refresh;
	}

	free {
		if(thisIMG.notNil) {
			thisIMG.free;
			thisIMG = nil;
			this.refresh;
		};
	}

	// Custom drawFunc
	draw {
		super.drawBackground(super.backgroundColor);

		if(thisIMG.notNil) {
			thisIMG.drawAtPoint(
				Point(
					(super.bounds.width / 2)
					- (thisIMG.width / 2),
					(super.bounds.height / 2)
					- (thisIMG.height / 2)
				)
			);
		};

		this.prDrawSlider;

		if(thisDisplayHelpers) {
			if(thisHelpersStyle == \line)
			{ this.prDrawLineHelpers; };
			if(thisHelpersStyle == \dot)
			{ this.prDrawDotHelpers; };
		};

		if(thisDisplayValues) { this.prDrawValues; };

		// Draw the border on top of the symbol
		super.drawBorder;
	}

	prDrawSlider {
		var symbolPosition = Point(0, 0);

		var symbolSize = min(
			super.interactionRect.height * thisSymbolRatio,
			super.interactionRect.width * thisSymbolRatio
		);
		symbolSize = min(symbolSize, thisSymbolMaxSize);
		symbolSize = max(symbolSize, thisSymbolMinSize);

		if(super.polarities[0] == \uni) {
			symbolPosition.x =
			super.getXValueMapping(
				super.values[0],
				super.min[0],
				super.max[0],
				super.scales[0],
				super.expMins[0]
			);
		} {
			symbolPosition.x =
			super.getXValueMappingBipolar(
				super.values[0],
				super.min[0],
				super.max[0],
				super.scales[0],
				super.expMins[0]
			);
		};

		if(super.polarities[1] == \uni) {
			symbolPosition.y =
			super.getYValueMapping(
				super.values[1],
				super.min[1],
				super.max[1],
				super.scales[1],
				super.expMins[1]
			);
		} {
			symbolPosition.y =
			super.getYValueMappingBipolar(
				super.values[1],
				super.min[1],
				super.max[1],
				super.scales[1],
				super.expMins[1]
			);
		};

		symbolPosition.y = this.interactionRect.height - symbolPosition.y;

		symbolPosition = symbolPosition
		+ Point(
			super.interactionRect.left,
			super.interactionRect.top
		);

		Pen.strokeColor_(super.mainColor);
		Pen.width_(super.outlineSize);

		if(thisDisplayLine) {
			Pen.line(
				Point(symbolPosition.x, super.interactionRect.top),
				Point(symbolPosition.x, super.interactionRect.bottom)
			);
			Pen.line(
				Point(super.interactionRect.left, symbolPosition.y),
				Point(super.interactionRect.right, symbolPosition.y)
			);
			Pen.stroke;
		};

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

	prDrawValues {
		var string;
		var fontSize = min(
			super.interactionRect.width * thisFontRatio,
			super.interactionRect.height * thisFontRatio
		);

		if(thisXDisplayFunction.notNil) {
			string = thisXDisplayFunction.value(super.values[0]);
			string = string.asString;
		} {
			string = super.values[0].trunc(thisRoundValues[0]);
			if(thisRoundValues[0].isKindOf(Integer))
			{ string = string.asInteger; };
			string = string.asString;
		};

		super.stringCenteredIn(
			string,
			Rect(
				super.interactionRect.left,
				super.interactionRect.top,
				super.interactionRect.width / 4,
				super.interactionRect.height
			),
			super.font.deepCopy.size_(fontSize),
			super.valueFontColor,
		);

		if(thisYDisplayFunction.notNil) {
			string = thisYDisplayFunction.value(super.values[1]);
			string = string.asString;
		} {
			string = super.values[1].trunc(thisRoundValues[1]);
			if(thisRoundValues[1].isKindOf(Integer))
			{ string = string.asInteger; };
			string = string.asString;
		};

		super.stringCenteredIn(
			string,
			Rect(
				super.interactionRect.left,
				super.interactionRect.bottom -
				(super.interactionRect.height / 4),
				super.interactionRect.width,
				super.interactionRect.height / 4
			),
			super.font.deepCopy.size_(fontSize),
			super.valueFontColor,
		);
	}

	prDrawLineHelpers {
		var nHelpersX =
		((thisHelpersNumbers[0] - 1) * (thisHelperSubdivisions[0] + 1)) + 1;
		var nHelpersY =
		((thisHelpersNumbers[1] - 1) * (thisHelperSubdivisions[1] + 1)) + 1;
		var caseSizeX, caseSizeY;
		var helperSize;

		if(super.polarities[0] == \bi)
		{ nHelpersX = (nHelpersX * 2) - 1; };
		if(super.polarities[1] == \bi)
		{ nHelpersY = (nHelpersY * 2) - 1; };

		caseSizeX = super.interactionRect.width / (nHelpersX - 1);
		caseSizeY = super.interactionRect.height / (nHelpersY - 1);

		Pen.strokeColor_(super.helpersColor);

		nHelpersX.do({ |index|
			if((index % (thisHelperSubdivisions[0] + 1) == 0)) {
				helperSize = max(
					super.interactionRect.width * thisHelpersRatio,
					super.interactionRect.height * thisHelpersRatio
				);
				Pen.width_(super.outlineSize);
			} {
				helperSize = max(
					super.interactionRect.width * thisHelpersRatio,
					super.interactionRect.height * thisHelpersRatio
				);
				if(thisHelpersRatio < 1) { helperSize = helperSize / 2; };
				Pen.width_(
					max(
						(super.outlineSize / 2),
						1
					);
				);
			};

			if(thisCenterHelpers[0]) {
				Pen.line(
					Point(
						super.interactionRect.left +
						(caseSizeX * index),
						(super.bounds.height / 2)
						- (helperSize / 2)
					),
					Point(
						super.interactionRect.left +
						(caseSizeX * index),
						(super.bounds.height / 2)
						+ (helperSize / 2)
					)
				);
			} {
				Pen.line(
					Point(
						super.interactionRect.left +
						(caseSizeX * index),
						super.interactionRect.bottom
						- helperSize
					),
					Point(
						super.interactionRect.left +
						(caseSizeX * index),
						super.interactionRect.bottom
					)
				);
			};

			Pen.stroke;
		});

		nHelpersY.do({ |index|
			if((index % (thisHelperSubdivisions[1] + 1) == 0)) {
				helperSize = max(
					super.interactionRect.width * thisHelpersRatio,
					super.interactionRect.height * thisHelpersRatio
				);
				Pen.width_(super.outlineSize);
			} {
				helperSize = max(
					super.interactionRect.width * thisHelpersRatio,
					super.interactionRect.height * thisHelpersRatio
				);
				if(thisHelpersRatio < 1) { helperSize = helperSize / 2; };
				Pen.width_(
					max(
						(super.outlineSize / 2),
						1
					);
				);
			};

			if(thisCenterHelpers[1]) {
				Pen.line(
					Point(
						(super.bounds.width / 2)
						- (helperSize / 2),
						super.interactionRect.top +
						(caseSizeY * index)

					),
					Point(
						(super.bounds.width / 2)
						+ (helperSize / 2),
						super.interactionRect.top +
						(caseSizeY * index)
					)
				);
			} {
				Pen.line(
					Point(
						super.interactionRect.left,
						super.interactionRect.top +
						(caseSizeY * index)
					),
					Point(
						super.interactionRect.left
						+ helperSize,
						super.interactionRect.top +
						(caseSizeY * index)
					)
				);
			};

			Pen.stroke;
		});
	}

	prDrawDotHelpers {
		var nHelpersX =
		((thisHelpersNumbers[0] - 1) * (thisHelperSubdivisions[0] + 1)) + 1;
		var nHelpersY =
		((thisHelpersNumbers[1] - 1) * (thisHelperSubdivisions[1] + 1)) + 1;
		var caseSizeX, caseSizeY;
		var helperSize, thisHelperSize;

		if(super.polarities[0] == \bi)
		{ nHelpersX = (nHelpersX * 2) - 1; };
		if(super.polarities[1] == \bi)
		{ nHelpersY = (nHelpersY * 2) - 1; };

		caseSizeX = super.interactionRect.width / (nHelpersX - 1);
		caseSizeY = super.interactionRect.height / (nHelpersY - 1);

		Pen.fillColor_(super.helpersColor);

		helperSize = min(
			caseSizeX * thisHelpersRatio,
			caseSizeY * thisHelpersRatio
		);

		nHelpersX.do({ |xIndex|
			nHelpersY.do({ |yIndex|
				thisHelperSize = helperSize;
				if((xIndex % (thisHelperSubdivisions[0] + 1) == 0).not
					or: { (yIndex % (thisHelperSubdivisions[1] + 1) == 0).not; })
				{ thisHelperSize = thisHelperSize / 2; };

				Pen.addArc(
					Point(
						super.interactionRect.left
						+ (caseSizeX * xIndex),
						super.interactionRect.top
						+ (caseSizeY * yIndex)
					),
					thisHelperSize / 2,
					0, 2pi
				);
			});
		});

		Pen.fill;
	}

	prResizeSVG {
		var hRatio, vRatio;
		var width, height;
		if(thisIMG.notNil) {
			hRatio = super.interactionRect.width / thisSVGSize.x;
			vRatio = super.interactionRect.height / thisSVGSize.y;

			thisIMG.free;

			if(hRatio < vRatio) {
				width = (super.interactionRect.width * thisSVGRatio);
				height = (thisSVGSize.y * (width / thisSVGSize.x));
			} {
				height = (super.interactionRect.height * thisSVGRatio);
				width = (thisSVGSize.x * (height / thisSVGSize.y));
			};
			thisIMG = Image.openSVG(
				thisSVGPath,
				Size(width, height)
			);

			this.refresh;
		};
	}
}
