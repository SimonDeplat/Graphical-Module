GMDuoSequencer : GMXYUserView {
	var thisValues;
	var thisProbabilities;

	var thisSymbol = \circle;
	var thisSymbolRatio = 0.8;

	var thisCurrentBeat = -1;

	var thisDrawHighlights = true;
	var thisHighlights;

	var thisCurrentIndex = -1;

	var thisAllowMouseMoveAction = true;

	var thisActionMode = \values;

	*new {
		^super.new.init;
	}

	init {
		super.init;

		this.setEventHandler(
			QObject.mouseUpEvent,
			\mouseDownEvent, true);
		this.setEventHandler(
			QObject.mouseUpEvent,
			\mouseUpEvent, false);
		this.setEventHandler(
			QObject.mouseDblClickEvent,
			\mouseDownEvent, false);
		this.setEventHandler(
			QObject.mouseMoveEvent,
			\mouseMoveEvent, true);

		this.action_({});
		this.drawFunc_({ this.draw });

		thisValues = Array.fill(8, { 0 });
		thisProbabilities = [0, 0.25, 0.5, 0.75, 1];
	}

	values {
		^thisValues
	}

	values_ { |anArray|
		thisValues = anArray;
		thisValues.do({ |value, index|
			if(thisProbabilities.includesEqual(value).not)
			{ thisValues[index] = thisProbabilities[0]; };
		});
		this.refresh;
	}

	probabilities {
		^thisProbabilities
	}

	probabilities_ { |anArray|
		thisProbabilities = anArray;
		thisValues.do({ |value, index|
			if(thisProbabilities.includesEqual(value).not)
			{ thisValues[index] = thisProbabilities[0]; };
		});
		this.refresh;
	}

	beat {
		^thisCurrentBeat
	}

	beat_ { |aNumber|
		thisCurrentBeat = aNumber;
		{ this.refresh }.defer;
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
		if(thisDrawHighlights)
		{ this.refresh };
	}

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

	allowMouseMoveAction {
		^thisAllowMouseMoveAction
	}

	allowMouseMoveAction_ { |aBoolean|
		thisAllowMouseMoveAction = aBoolean;
		this.setEventHandler(
			QObject.mouseMoveEvent,
			\mouseMoveEvent, aBoolean);
	}

	actionMode {
		^thisActionMode
	}

	actionMode_ { |aSymbol|
		thisActionMode = aSymbol;
	}

	action_ { |aFunction|
		var index, xIndex, yIndex, probaMax;
		this.mouseDownAction = { |view, mousePosX, mousePosY|
			xIndex = super.getXIndex(
				mousePosX, thisValues.size);
			yIndex = super.getYIndex(
				mousePosY, 2, false);

			if(yIndex == 0) {
				probaMax = thisProbabilities[
					thisProbabilities.size - 1];
				if(thisValues[xIndex] == probaMax)
				{ thisValues[xIndex] = thisProbabilities[0]; }
				{ thisValues[xIndex] = probaMax; };
			} {
				probaMax = thisProbabilities[
					thisProbabilities.size - 1];
				if(thisValues[xIndex] == probaMax) {
					thisValues[xIndex] = thisProbabilities[1];
				} {
					index = thisProbabilities.detectIndex({ |item|
						item == thisValues[xIndex]; });
					thisValues[xIndex] = thisProbabilities[index + 1];
				};
			};

			if(thisActionMode == \values)
			{ aFunction.value(thisValues); }
			{ aFunction.value(xIndex, thisValues[xIndex]); };

			thisCurrentIndex = xIndex;
			this.refresh;
		};

		this.mouseMoveAction = { |view, mousePosX, mousePosY|
			var index, xIndex, yIndex, probaMax;
			if(thisAllowMouseMoveAction) {
				xIndex = super.getXIndex(
					mousePosX, thisValues.size);
				if(thisCurrentIndex != xIndex) {
					yIndex = super.getYIndex(
						mousePosY, 2, false);

					if(yIndex == 0) {
						probaMax = thisProbabilities[
							thisProbabilities.size - 1];
						if(thisValues[xIndex] == probaMax)
						{ thisValues[xIndex] = thisProbabilities[0]; }
						{ thisValues[xIndex] = probaMax; };
					} {
						probaMax = thisProbabilities[
							thisProbabilities.size - 1];
						if(thisValues[xIndex] == probaMax) {
							thisValues[xIndex] = thisProbabilities[1];
						} {
							index = thisProbabilities.detectIndex({ |item|
								item == thisValues[xIndex]; });
							thisValues[xIndex] = thisProbabilities[index + 1];
						};
					};

					if(thisActionMode == \values)
					{ aFunction.value(thisValues); }
					{ aFunction.value(xIndex, thisValues[xIndex]); };

					thisCurrentIndex = xIndex;
					this.refresh;
				};
			};
		};
	}

	draw {
		var caseSize = Point(
			super.interactionRect.width / thisValues.size,
			super.interactionRect.height
		);

		var symbolSize = min(
			caseSize.x * thisSymbolRatio,
			caseSize.y / 2 * thisSymbolRatio
		);
		var newSymbolSize;

		symbolSize = symbolSize - super.outlineSize;

		Pen.strokeColor_(super.outlineColor);

		super.drawFrame(super.backgroundColor);

		// Draw highlights
		if(thisDrawHighlights) {
			if(thisHighlights.notNil) {
				min(
					thisValues.size,
					thisHighlights.size
				).do({ |index|
					if(index != thisCurrentBeat) {
						if(thisHighlights[index] > 0) {
							Pen.fillColor_(
								Color(
									super.highlightColor.red
									* thisHighlights[index],
									super.highlightColor.green
									* thisHighlights[index],
									super.highlightColor.blue
									* thisHighlights[index],
									super.highlightColor.alpha
								)
							);

							Pen.fillRect(
								Rect(
									super.interactionRect.left
									+ (index * caseSize.x)
									+ (caseSize.x / 2)
									- ((caseSize.x / 2) * thisHighlights[index]),
									super.interactionRect.top,
									caseSize.x * thisHighlights[index],
									caseSize.y
								)
							);
						};
					};
				});
			};
		};

		// Draw current beat
		if(thisCurrentBeat > -1) {
			Pen.fillColor_(super.beatColor);

			Pen.fillRect(
				Rect(
					super.interactionRect.left
					+ (thisCurrentBeat * caseSize.x),
					super.interactionRect.top,
					caseSize.x,
					caseSize.y
				)
			);
		};

		// Draw symbols
		thisValues.do({ |value, index|
			Pen.width_(super.outlineSize * value);

			// Value equals 0
			if(value == 0) {
				Pen.fillColor_(Color(0.75, 0.75, 0.75));
				Pen.addArc(
					Point(
						super.interactionRect.left
						+ (index * caseSize.x)
						+ (caseSize.x / 2),
						super.bounds.height / 2
						- (caseSize.y / 4)
					), 3, 0, 2pi
				);
				Pen.addArc(
					Point(
						super.interactionRect.left
						+ (index * caseSize.x)
						+ (caseSize.x / 2),
						super.bounds.height / 2
						+ (caseSize.y / 4)
					), 3, 0, 2pi
				);
				Pen.fill;
			} { // Else equals to max probability
				if(value == thisProbabilities[thisProbabilities.size - 1]) {
					if(thisSymbol == \circle) {
						Pen.addArc(
							Point(
								super.interactionRect.left
								+ (index * caseSize.x)
								+ (caseSize.x / 2),
								super.bounds.height / 2
								- (caseSize.y / 4)
							),
							symbolSize / 2, 0, 2pi
						);
					} {
						if(thisSymbol == \square) {

							Pen.addRect(
								Rect(
									super.interactionRect.left
									+ (index * caseSize.x)
									+ (caseSize.x / 2)
									- (symbolSize / 2),
									super.bounds.height / 2
									- (caseSize.y / 4)
									- (symbolSize / 2),
									symbolSize,
									symbolSize
								)
							);
						} {
							Pen.moveTo(
								Point(
									super.interactionRect.left
									+ (index * caseSize.x)
									+ (caseSize.x / 2)
									- (symbolSize / 2),
									super.bounds.height / 2
									- (caseSize.y / 4)
								)
							);
							Pen.lineTo(
								Point(
									super.interactionRect.left
									+ (index * caseSize.x)
									+ (caseSize.x / 2),
									super.bounds.height / 2
									- (caseSize.y / 4)
									- (symbolSize / 2)
								)
							);
							Pen.lineTo(
								Point(
									super.interactionRect.left
									+ (index * caseSize.x)
									+ (caseSize.x / 2)
									+ (symbolSize / 2),
									super.bounds.height / 2
									- (caseSize.y / 4)
								)
							);
							Pen.lineTo(
								Point(
									super.interactionRect.left
									+ (index * caseSize.x)
									+ (caseSize.x / 2),
									super.bounds.height / 2
									- (caseSize.y / 4)
									+ (symbolSize / 2)
								)
							);
							Pen.lineTo(
								Point(
									super.interactionRect.left
									+ (index * caseSize.x)
									+ (caseSize.x / 2)
									- (symbolSize / 2),
									super.bounds.height / 2
									- (caseSize.y / 4)
								)
							);
						};
					};
					Pen.fillColor_(super.mainColor);
					if(super.outlineSize > 0) {
						Pen.fillStroke;
					} {
						Pen.fill;
					};
					Pen.fillColor_(Color(0.75, 0.75, 0.75));
					Pen.addArc(
						Point(
							super.interactionRect.left
							+ (index * caseSize.x)
							+ (caseSize.x / 2),
							super.bounds.height / 2
							+ (caseSize.y / 4)
						), 3, 0, 2pi
					);
					Pen.fill;
				} { // Else
					newSymbolSize = symbolSize * value;

					Pen.fillColor_(Color(0.75, 0.75, 0.75));
					Pen.addArc(
						Point(
							super.interactionRect.left
							+ (index * caseSize.x)
							+ (caseSize.x / 2),
							super.bounds.height / 2
							- (caseSize.y / 4)
						), 3, 0, 2pi
					);
					Pen.fill;

					if(thisSymbol == \circle) {
						Pen.addArc(
							Point(
								super.interactionRect.left
								+ (index * caseSize.x)
								+ (caseSize.x / 2),
								super.bounds.height / 2
								+ (caseSize.y / 4)
							),
							newSymbolSize / 2, 0, 2pi
						);
					} {
						if(thisSymbol == \square) {
							Pen.addRect(
								Rect(
									super.interactionRect.left
									+ (index * caseSize.x)
									+ (caseSize.x / 2)
									- (newSymbolSize / 2),
									super.bounds.height / 2
									+ (caseSize.y / 4)
									- (newSymbolSize / 2),
									newSymbolSize,
									newSymbolSize
								)
							);
						} {
							Pen.moveTo(
								Point(
									super.interactionRect.left
									+ (index * caseSize.x)
									+ (caseSize.x / 2)
									- (newSymbolSize / 2),
									super.bounds.height / 2
									+ (caseSize.y / 4)
								)
							);
							Pen.lineTo(
								Point(
									super.interactionRect.left
									+ (index * caseSize.x)
									+ (caseSize.x / 2),
									super.bounds.height / 2
									+ (caseSize.y / 4)
									- (newSymbolSize / 2)
								)
							);
							Pen.lineTo(
								Point(
									super.interactionRect.left
									+ (index * caseSize.x)
									+ (caseSize.x / 2)
									+ (newSymbolSize / 2),
									super.bounds.height / 2
									+ (caseSize.y / 4)
								)
							);
							Pen.lineTo(
								Point(
									super.interactionRect.left
									+ (index * caseSize.x)
									+ (caseSize.x / 2),
									super.bounds.height / 2
									+ (caseSize.y / 4)
									+ (newSymbolSize / 2)
								)
							);
							Pen.lineTo(
								Point(
									super.interactionRect.left
									+ (index * caseSize.x)
									+ (caseSize.x / 2)
									- (newSymbolSize / 2),
									super.bounds.height / 2
									+ (caseSize.y / 4)
								)
							);
						};
					};

					Pen.fillColor_(super.mainColor);
					if(super.outlineSize > 0) {
						Pen.fillStroke;
					} {
						Pen.fill;
					};
				};
			};
		});
	}
}