GMCloseButton : GMUserView {

	var crossHalfSize;

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
	}

	// Setters & Getters - we're not using <>crossColor,
	// because we want to refresh the view
	// when a value has been updated

	// Interaction
	action_ { |aFunction|
		mouseDownAction = aFunction;
	}

	mouseDownAction_ { |aFunction|
		this.action_(aFunction);
	}

	// Custom drawFunc
	draw {
		crossHalfSize = super.interactionRect.width * 0.33;

		if(super.interactionRect.height < super.interactionRect.width)
		{ crossHalfSize = super.interactionRect.height * 0.33 };

		Pen.width_(crossHalfSize * 0.5);

		super.drawFrame(super.backgroundColor);

		Pen.moveTo(
			Point(
				this.bounds.width / 2 - crossHalfSize,
				this.bounds.height / 2 - crossHalfSize));
		Pen.lineTo(
			Point(
				this.bounds.width / 2 + crossHalfSize,
				this.bounds.height / 2 + crossHalfSize));
		Pen.moveTo(
			Point(
				this.bounds.width / 2 - crossHalfSize,
				this.bounds.height / 2 + crossHalfSize));
		Pen.lineTo(
			Point(
				this.bounds.width / 2 + crossHalfSize,
				this.bounds.height / 2 - crossHalfSize));
		Pen.strokeColor_(super.mainColor);
		Pen.stroke;
	}

}