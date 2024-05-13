GMRecordButton : GMUserView {

	var thisSymbolRatio = 0.75;
	var isRecording = false;
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

		this.action_({ |isRecording|
			if(isRecording)
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
			isRecording = isRecording.not;
			if(isRecording.not)
			{
				this.animate_(false);
				this.refresh;
			}
			{
				this.animate_(true);
			};
			aFunction.(isRecording);
		};
	}

	mouseDownAction_ { |aFunction|
		this.action_(aFunction);
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

		if(isRecording.not)
		{
			thisCurrentBackgroundColor = Color.black;
		}
		{
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

		if(isRecording.not)
		{
			Pen.addArc(
				Point(
					this.bounds.width / 2,
					this.bounds.height / 2
				),
				symbolSize / 2,
				0, 2pi
			)
		}
		{
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