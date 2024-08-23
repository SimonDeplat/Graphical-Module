GMMultiButton : GMXYUserView {
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
		var newStates = anArray.deepCopy;
		this.free;
		newStates.do({ |item, index|
			if(item.isKindOf(String)) {
				newStates[index] = (
					string: item,
					color: super.backgroundColor,
					fontColor: super.valueFontColor
				);
			};
		});

		newStates.do({ |item|
			if(item.includesKey(\svg)) {
				item[\img] = Image.openSVG(item[\svg]);
				item[\svgSize] = Point(
					item[\img].width,
					item[\img].height
				);
			};
		});

		thisStates = newStates;
		thisCurrentState = 0;

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
		thisStates.do({ |state|
			if(state.includesKey(\svg)) {
				var hRatio, vRatio;
				var width, height;

				if(thisOrientation == \horizontal) {
					hRatio = (super.interactionRect.width / thisStates.size)
					/ state[\svgSize].x;
					vRatio = super.interactionRect.height / state[\svgSize].y;

					if(hRatio < vRatio) {
						width = ((super.interactionRect.width / thisStates.size)
							* thisSVGRatio);
						height = (state[\svgSize].y * (width / state[\svgSize].x));
					} {
						height = super.interactionRect.height * thisSVGRatio;
						width = (state[\svgSize].x * (height / state[\svgSize].y));
					};
				} {
					hRatio = super.interactionRect.width / state[\svgSize].x;
					vRatio = (super.interactionRect.height / thisStates.size)
					/ state[\svgSize].y;

					if(hRatio < vRatio) {
						width = super.interactionRect.width	* thisSVGRatio;
						height = (state[\svgSize].y * (width / state[\svgSize].x));
					} {
						height = ((super.interactionRect.height / thisStates.size)
							* thisSVGRatio);
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

		this.refresh;
	}

	// Interaction
	action_ { |aFunction|
		mouseDownAction = { |view, x, y, mod|
			var index;

			if(thisOrientation == \horizontal)
			{ index = super.getXIndex(x, thisStates.size); }
			{ index = super.getYIndex(y, thisStates.size, false); };

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
			if(thisAllowMouseMove) {
				var index;
				if(thisOrientation == \horizontal)
				{ index = super.getXIndex(x, thisStates.size); }
				{ index = super.getYIndex(y, thisStates.size, false); };

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
		var caseSize, fontSize, rectSize, color, fontColor, currentRect;

		super.drawFrame(super.backgroundColor);

		if(thisOrientation == \horizontal) {
			caseSize = super.interactionRect.width / thisStates.size;

			thisStates.do({ |item, index|
				rectSize = Point(caseSize, super.interactionRect.height);

				fontSize = super.interactionRect.height * thisFontRatio;

				if((index == thisCurrentState) and: { thisDrawSelected }) {
					color = super.selectedColor;
					fontColor = super.fontColor;
				} {
					rectSize = rectSize.deepCopy * thisUnselectedRatio;
					fontSize = fontSize.deepCopy * thisUnselectedRatio;
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

				currentRect = Rect(
					super.interactionRect.left
					+ (caseSize * index)
					+ (caseSize / 2)
					- (rectSize.x / 2),
					(super.bounds.height / 2)
					- (rectSize.y / 2),
					rectSize.x,
					rectSize.y
				);

				Pen.fillColor_(color);
				Pen.fillRect(currentRect);

				if(item.includesKey(\img)) {
					if((index == thisCurrentState) and: { thisDrawSelected }) {
						item[\img].drawAtPoint(
							Point(
								super.interactionRect.left
								+ (caseSize * index)
								+ (caseSize / 2)
								- (item[\img].width / 2),
								(super.bounds.height / 2)
								- (item[\img].height / 2)
							);
						);
					} {
						item[\smallImg].drawAtPoint(
							Point(
								super.interactionRect.left
								+ (caseSize * index)
								+ (caseSize / 2)
								- (item[\smallImg].width / 2),
								(super.bounds.height / 2)
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
			});
		} {
			caseSize = super.interactionRect.height / thisStates.size;

			thisStates.do({ |item, index|
				rectSize = Point(super.interactionRect.width, caseSize);

				fontSize = caseSize * thisFontRatio;

				if((index == thisCurrentState) and: { thisDrawSelected }) {
					color = super.selectedColor;
					fontColor = super.fontColor;
				} {
					rectSize = rectSize.deepCopy * thisUnselectedRatio;
					fontSize = fontSize.deepCopy * thisUnselectedRatio;
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

				currentRect = Rect(
					(super.bounds.width / 2)
					- (rectSize.x / 2),
					super.interactionRect.top
					+ (caseSize * index)
					+ (caseSize / 2)
					- (rectSize.y / 2),
					rectSize.x,
					rectSize.y
				);

				Pen.fillColor_(color);
				Pen.fillRect(currentRect);

				if(item.includesKey(\img)) {
					if((index == thisCurrentState) and: { thisDrawSelected }) {
						item[\img].drawAtPoint(
							Point(
								(super.bounds.width / 2)
								- (item[\img].width / 2),
								super.interactionRect.top
								+ (caseSize * index)
								+ (caseSize / 2)
								- (item[\img].height / 2)
							);
						);
					} {
						item[\smallImg].drawAtPoint(
							Point(
								(super.bounds.width / 2)
								- (item[\smallImg].width / 2),
								super.interactionRect.top
								+ (caseSize * index)
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
			});
		};
	}
}