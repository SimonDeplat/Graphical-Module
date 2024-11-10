GMUserView : UserView {

	var thisStyle;
	var thisDisplayBackground = true;
	var thisDisplayBorder = true;

	*new { |parent, bounds|
		^super.new.init;
	}

	*initClass {
		// Those classes needs to be compiled
		// BEFORE we can compile this particular class
		Class.initClassTree(Pen);
		Class.initClassTree(GMStyle);
	}

	// Custom init function, to be called by subclasses
	// when they init
	init {
		this.style_(GMStyle.default);
	}

	style {
		^thisStyle
	}

	style_ { |aGMStyle|
		// Remove itself from previous style's listeners
		// on new association
		thisStyle !? { thisStyle.removeListener(this) };
		// Store a reference to the new style
		thisStyle = aGMStyle;
		// Add itself to the new style's listeners
		aGMStyle.addListener(this);
	}

	displayBorder {
		^thisDisplayBorder
	}

	displayBorder_ { |aBoolean|
		thisDisplayBorder = aBoolean;
		this.refresh;
	}

	displayBackground {
		^thisDisplayBackground
	}

	displayBackground_ { |aBoolean|
		thisDisplayBackground = aBoolean;
		this.refresh;
	}

	displayFrame_ { |aBoolean|
		thisDisplayBorder = aBoolean;
		thisDisplayBackground = aBoolean;
		this.refresh;
	}

	// Function to draw the widget's background
	// takes a Color as argument
	// allowing to discriminate between "backColor", and "backgroundColor"
	drawFrame { |aColor|
		this.drawBackground(aColor);
		this.drawBorder;
	}

	drawBackground { |aColor|
		if(thisDisplayBackground) {
			Pen.fillColor_(aColor);
			Pen.fillRect(
				Rect(
					0, 0,
					this.bounds.width, this.bounds.height
				)
			);
		};
	}

	drawBorder {
		if(thisDisplayBorder) {
			var borderSize3 = this.borderSize
			+ this.secondBorderSize
			+ this.thirdBorderSize;
			var borderSize2 = this.borderSize
			+ this.secondBorderSize;
			var borderSize = this.borderSize;

			Pen.fillColor_(this.thirdBorderColor);
			Pen.fillRect(
				Rect(
					0, 0,
					borderSize3, this.bounds.height));
			Pen.fillRect(
				Rect(
					this.bounds.width - borderSize3, 0,
					borderSize3, this.bounds.height));
			Pen.fillRect(
				Rect(
					0, 0,
					this.bounds.width, borderSize3));
			Pen.fillRect(
				Rect(
					0, this.bounds.height - borderSize3,
					this.bounds.width, borderSize3));

			Pen.fillColor_(this.secondBorderColor);
			Pen.fillRect(
				Rect(
					0, 0,
					borderSize2, this.bounds.height));
			Pen.fillRect(
				Rect(
					this.bounds.width - borderSize2, 0,
					borderSize2, this.bounds.height));
			Pen.fillRect(
				Rect(
					0, 0,
					this.bounds.width, borderSize2));
			Pen.fillRect(
				Rect(
					0, this.bounds.height - borderSize2,
					this.bounds.width, borderSize2));

			Pen.fillColor_(this.borderColor);
			Pen.fillRect(
				Rect(
					0, 0,
					borderSize, this.bounds.height));
			Pen.fillRect(
				Rect(
					this.bounds.width - borderSize, 0,
					borderSize, this.bounds.height));
			Pen.fillRect(
				Rect(
					0, 0,
					this.bounds.width, borderSize));
			Pen.fillRect(
				Rect(
					0, this.bounds.height - borderSize,
					this.bounds.width, borderSize));
		};
	}

	stringCenteredIn { |string, rect, font,
		color, direction = \right|

		if(direction == \top) {
			Pen.rotate(
				0.5pi.neg,
				rect.left + (rect.width / 2),
				rect.top + (rect.height / 2)
			);
		};

		if(direction == \bottom) {
			Pen.rotate(
				0.5pi,
				rect.left + (rect.width / 2),
				rect.top + (rect.height / 2)
			);
		};

		if((direction == \top) or: { direction == \bottom }) {
			rect = Rect(
				rect.left + (rect.width / 2) - (rect.height / 2),
				rect.top + (rect.height / 2) - (rect.width / 2),
				rect.height,
				rect.width
			);
		};

		if(direction == \left) {
			Pen.rotate(
				pi,
				rect.left + (rect.width / 2),
				rect.top + (rect.height / 2)
			);
		};

		Pen.stringCenteredIn(
			string,
			rect,
			font,
			color
		);

		// Put Pen back in place
		if(direction == \top) {
			Pen.rotate(
				0.5pi,
				rect.left + (rect.width / 2),
				rect.top + (rect.height / 2)
		); };
		if(direction == \bottom) {
			Pen.rotate(
				0.5pi.neg,
				rect.left + (rect.width / 2),
				rect.top + (rect.height / 2)
		); };
		if(direction == \left) {
			Pen.rotate(
				pi.neg,
				rect.left + (rect.width / 2),
				rect.top + (rect.height / 2)
		); };
	}

	interactionRect {
		var rect = Rect(0, 0, 0, 0);
		if(this.bounds.notNil) {
			if(thisDisplayBorder) {
				rect = Rect(
					thisStyle.borderSize
					+ thisStyle.secondBorderSize +
					thisStyle.thirdBorderSize,
					thisStyle.borderSize
					+ thisStyle.secondBorderSize
					+ thisStyle.thirdBorderSize,

					this.bounds.width -
					((thisStyle.borderSize
						+ thisStyle.secondBorderSize
						+ thisStyle.thirdBorderSize) * 2),
					this.bounds.height -
					((thisStyle.borderSize
						+ thisStyle.secondBorderSize
						+ thisStyle.thirdBorderSize) * 2)
				);
			} {
				rect = Rect(
					0, 0,
					this.bounds.width,
					this.bounds.height
				)
			};
		};
		^rect
	}

	// No setters because the GMStyle associated is responsible
	// for holding datas, which are shared among Views

	mainColor { ^thisStyle.mainColor }
	secondColor { ^thisStyle.secondColor }
	backColor { ^thisStyle.backColor }
	backgroundColor { ^thisStyle.backgroundColor }
	beatColor { ^thisStyle.beatColor }
	borderSize { ^thisStyle.borderSize }
	secondBorderSize { ^thisStyle.secondBorderSize }
	thirdBorderSize { ^thisStyle.thirdBorderSize }
	borderColor { ^thisStyle.borderColor }
	secondBorderColor { ^thisStyle.secondBorderColor }
	thirdBorderColor { ^thisStyle.thirdBorderColor }
	font { ^thisStyle.font }
	fontColor { ^thisStyle.fontColor }
	outlineSize { ^thisStyle.outlineSize }
	outlineColor { ^thisStyle.outlineColor }
	fontColorDisabled { ^thisStyle.fontColorDisabled }
	disabledColor { ^thisStyle.disabledColor }
	selectedColor { ^thisStyle.selectedColor }
	helpersColor { ^thisStyle.helpersColor }
	valueFontColor { ^thisStyle.valueFontColor }
	highlightColor { ^thisStyle.highlightColor }

}
