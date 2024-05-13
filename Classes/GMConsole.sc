GMConsole : GMUserView {

	var currentString = "";
	var currentTimeStamp = "";
	var currentColor;
	var thisMsgList;
	var index = -1;
	var thisListMaxSize = inf;
	var locked = false;
	var thisFontRatio = 0.4;
	var thisFontInstance;

	var buttonSize = 0;

	var thisDefaultStringColor;
	var disabledColor;
	var buttonMargin = 3;

	*new {
		^super.new.init;
	}

	init {
		super.init;

		// Allowed events
		this.setEventHandler(
			QObject.mouseUpEvent,
			\mouseDownEvent, true);

		// Forbidden events
		this.setEventHandler(
			QObject.mouseUpEvent,
			\mouseUpEvent, false);
		this.setEventHandler(
			QObject.mouseMoveEvent,
			\mouseMoveEvent, false);
		this.setEventHandler(
			QObject.mouseDblClickEvent,
			\mouseDownEvent, false);

		this.drawFunc_({ this.draw; });
		this.onResize_({ this.prUpdateFont; });

		thisMsgList = List(0);
		thisDefaultStringColor = Color(1, 1, 1);
		currentColor = Color(1, 1, 1);
		disabledColor = Color(0.2, 0.2, 0.2);

		this.prUpdateFont;

		this.mouseUpAction_({ |view, x, y|

			if(x >= (super.interactionRect.right - buttonSize)) {
				if(x <= super.interactionRect.right) {
					if(y >= super.interactionRect.top) {
						if(y <= (super.interactionRect.top + buttonSize))
						{ this.previousMessage; }
						{ if(y <= (super.interactionRect.top + (buttonSize * 2)))
							{ this.nextMessage; }
							{ if(y <= (super.interactionRect.top + (buttonSize * 3)))
								{ this.lastMessage; };
							};
						};
			}; }; };
		});
	}

	postMsg { |aString, aColor = nil|
		var dictFull = false;
		var dict = (
			string: aString,
			color: aColor,
			timeStamp: Date.getDate.format("%H:%M:%S");
		);
		thisMsgList.add(dict);
		if(thisMsgList.size > thisListMaxSize)
		{
			thisMsgList.removeAt(0);
			dictFull = true;
		};

		if(locked.not)
		{
			if(dictFull.not) { index = index + 1; };
			currentString = aString;
			currentTimeStamp = dict[\timeStamp];
			if(aColor != nil)
			{ currentColor = aColor; }
			{ currentColor = thisDefaultStringColor; };
			this.refresh;
		} {
			if(dictFull) {
				if(index > - 1) {
					index = index - 1; }; };
		};
	}

	previousMessage {
		if(index > 0) {
			index = index - 1;
			currentString = thisMsgList[index][\string];
			currentTimeStamp = thisMsgList[index][\timeStamp];
			if(thisMsgList[index][\color] != nil)
			{ currentColor = thisMsgList[index][\color]; }
			{ currentColor = thisDefaultStringColor; };
			this.refresh;
			locked = true;
		}
	}

	nextMessage {
		if(index < (thisMsgList.size - 1)) {
			index = index + 1;
			currentString = thisMsgList[index][\string];
			currentTimeStamp = thisMsgList[index][\timeStamp];
			if(thisMsgList[index][\color] != nil)
			{ currentColor = thisMsgList[index][\color]; }
			{ currentColor = thisDefaultStringColor; };
			this.refresh;
			if(index == (thisMsgList.size - 1))
			{ locked = true; }
			{ locked = false; };
		}
	}

	lastMessage {
		if((thisMsgList.size != 0) and: (index != (thisMsgList.size - 1))) {
			index = thisMsgList.size - 1;
			currentString = thisMsgList[index][\string];
			currentTimeStamp = thisMsgList[index][\timeStamp];
			if(thisMsgList[index][\color] != nil)
			{ currentColor = thisMsgList[index][\color]; }
			{ currentColor = thisDefaultStringColor; };
			this.refresh;
			locked = false;
		};
	}

	removeLastMessage {
		if(thisMsgList.size == 1)
		{ this.reset }
		{
			if(thisMsgList.size != 0) {
				thisMsgList.pop;
				if(index == thisMsgList.size) {
					index = index - 1;
					currentString = thisMsgList[index][\string];
					currentTimeStamp = thisMsgList[index][\timeStamp];
					if(thisMsgList[index][\color] != nil)
					{ currentColor = thisMsgList[index][\color]; }
					{ currentColor = thisDefaultStringColor; };
					this.refresh;
					locked = false;
				};
			};
		};
	}

	reset {
		index = -1;
		currentString = "";
		currentColor = thisDefaultStringColor;
		thisMsgList = List(0);
		this.refresh;
	}

	listMaxSize {
		^thisListMaxSize
	}

	listMaxSize_ { |anInteger|
		thisListMaxSize = anInteger;
		while
		{ thisMsgList.size > thisListMaxSize }
		{
			thisMsgList.removeAt(0);
			if(index > -1)
			{ index = index - 1; };
		};
	}

	defaultStringColor {
		^thisDefaultStringColor
	}

	defaultStringColor_ { |aColor|
		thisDefaultStringColor = aColor;
		this.refresh;
	}

	fontRatio {
		^thisFontRatio
	}

	fontRatio_ { |aFloat|
		thisFontRatio = aFloat;
		this.prUpdateFont;
	}

	prUpdateFont {
		thisFontInstance = super.font.deepCopy;
		thisFontInstance.size_(
			super.interactionRect.height * thisFontRatio);
		this.refresh;
	}

	msgList {
		^thisMsgList
	}

	// Internal method
	prGetColor { |aBoolean|
		if(aBoolean)
		{ ^super.mainColor }
		{ ^disabledColor };
	}

	// Custom drawFunc
	draw {
		buttonSize = super.interactionRect.height / 3;

		super.drawFrame;

		// Draw buttons
		this.prDrawPreviousButton;
		this.prDrawNextButton;
		this.prDrawLastButton;

		// Write the time stamp
		Pen.stringAtPoint(
			currentTimeStamp,
			Point(
				super.interactionRect.left + 5,
				super.interactionRect.top
			),
			thisFontInstance.deepCopy.size_(
				min(
					12,
					super.interactionRect.height * 0.8,
				)
			),
			Color.grey;
		);

		// Write the text
		Pen.stringCenteredIn(
			currentString,
			Rect(
				super.interactionRect.left,
				super.interactionRect.top,
				super.interactionRect.width - buttonSize,
				super.interactionRect.height
			),
			thisFontInstance,
			currentColor;
		);
	}

	prDrawPreviousButton {
		Pen.fillColor_(this.prGetColor((index > 0)));
		Pen.fillRect(
			Rect(
				super.interactionRect.right - buttonSize,
				super.interactionRect.top,
				buttonSize, buttonSize
			);
		);

		Pen.fillColor_(super.backgroundColor);
		Pen.fillRect(
			Rect(
				super.interactionRect.right - buttonSize + buttonMargin,
				super.interactionRect.top + buttonMargin,
				buttonSize - (buttonMargin * 2), buttonSize - (buttonMargin * 2)
			);
		);

		Pen.strokeColor_(this.prGetColor((index > 0)));
		Pen.width_(buttonMargin);
		Pen.moveTo(
			Point(
				super.interactionRect.right - buttonSize + min((buttonMargin * 3), 8),
				super.interactionRect.top + buttonSize - min((buttonMargin * 3), 8)
			)
		);
		Pen.lineTo(
			Point(
				super.interactionRect.right - (buttonSize / 2),
				super.interactionRect.top + min((buttonMargin * 3), 8)
			)
		);
		Pen.lineTo(
			Point(
				super.interactionRect.right - min((buttonMargin * 3), 8),
				super.interactionRect.top + buttonSize - min((buttonMargin * 3), 8)
			)
		);
		Pen.stroke;
	}

	prDrawNextButton {
		Pen.fillColor_(this.prGetColor((index != (thisMsgList.size - 1))&&(thisMsgList.size != 0)));
		Pen.fillRect(
			Rect(
				super.interactionRect.right - buttonSize,
				super.interactionRect.top + buttonSize,
				buttonSize, buttonSize
			);
		);

		Pen.fillColor_(super.backgroundColor);
		Pen.fillRect(
			Rect(
				super.interactionRect.right - buttonSize + buttonMargin,
				super.interactionRect.top + buttonSize + buttonMargin,
				buttonSize - (buttonMargin * 2), buttonSize - (buttonMargin * 2)
			);
		);

		Pen.strokeColor_(this.prGetColor((index != (thisMsgList.size - 1))&&(thisMsgList.size != 0)));
		Pen.width_(buttonMargin);
		Pen.moveTo(
			Point(
				super.interactionRect.right - buttonSize + min((buttonMargin * 3), 8),
				super.interactionRect.top + buttonSize + min((buttonMargin * 3), 8)
			)
		);
		Pen.lineTo(
			Point(
				super.interactionRect.right - (buttonSize / 2),
				super.interactionRect.top + (buttonSize * 2) - min((buttonMargin * 3), 8)
			)
		);
		Pen.lineTo(
			Point(
				super.interactionRect.right - min((buttonMargin * 3), 8),
				super.interactionRect.top + buttonSize + min((buttonMargin * 3), 8)
			)
		);
		Pen.stroke;
	}

	prDrawLastButton {
		Pen.fillColor_(
			this.prGetColor(
				(index != (thisMsgList.size - 1))&&(thisMsgList.size != 0)));
		Pen.fillRect(
			Rect(
				super.interactionRect.right - buttonSize,
				super.interactionRect.top + (buttonSize * 2),
				buttonSize, buttonSize
			);
		);

		Pen.fillColor_(super.backgroundColor);
		Pen.fillRect(
			Rect(
				super.interactionRect.right - buttonSize + buttonMargin,
				super.interactionRect.top + (buttonSize * 2) + buttonMargin,
				buttonSize - (buttonMargin * 2),
				buttonSize - (buttonMargin * 2)
			);
		);

		Pen.strokeColor_(
			this.prGetColor(
				(index != (thisMsgList.size - 1))&&(thisMsgList.size != 0)));
		Pen.width_(buttonMargin);
		Pen.moveTo(
			Point(
				super.interactionRect.right - buttonSize + min((buttonMargin * 3), 8),
				super.interactionRect.top + (buttonSize * 2)
				+ min((buttonMargin * 3), 8)
			)
		);
		Pen.lineTo(
			Point(
				super.interactionRect.right - (buttonSize / 2),
				super.interactionRect.top + (buttonSize * 3)
				- min((buttonMargin * 3), 8)
			)
		);
		Pen.lineTo(
			Point(
				super.interactionRect.right - min((buttonMargin * 3), 8),
				super.interactionRect.top + (buttonSize * 2)
				+ min((buttonMargin * 3), 8)
			)
		);

		Pen.moveTo(
			Point(
				super.interactionRect.right - buttonSize + min((buttonMargin * 3), 8),
				super.interactionRect.bottom - min((buttonMargin * 3), 8)
			)
		);
		Pen.lineTo(
			Point(
				super.interactionRect.right - min((buttonMargin * 3), 8),
				super.interactionRect.bottom - min((buttonMargin * 3), 8)
			)
		);
		Pen.stroke;
	}
}