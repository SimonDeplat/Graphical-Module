GMListPianoRoll : GMXYUserView {
	var thisValues;
	var thisEntries;
	var thisGridColor;
	var thisGridWidth = 4;

	var thisHighlights = nil;
	var thisDrawHighlights = true;
	var thisVHighlights = nil;
	var thisDrawVHighlights = true;

	var thisActionMode = \values;
	var thisAllowMouseMoveAction = true;
	var currentPos;
	var thisCurrentBeat = -1;
	var thisBoxRatio = 0.9;

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

		thisValues = Array.fill(8, { nil });
		thisEntries = (0..7);
		thisGridColor = Color(0.5, 0.5, 0.5, 0.5);
		currentPos = Point(-1, -1);
	}

	values {
		^thisValues
	}

	values_ { |aCollection|
		thisValues = aCollection.deepCopy;
		thisValues.do({ |value, index|
			if(value.notNil) {
				if(value.isKindOf(Array)) {
					thisValues[index] = value.asList;
				};
			};
		});
		this.refresh;
	}

	entries {
		^thisEntries
	}

	entries_ { |anArray|
		thisEntries = anArray;
		// Should implement current values safety check
		this.refresh;
	}

	gridColor {
		^thisGridColor
	}

	gridColor_ { |aColor|
		thisGridColor = aColor;
		this.refresh;
	}

	gridWidth {
		^thisGridWidth
	}

	gridWidth_ { |aNumber|
		thisGridWidth = aNumber;
		this.refresh;
	}

	highlights {
		^thisHighlights
	}

	highlights_ { |anArray|
		thisHighlights = anArray;
		if(thisDrawHighlights)
		{ this.refresh; };
	}

	drawHighlights {
		^thisDrawHighlights
	}

	drawHighlights_ { |aBoolean|
		thisDrawHighlights = aBoolean;
		this.refresh;
	}

	vHighlights {
		^thisVHighlights
	}

	vHighlights_ { |anArray|
		thisVHighlights = anArray;
		if(thisDrawVHighlights)
		{ this.refresh; };
	}

	drawVHighlights {
		^thisDrawVHighlights
	}

	drawVHighlights_ { |aBoolean|
		thisDrawVHighlights = aBoolean;
		this.refresh;
	}

	actionMode {
		^thisActionMode
	}

	actionMode_ { |aSymbol|
		thisActionMode = aSymbol;
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

	boxRatio {
		^thisBoxRatio
	}

	boxRatio_ { |aFloat|
		thisBoxRatio = aFloat;
		this.refresh;
	}

	beat {
		^thisCurrentBeat
	}

	beat_ { |anInteger|
		thisCurrentBeat = anInteger;
		{ this.refresh; }.defer;
	}

	action_ { |aFunction|
		this.mouseDownAction = { |view, mousePosX, mousePosY|
			var xIndex = super.getXIndex(
				mousePosX, thisValues.size);
			var yIndex = super.getYIndex(
				mousePosY, thisEntries.size, true);
			this.prUpdateValues(xIndex, yIndex);
			if(thisActionMode == \values)
			{ aFunction.value(thisValues); }
			{ aFunction.value(xIndex, thisValues[xIndex]); };
			this.refresh;
			currentPos = Point(xIndex, yIndex);
		};

		this.mouseMoveAction = { |view, mousePosX, mousePosY|
			if(thisAllowMouseMoveAction) {
				var xIndex = super.getXIndex(
					mousePosX, thisValues.size);
				var yIndex = super.getYIndex(
					mousePosY, thisEntries.size, true);
				if(currentPos != Point(xIndex, yIndex)) {
					this.prUpdateValues(xIndex, yIndex);
					if(thisActionMode == \values)
					{ aFunction.value(thisValues); }
					{ aFunction.value(xIndex, thisValues[xIndex]); };
					this.refresh;
					currentPos = Point(xIndex, yIndex);
				};
			};
		};
	}

	prUpdateValues { |x, y|
		var entry = thisEntries[y];
		if(thisValues[x].isNil) {
			thisValues[x] = [entry].asList;
		} {
			if(thisValues[x] == [entry].asList) {
				thisValues[x] = nil;
			} {
				if(thisValues[x].includesEqual(entry)) {
					thisValues[x].remove(entry);
				} {
					thisValues[x].add(entry);
				};
			};
		};
	}

	draw {
		super.drawFrame(super.backgroundColor);

		if(thisDrawHighlights)
		{ this.prDrawHighlights; };

		if(thisDrawVHighlights)
		{ this.prDrawVHighlights; };

		if(thisCurrentBeat > -1)
		{ this.prDrawBeat; };

		this.prDrawGrid;

		this.prDrawValues;
	}

	prDrawValues {
		var caseSize = Point(
			super.interactionRect.width / thisValues.size,
			super.interactionRect.height / thisEntries.size
		);

		var ratioOffset = min(
			caseSize.x * (1 - thisBoxRatio),
			caseSize.y * (1 - thisBoxRatio)
		);

		thisValues.do({ |values, xIndex|
			if(values.notNil) {
				values.do({ |value|
					var yIndex = thisEntries.detectIndex({ |item|
						item == value; });
					Pen.addRect(
						Rect(
							super.interactionRect.left
							+ (caseSize.x * xIndex)
							+ (ratioOffset / 2)
							+ (super.outlineSize / 2),
							super.interactionRect.bottom
							- (caseSize.y * yIndex)
							- caseSize.y
							+ (ratioOffset / 2)
							+ (super.outlineSize / 2),
							caseSize.x - ratioOffset - super.outlineSize,
							caseSize.y - ratioOffset - super.outlineSize
						)
					);
				});
			};
		});

		Pen.fillColor_(super.mainColor);
		if(super.outlineSize > 0) {
			Pen.strokeColor_(super.outlineColor);
			Pen.width_(super.outlineSize);
			Pen.fillStroke;
		} {
			Pen.fill;
		};
	}

	prDrawGrid {
		var caseSize = Point(
			super.interactionRect.width / thisValues.size,
			super.interactionRect.height / thisEntries.size
		);

		(thisValues.size + 1).do({ |index|
			Pen.line(
				Point(
					super.interactionRect.left
					+ (caseSize.x * index),
					super.interactionRect.top
				),
				Point(
					super.interactionRect.left
					+ (caseSize.x * index),
					super.interactionRect.bottom
				)
			);
		});

		(thisEntries.size + 1).do({ |index|
			Pen.line(
				Point(
					super.interactionRect.left,
					super.interactionRect.top
					+ (caseSize.y * index)
				),
				Point(
					super.interactionRect.right,
					super.interactionRect.top
					+ (caseSize.y * index)
				)
			);
		});

		Pen.width_(thisGridWidth);
		Pen.strokeColor_(thisGridColor);
		Pen.stroke;
	}

	prDrawBeat {
		var caseSize = super.interactionRect.width / thisValues.size;
		Pen.fillColor_(super.beatColor);
		Pen.fillRect(
			Rect(
				super.interactionRect.left
				+ (caseSize * thisCurrentBeat),
				super.interactionRect.top,
				caseSize,
				super.interactionRect.height
			)
		);
	}

	prDrawHighlights {
		if(thisHighlights.notNil) {
			var caseSize = super.interactionRect.width / thisValues.size;
			min(
				thisHighlights.size,
				thisValues.size
			).do({ |index|
				if(index != thisCurrentBeat) {
					Pen.fillColor_(
						Color(
							super.highlightColor.red
							* thisHighlights[index],
							super.highlightColor.green
							* thisHighlights[index],
							super.highlightColor.blue
							* thisHighlights[index],
							super.highlightColor.alpha
							* thisHighlights[index]
						)
					);
					Pen.fillRect(
						Rect(
							super.interactionRect.left
							+ (caseSize * index),
							super.interactionRect.top,
							caseSize,
							super.interactionRect.height
						)
					);
				};
			});
		};
	}

	prDrawVHighlights {
		if(thisVHighlights.notNil) {
			var caseSize = super.interactionRect.height / thisEntries.size;
			min(
				thisVHighlights.size,
				thisEntries.size
			).do({ |index|
				Pen.fillColor_(
					Color(
						super.highlightColor.red
						* thisVHighlights[index],
						super.highlightColor.green
						* thisVHighlights[index],
						super.highlightColor.blue
						* thisVHighlights[index],
						super.highlightColor.alpha
						* thisVHighlights[index]
					)
				);
				Pen.fillRect(
					Rect(
						super.interactionRect.left,
						super.interactionRect.bottom
						- (caseSize * index)
						- caseSize,
						super.interactionRect.width,
						caseSize
					)
				);
			});
		};
	}
}