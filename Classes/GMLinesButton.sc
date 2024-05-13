GMLinesButton : GMXYUserView {
	var thisStates;
	var thisCurrentState;

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
			[
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
				)
			], [
				(
					string: "IV",
					color: Color.grey,
					fontColor: Color.black
				),
				(
					string: "V",
					color: Color.grey,
					fontColor: Color.black
				)
			], [
				(
					string: "VI",
					color: Color.grey,
					fontColor: Color.black

				),
				(
					string: "VII",
					color: Color.grey,
					fontColor: Color.black

				),
				(
					string: "VIII",
					color: Color.grey,
					fontColor: Color.black
				)
			]
		];

		thisCurrentState = [0, 0];
	}

	// Setters & Getters - we're not using <>states,
	// because we want to refresh the view when a value has been updated

	desindex {
		thisCurrentState = [-1, -1];
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

		anArray.do({ |subArray|
			subArray.do({ |item, index|
				if(item.isKindOf(String)) {
					subArray[index] = (
						string: item,
						color: super.backgroundColor,
						fontColor: super.valueFontColor
					);
				};
			});
		});

		anArray.do({ |subArray|
			subArray.do({ |item|
				if(item.includesKey(\svg)) {
					item[\img] = Image.openSVG(item[\svg]);
					item[\svgSize] = Point(
						item[\img].width,
						item[\img].height
					);
				};
			});
		});

		thisStates = anArray;
		thisCurrentState = [0, 0];

		this.prResizeSVGs;
	}

	state {
		^thisCurrentState
	}

	state_ { |anArray|
		thisCurrentState = anArray;
		this.refresh;
	}

	setColorAll_ { |aColor|
		thisStates.do({ |subArray|
			subArray.do({ |state|
				state[\color] = aColor;
			});
		});
		this.refresh;
	}

	setFontColorAll_ { |aColor|
		thisStates.do({ |subArray|
			subArray.do({ |state|
				state[\fontColor] = aColor;
			});
		});
		this.refresh;
	}

	// Graphical settings
	orientation {
		^thisOrientation
	}

	orientation_ { |aSymbol|
		thisOrientation = aSymbol;
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
		thisStates.do({ |subArray|
			subArray.do({ |state|
				if(state.includesKey(\img)) {
					state[\img].free;
					state[\smallImg].free;
					state.removeAt(\img);
					state.removeAt(\smallImg);
					state.removeAt(\svg);
				};
			});
		});
	}

	prResizeSVGs {
		var maxAxisSize = 0;

		thisStates.do({ |subArray|
			maxAxisSize = max(
				maxAxisSize,
				subArray.size
			);
		});

		thisStates.do({ |subArray|
			subArray.do({ |state|
				if(state.includesKey(\svg)) {
					var hRatio, vRatio;
					var width, height;

					if(thisOrientation == \horizontal) {
						hRatio = (super.interactionRect.width / maxAxisSize)
						/ state[\svgSize].x;
						vRatio = (super.interactionRect.height / thisStates.size)
						/ state[\svgSize].y;

						if(hRatio < vRatio) {
							width = ((super.interactionRect.width / maxAxisSize)
								* thisSVGRatio);
							height = (state[\svgSize].y * (width / state[\svgSize].x));
						} {
							height = (super.interactionRect.height / thisStates.size) * thisSVGRatio;
							width = (state[\svgSize].x * (height / state[\svgSize].y));
						};
					} {
						hRatio = (super.interactionRect.width / thisStates.size)
						/ state[\svgSize].x;
						vRatio = (super.interactionRect.height / maxAxisSize)
						/ state[\svgSize].y;

						if(hRatio < vRatio) {
							width = (super.interactionRect.width / thisStates.size) * thisSVGRatio;
							height = (state[\svgSize].y * (width / state[\svgSize].x));
						} {
							height = (super.interactionRect.height / maxAxisSize) * thisSVGRatio;
							width = (state[\svgSize].x * (height / state[\svgSize].y));
						};
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
		});

		this.refresh;
	}

	// Interaction
	action_ { |aFunction|
		mouseDownAction = { |view, x, y, mod|
			var index1, index2;

			if(thisOrientation == \horizontal) {
				index1 = super.getYIndex(y, thisStates.size, false);
				index2 = super.getXIndex(x, thisStates[index1].size);
			} {
				index1 = super.getXIndex(x, thisStates.size);
				index2 = super.getYIndex(y, thisStates[index1].size, false);
			};

			// If ALT pressed
			if(mod == 524288)
			{ thisAltIndex = thisCurrentState; };

			if([index1, index2] != thisCurrentState) {
				thisCurrentState = [index1, index2];
				aFunction.value(thisCurrentState[0], thisCurrentState[1]);
				this.refresh;
			} {
				if(thisAllowRetrigger)
				{ aFunction.value(thisCurrentState[0], thisCurrentState[1]); };
			};
		};

		mouseMoveAction = { |view, x, y|
			if(thisAllowMouseMove) {
				var index1, index2;

				if(thisOrientation == \horizontal) {
					index1 = super.getYIndex(y, thisStates.size, false);
					index2 = super.getXIndex(x, thisStates[index1].size);
				} {
					index1 = super.getXIndex(x, thisStates.size);
					index2 = super.getYIndex(y, thisStates[index1].size, false);
				};

				if([index1, index2] != thisCurrentState) {
					thisCurrentState = [index1, index2];
					aFunction.value(thisCurrentState[0], thisCurrentState[1]);
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
						aFunction.value(thisCurrentState[0], thisCurrentState[1]);
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
		var constantAxisSize, currentPos, currentRect,
		caseSize, fontSize, color, fontColor;

		var minHeight = inf;
		thisStates.do({ |subArray|
			minHeight = min(
				minHeight,
				super.interactionRect.height / subArray.size
			);
		});

		super.drawFrame(super.backgroundColor);

		if(thisOrientation == \horizontal)
		{ constantAxisSize = super.interactionRect.height / thisStates.size; }
		{ constantAxisSize = super.interactionRect.width / thisStates.size; };

		thisStates.do({ |subArray, index1|
			subArray.do({ |item, index2|
				if(thisOrientation == \horizontal) {
					fontSize = constantAxisSize * thisFontRatio;
					caseSize = super.interactionRect.width / subArray.size;
					if([index1, index2] == thisCurrentState) {
						currentRect = Rect(
							super.interactionRect.left
							+ (caseSize * index2),
							super.interactionRect.top
							+ (constantAxisSize * index1),
							caseSize,
							constantAxisSize
						);
					} {
						fontSize = fontSize * thisUnselectedRatio;
						currentRect = Rect(
							super.interactionRect.left
							+ (caseSize * index2)
							+ (caseSize / 2)
							- ((caseSize / 2) * thisUnselectedRatio),
							super.interactionRect.top
							+ (constantAxisSize * index1)
							+ (constantAxisSize / 2)
							- ((constantAxisSize / 2) * thisUnselectedRatio),
							caseSize * thisUnselectedRatio,
							constantAxisSize * thisUnselectedRatio
						);
					};

					if(([index1, index2] == thisCurrentState)
						and: { thisDrawSelected }) {
						color = super.selectedColor;
						fontColor = super.fontColor;
					} {
						if(thisDisplayAltValue
							and: { thisAltIndex.notNil
								and: { ([index1, index2] == thisAltIndex) } }) {
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
						if(([index1, index2] == thisCurrentState)
							and: { thisDrawSelected }) {
							item[\img].drawAtPoint(
								Point(
									super.interactionRect.left
									+ (caseSize * index2)
									+ (caseSize / 2)
									- (item[\img].width / 2),
									super.interactionRect.top
									+ (constantAxisSize * index1)
									+ (constantAxisSize / 2)
									- (item[\img].height / 2)
								);
							);
						} {
							item[\smallImg].drawAtPoint(
								Point(
									super.interactionRect.left
									+ (caseSize * index2)
									+ (caseSize / 2)
									- (item[\smallImg].width / 2),
									super.interactionRect.top
									+ (constantAxisSize * index1)
									+ (constantAxisSize / 2)
									- (item[\smallImg].height / 2)
								);
							);
						};
					};

					super.stringCenteredIn(
						item[\string],
						currentRect,
						super.font.deepCopy.size_(fontSize),
						fontColor,
						thisDirection
					);
				} { // Vertical orientation
					caseSize = super.interactionRect.height / subArray.size;
					fontSize = minHeight * thisFontRatio;

					if([index1, index2] == thisCurrentState) {
						currentRect = Rect(
							super.interactionRect.left
							+ (constantAxisSize * index1),
							super.interactionRect.top
							+ (caseSize * index2),
							constantAxisSize,
							caseSize
						);
					} {
						fontSize = fontSize * thisUnselectedRatio;
						currentRect = Rect(
							super.interactionRect.left
							+ (constantAxisSize * index1)
							+ (constantAxisSize / 2)
							- ((constantAxisSize / 2) * thisUnselectedRatio),
							super.interactionRect.top
							+ (caseSize * index2)
							+ (caseSize / 2)
							- ((caseSize / 2) * thisUnselectedRatio),
							constantAxisSize * thisUnselectedRatio,
							caseSize * thisUnselectedRatio
						);
					};

					if(([index1, index2] == thisCurrentState)
						and: { thisDrawSelected }) {
						color = super.selectedColor;
						fontColor = super.fontColor;
					} {
						if(thisDisplayAltValue
							and: { thisAltIndex.notNil
								and: { ([index1, index2] == thisAltIndex) } }) {
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
						if(([index1, index2] == thisCurrentState)
							and: { thisDrawSelected }) {
							item[\img].drawAtPoint(
								Point(
									super.interactionRect.left
									+ (constantAxisSize * index1)
									+ (constantAxisSize / 2)
									- (item[\img].width / 2),
									super.interactionRect.top
									+ (caseSize * index2)
									+ (caseSize / 2)
									- (item[\img].height / 2)
								);
							);
						} {
							item[\smallImg].drawAtPoint(
								Point(
									super.interactionRect.left
									+ (constantAxisSize * index1)
									+ (constantAxisSize / 2)
									- (item[\smallImg].width / 2),
									super.interactionRect.top
									+ (caseSize * index2)
									+ (caseSize / 2)
									- (item[\smallImg].height / 2)
								);
							);
						};
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
		});
	}

}