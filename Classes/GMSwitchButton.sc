GMSwitchButton : GMUserView {
	var thisStates;
	var currentState = 0;

	*new {
		^super.new.init;
	}

	init {
		super.init;

		this.setEventHandler(
			QObject.mouseUpEvent, \mouseDownEvent, true);
		this.setEventHandler(
			QObject.mouseUpEvent, \mouseUpEvent, false);
		// Allows to switch rapidly between states
		this.setEventHandler(
			QObject.mouseDblClickEvent, \mouseDownEvent, false);

		this.drawFunc_({ this.draw });
		this.action_({});

		thisStates = [
			(
				string: "OFF",
				color: Color.red
			),
			(
				string: "ON",
				color: Color.green
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
		anArray.do({ |item, index|
			if(item.isKindOf(IdentityDictionary).not) {
				anArray[index] = (
					string: item,
					color: super.mainColor
				);
			};
		});
		thisStates = anArray;
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

	// Custom drawFunc
	draw {

		super.drawFrame(thisStates[currentState].color);

		Pen.stringCenteredIn(
			thisStates[currentState].string,
			Rect(
				0, 0,
				this.bounds.width, this.bounds.height
			),
			super.font,
			super.fontColor
		);
	}
}