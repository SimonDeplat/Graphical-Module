GMRecordButton : GMUserView {

	var thisSymbolRatio = 0.75;
	var thisIsRecording = false;
	var thisCurrentBackgroundColor;

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

		this.action_({ |thisIsRecording|
			if(thisIsRecording)
			{ Server.default.record; }
			{ Server.default.stopRecording; };
		});

		thisCurrentBackgroundColor = Color.black;
	}

	// Setters & Getters - we're not using <>action,
	// because we want to refresh the view
	// when a value has been updated

	// Interaction
	action_ { |aFunction|
		mouseDownAction = {
			thisIsRecording = thisIsRecording.not;
			aFunction.value(thisIsRecording);
			if(thisIsRecording.not) {
				this.animate_(false);
				this.refresh;
			} {
				this.animate_(true);
			};
		};
	}

	mouseDownAction_ { |aFunction|
		this.action_(aFunction);
	}

	setDisplay { |aBoolean|
		if(aBoolean and: { thisIsRecording.not }) {
			thisIsRecording = true;
			{ this.animate_(true); }.defer;
		};
		if(aBoolean.not and: { thisIsRecording }) {
			thisIsRecording = false;
			{
				this.animate_(false);
				this.refresh;
			}.defer;
		};
	}

	symbolRatio {
		^thisSymbolRatio
	}

	symbolRatio_ { |aNumber|
		thisSymbolRatio = aNumber;
		this.refresh;
	}

	isRecording {
		^thisIsRecording
	}

	// Custom drawFunc
	draw {
		var symbolSize = min(
			super.interactionRect.height * thisSymbolRatio,
			super.interactionRect.width * thisSymbolRatio
		);

		if(thisIsRecording.not) {
			thisCurrentBackgroundColor = Color.black;
		} {
			thisCurrentBackgroundColor = Color(
				sin(this.frame / 60 * pi).abs,
				sin(this.frame / 60 * pi).abs,
				sin(this.frame / 60 * pi).abs
			);
		};

		// super.drawFrame "override hack"
		super.drawFrame;
		Pen.fillColor_(thisCurrentBackgroundColor);
		Pen.fillRect(super.interactionRect);

		Pen.fillColor_(Color.red);
		Pen.strokeColor_(Color.white);
		Pen.width_(symbolSize * 0.05);

		if(thisIsRecording.not) {
			Pen.addArc(
				Point(
					this.bounds.width / 2,
					this.bounds.height / 2
				),
				symbolSize / 2,
				0, 2pi
			)
		} {
			Pen.addRect(
				Rect(
					(this.bounds.width / 2) - (symbolSize / 2),
					(this.bounds.height / 2) - (symbolSize / 2),
					symbolSize,
					symbolSize
				)
			)
		};

		Pen.fillStroke;
	}

}