GMGridButton : GMXYUserView {
	var thisStates;
	var thisCurrentState = 0;

	var thisOrientation = \horizontal;
	var thisDirection = \right;

	var thisAllowRetrigger = false;
	var thisAllowMouseMove = true;

	var thisFontRatio = 0.4;
	var thisUnselectedRatio = 0.8;

	var thisDrawSelected = true;
	var thisAltIndex = nil;
	var thisDisplayAltValue = true;

	var thisSVGRatio = 0.8;

	var thisGridOrga;

	*new {
		^super.new.init;
	}

	init {
		super.init;

		// Allowed events
		this.setEventHandler(
			QObject.mouseUpEvent,
			\mouseDownEvent, true);
		this.setEventHandler(
			QObject.mouseMoveEvent,
			\mouseMoveEvent, true);
		this.setEventHandler(
			QObject.mouseUpEvent,
			\mouseUpEvent, true);

		// Forbidden events
		// Allows to switch rapidly between states
		this.setEventHandler(
			QObject.mouseDblClickEvent,
			\mouseDownEvent, false);

		this.drawFunc_({ this.draw });
		this.action_({});

		this.onResize_(
			FunctionList()
			.addFunc({ this.prResizeSVGs; })
		);

		this.onClose_(
			FunctionList()
			.addFunc({ this.free; })
		);

		thisStates = [
			(
				string: "I",
				color: Color.grey,
				fontColor: Color.black

			),
			(
				string: "II",
				color: Color.grey,
				fontColor: Color.black

			),
			(
				string: "III",
				color: Color.grey,
				fontColor: Color.black
			),
			(
				string: "IV",
				color: Color.grey,
				fontColor: Color.black
			)
		];

		thisGridOrga = [2, 2];
	}

	// Setters & Getters - we're not using <>states,
	// because we want to refresh the view when a value has been updated

	desindex {
		thisCurrentState = -1;
		this.refresh;
	}

	allowRetrigger {
		thisAllowRetrigger
	}

	allowRetrigger_ { |aBoolean|
		thisAllowRetrigger = aBoolean;
	}

	allowMouseMove {
		^thisAllowMouseMove
	}

	allowMouseMove_ { |aBoolean|
		thisAllowMouseMove = aBoolean;
		this.setEventHandler(
			QObject.mouseMoveEvent,
			\mouseMoveEvent, aBoolean);
	}

	mouseDownAction_ { |aFunction|
		this.action_(aFunction);
	}

	mouseMoveAction_ { |aFunction|
		this.action_(aFunction);
	}

	states {
		^thisStates
	}

	states_ { |anArray|
		this.free;

		anArray.do({ |item, index|
			if(item.isKindOf(String)) {
				anArray[index] = (
					string: item,
					color: super.backgroundColor,
					fontColor: super.valueFontColor
				);
			};
		});

		anArray.do({ |item|
			if(item.includesKey(\svg)) {
				item[\img] = Image.openSVG(item[\svg]);
				item[\svgSize] = Point(
					item[\img].width,
					item[\img].height
				);
			};
		});

		thisStates = anArray;
		thisCurrentState = 0;

		thisGridOrga = this.prGetGridOrga(thisStates.size);

		this.prResizeSVGs;
	}

	state {
		^thisCurrentState
	}

	state_ { |aNumber|
		thisCurrentState = aNumber;
		if(thisCurrentState >= thisStates.size)
		{ thisCurrentState = 0 };
		this.refresh;
	}

	setColorAll_ { |aColor|
		thisStates.do({ |state| state[\color] = aColor; });
		this.refresh;
	}

	setFontColorAll_ { |aColor|
		thisStates.do({ |state| state[\fontColor] = aColor; });
		this.refresh;
	}

	// Graphical settings
	orientation {
		^thisOrientation
	}

	orientation_ { |aSymbol|
		thisOrientation = aSymbol;
		thisGridOrga = this.prGetGridOrga(thisStates.size);
		this.prResizeSVGs;
	}

	direction {
		^thisDirection
	}

	direction_ { |aSymbol|
		thisDirection = aSymbol;
		this.refresh;
	}

	drawSelected {
		^thisDrawSelected
	}

	drawSelected_ { |aBoolean|
		thisDrawSelected = aBoolean;
		this.refresh;
	}

	displayAltValue {
		^thisDisplayAltValue
	}

	displayAltValue_ { |aBoolean|
		thisDisplayAltValue = aBoolean;
		this.refresh;
	}

	fontRatio {
		^thisFontRatio
	}

	fontRatio_ { |aFloat|
		thisFontRatio = aFloat;
		this.refresh;
	}

	unselectedRatio {
		^thisUnselectedRatio
	}

	unselectedRatio_ { |aFloat|
		thisUnselectedRatio = aFloat;
		this.prResizeSVGs;
	}

	svgRatio {
		^thisSVGRatio
	}

	svgRatio_ { |aFloat|
		thisSVGRatio = aFloat;
		this.prResizeSVGs;
	}

	free {
		thisStates.do({ |state|
			if(state.includesKey(\img)) {
				state[\img].free;
				state[\smallImg].free;
				state.removeAt(\img);
				state.removeAt(\smallImg);
				state.removeAt(\svg);
			};
		});
	}

	prResizeSVGs {
		var minCaseSize = Point(0, 0);
		if(thisOrientation == \horizontal) {
			minCaseSize.y = super.interactionRect.height / thisGridOrga.size;
			thisGridOrga.do({ |value|
				minCaseSize.x = max(
					minCaseSize.x,
					value
				);
			});
			minCaseSize.x = super.interactionRect.width / minCaseSize.x;
		} {
			minCaseSize.x = super.interactionRect.width / thisGridOrga.size;
			thisGridOrga.do({ |value|
				minCaseSize.y = max(
					minCaseSize.y,
					value
				);
			});
			minCaseSize.y = super.interactionRect.height / minCaseSize.y;
		};

		thisStates.do({ |state|
			var hRatio, vRatio;
			var width, height;
			if(state.includesKey(\svg)) {
				hRatio = minCaseSize.x / state[\svgSize].x;
				vRatio = minCaseSize.y / state[\svgSize].y;

				if(hRatio < vRatio) {
					width = minCaseSize.x * thisSVGRatio;
					height = (state[\svgSize].y * (width / state[\svgSize].x));
				} {
					height = minCaseSize.y * thisSVGRatio;
					width = (state[\svgSize].x * (height / state[\svgSize].y));
				};

				state[\img].free;
				state[\smallImg].free;

				state[\img] = Image.openSVG(
					state[\svg],
					Size(width, height)
				);

				state[\smallImg] = Image.openSVG(
					state[\svg],
					Size(
						width * thisUnselectedRatio,
						height * thisUnselectedRatio
					)
				);
			};
		});

		this.refresh;
	}

	// Interaction
	action_ { |aFunction|
		mouseDownAction = { |view, x, y, mod|
			var xIndex, yIndex;
			var index = 0;
			if(thisOrientation == \horizontal) {
				yIndex = super.getYIndex(y, thisGridOrga.size, false);
				xIndex = super.getXIndex(x, thisGridOrga[yIndex]);
				yIndex.do({ |i|
					index = index + thisGridOrga[i];
				});
				index = index + xIndex;
			} {
				xIndex = super.getXIndex(x, thisGridOrga.size);
				yIndex = super.getYIndex(y, thisGridOrga[xIndex], false);
				xIndex.do({ |i|
					index = index + thisGridOrga[i];
				});
				index = index + yIndex;
			};

			// If ALT pressed
			if(mod == 524288)
			{ thisAltIndex = thisCurrentState; };

			if(index != thisCurrentState) {
				thisCurrentState = index;
				aFunction.value(thisCurrentState);
				this.refresh;
			} {
				if(thisAllowRetrigger)
				{ aFunction.value(thisCurrentState); };
			};
		};

		mouseMoveAction = { |view, x, y|
			var xIndex, yIndex;
			var index = 0;
			if(thisAllowMouseMove) {
				if(thisOrientation == \horizontal) {
					yIndex = super.getYIndex(y, thisGridOrga.size, false);
					xIndex = super.getXIndex(x, thisGridOrga[yIndex]);
					yIndex.do({ |i|
						index = index + thisGridOrga[i];
					});
					index = index + xIndex;
				} {
					xIndex = super.getXIndex(x, thisGridOrga.size);
					yIndex = super.getYIndex(y, thisGridOrga[xIndex], false);
					xIndex.do({ |i|
						index = index + thisGridOrga[i];
					});
					index = index + yIndex;
				};

				if(index != thisCurrentState) {
					thisCurrentState = index;
					aFunction.value(thisCurrentState);
					this.refresh;
				};
			};
		};

		mouseUpAction = { |view, x, y, mod|
			// If ALT pressed
			if(mod == 524288) {
				if(thisAltIndex.notNil) {
					if(thisAltIndex != thisCurrentState) {
						thisCurrentState = thisAltIndex;
						aFunction.value(thisCurrentState);
						thisAltIndex = nil;
						this.refresh;
					};
				};
			} {
				if(thisAltIndex.notNil) {
					thisAltIndex = nil;
					this.refresh;
				};
			};
		};
	}

	// Custom drawFunc
	draw {
		var constantAxisSize, caseSize, itemPosition,
		currentRect, color, fontColor, fontSize, indexInAxis;
		var minCaseHeight;

		super.drawFrame(super.backgroundColor);

		if(thisOrientation == \horizontal)
		{ constantAxisSize = super.interactionRect.height / thisGridOrga.size; }
		{ constantAxisSize = super.interactionRect.width / thisGridOrga.size; };

		thisStates.do({ |item, index|
			indexInAxis = index;
			itemPosition = Point(0, thisGridOrga[0]);

			while { (itemPosition.y - 1) < index } {
				itemPosition.x = itemPosition.x + 1;
				itemPosition.y = itemPosition.y + thisGridOrga[itemPosition.x];
			};

			itemPosition.x.do({ |index| indexInAxis = indexInAxis - thisGridOrga[index]; });

			if(thisOrientation == \horizontal) {
				fontSize = constantAxisSize * thisFontRatio;
				caseSize = super.interactionRect.width / thisGridOrga[itemPosition.x];
				itemPosition = Point(
					super.interactionRect.left
					+ (caseSize * indexInAxis)
					+ (caseSize / 2),
					super.interactionRect.top
					+ (constantAxisSize * itemPosition.x)
					+ (constantAxisSize / 2)
				);

				if((index == thisCurrentState) and: { thisDrawSelected }) {
					currentRect = Rect(
						itemPosition.x - (caseSize / 2),
						itemPosition.y - (constantAxisSize / 2),
						caseSize,
						constantAxisSize
					);
				} {
					fontSize = fontSize * thisUnselectedRatio;
					currentRect = Rect(
						itemPosition.x - ((caseSize / 2) * thisUnselectedRatio),
						itemPosition.y - ((constantAxisSize / 2) * thisUnselectedRatio),
						caseSize * thisUnselectedRatio,
						constantAxisSize * thisUnselectedRatio
					);
				};

				if((index == thisCurrentState)
					and: { thisDrawSelected }) {
					color = super.selectedColor;
					fontColor = super.fontColor;
				} {
					if(thisDisplayAltValue
						and: { thisAltIndex.notNil
							and: { (index == thisAltIndex) } }) {
						color = super.secondColor;
						fontColor = super.fontColor;
					} {
						color = item[\color];
						fontColor = item[\fontColor];
					};
				};

				Pen.fillColor_(color);
				Pen.fillRect(currentRect);

				if(item.includesKey(\img)) {
					if((index == thisCurrentState)
						and: { thisDrawSelected }) {
						item[\img].drawAtPoint(
							Point(
								itemPosition.x
								- (item[\img].width / 2),
								itemPosition.y
								- (item[\img].height / 2)
							);
						);
					} {
						item[\smallImg].drawAtPoint(
							Point(
								itemPosition.x
								- (item[\smallImg].width / 2),
								itemPosition.y
								- (item[\smallImg].height / 2)
							);
						);
					}
				};

				super.stringCenteredIn(
					item[\string],
					currentRect,
					super.font.deepCopy.size_(fontSize),
					fontColor,
					thisDirection
				);
			} { // vertical orientation
				minCaseHeight = inf;
				thisGridOrga.do({ |item|
					minCaseHeight = min(
						minCaseHeight,
						super.interactionRect.height / item
					);
				});
				fontSize = minCaseHeight * thisFontRatio;

				caseSize = super.interactionRect.height / thisGridOrga[itemPosition.x];

				itemPosition = Point(
					super.interactionRect.left
					+ (constantAxisSize * itemPosition.x)
					+ (constantAxisSize / 2),
					super.interactionRect.top
					+ (caseSize * indexInAxis)
					+ (caseSize / 2)
				);

				if((index == thisCurrentState) and: { thisDrawSelected }) {
					currentRect = Rect(
						itemPosition.x - (constantAxisSize / 2),
						itemPosition.y - (caseSize / 2),
						constantAxisSize,
						caseSize
					);
				} {
					fontSize = fontSize.deepCopy * thisUnselectedRatio;
					currentRect = Rect(
						itemPosition.x
						- ((constantAxisSize / 2) * thisUnselectedRatio),
						itemPosition.y
						- ((caseSize / 2) * thisUnselectedRatio),
						constantAxisSize * thisUnselectedRatio,
						caseSize * thisUnselectedRatio
					);
				};

				if((index == thisCurrentState)
					and: { thisDrawSelected }) {
					color = super.selectedColor;
					fontColor = super.fontColor;
				} {
					if(thisDisplayAltValue
						and: { thisAltIndex.notNil
							and: { (index == thisAltIndex) } }) {
						color = super.secondColor;
						fontColor = super.fontColor;
					} {
						color = item[\color];
						fontColor = item[\fontColor];
					};
				};

				Pen.fillColor_(color);
				Pen.fillRect(currentRect);

				if(item.includesKey(\img)) {
					if((index == thisCurrentState)
						and: { thisDrawSelected }) {
						item[\img].drawAtPoint(
							Point(
								itemPosition.x
								- (item[\img].width / 2),
								itemPosition.y
								- (item[\img].height / 2)
							);
						);
					} {
						item[\smallImg].drawAtPoint(
							Point(
								itemPosition.x
								- (item[\smallImg].width / 2),
								itemPosition.y
								- (item[\smallImg].height / 2)
							);
						);
					}
				};

				super.stringCenteredIn(
					item[\string],
					currentRect,
					super.font.deepCopy.size_(fontSize),
					fontColor,
					thisDirection
				);
			};
		});
	}

	prGetGridOrga { |value|
		var lowerSquare = value.sqrt.trunc(1);
		var gridOrga;

		if(value <= (lowerSquare * (lowerSquare + 1)))
		{ gridOrga = Array.fill(lowerSquare, { 0 }); }
		{ gridOrga = Array.fill(lowerSquare + 1, { 0 }); };

		value.do({ |index|
			gridOrga[index % gridOrga.size]
			= gridOrga[index % gridOrga.size] + 1;
		});

		^gridOrga
	}
}