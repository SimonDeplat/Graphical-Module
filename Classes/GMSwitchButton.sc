GMSwitchButton : GMUserView {
	var thisStates;
	var currentState = 0;

	var thisDirection = \right;
	var thisFontRatio = 0.4;
	var thisSVGRatio = 0.8;

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
				string: "OFF",
				color: Color.red,
				fontColor: Color.black,
			),
			(
				string: "ON",
				color: Color.green,
				fontColor: Color.black,
			)

		];
	}

	// Setters & Getters - we're not using <>state,
	// because we want to refresh the view
	// when a value has been updated

	// Interacton
	action_ { |aFunction|
		mouseDownAction = {
			currentState = currentState + 1;
			if(currentState == thisStates.size)
			{ currentState = 0 };
			aFunction.value(currentState);
			this.refresh;
		};
	}

	mouseDownAction_ { |aFunction|
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
			if(item.includesKey(\string).not) {
				item[\string] = "";
			};
		});

		newStates.do({ |item|
			if(item.includesKey(\color).not) {
				item[\color] = super.backgroundColor;
			};
		});

		newStates.do({ |item|
			if(item.includesKey(\fontColor).not) {
				item[\fontColor] = super.valueFontColor;
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
		currentState = 0;
		this.refresh;
	}

	state {
		^currentState
	}

	state_ { |anInteger|
		currentState = anInteger;
		if(currentState >= thisStates.size)
		{ currentState = 0 };
		if(currentState < 0)
		{ currentState = 0 };
		this.refresh;
	}

	// Graphical settings
	setColorAll { |aColor|
		thisStates.do({ |state| state[\color] = aColor; });
		this.refresh;
	}

	setFontColorAll { |aColor|
		thisStates.do({ |state| state[\fontColor] = aColor; });
		this.refresh;
	}

	direction {
		^thisDirection
	}

	direction_ { |aSymbol|
		thisDirection = aSymbol;
		this.refresh;
	}

	fontRatio {
		^thisFontRatio
	}

	fontRatio_ { |aFloat|
		thisFontRatio = aFloat;
		this.refresh;
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
				state.removeAt(\img);
				state.removeAt(\svg);
			};
		});
	}

	prResizeSVGs {
		thisStates.do({ |state|
			if(state.includesKey(\svg)) {
				var hRatio, vRatio;
				var width, height;

				hRatio = super.interactionRect.width / state[\svgSize].x;
				vRatio = super.interactionRect.height / state[\svgSize].y;

				if(hRatio < vRatio) {
					width = super.interactionRect.width	* thisSVGRatio;
					height = (state[\svgSize].y * (width / state[\svgSize].x));
				} {
					height = super.interactionRect.height * thisSVGRatio;
					width = (state[\svgSize].x * (height / state[\svgSize].y));
				};

				state[\img].free;
				state[\img] = Image.openSVG(
					state[\svg],
					Size(width, height)
				);
			};
		});

		this.refresh;
	}

	// Custom drawFunc
	draw {
		var fontSize;

		if((thisDirection == \right)
			or: { thisDirection == \left })
		{ fontSize = super.interactionRect.height * thisFontRatio }
		{ fontSize = super.interactionRect.width * thisFontRatio };
		
		super.drawFrame(thisStates[currentState][\color]);

		if(thisStates[currentState].includesKey(\img)) {
			thisStates[currentState][\img].drawAtPoint(
				Point(
					(super.bounds.width / 2)
					- (thisStates[currentState][\img].width / 2),
					(super.bounds.height / 2)
					- (thisStates[currentState][\img].height / 2)
				)
			);
		};

		super.stringCenteredIn(
			thisStates[currentState][\string],
			super.interactionRect,
			super.font.deepCopy.size_(fontSize),
			thisStates[currentState][\fontColor],
			thisDirection
		);
	}
}
