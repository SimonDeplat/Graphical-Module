GMKeyboard : GMXYUserView {

	var thisKeyNumber = 8;
	var thisMargins = 8;
	var thisMode = \diatonic;
	var thisDisplayNames = false;
	var thisNames;
	var thisDisplayHighlights = false;
	var thisHighlights;
	var thisOutlineKeys = true;

	var thisKeyColor;
	var thisBlackKeyColor;
	var thisKeyOutlineColor;
	var thisBlackKeyOutlineColor;
	var thisBlinkColor;
	var thisBlinkFontColor;
	var thisFontRatio = 0.3;
	var font;

	var thisCurrentKeyColor;
	var thisCurrentFontColor;

	var blinks;
	var thisFrameRate = 60;
	var thisBlinkTime = 0.5;

	var thisChromaticStartNote = 0;
	var thisBlackKeyRatio = 0.5;

	var thisSustainMode = false;
	var sustains;

	var currentKey = nil;
	var whiteKeysNumber;

	var keyboardLayout;

	var thisAction;

	*new {
		^super.new.init;
	}

	init {
		super.init;
		this.drawFunc_({ this.draw; });
		thisNames = ["C", "D", "E", "F", "G", "A", "B", "C"];
		thisHighlights = [1, 0.5, 0.75, 0.5, 0.75, 0.5 , 0.5];

		thisKeyColor = super.mainColor;
		thisBlackKeyColor = super.secondColor;
		thisKeyOutlineColor = super.outlineColor;
		thisBlackKeyOutlineColor = super.outlineColor;
		thisBlinkColor = Color.white;
		thisBlinkFontColor = Color.black;

		thisAction = {};

		this.mouseDownAction = { |view, x, y|
			var key;
			if(thisMode == \diatonic)
			{ key = this.getXIndex(x, thisKeyNumber); }
			{ key = this.prDetectChromaKey(x, y); };
			if(key.notNil) {
				if(thisSustainMode.not) {
					thisAction.value(key);
					this.prAddBlink(key);
					this.animate_(true);
				} {
					thisAction.value(key, true);
					this.sustainKey(key);
				};
			};
			currentKey = key;
		};

		this.mouseUpAction_({
			if(thisSustainMode) {
				if(currentKey.notNil)
				{ this.releaseKey(currentKey, true); };
			};
			currentKey = nil;
		});

		this.mouseMoveAction = { |view, x, y|
			var key;
			if(thisMode == \diatonic)
			{ key = this.getXIndex(x, thisKeyNumber); }
			{ key = this.prDetectChromaKey(x, y); };
			if(key != currentKey) {
				if(currentKey.notNil and: { thisSustainMode })
				{ this.releaseKey(currentKey, true); };
				if(key.notNil) {
					if(thisSustainMode.not) {
						thisAction.value(key);
						this.prAddBlink(key);
						this.animate_(true);
					} {
						thisAction.value(key, true);
						this.sustainKey(key);
					};
				};
				currentKey = key;
			};
		};

		this.action_(thisAction);

		blinks = List(0);
		sustains = List(0);

		keyboardLayout = [
			\w, \b, \w, \b, \w,
			\w, \b, \w, \b, \w, \b, \w
		];
	}

	keyNumber {
		^thisKeyNumber
	}

	keyNumber_ { |anInteger|
		thisKeyNumber = anInteger;
		blinks = List(0);
		sustains = List(0);
		if(thisMode == \chromatic)
		{ this.prGetWhiteKeysNumber; };
		this.refresh;
	}

	margins {
		^thisMargins
	}

	margins_ { |aNumber|
		thisMargins = aNumber;
		this.refresh;
	}

	keyColor {
		^thisKeyColor
	}

	keyColor_ { |aColor|
		thisKeyColor = aColor;
		this.refresh;
	}

	blackKeyColor {
		^thisBlackKeyColor
	}

	blackKeyColor_ { |aColor|
		thisBlackKeyColor = aColor;
		this.refresh;
	}

	keyOutlineColor {
		^thisKeyOutlineColor
	}

	keyOutlineColor_ { |aColor|
		thisKeyOutlineColor = aColor;
		this.refresh;
	}

	blackKeyOutlineColor {
		^thisBlackKeyOutlineColor
	}

	blackKeyOutlineColor_ { |aColor|
		thisBlackKeyOutlineColor = aColor;
		this.refresh;
	}

	blinkColor {
		^thisBlinkColor
	}

	blinkColor_ { |aColor|
		thisBlinkColor = aColor;
	}

	blinkFontColor {
		^thisBlinkFontColor
	}

	blinkFontColor_ { |aColor|
		thisBlinkFontColor = aColor;
	}

	blackKeyRatio {
		^thisBlackKeyRatio
	}

	blackKeyRatio_ { |aFloat|
		thisBlackKeyRatio = aFloat;
		if(thisMode == \chromatic)
		{ this.refresh; };
	}

	displayNames {
		^thisDisplayNames
	}

	displayNames_ { |aBoolean|
		thisDisplayNames = aBoolean;
		this.refresh;
	}

	displayHighlights {
		^thisDisplayHighlights
	}

	displayHighlights_ { |aBoolean|
		thisDisplayHighlights = aBoolean;
		this.refresh;
	}

	fontRatio {
		^thisFontRatio
	}

	fontRatio_ { |aFloat|
		thisFontRatio = aFloat;
		if(thisDisplayNames)
		{ this.refresh; };
	}

	mode {
		^thisMode
	}

	mode_ { |aSymbol|
		if((aSymbol == \diatonic)
			or: { aSymbol == \chromatic }) {
			thisMode = aSymbol;
			blinks = List(0);
			sustains = List(0);
			if(thisMode == \chromatic) {
				this.prGetWhiteKeysNumber;
				thisNames = [
					"C", "Db", "D", "Eb", "E", "F", "F#",
					"G", "Ab", "A", "Bb", "B"
				];
				thisHighlights = [
					1, 0.5, 0.5, 0.5,
					0.75, 0.5, 0.5,
					0.75, 0.5, 0.5,
					0.5, 0.5
				];
			} {
				thisNames = ["C", "D", "E", "F", "G", "A", "B"];
				thisHighlights = [1, 0.5, 0.75, 0.5, 0.75, 0.5 , 0.5];
			};
			this.refresh;
		} {
			(
				"GMKeyboard: wrong mode assignment"
				++ " : \diatonic or \chromatic expected"
			).warn;
		};
	}

	sustainMode {
		^thisSustainMode
	}

	sustainMode_ { |aBoolean|
		thisSustainMode = aBoolean;
	}

	chromaticStartNote {
		^thisChromaticStartNote
	}

	chromaticStartNote_ { |aNumber|
		if((aNumber >= 0 ) and: { aNumber <= 11 }) {
			thisChromaticStartNote = aNumber;
			this.prGetWhiteKeysNumber;
			this.refresh;
		} {
			(
				"GMKeyboard: wrong chromatic start note :"
				++ " should be a number between 0 and 11"
			).warn;
		};
	}

	outlineKeys {
		^thisOutlineKeys
	}

	outlineKeys_ { |aBoolean|
		thisOutlineKeys = aBoolean;
		this.refresh;
	}

	prAddBlink { |key|
		var blinkingAllready = false;
		blinks.do({ |blink, index|
			if(blinkingAllready.not) {
				if(blink[0] == key) {
					blinks[index][1] = 1;
					blinkingAllready = true;
				};
			};
		});

		if(blinkingAllready.not)
		{ blinks.add([key, 1]); };
	}

	prUpdateBlinks {
		var newBlinks = List(0);
		blinks.do({ |blink|
			blink[1] = blink[1]
			- ((1 / thisFrameRate) / thisBlinkTime);
			if(blink[1] > 0)
			{ newBlinks.add(blink); };
		});
		blinks = newBlinks;
		if(blinks.size == 0)
		{ this.animate_(false); };
	}

	sustainKey { |key, doAction = false|
		var blinkIndex = nil;

		if(sustains.includesEqual(key).not) {
			// Remove key from blinks if needed
			if(doAction)
			{ thisAction.value(key, true); };

			blinks.do({ |item, index|
				if((blinkIndex.notNil) and: { item[0] == key })
				{ blinkIndex = index; };
			});
			if(blinkIndex.notNil) {
				blinks.removeAt(blinkIndex);
				if(blinks.size == 0)
				{ { this.animate_(false); }.defer; };
			};

			sustains.add(key);

			{ this.refresh; }.defer;
		};
	}

	releaseKey { |key, doAction = false|
		if(sustains.includesEqual(key)) {
			if(doAction)
			{ thisAction.value(key, false); };

			sustains.remove(key);

			if(blinks.size == 0)
			{ { this.animate_(true); }.defer; };

			this.prAddBlink(key);

			{ this.refresh; }.defer;
		};
	}

	playKey { |key, doAction = false|
		if(doAction)
		{ thisAction.value(key); };

		this.prAddBlink(key);

		{
			this.animate_(true);
			this.refresh;
		}.defer;
	}

	names {
		^thisNames
	}

	names_ { |anArray|
		thisNames = anArray;
		if(thisDisplayNames)
		{ this.refresh; };
	}

	highlights {
		^thisHighlights
	}

	highlights_ { |anArray|
		thisHighlights = anArray;
		if(thisDisplayHighlights)
		{ this.refresh; };
	}

	// Interaction
	action_ { |aFunction|
		thisAction = aFunction;
	}

	draw {
		if(super.displayBorder)
		{ super.drawFrame(super.backgroundColor); };

		if(this.animate)
		{ this.prUpdateBlinks; };

		if(thisMode == \diatonic)
		{ this.prDrawDiatonic; }
		{ this.prDrawChromatic; };
	}

	prDrawDiatonic {
		var caseSize = super.interactionRect.width;
		var outlineSize = super.outlineSize;

		caseSize = caseSize - (thisMargins * (thisKeyNumber + 1));
		caseSize = caseSize / thisKeyNumber;

		font = super.font.deepCopy.size_(caseSize * thisFontRatio);

		if(thisOutlineKeys.not) { outlineSize = 0; };

		thisKeyNumber.do({ |index|
			var rect = Rect(
				super.interactionRect.left
				+ thisMargins
				+ (index * (caseSize + thisMargins)),
				super.interactionRect.top + thisMargins,
				caseSize,
				super.interactionRect.height - (thisMargins * 2)
			);

			this.prDrawKey(rect, outlineSize, index);
		});
	}

	prDrawChromatic {
		var caseSize = this.prGetChromaticCaseSize;
		var outlineSize = super.outlineSize;
		var offset = 0;
		var octave, notePos, realNotePos, noteType;
		var noteRect;

		font = super.font.deepCopy.size_(caseSize * thisFontRatio);

		if(
			[1, 3, 6, 8, 10]
			.includesEqual(thisChromaticStartNote)
		) { offset = caseSize / 3; };

		if(thisOutlineKeys.not) { outlineSize = 0; };

		thisKeyNumber.do({ |index|
			octave = 0;
			notePos = index;
			noteType = (index + thisChromaticStartNote)%12;

			while { notePos > 11 } {
				notePos = notePos - 12;
				octave = octave + 1;
			};

			realNotePos = 0;
			notePos.do({ |index|
				if(
					keyboardLayout[
						(index + thisChromaticStartNote)%12
					]
					== \w
				) { realNotePos = realNotePos + 1; };
			});

			if(
				[0, 2, 4, 5, 7, 9, 11]
				.includesEqual(noteType)
			) {
				noteRect = Rect(
					super.interactionRect.left
					+ offset
					+ (
						(caseSize + 0)
						* ((octave * 7) + realNotePos)
					)
					+ (thisMargins / 2),
					super.interactionRect.top + thisMargins,
					caseSize - thisMargins,
					super.interactionRect.height
					- (thisMargins * 2)
				);
			} {
				noteRect = Rect(
					super.interactionRect.left
					+ offset
					+ (
						(caseSize + 0)
						* ((octave * 7) + realNotePos)
					)
					- (caseSize / 3)
					+ (thisMargins / 2),
					super.interactionRect.top + thisMargins,
					((caseSize / 3) * 2) - thisMargins,
					(super.interactionRect.height
						* thisBlackKeyRatio)
					- (thisMargins * 1.5)
				);
			};

			case

			// C - F
			{ (noteType == 0) or: { noteType == 5 } } {
				if(index < (thisKeyNumber - 1)) {
					this.prDrawLeftKey(
						noteRect,
						outlineSize,
						index
					);
				} {
					this.prDrawKey(
						noteRect,
						outlineSize,
						index
					);
				};
			}

			// E - B
			{ (noteType == 4) or: { noteType == 11 } } {
				if(index == 0) {
					this.prDrawKey(
						noteRect,
						outlineSize,
						index
					);
				} {
					this.prDrawRightKey(
						noteRect,
						outlineSize,
						index
					);
				};
			}

			// D - G - A
			{ (noteType == 2)
				or: { (noteType == 7)
					or: { noteType == 9 } } }
			{
				if(index == 0) {
					this.prDrawLeftKey(
						noteRect,
						outlineSize,
						index
					);
				} {
					if(index == (thisKeyNumber - 1)) {
						this.prDrawRightKey(
							noteRect,
							outlineSize,
							index
						);
					} {
						this.prDrawMiddleKey(
							noteRect,
							outlineSize,
							index
						);
					};
				};
			}

			// Black keys
			{
				this.prDrawKey(
					noteRect,
					outlineSize,
					index,
					\black
				);
			};
		});
	}

	prGetChromaticCaseSize {
		var caseSize = whiteKeysNumber;
		var viewWidth = super.interactionRect.width;

		if(
			[1, 3, 6, 8, 10]
			.includesEqual(thisChromaticStartNote)
		) { caseSize = caseSize + 0.333; };

		if(
			[1, 3, 6, 8, 10]
			.includesEqual(
				(thisChromaticStartNote + (thisKeyNumber - 1))%12
			)
		) { caseSize = caseSize + 0.333; };

		caseSize = super.interactionRect.width / caseSize;

		^caseSize
	}

	prDrawKey { |aRect, outlineSize, index, type = \white|
		var blinkTimer = nil;
		var highlight = 1;
		if(thisDisplayHighlights)
		{ highlight = thisHighlights[index%thisHighlights.size]; };

		blinks.do({ |blink|
			if(blinkTimer.isNil) {
				if(blink[0] == index)
				{ blinkTimer = blink[1]; };
			};
		});

		if(thisSustainMode and: { sustains.includesEqual(index) }) {
			thisCurrentKeyColor = thisBlinkColor;
			thisCurrentFontColor = thisBlinkFontColor;
		} {
			if(blinkTimer.isNil) {
				if(type == \white) {
					thisCurrentKeyColor = Color(
						thisKeyColor.red * highlight,
						thisKeyColor.green * highlight,
						thisKeyColor.blue * highlight,
						thisKeyColor.alpha
					);
				} {
					thisCurrentKeyColor = Color(
						thisBlackKeyColor.red * highlight,
						thisBlackKeyColor.green * highlight,
						thisBlackKeyColor.blue * highlight,
						thisBlackKeyColor.alpha
					);
				};
				thisCurrentFontColor = super.valueFontColor;
			} {
				if(type == \white) {
					this.prGetTimerColor(
						blinkTimer,
						thisKeyColor,
						thisBlinkColor,
						highlight
					);
				} {
					this.prGetTimerColor(
						blinkTimer,
						thisBlackKeyColor,
						thisBlinkColor,
						highlight
					);
				};
			};
		};

		Pen.moveTo(
			aRect.leftTop
			+ Point(outlineSize / 2, outlineSize / 2);
		);
		Pen.lineTo(
			aRect.rightTop
			+ Point((outlineSize / 2).neg, outlineSize / 2);
		);
		Pen.lineTo(
			aRect.rightBottom
			+ Point((outlineSize / 2).neg, (outlineSize / 2).neg);
		);
		Pen.lineTo(
			aRect.leftBottom
			+ Point(outlineSize / 2, (outlineSize / 2).neg);
		);
		Pen.lineTo(
			aRect.leftTop
			+ Point(outlineSize / 2, outlineSize / 2);
		);

		if(thisOutlineKeys) {
			Pen.width_(outlineSize);
			Pen.strokeColor_(thisKeyOutlineColor);
			Pen.fillColor_(thisCurrentKeyColor);
			Pen.fillStroke;
		} {
			Pen.fillColor_(thisCurrentKeyColor);
			Pen.fill;
		};

		if(thisDisplayNames) {
			var noteName = thisNames[index%thisNames.size];
			if(thisMode == \chromatic) {
				noteName =
				thisNames[(index + thisChromaticStartNote)%thisNames.size];
			};
			super.stringCenteredIn(
				noteName,
				Rect(
					aRect.left,
					aRect.bottom - aRect.width,
					aRect.width,
					aRect.width
				),
				font,
				thisCurrentFontColor
			);
		};
	}

	prDrawLeftKey { |aRect, outlineSize, index|
		var blinkTimer = nil;
		var highlight = 1;
		if(thisDisplayHighlights)
		{ highlight = thisHighlights[index%thisHighlights.size]; };

		blinks.do({ |blink|
			if(blinkTimer.isNil) {
				if(blink[0] == index)
				{ blinkTimer = blink[1]; };
			};
		});

		if(thisSustainMode and: { sustains.includesEqual(index) }) {
			thisCurrentKeyColor = thisBlinkColor;
			thisCurrentFontColor = thisBlinkFontColor;
		} {
			if(blinkTimer.isNil) {
				thisCurrentKeyColor = Color(
					thisKeyColor.red * highlight,
					thisKeyColor.green * highlight,
					thisKeyColor.blue * highlight,
					thisKeyColor.alpha
				);
				thisCurrentFontColor = super.valueFontColor;
			} {
				this.prGetTimerColor(
					blinkTimer,
					thisKeyColor,
					thisBlinkColor,
					highlight
				);
			};
		};

		Pen.moveTo(
			aRect.leftTop
			+ Point(outlineSize / 2, outlineSize / 2);
		);
		Pen.lineTo(
			Point(
				aRect.left
				+ ((aRect.width / 3) * 2)
				- (thisMargins / 2)
				- (outlineSize / 2),
				aRect.top + (outlineSize / 2)
			);
		);
		Pen.lineTo(
			Point(
				aRect.left
				+ ((aRect.width / 3) * 2)
				- (thisMargins / 2)
				- (outlineSize / 2),
				aRect.top
				+ (aRect.height * thisBlackKeyRatio)
				+ (thisMargins / 2)
				+ (outlineSize / 2)
			);
		);
		Pen.lineTo(
			Point(
				aRect.right
				- (outlineSize / 2),
				aRect.top
				+ (aRect.height * thisBlackKeyRatio)
				+ (thisMargins / 2)
				+ (outlineSize / 2)
			);
		);
		Pen.lineTo(
			aRect.rightBottom
			+ Point((outlineSize / 2).neg, (outlineSize / 2).neg);
		);
		Pen.lineTo(
			aRect.leftBottom
			+ Point(outlineSize / 2, (outlineSize / 2).neg);
		);
		Pen.lineTo(
			aRect.leftTop
			+ Point(outlineSize / 2, outlineSize / 2);
		);

		if(thisOutlineKeys) {
			Pen.width_(outlineSize);
			Pen.strokeColor_(thisKeyOutlineColor);
			Pen.fillColor_(thisCurrentKeyColor);
			Pen.fillStroke;
		} {
			Pen.fillColor_(thisCurrentKeyColor);
			Pen.fill;
		};

		if(thisDisplayNames) {
			var noteName = thisNames[index%thisNames.size];
			if(thisMode == \chromatic) {
				noteName =
				thisNames[(index + thisChromaticStartNote)%thisNames.size];
			};
			super.stringCenteredIn(
				noteName,
				Rect(
					aRect.left,
					aRect.bottom - aRect.width,
					aRect.width,
					aRect.width
				),
				font,
				thisCurrentFontColor
			);
		};
	}

	prDrawRightKey { |aRect, outlineSize, index|
		var blinkTimer = nil;
		var highlight = 1;
		if(thisDisplayHighlights)
		{ highlight = thisHighlights[index%thisHighlights.size]; };

		blinks.do({ |blink|
			if(blinkTimer.isNil) {
				if(blink[0] == index)
				{ blinkTimer = blink[1]; };
			};
		});

		if(thisSustainMode and: { sustains.includesEqual(index) }) {
			thisCurrentKeyColor = thisBlinkColor;
			thisCurrentFontColor = thisBlinkFontColor;
		} {
			if(blinkTimer.isNil) {
				thisCurrentKeyColor = Color(
					thisKeyColor.red * highlight,
					thisKeyColor.green * highlight,
					thisKeyColor.blue * highlight,
					thisKeyColor.alpha
				);
				thisCurrentFontColor = super.valueFontColor;
			} {
				this.prGetTimerColor(
					blinkTimer,
					thisKeyColor,
					thisBlinkColor,
					highlight
				);
			};
		};

		Pen.moveTo(
			Point(
				aRect.left
				+ (outlineSize / 2),
				aRect.top
				+ (aRect.height * thisBlackKeyRatio)
				+ (thisMargins / 2)
				+ (outlineSize / 2)
			);
		);
		Pen.lineTo(
			Point(
				aRect.left
				+ (aRect.width / 3)
				+ (thisMargins / 2)
				+ (outlineSize / 2),
				aRect.top
				+ (aRect.height * thisBlackKeyRatio)
				+ (thisMargins / 2)
				+ (outlineSize / 2)
			);
		);
		Pen.lineTo(
			Point(
				aRect.left
				+ (aRect.width / 3)
				+ (thisMargins / 2)
				+ (outlineSize / 2),
				aRect.top + (outlineSize / 2)
			);
		);
		Pen.lineTo(
			aRect.rightTop
			+ Point((outlineSize / 2).neg, outlineSize / 2);
		);
		Pen.lineTo(
			Point(
				aRect.right
				- (outlineSize / 2),
				aRect.top
				+ (aRect.height * thisBlackKeyRatio)
				+ (thisMargins / 2)
				+ (outlineSize / 2)
			);
		);
		Pen.lineTo(
			aRect.rightBottom
			+ Point((outlineSize / 2).neg, (outlineSize / 2).neg);
		);
		Pen.lineTo(
			aRect.leftBottom
			+ Point(outlineSize / 2, (outlineSize / 2).neg);
		);
		Pen.lineTo(
			Point(
				aRect.left
				+ (outlineSize / 2),
				aRect.top
				+ (aRect.height * thisBlackKeyRatio)
				+ (thisMargins / 2)
				+ (outlineSize / 2)
			);
		);

		if(thisOutlineKeys) {
			Pen.width_(outlineSize);
			Pen.strokeColor_(thisKeyOutlineColor);
			Pen.fillColor_(thisCurrentKeyColor);
			Pen.fillStroke;
		} {
			Pen.fillColor_(thisCurrentKeyColor);
			Pen.fill;
		};

		if(thisDisplayNames) {
			var noteName = thisNames[index%thisNames.size];
			if(thisMode == \chromatic) {
				noteName =
				thisNames[(index + thisChromaticStartNote)%thisNames.size];
			};
			super.stringCenteredIn(
				noteName,
				Rect(
					aRect.left,
					aRect.bottom - aRect.width,
					aRect.width,
					aRect.width
				),
				font,
				thisCurrentFontColor
			);
		};
	}

	prDrawMiddleKey { |aRect, outlineSize, index|
		var blinkTimer = nil;
		var highlight = 1;
		if(thisDisplayHighlights)
		{ highlight = thisHighlights[index%thisHighlights.size]; };

		blinks.do({ |blink|
			if(blinkTimer.isNil) {
				if(blink[0] == index)
				{ blinkTimer = blink[1]; };
			};
		});

		if(thisSustainMode and: { sustains.includesEqual(index) }) {
			thisCurrentKeyColor = thisBlinkColor;
			thisCurrentFontColor = thisBlinkFontColor;
		} {
			if(blinkTimer.isNil) {
				thisCurrentKeyColor = Color(
					thisKeyColor.red * highlight,
					thisKeyColor.green * highlight,
					thisKeyColor.blue * highlight,
					thisKeyColor.alpha
				);
				thisCurrentFontColor = super.valueFontColor;
			} {
				this.prGetTimerColor(
					blinkTimer,
					thisKeyColor,
					thisBlinkColor,
					highlight
				);
			};
		};

		Pen.moveTo(
			Point(
				aRect.left
				+ (outlineSize / 2),
				aRect.top
				+ (aRect.height * thisBlackKeyRatio)
				+ (thisMargins / 2)
				+ (outlineSize / 2)
			);
		);
		Pen.lineTo(
			Point(
				aRect.left
				+ (aRect.width / 3)
				+ (thisMargins / 2)
				+ (outlineSize / 2),
				aRect.top
				+ (aRect.height * thisBlackKeyRatio)
				+ (thisMargins / 2)
				+ (outlineSize / 2)
			);
		);
		Pen.lineTo(
			Point(
				aRect.left
				+ (aRect.width / 3)
				+ (thisMargins / 2)
				+ (outlineSize / 2),
				aRect.top + (outlineSize / 2)
			);
		);
		Pen.lineTo(
			Point(
				aRect.left
				+ ((aRect.width / 3) * 2)
				- (thisMargins / 2)
				- (outlineSize / 2),
				aRect.top + (outlineSize / 2)
			);
		);
		Pen.lineTo(
			Point(
				aRect.left
				+ ((aRect.width / 3) * 2)
				- (thisMargins / 2)
				- (outlineSize / 2),
				aRect.top
				+ (aRect.height * thisBlackKeyRatio)
				+ (thisMargins / 2)
				+ (outlineSize / 2)
			);
		);
		Pen.lineTo(
			Point(
				aRect.right
				- (outlineSize / 2),
				aRect.top
				+ (aRect.height * thisBlackKeyRatio)
				+ (thisMargins / 2)
				+ (outlineSize / 2)
			);
		);
		Pen.lineTo(
			aRect.rightBottom
			+ Point((outlineSize / 2).neg, (outlineSize / 2).neg);
		);
		Pen.lineTo(
			aRect.leftBottom
			+ Point(outlineSize / 2, (outlineSize / 2).neg);
		);
		Pen.lineTo(
			Point(
				aRect.left
				+ (outlineSize / 2),
				aRect.top
				+ (aRect.height * thisBlackKeyRatio)
				+ (thisMargins / 2)
				+ (outlineSize / 2)
			);
		);

		if(thisOutlineKeys) {
			Pen.width_(outlineSize);
			Pen.strokeColor_(thisKeyOutlineColor);
			Pen.fillColor_(thisCurrentKeyColor);
			Pen.fillStroke;
		} {
			Pen.fillColor_(thisCurrentKeyColor);
			Pen.fill;
		};

		if(thisDisplayNames) {
			var noteName = thisNames[index%thisNames.size];
			if(thisMode == \chromatic) {
				noteName =
				thisNames[(index + thisChromaticStartNote)%thisNames.size];
			};
			super.stringCenteredIn(
				noteName,
				Rect(
					aRect.left,
					aRect.bottom - aRect.width,
					aRect.width,
					aRect.width
				),
				font,
				thisCurrentFontColor
			);
		};
	}

	prDetectChromaKey { |x, y|
		var key = 0;
		var range = Point(0, super.interactionRect.width);
		var caseSize = this.prGetChromaticCaseSize;
		var whiteKey, keyPos;
		var startNoteIsBlack =
		[1, 3, 6, 8, 10].includesEqual(thisChromaticStartNote);
		var endNoteIsBlack =
		[1, 3, 6, 8, 10]
		.includesEqual((
			(thisChromaticStartNote + (thisKeyNumber - 1))%12)
		);

		// This algo checks which white key is focused,
		// then checks if an overlapping black key was pressed instead
		if(startNoteIsBlack)
		{ range.x = (caseSize / 3); };
		if(endNoteIsBlack)
		{ range.y = super.interactionRect.width - (caseSize / 3); };

		whiteKey = x.linlin(
			super.interactionRect.left + range.x,
			super.interactionRect.left + range.y,
			0, whiteKeysNumber,
			nil
		);

		if(startNoteIsBlack)
		{ whiteKey = max(-1, whiteKey); }
		{ whiteKey = max(0, whiteKey); };
		if(endNoteIsBlack)
		{ whiteKey = min(whiteKeysNumber, whiteKey); }
		{ whiteKey = min((whiteKeysNumber - 1), whiteKey); };

		whiteKey = whiteKey.trunc(1.0);

		// Special cases (first black key or last black key)
		if(startNoteIsBlack and: { whiteKey == -1 }) {
			if(y < (
				(super.interactionRect.height * thisBlackKeyRatio)
				+ super.interactionRect.top))
			{ ^0 }
			{ ^nil };
		};

		if(endNoteIsBlack and: { whiteKey == whiteKeysNumber }) {
			if(y < (
				(super.interactionRect.height * thisBlackKeyRatio)
				+ super.interactionRect.top))
			{ ^(thisKeyNumber - 1) }
			{ ^nil };
		};

		// Reference focused white key central point
		// to check if black key has been pressed later on
		keyPos = caseSize * whiteKey;
		keyPos = keyPos + (caseSize / 2);
		keyPos = keyPos + super.interactionRect.left;
		if(startNoteIsBlack)
		{ keyPos = keyPos + (caseSize / 3); };

		while { whiteKey > 6 } {
			whiteKey = whiteKey - 7;
			key = key + 12;
		};

		if(startNoteIsBlack)
		{ key = key + 1; };

		while { whiteKey > 0 } {
			key = key + 1;
			if((keyboardLayout[
				(thisChromaticStartNote + key)%12]) == \w
			) {
				whiteKey = whiteKey - 1;
			};
		};

		if(y > (
			(super.interactionRect.height * thisBlackKeyRatio)
			+ super.interactionRect.top)) {
			^key
		} {
			// Check if black key was pressed
			if(
				(x < (keyPos - (caseSize * 0.1665)))
				and: { keyboardLayout[
					(thisChromaticStartNote + key - 1)%12] == \b }
			) { ^(key - 1) };

			if(
				(x > (keyPos + (caseSize * 0.1665)))
				and: { keyboardLayout[
					(thisChromaticStartNote + key + 1)%12] == \b }
			) { ^(key + 1) };
		};

		^key
	}

	prGetWhiteKeysNumber {
		whiteKeysNumber = 0;

		thisKeyNumber.do({ |index|
			if(
				keyboardLayout[(thisChromaticStartNote + index)%12]
				== \w
			) { whiteKeysNumber = whiteKeysNumber + 1; };
		});
	}

	prGetTimerColor { |timer, colorA, colorB, highlight|
		thisCurrentKeyColor = Color(
			(colorA.red * highlight) +
			((colorB.red - (colorA.red * highlight)) * timer),
			(colorA.green * highlight) +
			((colorB.green - (colorA.green * highlight)) * timer),
			(colorA.blue * highlight) +
			((colorB.blue - (colorA.blue * highlight)) * timer),
			colorA.alpha +
			((colorB.alpha - colorA.alpha) * timer),
		);

		if(thisDisplayNames) {
			thisCurrentFontColor = Color(
				super.valueFontColor.red +
				((thisBlinkFontColor.red - super.valueFontColor.red) * timer),
				super.valueFontColor.green +
				((thisBlinkFontColor.green - super.valueFontColor.green) * timer),
				super.valueFontColor.blue +
				((thisBlinkFontColor.blue - super.valueFontColor.blue) * timer),
				super.valueFontColor.alpha +
				((thisBlinkFontColor.alpha - super.valueFontColor.alpha) * timer),
			);
		};
	}
}