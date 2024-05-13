GMPlayButton : GMUserView {

	var isPlaying = false;
	var thisMode = \stop;
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

	// Setters & Getters - we're not using <>action,
	// because we want to refresh the view when a value has been updated

	// Interaction
	action_ { |aFunction|
		mouseDownAction = {
			isPlaying = isPlaying.not;
			aFunction.(isPlaying);
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
		isPlaying = isPlaying.not;
		// Defer it because it's likely to be called
		// within a routine, when it ends
		{ this.refresh; }.defer;
	}

	// Graphical settings
	mode {
		^thisMode
	}

	mode_ { |aSymbol|
		thisMode = aSymbol;
		this.refresh;
	}

	setPlaying { |aBoolean|
		isPlaying = aBoolean;
		{ this.refresh; }.defer;
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
		var symbolSize = min(
			super.interactionRect.height * thisSymbolRatio,
			super.interactionRect.width * thisSymbolRatio
		);

		super.drawFrame(super.backgroundColor);

		Pen.fillColor_(super.mainColor);
		Pen.strokeColor_(super.outlineColor);
		Pen.width_(super.outlineSize);

		if(isPlaying.not)
		{
			Pen.moveTo(
				Point(
					symbolSize / 2,
					0
				).rotate(0 - (2pi / 3)).translate(
					Point(
						this.bounds.width / 2,
						this.bounds.height / 2
					)
				);
			);
			Pen.lineTo(
				Point(
					symbolSize / 2,
					0
				).translate(
					Point(
						this.bounds.width / 2,
						this.bounds.height / 2
					)
				);
			);
			Pen.lineTo(
				Point(
					symbolSize / 2,
					0
				).rotate(2pi / 3).translate(
					Point(
						this.bounds.width / 2,
						this.bounds.height / 2
					)
				);
			);
			Pen.lineTo(
				Point(
					symbolSize / 2,
					0
				).rotate(0 - (2pi / 3)).translate(
					Point(
						this.bounds.width / 2,
						this.bounds.height / 2
					)
				);
			);
		}
		{
			if(thisMode == \pause)
			{
				Pen.addRect(
					Rect(
						(this.bounds.width / 2) - (symbolSize / 2),
						(this.bounds.height / 2) - (symbolSize / 2),
						symbolSize / 3,
						symbolSize
					)
				);
				Pen.addRect(
					Rect(
						(this.bounds.width / 2)
						+ (symbolSize / 2) - (symbolSize / 3),
						(this.bounds.height / 2)
						- (symbolSize / 2),
						symbolSize / 3,
						symbolSize
					)
				);
			}
			{
				Pen.addRect(
					Rect(
						(this.bounds.width / 2) - (symbolSize / 2),
						(this.bounds.height / 2) - (symbolSize / 2),
						symbolSize,
						symbolSize
					)
				);
			};
		};

		Pen.fillStroke;
	}
}