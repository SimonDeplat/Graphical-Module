GMZZSlider : GMXYUserView {

	var thisValue = 0;
	var thisMin = 0;
	var thisMax = 1;
	var thisScale = \lin;
	var thisPolarity = \uni;

	var thisOrientation = \vertical;

	var thisModStep = 0.01;
	var thisModStepOperator = \add;
	var thisModStep2 = 2;
	var thisModStep2Operator = \mul;
	var thisModValueReference = nil;
	var thisModReference = nil;
	var thisAltModValue = nil;
	var thisModStepPixelRange = 3;
	var thisModStep2PixelRange = 20;

	var thisHelpersNumber = 2;
	var thisHelperSubdivisions = 3;

	var thisExpMin = 0.001;

	var altPressed = false;
	var ctrlPressed = false;
	var shiftPressed = false;

	*new {
		^super.new.init;
	}

	init {
		super.init;

		// Allowed events
		this.setEventHandler(
			QObject.mouseUpEvent,
			\mouseDownEvent, true);
		this.setEventHandler(
			QObject.mouseUpEvent,
			\mouseUpEvent, true);
		this.setEventHandler(
			QObject.mouseMoveEvent,
			\mouseMoveEvent, true);

		// Forbidden events
		this.setEventHandler(
			QObject.mouseDblClickEvent,
			\mouseDownEvent, false);

		this.action_({});
	}

	// Setters & Getters - we're not using <>string,
	// because we want to refresh the view when a value has been updated

	value {
		^thisValue
	}

	value_ { |aNumber|
		aNumber = aNumber.clip(thisMin, thisMax);
		thisValue = aNumber;
		this.refresh;
	}

	min {
		^thisMin
	}

	min_ { |aNumber|
		thisMin = aNumber;
		if(thisMax < thisMin)
		{ thisMax = thisMin; };
		if(thisValue < thisMin)
		{ thisValue = thisMin; };
		this.refresh;
	}

	max {
		^thisMax
	}

	max_ { |aNumber|
		thisMax = aNumber;
		if(thisMin > thisMax)
		{ thisMin = thisMax; };
		if(thisValue > thisMax)
		{ thisValue = thisMax; };
		this.refresh;
	}

	scale {
		^thisScale
	}

	scale_ { |aScale|
		if(
			(aScale == \lin) ||
			(aScale == \exp) ||
			(aScale == \log) ||
			(aScale.isKindOf(SimpleNumber))
		) {
			thisScale = aScale;
			this.refresh;
		} {
			"GMZZSlider: wrong scale assignment, should be \lin, \exp, \log or a number".warn;
		};
	}

	expMin {
		^thisExpMin
	}

	expMin_ { |aNumber|
		thisExpMin = aNumber;
		this.refresh;
	}

	polarity {
		^thisPolarity
	}

	polarity_ { |aSymbol|
		if((aSymbol == \uni) or: { aSymbol == \bi }) {
			thisPolarity = aSymbol;
			this.refresh;
		} {
			"GMZZSlider: wrong polarity symbol, should be \uni or \bi".warn;
		};
	}

	// Graphical settings
	orientation {
		^thisOrientation
	}

	orientation_ { |aSymbol|
		thisOrientation = aSymbol;
		this.refresh;
	}

	modStep {
		^thisModStep
	}

	modStep_ { |aNumber|
		thisModStep = aNumber;
	}

	modStepOperator {
		^thisModStepOperator
	}

	modStepOperator_ { |aSymbol|
		thisModStepOperator = aSymbol;
	}

	modStepPixelRange {
		^thisModStepPixelRange
	}

	modStepPixelRange_ { |aNumber|
		thisModStepPixelRange = aNumber;
	}

	modStep2 {
		^thisModStep2
	}

	modStep2_ { |aNumber|
		thisModStep2 = aNumber;
	}

	modStep2Operator {
		^thisModStep2Operator
	}

	modStep2Operator_ { |aSymbol|
		thisModStep2Operator = aSymbol;
	}

	modStep2PixelRange {
		^thisModStep2PixelRange
	}

	modStep2PixelRange_ { |aNumber|
		thisModStep2PixelRange = aNumber;
	}

	modSteps {
		^[thisModStep, thisModStep2]
	}

	modSteps_ { |anArray|
		thisModStep = anArray[0];
		thisModStep2 = anArray[1];
	}

	modStepsOperators {
		^[thisModStepOperator, thisModStep2Operator]
	}

	modStepsOperators_ { |anArray|
		thisModStepOperator = anArray[0];
		thisModStep2Operator = anArray[1];
	}

	modStepsPixelRanges {
		^[thisModStepPixelRange, thisModStep2PixelRange]
	}

	modStepsPixelRanges_ { |anArray|
		thisModStepPixelRange = anArray[0];
		thisModStep2PixelRange = anArray[1];
	}

	helpersNumber {
		^thisHelpersNumber
	}

	helpersNumber_ { |anInteger|
		thisHelpersNumber = anInteger;
		this.refresh;
	}

	helperSubdivisions {
		^thisHelperSubdivisions
	}

	helperSubdivisions_ { |anInteger|
		thisHelperSubdivisions = anInteger;
		this.refresh;
	}

	// Interaction
	action_ { |aFunction|
		mouseDownAction = { |view, x, y, mod|
			var newValue;
			// Ckeck if we have to update at the end
			var update = false;

			// We'll block the normal value update
			// if SHIFT without CTRL is pressed
			var shiftBlock = false;

			mod = this.prCheckMod(mod);

			// ALT behavior : store current value, then update value
			if(altPressed) {
				thisAltModValue = thisValue;
				update = true;
			};

			// Normal behavior
			if(mod == 0) {
				update = true;
			};

			// SHIFT behavior without CTRL :
			// get closest helper
			if(shiftPressed and: { ctrlPressed.not }) {
				thisValue = this.prGetClosestHelper(x, y);
				update = true;
				shiftBlock = true;
			};

			if(update) {
				if(shiftBlock.not) {
					newValue = this.prUpdateValue(x, y);
					if(thisValue != newValue) {
						thisValue = newValue;
						aFunction.(thisValue);
						this.refresh;
					};
				} {
					aFunction.(thisValue);
					this.refresh;
				};
			};

			// CTRL behavior : store both value and mouse pos
			if(ctrlPressed) {
				if(thisOrientation == \horizontal)
				{ thisModReference = x; }
				{ thisModReference = y; };
				thisModValueReference = thisValue;
			};

		};

		mouseMoveAction = { |view, x, y, mod|
			var newValue, modStep, modOperator, pixelRange;
			mod = this.prCheckMod(mod);

			// Normal behavior : NO MOD or ALT ONLY
			if((mod == 0) or: { mod == 524288 }) {
				newValue = this.prUpdateValue(x, y);
				if(thisValue != newValue) {
					thisValue = newValue;
					aFunction.(thisValue);
					this.refresh;
				};
				if(thisModReference != nil) { thisModReference = nil };
			};

			// SHIFT behavior without CTRL :
			// get closest helper
			if(shiftPressed and: { ctrlPressed.not }) {
				newValue = this.prGetClosestHelper(x, y);
				if(thisValue != newValue) {
					thisValue = newValue;
					aFunction.(thisValue);
					this.refresh;
				};
			};

			// CTRL behavior :
			if(ctrlPressed) {
				// First check if it was pressed before,
				// if not, store references
				if(ctrlPressed and: { thisModReference.isNil }) {
					if(thisOrientation == \horizontal)
					{ thisModReference = x; }
					{ thisModReference = y; };
					thisModValueReference = thisValue;
				};

				// Then select adequate mod step
				if(shiftPressed.not) {
					modStep = thisModStep;
					modOperator = thisModStepOperator;
					pixelRange = thisModStepPixelRange;
				} {
					modStep = thisModStep2;
					modOperator = thisModStep2Operator;
					pixelRange = thisModStep2PixelRange;
				};

				// Finally, update value according to the modstep
				newValue = this.prGetModStepValue(
					x, y,
					modStep,
					modOperator,
					pixelRange
				);

				// Apply if new value differs
				if(thisValue != newValue) {
					thisValue = newValue;
					aFunction.(thisValue);
					this.refresh;
				};
			} {
				// else if ctrl not pressed,
				// reset references if needed
				if(thisModReference.notNil) {
					thisModValueReference = nil;
					thisModReference = nil;
				};
			};

		};

		mouseUpAction = { |view, x, y, mod|

			mod = this.prCheckMod(mod);

			if(altPressed) {
				if(thisAltModValue.notNil) {
					if(thisValue != thisAltModValue) {
						thisValue = thisAltModValue;
						aFunction.(thisValue);
						this.refresh;
					};
				};
			};

			thisModReference = nil;
			thisModValueReference = nil;
			if(thisAltModValue.notNil)
			{ thisAltModValue = nil; };
		};
	}

	mouseDownAction_ { |aFunction|
		this.action_(aFunction);
	}

	prUpdateValue { |x, y|
		var value;

		if(thisOrientation == \horizontal) {
			if(thisPolarity == \uni) {
				value = super.getXMouseMapping(
					x,
					thisMin,
					thisMax,
					thisScale,
					false,
					thisExpMin
				);
			} {
				value = super.getXMouseMappingBipolar(
					x,
					thisMin,
					thisMax,
					thisScale,
					false,
					thisExpMin
				);
			};
		} {
			if(thisPolarity == \uni) {
				value = super.getYMouseMapping(
					y,
					thisMin,
					thisMax,
					thisScale,
					true,
					thisExpMin
				);
			} {
				value = super.getYMouseMappingBipolar(
					y,
					thisMin,
					thisMax,
					thisScale,
					true,
					thisExpMin
				);
			};
		};

		^value
	}

	prGetModStepValue { |x, y, modStep, modOperator, pixelRange|

		var offset;
		var newValue;
		var reference;

		if(thisOrientation == \horizontal)
		{ offset = x - thisModReference; }
		{ offset = thisModReference - y; };

		if(offset >= 0) {
			offset = offset - (offset % pixelRange);
			offset = offset / pixelRange;
			offset = offset.asInteger;
		} {
			offset = offset.abs;
			offset = offset - (offset % pixelRange);
			offset = offset / pixelRange;
			offset = offset.asInteger;
			offset = offset.neg;
		};

		if(thisPolarity == \uni) {
			if(modOperator == \mul) {
				reference = thisModValueReference;
				// Arbitrary problem solving
				if(reference == 0) { reference = 0.01; };

				if(offset == 0)
				{ newValue = thisModValueReference; } {
					if(offset > 0) {
						offset.do({ reference = reference * modStep; });
						newValue = reference;
					} {
						offset.abs.do({ reference = reference * modStep.reciprocal; });
						newValue = reference;
					};
				};
			} {
				if(offset == 0)
				{ newValue = thisModValueReference; } {
					if(offset > 0) {
						newValue = thisModValueReference + (offset * modStep);
					} {
						newValue = thisModValueReference - (offset.abs * modStep);
					};
				};
			};

			newValue = newValue.clip(thisMin, thisMax);

		} { // Bipolar scale

			if(modOperator == \mul) {
				reference = thisModValueReference;
				// Arbitrary problem solving
				if(reference == 0) { reference = 0.01; };
				// Only difference with unipolar scale
				// is to invert scroll logic on negative values
				if(reference < 0) { offset = offset.neg; };

				if(offset == 0)
				{ newValue = thisModValueReference; } {
					if(offset > 0) {
						offset.do({ reference = reference * modStep; });
						newValue = reference;
					} {
						offset.abs.do({ reference = reference * modStep.reciprocal; });
						newValue = reference;
					};
				};
			} {
				if(offset == 0)
				{ newValue = thisModValueReference; } {
					if(offset > 0) {
						newValue = thisModValueReference + (offset * modStep);
					} {
						newValue = thisModValueReference - (offset.abs * modStep);
					};
				};

				// Only difference with unipolar scale
				// is to add the min to min.neg range
				// when the min has been crossed during the input.
				if(thisMin != 0) {
					if((thisModValueReference >= thisMin) and: { newValue < thisMin })
					{ newValue = newValue - (thisMin * 2) + modStep; };
					if((thisModValueReference <= thisMin.neg) and: { newValue > thisMin.neg })
					{ newValue = newValue + (thisMin * 2) - modStep; };
				};
			};

			if(newValue > 0)
			{ newValue = newValue.clip(thisMin, thisMax); }
			{ newValue = newValue.clip(thisMax.neg, thisMin.neg); };
		};

		^newValue
	}

	prCheckMod { |mod|
		// MOD VALUES
		// CTRL : 262144
		// SHIFT : 131072
		// CTRL + SHIFT : 393216
		// ALT : 524288
		// ALT + CTRL : 786432
		// ALT + SHIFT: 655360
		// ALT + SHIFT + CTRL: 917504

		// Check if mod is supported
		if([
			262144,
			131072,
			393216,
			524288,
			655360,
			786432,
			917504
		].includes(mod).not) {
			mod = 0;
			altPressed = false;
			ctrlPressed = false;
			shiftPressed = false;
		} {
			altPressed =
			[524288, 655360, 786432, 917504].includes(mod);
			ctrlPressed =
			[262144, 393216, 786432, 917504].includes(mod);
			shiftPressed =
			[131072, 393216, 655360, 917504].includes(mod);
		};

		^mod
	}

	prGetClosestHelper { |x, y|
		var nDiv = ((thisHelpersNumber - 1) * (thisHelperSubdivisions + 1)) + 1;
		var value;
		var min;

		if(thisPolarity == \uni) {
			if(thisOrientation == \horizontal) {
				value = x.linlin(
					super.interactionRect.left,
					super.interactionRect.right,
					0,
					(nDiv - 1)
				);
			} {
				value = y.linlin(
					super.interactionRect.top,
					super.interactionRect.bottom,
					(nDiv - 1),
					0
				);
			};

			value = value.round(1.0);
			value = min(value, nDiv);

			if(value == 0) { value = thisMin; } {
				if(value == nDiv) { value = thisMax; } {

					if(thisScale == \lin)
					{ value = value.linlin(0, (nDiv - 1), thisMin, thisMax); };

					if(thisScale == \exp) {
						min = thisMin;
						if(min == 0) { min = thisExpMin; };
						value = value.linexp(0, (nDiv - 1), min, thisMax);
					};

					if(thisScale == \log) {
						min = thisMin;
						if(min == 0) { min = thisExpMin; };
						value = ((nDiv  - 1) - value).linexp(0, (nDiv - 1), min, thisMax);
						value = thisMax - value;
					};

					if(thisScale.isKindOf(SimpleNumber))
					{ value = value.lincurve(0, (nDiv - 1), thisMin, thisMax, thisScale); };
				};
			};
		} {
			nDiv = (nDiv * 2) - 1;

			if(thisOrientation == \horizontal) {
				value = x.linlin(
					super.interactionRect.left,
					super.interactionRect.right,
					0,
					(nDiv - 1)
				);
			} {
				value = y.linlin(
					super.interactionRect.top,
					super.interactionRect.bottom,
					(nDiv - 1),
					0
				);
			};

			value = value.round(1.0);
			value = min(value, nDiv);

			if(value == 0) { value = thisMax.neg; } {
				if(value == (((nDiv + 1) / 2) - 1))
				{ value = thisMin; } {
					if(value == (nDiv - 1)) { value = thisMax; } {

						value = value - ((nDiv + 1) / 2);
						value = value + 1;

						if(thisScale == \lin) {
							if(value > 0) {
								value = value.linlin(
									0,
									((nDiv + 1) / 2) - 1,
									thisMin,
									thisMax
								);
							} {
								value = value.abs.linlin(
									0,
									((nDiv + 1) / 2) - 1,
									thisMin,
									thisMax
								).neg;
							};
						};

						if(thisScale == \exp) {
							min = thisMin;
							if(min == 0) { min = thisExpMin; };
							if(value > 0) {
								value = value.linexp(
									0,
									((nDiv + 1) / 2) - 1,
									min,
									thisMax
								);
							} {
								value = value.abs.linexp(
									0,
									((nDiv + 1) / 2) - 1,
									min,
									thisMax
								).neg;
							};
						};

						if(thisScale == \log) {
							min = thisMin;
							if(min == 0) { min = thisExpMin; };
							if(value > 0) {
								value = ((((nDiv + 1) / 2) - 1) - value).linexp(
									0,
									((nDiv + 1) / 2) - 1,
									min,
									thisMax
								);
								value = thisMax - value;
							} {
								value = ((((nDiv + 1) / 2) - 1) - value.abs).linexp(
									0,
									((nDiv + 1) / 2) - 1,
									min,
									thisMax
								);
								value = thisMax - value;
								value = value.neg;
							};
						};

						if(thisScale.isKindOf(SimpleNumber)) {
							if(value > 0) {
								value = value.lincurve(
									0,
									((nDiv + 1) / 2) - 1,
									thisMin,
									thisMax,
									thisScale
								);
							} {
								value = value.abs.lincurve(
									0,
									((nDiv + 1) / 2) - 1,
									thisMin,
									thisMax,
									thisScale
								).neg;
							};
						};
					};
				};
			};
		};

		^value
	}
}