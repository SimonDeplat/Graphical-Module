GMCheckButton : GMUserView {

	var thisDisplaySymbol = \circle;
	var isPressed = false;
	var thisSymbolRatio = 0.75;

	*new {
		^super.new.init;
	}

	init {
		super.init;

		this.setEventHandler(
			QObject.mouseUpEvent, \mouseDownEvent, true);
		this.setEventHandler(
			QObject.mouseUpEvent, \mouseUpEvent, false);
		this.setEventHandler(
			QObject.mouseDblClickEvent, \mouseDownEvent, false);

		this.drawFunc_({ this.draw });

		this.action_({});
	}

	// Setters & Getters - we're not using <>displaySymbol,
	// because we want to refresh the view when a value has been updated

	// Interaction
	action_ { |aFunction|
		mouseDownAction = {
			isPressed = isPressed.not;
			aFunction.(isPressed);
			this.refresh;
		};
	}

	mouseDownAction_ { |aFunction|
		this.action_(aFunction);
	}

	toggle {
		this.mouseDownAction.value;
	}

	toggleDisplay {
		isPressed = isPressed.not;
		this.refresh;
	}

	// Graphical settings
	displaySymbol {
		^thisDisplaySymbol
	}

	displaySymbol_ { |aSymbol|
		thisDisplaySymbol = aSymbol;
		this.refresh;
	}

	symbolRatio {
		^thisSymbolRatio
	}

	symbolRatio_ { |aNumber|
		thisSymbolRatio = aNumber;
		this.refresh;
	}

	// Custom drawFunc
	draw {

		var thisDisplaySymbolSize = min(
			this.bounds.width * thisSymbolRatio,
			this.bounds.height * thisSymbolRatio,
		);
		var thisInnerdisplaySymbolSize =
		max(
			thisDisplaySymbolSize
			- (super.outlineSize * 4),
			0
		);

		if(thisDisplaySymbol == \circle) {

			Pen.fillColor_(super.backgroundColor);
			Pen.strokeColor_(super.outlineColor);
			Pen.width_(super.outlineSize);

			Pen.addArc(
				Point(
					this.bounds.width / 2,
					this.bounds.height / 2
				),
				thisDisplaySymbolSize / 2,
				0, 2pi
			);
			Pen.fillStroke;

			if(isPressed) {
				Pen.fillColor_(super.mainColor);
				Pen.addArc(
					Point(
						this.bounds.width / 2,
						this.bounds.height / 2
					),
					thisInnerdisplaySymbolSize / 2,
					0, 2pi
				);
				Pen.fill;
			};
		}

		{
			Pen.fillColor_(super.backgroundColor);
			Pen.strokeColor_(super.outlineColor);
			Pen.width_(super.outlineSize);

			Pen.addRect(
				Rect(
					(this.bounds.width / 2) - (thisDisplaySymbolSize / 2),
					(this.bounds.height / 2) - (thisDisplaySymbolSize / 2),
					thisDisplaySymbolSize,
					thisDisplaySymbolSize
				)
			);
			Pen.fillStroke;

			if(isPressed) {
				Pen.fillColor_(super.mainColor);
				Pen.addRect(
					Rect(
						(this.bounds.width / 2)
						- (thisInnerdisplaySymbolSize / 2),
						(this.bounds.height / 2)
						- (thisInnerdisplaySymbolSize / 2),
						thisInnerdisplaySymbolSize,
						thisInnerdisplaySymbolSize
					)
				);
				Pen.fill;
			};
		};
	}
}
