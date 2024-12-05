GMZZ2DSlider : GMXYUserView {

	var thisValues;
	var thisMin;
	var thisMax;
	var thisScales;
	var thisPolarities;

	var thisModSteps;
	var thisModStepsOperators;
	var thisModStepsPixelRanges;

	var thisHelpersNumbers;
	var thisHelperSubdivisions;

	var thisModValuesReferences = nil;
	var thisModReferences = nil;
	var thisAltModValues = nil;

	var thisExpMins;

	var altPressed = false;
	var ctrlPressed = false;
	var shiftPressed = false;

	var thisCtrlShiftBehavior = \helper;

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

		thisValues = [0, 0];
		thisMin = [0, 0];
		thisMax = [1, 1];
		thisScales = [\lin, \lin];
		thisPolarities = [\uni, \uni];
		thisExpMins = [0.001, 0.001];

		thisModSteps = [2, 2];
		thisModStepsOperators = [\mul, \mul];
		thisModStepsPixelRanges = [20, 20];

		thisHelpersNumbers = [2, 2];
		thisHelperSubdivisions = [3, 3];
	}

	// Setters & Getters - we're not using <>string,
	// because we want to refresh the view when a value has been updated

	values {
		^thisValues
	}

	values_ { |anArray|
		thisValues = anArray;
		this.refresh;
	}

	min {
		^thisMin
	}

	min_ { |anArray|
		thisMin = anArray;
		if(thisMax[0] < thisMin[0])
		{ thisMax[0] = thisMin[0]; };
		if(thisValues[0] < thisMin[0])
		{ thisValues[0] = thisMin[0]; };
		if(thisMax[1] < thisMin[1])
		{ thisMax[1] = thisMin[1]; };
		if(thisValues[1] < thisMin[1])
		{ thisValues[1] = thisMin[1]; };
		this.refresh;
	}

	max {
		^thisMax
	}

	max_ { |anArray|
		thisMax = anArray;
		if(thisMin[0] > thisMax[0])
		{ thisMin[0] = thisMax[0]; };
		if(thisValues[0] > thisMax[0])
		{ thisValues[0] = thisMax[0]; };
		if(thisMin[1] > thisMax[1])
		{ thisMin[1] = thisMax[1]; };
		if(thisValues[1] > thisMax[1])
		{ thisValues[1] = thisMax[1]; };
		this.refresh;
	}

	scales {
		^thisScales
	}

	scales_ { |anArray|
		thisScales = anArray;
		this.refresh;
	}

	expMins {
		^thisExpMins
	}

	expMins_ { |anArray|
		thisExpMins = anArray;
		this.refresh;
	}

	polarities {
		^thisPolarities
	}

	polarities_ { |anArray|
		thisPolarities = anArray;
		this.refresh;
	}

	modSteps {
		^thisModSteps
	}

	modSteps_ { |anArray|
		thisModSteps = anArray;
	}

	modStepsOperators {
		^thisModStepsOperators
	}

	modStepsOperators_ { |anArray|
		thisModStepsOperators = anArray;
	}

	modStepsPixelRanges {
		^thisModStepsPixelRanges
	}

	modStepsPixelRanges_ { |anArray|
		thisModStepsPixelRanges = anArray;
	}

	helpersNumbers {
		^thisHelpersNumbers
	}

	helpersNumbers_ { |anArray|
		thisHelpersNumbers = anArray;
		this.refresh;
	}

	helperSubdivisions {
		^thisHelperSubdivisions
	}

	helperSubdivisions_ { |anArray|
		thisHelperSubdivisions = anArray;
		this.refresh;
	}

	ctrlShiftBehavior {
		^thisCtrlShiftBehavior
	}

	ctrlShiftBehavior_ { |aSymbol|
		thisCtrlShiftBehavior = aSymbol;
	}

	// Interaction
	action_ { |aFunction|
		mouseDownAction = { |view, x, y, mod|
			var newValues = [nil, nil];

			mod = this.prCheckMod(mod);

			// ALT behavior : store current values
			if(altPressed)
			{ thisAltModValues = thisValues; };

			if(ctrlPressed.not and: { shiftPressed.not }) {
				newValues = [
					this.prUpdateValue(\x, x, y),
					this.prUpdateValue(\y, x, y)
				];
			};

			if(ctrlPressed and: { shiftPressed.not })
			{ newValues[0] = this.prUpdateValue(\x, x, y); };

			if(shiftPressed and: { ctrlPressed.not })
			{ newValues[1] = this.prUpdateValue(\y, x, y); };

			if(ctrlPressed and: { shiftPressed }) {
				if(thisCtrlShiftBehavior == \helper) {
					newValues = this.prGetClosestHelpers(x, y);
				} {
					if(altPressed.not) {
						thisModValuesReferences = thisValues;
						thisModReferences = [x, y];
					} {
						newValues = [
							this.prUpdateValue(\x, x, y),
							this.prUpdateValue(\y, x, y)
						];
						thisModValuesReferences = newValues;
						thisAltModValues = newValues;
						thisModReferences = [x, y];
					};
				};
			};

			if(newValues[0] == nil)
			{ newValues[0] = thisValues[0]; };
			if(newValues[1] == nil)
			{ newValues[1] = thisValues[1]; };

			if(thisValues != newValues) {
				thisValues = newValues;
				aFunction.(thisValues[0], thisValues[1]);
				this.refresh;
			};
		};

		mouseMoveAction = { |view, x, y, mod|
			var newValues = [nil, nil];

			mod = this.prCheckMod(mod);

			// Normal behavior : NO MOD or ALT ONLY
			if((mod == 0) or: { mod == 524288 }) {
				newValues = [
					this.prUpdateValue(\x, x, y),
					this.prUpdateValue(\y, x, y)
				];
				if(thisModReferences != nil) { thisModReferences = nil };
			};

			if(ctrlPressed and: { shiftPressed.not })
			{ newValues[0] = this.prUpdateValue(\x, x, y); };

			if(shiftPressed and: { ctrlPressed.not })
			{ newValues[1] = this.prUpdateValue(\y, x, y); };

			if(shiftPressed and: { ctrlPressed }) {
				if(thisCtrlShiftBehavior == \helper) {
					newValues = this.prGetClosestHelpers(x, y);
				} {
					if(thisModValuesReferences.isNil) {
						thisModValuesReferences = [
							this.prUpdateValue(\x, x, y),
							this.prUpdateValue(\y, x, y)
						];
						thisModReferences = [x, y];
					};
					newValues = this.prGetModStepValues(x, y);
				};
			};

			if(newValues[0] == nil)
			{ newValues[0] = thisValues[0]; };
			if(newValues[1] == nil)
			{ newValues[1] = thisValues[1]; };

			if(thisValues != newValues) {
				thisValues = newValues;
				aFunction.(thisValues[0], thisValues[1]);
				this.refresh;
			};
		};

		mouseUpAction = { |view, x, y, mod|

			mod = this.prCheckMod(mod);

			if(altPressed) {
				if(thisAltModValues.notNil) {
					if(thisValues != thisAltModValues) {
						thisValues = thisAltModValues;
						aFunction.(thisValues[0], thisValues[1]);
						this.refresh;
					};
				};
			};

			thisModReferences = nil;
			thisModValuesReferences = nil;
			if(thisAltModValues.notNil)
			{ thisAltModValues = nil; };
		};
	}

	prUpdateValue { |axis, x, y|
		var value;

		if(axis == \x) {
			if(thisPolarities[0] == \uni) {
				value = super.getXMouseMapping(
					x,
					thisMin[0],
					thisMax[0],
					thisScales[0],
					false,
					thisExpMins[0]
				);
			} {
				value = super.getXMouseMappingBipolar(
					x,
					thisMin[0],
					thisMax[0],
					thisScales[0],
					false,
					thisExpMins[0]
				);
			};
		} {
			if(thisPolarities[1] == \uni) {
				value = super.getYMouseMapping(
					y,
					thisMin[1],
					thisMax[1],
					thisScales[1],
					true,
					thisExpMins[1]
				);
			} {
				value = super.getYMouseMappingBipolar(
					y,
					thisMin[1],
					thisMax[1],
					thisScales[1],
					true,
					thisExpMins[1]
				);
			};
		};

		^value
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

	prGetClosestHelpers { |x, y|
		var nDivX = ((thisHelpersNumbers[0] - 1)
			* (thisHelperSubdivisions[0] + 1)) + 1;
		var nDivY = ((thisHelpersNumbers[1] - 1)
			* (thisHelperSubdivisions[1] + 1)) + 1;
		var values = [0, 0];
		var min;

		if(thisPolarities[0] == \uni) {
			values[0] = x.linlin(
				super.interactionRect.left,
				super.interactionRect.right,
				0,
				(nDivX - 1)
			);
		} {
			nDivX = (nDivX * 2) - 1;
			values[0] = x.linlin(
				super.interactionRect.left,
				super.interactionRect.right,
				0,
				(nDivX - 1)
			);
		};

		if(thisPolarities[1] == \uni) {
			values[1] = y.linlin(
				super.interactionRect.top,
				super.interactionRect.bottom,
				(nDivY - 1),
				0
			);
		} {
			nDivY = (nDivY * 2) - 1;
			values[1] = y.linlin(
				super.interactionRect.top,
				super.interactionRect.bottom,
				(nDivY - 1),
				0
			);
		};

		values[0] = values[0].round(1.0);
		values[0] = min(values[0], nDivX);
		values[1] = values[1].round(1.0);
		values[1] = min(values[1], nDivY);

		if(thisPolarities[0] == \uni) {
			if(values[0] == 0) { values[0] = thisMin[0]; } {
				if(values[0] == nDivX) { values[0] = thisMax[0]; } {
					if(thisScales[0] == \lin) {
						values[0] = values[0].linlin(
							0,
							(nDivX - 1),
							thisMin[0],
							thisMax[0]);
					};

					if(thisScales[0] == \exp) {
						min = thisMin[0];
						if(min == 0) { min = thisExpMins[0]; };
						values[0] = values[0].linexp(
							0,
							(nDivX - 1),
							min,
							thisMax[0]
						);
					};

					if(thisScales[0] == \log) {
						min = thisMin[0];
						if(min == 0) { min = thisExpMins[0]; };
						values[0] = ((nDivX  - 1)
							- values[0]).linexp(
							0,
							(nDivX - 1),
							min,
							thisMax[0]
						);
						values[0] = thisMax[0] - values[0];
					};

					if(thisScales[0].isKindOf(SimpleNumber)) {
						values[0] = values[0].lincurve(
							0,
							(nDivX - 1),
							thisMin[0],
							thisMax[0],
							thisScales[0])
					};
				};
			};
		} {
			if(values[0] == 0) { values[0] = thisMax[0].neg; } {
				if(values[0] == (((nDivX + 1) / 2) - 1))
				{ values[0] = thisMin[0]; } {
					if(values[0] == (nDivX - 1))
					{ values[0] = thisMax[0]; } {
						values[0] = values[0] - ((nDivX + 1) / 2);
						values[0] = values[0] + 1;

						if(thisScales[0] == \lin) {
							if(values[0] > 0) {
								values[0] = values[0].linlin(
									0,
									((nDivX + 1) / 2) - 1,
									thisMin[0],
									thisMax[0]
								);
							} {
								values[0] = values[0].abs.linlin(
									0,
									((nDivX + 1) / 2) - 1,
									thisMin[0],
									thisMax[0]
								).neg;
							};
						};

						if(thisScales[0] == \exp) {
							min = thisMin[0];
							if(min == 0) { min = thisExpMins[0]; };
							if(values[0] > 0) {
								values[0] = values[0].linexp(
									0,
									((nDivX + 1) / 2) - 1,
									min,
									thisMax[0]
								);
							} {
								values[0] = values[0].abs.linexp(
									0,
									((nDivX + 1) / 2) - 1,
									min,
									thisMax[0]
								).neg;
							};
						};

						if(thisScales[0] == \log) {
							min = thisMin[0];
							if(min == 0) { min = thisExpMins[0]; };
							if(values[0] > 0) {
								values[0] = ((((nDivX + 1) / 2) - 1) - values[0]).linexp(
									0,
									((nDivX + 1) / 2) - 1,
									min,
									thisMax[0]
								);
								values[0] = thisMax[0] - values[0];
							} {
								values[0] = ((((nDivX + 1) / 2) - 1) - values[0].abs).linexp(
									0,
									((nDivX + 1) / 2) - 1,
									min,
									thisMax[0]
								);
								values[0] = thisMax[0] - values[0];
								values[0] = values[0].neg;
							};
						};

						if(thisScales[0].isKindOf(SimpleNumber)) {
							if(values[0] > 0) {
								values[0] = values[0].lincurve(
									0,
									((nDivX + 1) / 2) - 1,
									thisMin[0],
									thisMax[0],
									thisScales[0]
								);
							} {
								values[0] = values[0].abs.lincurve(
									0,
									((nDivX + 1) / 2) - 1,
									thisMin[0],
									thisMax[0],
									thisScales[0]
								).neg;
							};
						};

					};

				};
			};
		};

		// Should be factorized, sorry
		if(thisPolarities[1] == \uni) {
			if(values[1] == 0) { values[1] = thisMin[1]; } {
				if(values[1] == nDivY) { values[1] = thisMax[1]; } {
					if(thisScales[1] == \lin) {
						values[1] = values[1].linlin(
							0,
							(nDivY - 1),
							thisMin[1],
							thisMax[1]);
					};

					if(thisScales[1] == \exp) {
						min = thisMin[1];
						if(min == 0) { min = thisExpMins[1]; };
						values[1] = values[1].linexp(
							0,
							(nDivY - 1),
							min,
							thisMax[1]
						);
					};

					if(thisScales[1] == \log) {
						min = thisMin[1];
						if(min == 1) { min = thisExpMins[1]; };
						values[1] = ((nDivY  - 1)
							- values[1]).linexp(0, (nDivY - 1), min, thisMax[1]);
						values[1] = thisMax[1] - values[1];
					};

					if(thisScales[1].isKindOf(SimpleNumber)) {
						values[1] = values[1].lincurve(
							0,
							(nDivY - 1),
							thisMin[1],
							thisMax[1],
							thisScales[1])
					};
				};
			};
		} {
			if(values[1] == 0) { values[1] = thisMax[1].neg; } {
				if(values[1] == (((nDivY + 1) / 2) - 1))
				{ values[1] = thisMin[1]; } {
					if(values[1] == (nDivY - 1))
					{ values[1] = thisMax[1]; } {
						values[1] = values[1] - ((nDivY + 1) / 2);
						values[1] = values[1] + 1;

						if(thisScales[1] == \lin) {
							if(values[1] > 0) {
								values[1] = values[1].linlin(
									0,
									((nDivY + 1) / 2) - 1,
									thisMin[1],
									thisMax[1]
								);
							} {
								values[1] = values[1].abs.linlin(
									0,
									((nDivY + 1) / 2) - 1,
									thisMin[1],
									thisMax[1]
								).neg;
							};
						};

						if(thisScales[1] == \exp) {
							min = thisMin[1];
							if(min == 0) { min = thisExpMins[1]; };
							if(values[1] > 0) {
								values[1] = values[1].linexp(
									0,
									((nDivY + 1) / 2) - 1,
									min,
									thisMax[1]
								);
							} {
								values[1] = values[1].abs.linexp(
									0,
									((nDivY + 1) / 2) - 1,
									min,
									thisMax[1]
								).neg;
							};
						};

						if(thisScales[1] == \log) {
							min = thisMin[1];
							if(min == 0) { min = thisExpMins[1]; };
							if(values[1] > 0) {
								values[1] = ((((nDivY + 1) / 2) - 1) - values[1]).linexp(
									0,
									((nDivY + 1) / 2) - 1,
									min,
									thisMax[1]
								);
								values[1] = thisMax[1] - values[1];
							} {
								values[1] = ((((nDivY + 1) / 2) - 1) - values[1].abs).linexp(
									0,
									((nDivY + 1) / 2) - 1,
									min,
									thisMax[1]
								);
								values[1] = thisMax[1] - values[1];
								values[1] = values[1].neg;
							};
						};

						if(thisScales[1].isKindOf(SimpleNumber)) {
							if(values[1] > 0) {
								values[1] = values[1].lincurve(
									0,
									((nDivY + 1) / 2) - 1,
									thisMin[1],
									thisMax[1],
									thisScales[1]
								);
							} {
								values[1] = values[1].abs.lincurve(
									0,
									((nDivY + 1) / 2) - 1,
									thisMin[1],
									thisMax[1],
									thisScales[1]
								).neg;
							};
						};

					};

				};
			};
		};

		^values
	}

	prGetModStepValues { |x, y|

		var offsets = [0, 0];
		var newValues = [0, 0];
		var reference;

		offsets[0] = x - thisModReferences[0];
		offsets[1] = thisModReferences[1] - y;

		offsets.do({ |offset, index|
			if(offset >= 0) {
				offset = offset - (offset % thisModStepsPixelRanges[index]);
				offset = offset / thisModStepsPixelRanges[index];
				offset = offset.asInteger;
			} {
				offset = offset.abs;
				offset = offset - (offset % thisModStepsPixelRanges[index]);
				offset = offset / thisModStepsPixelRanges[index];
				offset = offset.asInteger;
				offset = offset.neg;
			};

			offsets[index] = offset;
		});

		2.do({ |index|
			if(thisPolarities[index] == \uni) {
				if(thisModStepsOperators[index] == \mul) {
					reference = thisModValuesReferences[index];
					// Arbitrary problem solving
					if(reference == 0) { reference = 0.01; };

					if(offsets[index] == 0)
					{ newValues[index] = thisModValuesReferences[index]; } {
						if(offsets[index] > 0) {
							offsets[index].do({ reference = reference * thisModSteps[index]; });
							newValues[index] = reference;
						} {
							offsets[index].abs.do({
								reference = reference * thisModSteps[index].reciprocal; });
							newValues[index] = reference;
						};
					};
				} {
					if(offsets[index] == 0)
					{ newValues[index] = thisModValuesReferences[index]; } {
						if(offsets[index] > 0) {
							newValues[index] =
							thisModValuesReferences[index]
							+ (offsets[index] * thisModSteps[index]);
						} {
							newValues[index] =
							thisModValuesReferences[index]
							- (offsets[index].abs * thisModSteps[index]);
						};
					};
				};

				newValues[index] = newValues[index].clip(thisMin[index], thisMax[index]);

			} { // Bipolar scale

				if(thisModStepsOperators[index] == \mul) {
					reference = thisModValuesReferences[index];
					// Arbitrary problem solving
					if(reference == 0) { reference = 0.01; };
					// Only difference with unipolar scale
					// is to invert scroll logic on negative values
					if(reference < 0) { offsets[index] = offsets[index].neg; };

					if(offsets[index] == 0)
					{ newValues[index] = thisModValuesReferences[index]; } {
						if(offsets[index] > 0) {
							offsets[index].do({
								reference =
								reference * thisModSteps[index]; });
							newValues[index] = reference;
						} {
							offsets[index].abs.do({
								reference =
								reference * thisModSteps[index].reciprocal; });
							newValues[index] = reference;
						};
					};
				} {
					if(offsets[index] == 0)
					{ newValues[index] = thisModValuesReferences[index]; } {
						if(offsets[index] > 0) {
							newValues[index] =
							thisModValuesReferences[index]
							+ (offsets[index] * thisModSteps[index]);
						} {
							newValues[index] =
							thisModValuesReferences[index]
							- (offsets[index].abs * thisModSteps[index]);
						};
					};

					// Only difference with unipolar scale
					// is to add the min to min.neg range
					// when the min has been crossed during the input.
					if(thisMin[index] != 0) {
						if((thisModValuesReferences[index] >= thisMin[index]) and: { newValues[index] < thisMin[index] })
						{ newValues[index] = newValues[index] - (thisMin[index] * 2) + thisModSteps[index]; };
						if((thisModValuesReferences[index] <= thisMin[index].neg) and: { newValues[index] > thisMin[index].neg })
						{ newValues[index] = newValues[index] + (thisMin[index] * 2) - thisModSteps[index]; };
					};
				};

				if(newValues[index] > 0)
				{ newValues[index] = newValues[index].clip(thisMin[index], thisMax[index]); }
				{ newValues[index] = newValues[index].clip(thisMax[index].neg, thisMin[index].neg); };
			};
		});

		^newValues
	}
}