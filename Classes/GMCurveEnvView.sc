GMCurveEnvView : GMXYUserView {
	var thisEnv;
	var thisTimeScale = 2.0;
	var thisHandleSize = 12;
	var thisResolution = 50;
	var thisShapeAlpha = 0.5;
	var hoveredPoint = -1;
	var selectedPoint = -1;
	var hoveredHandle = -1;
	var selectedHandle = -1;
	var thisYMin = 0;
	var thisYMax = 1;
	var thisModColor;
	var currentTime = 0;
	var thisLastLevelIsZero = false;
	var thisMaxSize = inf;
	var ctrlPressed = false;
	var mousePos;
	var thisAction;
	var thisActionOnMove = true;
	var thisXHelpers = 3;
	var thisYHelpers = 3;
	var thisDisplayHelpers = false;
	var thisHelpersWidth = 1;
	var thisHelpersFontSize = 10;
	var thisXRoundValue = 0.01;
	var thisYRoundValue = 0.01;

	*new {
		^super.new.init;
	}

	init {
		super.init;
		this.drawFunc_({ this.draw; });

		thisEnv = Env(
			[0, 1, 0],
			[0.05, 0.5],
			[0, 0]
		);

		thisModColor = Color(0.0, 1.0, 1.0);
		mousePos = Point(0, 0);

		this.mouseOverAction_({ |view, x, y|
			if((selectedPoint == -1)
				and: { selectedHandle == -1; }) {
				this.prCheckHovering(x, y);
			};
			mousePos = Point(x, y);
			if(ctrlPressed) { this.refresh; };
		});

		this.keyDownAction_({ |view, char, mod, unicode, keycode|
			if(keycode == 65507) {
				ctrlPressed = true;
				this.refresh;
			};
		});

		this.keyUpAction_({ |view, char, mod, unicode, keycode|
			if(keycode == 65507) {
				ctrlPressed = false;
				this.refresh;
			};
		});

		this.mouseDownAction_({ |view, x, y, mod, button|

			// Hovering check for tactile screen
			this.prCheckHovering(x, y);

			if(button == 0) {
				if(hoveredPoint != -1) {
					selectedPoint = hoveredPoint;
					hoveredPoint = -1;
					if(selectedPoint > 0) {
						currentTime = thisEnv.times[selectedPoint - 1];
						if(selectedPoint < (thisEnv.levels.size - 1)) {
							currentTime =
							currentTime + thisEnv.times[selectedPoint];
						};
					};
					this.refresh;
				};

				if(hoveredHandle != -1) {
					selectedHandle = hoveredHandle;
					hoveredHandle = -1;
					this.refresh;
				};

				if(((((
					ctrlPressed
					and: { this.prMousePosInbounds })
				and: { hoveredPoint == -1 })
				and: { selectedPoint == -1 })
				and: { hoveredHandle == -1 })
				and: { selectedHandle == -1 })
				{ this.prAddPoint; };
			};

			if(button == 1) {
				if((hoveredPoint > 0)
					and: { thisEnv.levels.size > 2 }) {
					this.prRemovePoint;
				};

				if(hoveredHandle != -1) {
					thisEnv.curves[hoveredHandle] = 0;
					thisAction.value(thisEnv.deepCopy);
					this.prCheckHovering(x, y);
					this.refresh;
				};
			};
		});

		this.mouseUpAction_({ |view, x, y, mod, button|
			if(button == 0) {
				if(selectedPoint != -1) {
					if(thisActionOnMove.not)
					{ thisAction.value(thisEnv.deepCopy); };
					selectedPoint = -1;
					this.prCheckHovering(x, y);
					this.refresh;
				};

				if(selectedHandle != -1) {
					if(thisActionOnMove.not)
					{ thisAction.value(thisEnv.deepCopy); };
					selectedHandle = -1;
					this.prCheckHovering(x, y);
					this.refresh;
				};

				mousePos = Point(x, y);
			};
		});

		this.mouseMoveAction_({ |view, x, y, mod|
			if(selectedHandle != -1) {
				this.prMoveHandle(y);
			};

			if(selectedPoint != -1) {
				this.prMovePoint(x, y);
			};
		});

		// Recursively enable parent.acceptsMouseOver
		{
			var item = this;
			while { item.notNil } {
				item = item.parent;
				if(item.isKindOf(View))
				{ item.acceptsMouseOver_(true); };
			};
		}.defer(0.1);
		// And enable keyboard focus
		// when mouse enters view
		this.mouseEnterAction_({ |view|
			view.focus(true); });

		// Reset ctrl pressed and focus on mouseLeave
		// to avoid 'artifacts'
		this.mouseLeaveAction_({ |view|
			view.focus(false);
			if(ctrlPressed) {
				ctrlPressed = false;
				this.refresh;
			};
		});

		this.action_({});
	}

	env {
		^thisEnv
	}

	env_ { |anEnv, callback = true|
		var envModified = false;
		var levels = anEnv.levels.copyRange(
			0,
			min(
				anEnv.levels.size - 1,
				thisMaxSize
			).asInteger
		);
		var times = anEnv.times.copyRange(
			0, levels.size - 2);
		var curves;

		// anEnv.curves can either be an array,
		// or a single number, so conversion is needed
		if(anEnv.curves.isMemberOf(Array)) {
			curves = anEnv.curves.copyRange(
				0, levels.size - 2);
		} {
			curves = Array.fill(
				times.size, { anEnv.curves });
		};

		if(anEnv.levels.size > thisMaxSize)
		{ envModified = true; };

		curves.do({ |item, index|
			if(item.isKindOf(Number).not) {
				if(item != \lin)
				{ envModified = true; };
				curves[index] = 0;
			};
		});

		if(thisLastLevelIsZero and: { levels[levels.size - 1] != 0 }) {
			levels[levels.size - 1] = 0;
			envModified = true;
		};

		thisEnv = Env(levels, times, curves);
		if(envModified and: { callback })
		{ thisAction.value(thisEnv.deepCopy); };
		{ this.refresh; }.defer;
	}

	yMin {
		^thisYMin
	}

	yMin_ { |aNumber|
		thisYMin = aNumber;
		if(thisYMin != 0)
		{ thisLastLevelIsZero = false; };
		this.refresh;
	}

	yMax {
		^thisYMax
	}

	yMax_ { |aNumber|
		thisYMax = aNumber;
		this.refresh;
	}

	lastLevelIsZero {
		^thisLastLevelIsZero
	}

	lastLevelIsZero_ { |aBoolean|
		thisLastLevelIsZero = aBoolean;
		if(thisLastLevelIsZero) {
			thisEnv.levels[thisEnv.levels.size - 1] = 0;
			this.refresh;
		};
	}

	maxSize {
		^thisMaxSize
	}

	maxSize_ { |anInteger|
		thisMaxSize = anInteger;
	}

	modColor {
		^thisModColor
	}

	modColor_ { |aColor|
		thisModColor = aColor;
	}

	actionOnMove {
		^thisActionOnMove
	}

	actionOnMove_ { |aBoolean|
		thisActionOnMove = aBoolean;
	}

	xHelpers {
		^thisXHelpers
	}

	xHelpers_ { |anInteger|
		thisXHelpers = anInteger;
		if(thisDisplayHelpers)
		{ this.refresh; };
	}

	yHelpers {
		^thisYHelpers
	}

	yHelpers_ { |anInteger|
		thisYHelpers = anInteger;
		if(thisDisplayHelpers)
		{ this.refresh; };
	}

	helpersFontSize {
		^thisHelpersFontSize
	}

	helpersFontSize_ { |aNumber|
		thisHelpersFontSize = aNumber;
		if(thisDisplayHelpers)
		{ this.refresh; };
	}

	helpersWidth {
		^thisHelpersWidth
	}

	helpersWidth_ { |aNumber|
		thisHelpersWidth = aNumber;
		if(thisDisplayHelpers)
		{ this.refresh; };
	}

	xRoundValue {
		^thisXRoundValue
	}

	xRoundValue_ { |aNumber|
		thisXRoundValue = aNumber;
		if(thisDisplayHelpers)
		{ this.refresh; };
	}

	yRoundValue {
		^thisYRoundValue
	}

	yRoundValue_ { |aNumber|
		thisYRoundValue = aNumber;
		if(thisDisplayHelpers)
		{ this.refresh; };
	}

	displayHelpers {
		^thisDisplayHelpers
	}

	displayHelpers_ { |aBoolean|
		thisDisplayHelpers = aBoolean;
		this.refresh;
	}

	timeScale {
		^thisTimeScale
	}

	timeScale_ { |aNumber|
		thisTimeScale = aNumber;
		this.refresh;
	}

	action_ { |aFunction|
		thisAction = aFunction;
	}

	prMoveHandle { |y|
		var newMidLevel;
		var positions = this.prGetPointPositions;
		var min =
		super.interactionRect.height -
		max(
			positions[selectedHandle].y,
			positions[selectedHandle + 1].y
		) + super.interactionRect.top;

		var max =
		super.interactionRect.height -
		min(
			positions[selectedHandle].y,
			positions[selectedHandle + 1].y
		) + super.interactionRect.top;
		var curve;
		y = super.interactionRect.height - y;
		y = y + super.interactionRect.top;
		y = y.clip(0, super.interactionRect.height);
		newMidLevel = y.linlin(
			min, max,
			0.000001, 0.999999
		);

		// Check if curve is actually linear
		if(newMidLevel.fuzzyEqual(
			0.5,
			0.0001
		) > 0) {
			thisEnv.curves[selectedHandle] = 0;
		} {
			// Thank you internet for this
			curve = 1 - (2 * newMidLevel);
			curve = curve + newMidLevel.pow(2);
			curve = curve / newMidLevel.pow(2);
			curve = log(curve);
			if(positions[selectedHandle].y < positions[selectedHandle + 1].y)
			{ curve = curve.neg; };
			thisEnv.curves[selectedHandle] = curve;
		};

		if(thisActionOnMove)
		{ thisAction.value(thisEnv.deepCopy); };
		this.refresh;
	}

	prMovePoint { |x, y|
		var newLevel, positions;

		// Clip X inside view bounds to avoid trouble
		x = x.clip(super.interactionRect.left, super.interactionRect.right);

		y = super.interactionRect.height - y;
		y = y + super.interactionRect.top;
		y = y.clip(0, super.interactionRect.height);

		if((
			(selectedPoint == (thisEnv.levels.size - 1))
			and: { thisLastLevelIsZero }).not) {
			newLevel = y.linlin(
				0, super.interactionRect.height,
				thisYMin, thisYMax
			);

			thisEnv.levels[selectedPoint] = newLevel;
		};

		if(selectedPoint > 0) {
			positions = this.prGetPointPositions;
			x = max(
				x,
				positions[selectedPoint - 1].x
			);

			if(selectedPoint < (thisEnv.levels.size - 1)) {
				x = min(
					x,
					positions[selectedPoint + 1].x
				);

				x = x.linlin(
					positions[selectedPoint - 1].x,
					positions[selectedPoint + 1].x,
					0.0001, currentTime - 0.0002
				);

				thisEnv.times[selectedPoint - 1] = 0.0001 + x;
				thisEnv.times[selectedPoint] = (currentTime - x) - 0.0001;

			} { // Else, last point
				positions = this.prGetPointPositions;

				x = x - positions[positions.size - 2].x;

				thisEnv.times[selectedPoint - 1] = max(
					x.linlin(
						0, super.interactionRect.width,
						0, thisTimeScale
					),
					0.001
				);
			};
		};

		if(thisActionOnMove)
		{ thisAction.value(thisEnv.deepCopy); };
		this.refresh;
	}

	prRemovePoint {
		var levels, times, curves;

		if(hoveredPoint < (thisEnv.levels.size - 1)) {
			levels = thisEnv.levels.deepCopy;
			times = thisEnv.times.deepCopy;
			curves = thisEnv.curves.deepCopy;
			levels.removeAt(hoveredPoint);
			curves.removeAt(hoveredPoint);
			times[hoveredPoint - 1] =
			times[hoveredPoint - 1] + times[hoveredPoint];
			times.removeAt(hoveredPoint);
		} { // Else, last point
			levels = thisEnv.levels.copyRange(0, thisEnv.levels.size - 2);
			times = thisEnv.times.copyRange(0, thisEnv.times.size - 2);
			curves = thisEnv.curves.copyRange(0, thisEnv.curves.size - 2);
			if(thisLastLevelIsZero)
			{ levels[levels.size - 1] = 0; };
		};

		thisEnv = Env(levels, times, curves);
		thisAction.value(thisEnv.deepCopy);
		this.prCheckHovering(mousePos.x, mousePos.y);
		this.refresh;
	}

	draw {
		var positions = this.prGetPointPositions;
		super.drawBackground(super.backgroundColor);
		this.prFillShape(positions);
		if(thisDisplayHelpers)
		{ this.prDrawHelpers; };
		this.prDrawLine(positions);
		super.drawBorder;
		this.prDrawHandles;
		this.prDrawPoints(positions);
		if(((((
			ctrlPressed
			and: { this.prMousePosInbounds })
		and: { hoveredPoint == -1 })
		and: { selectedPoint == -1 })
		and: { hoveredHandle == -1 })
		and: { selectedHandle == -1 })
		{ this.prDrawNewPoint(positions); };
	}

	prCheckHovering { |x, y|
		var hover = -1;
		var changed = false;
		var positions = this.prGetPointPositions;

		positions.do({ |position, index|
			if(hover == -1) {
				if(
					this.prCollideWith(
						Point(x, y),
						position
					)
				) { hover = index; };
			};
		});

		if(hoveredPoint != hover) {
			hoveredPoint = hover;
			changed = true;
		};

		if(hover == -1) {
			positions = this.prGetHandlePositions;

			positions.do({ |position, index|
				if(hover == -1) {
					if(
						this.prCollideWith(
							Point(x, y),
							position
						)
					) { hover = index; };
				};
			});

			if(hoveredHandle != hover) {
				hoveredHandle = hover;
				changed = true;
			};
		} {
			if(hoveredHandle != -1) {
				hoveredHandle = -1;
				changed = true;
			};
		};

		if(changed) { this.refresh; };
	}

	prGetPointPositions {
		var currentTime = 0;
		var positions = List(0);

		thisEnv.levels.do({ |level, index|
			if(index > 0)
			{ currentTime = currentTime + thisEnv.times[index - 1]; };

			positions.add(
				Point(
					currentTime.linlin(
						0, thisTimeScale,
						super.interactionRect.left,
						super.interactionRect.right,
						nil
					),
					level.linlin(
						thisYMin, thisYMax,
						super.interactionRect.bottom,
						super.interactionRect.top
					)
				);
			);
		});

		^positions
	}

	prGetHandlePositions {
		var positions = List(0);
		var pointPositions = this.prGetPointPositions;
		var env = thisEnv.deepCopy;

		(pointPositions.size - 1).do({ |index|
			var midLevel = 0;
			index.do({ |i|
				midLevel = midLevel + thisEnv.times[i];
			});
			midLevel = midLevel + (thisEnv.times[index] / 2);
			midLevel = env.at(midLevel);

			positions.add(
				Point(
					pointPositions[index].x
					+ ((pointPositions[index + 1].x
						- pointPositions[index].x) / 2),
					midLevel.linlin(
						thisYMin, thisYMax,
						super.interactionRect.bottom,
						super.interactionRect.top
					)
				);
			);
		});

		^positions
	}

	prFillShape { |positions|
		var dx, dy, denom, ph, numer, y;
		var color = super.mainColor.deepCopy;
		color.alpha = thisShapeAlpha;

		Pen.moveTo(
			Point(
				0,
				0.linlin(
					thisYMin, thisYMax,
					super.interactionRect.bottom,
					super.interactionRect.top
				);
			)
		);
		Pen.lineTo(positions[0]);

		(positions.size - 1).do({ |index|

			if(index > 0)
			{ Pen.lineTo(positions[index]); };

			if((thisEnv.curves[index].abs < 0.0001).not) {
				dx = (positions[index + 1].x - positions[index].x);
				dy = (positions[index + 1].y - positions[index].y);
				denom = 1.0 - exp(thisEnv.curves[index]);
				ph = 1 / thisResolution;
				while({ ph < (1 - (1 / thisResolution)) }) {
					numer = 1.0 - exp(ph * thisEnv.curves[index]);
					y = positions[index].y + (dy * (numer / denom));
					Pen.lineTo(
						Point(
							positions[index].x + (dx * ph),
							y
						)
					);
					ph = ph + (1 / thisResolution);
				};
			};
		});

		Pen.lineTo(positions[positions.size - 1]);
		Pen.lineTo(
			Point(
				positions[positions.size - 1].x,
				0.linlin(
					thisYMin, thisYMax,
					super.interactionRect.bottom,
					super.interactionRect.top
				);
			);
		);
		Pen.fillColor_(color);
		Pen.fill;
	}

	prDrawLine { |positions|
		var dx, dy, denom, ph, numer, y;
		// See QcGraph.cpp, case QcGraphElement::Curvature:, line 739
		Pen.width_(super.outlineSize);
		Pen.strokeColor_(super.outlineColor);

		(positions.size - 1).do({ |index|

			Pen.moveTo(positions[index]);

			if((thisEnv.curves[index].abs < 0.0001).not) {
				dx = (positions[index + 1].x - positions[index].x);
				dy = (positions[index + 1].y - positions[index].y);
				denom = 1.0 - exp(thisEnv.curves[index]);
				ph = 1 / thisResolution;
				while({ ph < (1 - (1 / thisResolution)) }) {
					numer = 1.0 - exp(ph * thisEnv.curves[index]);
					y = positions[index].y + (dy * (numer / denom));
					Pen.lineTo(
						Point(
							positions[index].x + (dx * ph),
							y
						)
					);
					ph = ph + (1 / thisResolution);
				};
			};
			Pen.lineTo(positions[index + 1]);

			Pen.stroke;
		});
	}

	prDrawHandles {
		var positions = this.prGetHandlePositions;

		positions.do({ |position, index|
			if(index == hoveredHandle) {
				Pen.fillColor_(thisModColor);
			} {
				if(index == selectedHandle) {
					Pen.fillColor_(super.selectedColor);
				} {
					Pen.fillColor_(super.outlineColor);
				};
			};

			Pen.fillRect(
				Rect(
					position.x
					- (thisHandleSize / 2),
					position.y
					- (thisHandleSize / 2),
					thisHandleSize, thisHandleSize
				)
			);
		});
	}

	prDrawPoints { |positions|
		// Draw dots
		positions.do({ |position, index|
			if(index == hoveredPoint) {
				Pen.fillColor_(thisModColor);
			} {
				if(index == selectedPoint) {
					Pen.fillColor_(super.selectedColor);
				} {
					Pen.fillColor_(super.outlineColor);
				};
			};
			Pen.addArc(
				position,
				thisHandleSize / 2,
				0, 2pi
			);
			Pen.fill;
		});
	}

	prCollideWith { |pointA, pointB|
		var isColliding = false;

		if(pointA.x >= (pointB.x - (thisHandleSize / 2))) {
			if(pointA.x <= (pointB.x + (thisHandleSize / 2))) {
				if(pointA.y >= (pointB.y - (thisHandleSize / 2))) {
					if(pointA.y <= (pointB.y + (thisHandleSize / 2))) {
						isColliding = true;
					};
				};
			};
		};

		^isColliding
	}

	prDrawNewPoint { |positions|
		var prevPosIndex, point;
		if(thisEnv.levels.size < thisMaxSize) {
			positions.do({ |pos, index|
				if(pos.x <= mousePos.x)
				{ prevPosIndex = index; };
			});

			Pen.width_(super.outlineSize);
			Pen.strokeColor_(thisModColor);
			Pen.fillColor_(thisModColor);

			if(prevPosIndex == (positions.size - 1)) {
				point = mousePos;
				if(thisLastLevelIsZero)
				{ point = Point(mousePos.x, super.interactionRect.bottom); };
				Pen.line(
					positions[positions.size - 1],
					point
				);
				Pen.stroke;
				Pen.addArc(
					point,
					thisHandleSize / 2,
					0, 2pi
				);
				Pen.fill;
			} {
				Pen.line(
					positions[prevPosIndex],
					mousePos
				);
				Pen.line(
					mousePos,
					positions[prevPosIndex + 1]
				);
				Pen.stroke;
				Pen.addArc(
					mousePos,
					thisHandleSize / 2,
					0, 2pi
				);
				Pen.fill;
			};
		};
	}

	prMousePosInbounds {
		if((((
			mousePos.x >= super.interactionRect.left)
		and: { mousePos.x <= super.interactionRect.right })
		and: { mousePos.y >= super.interactionRect.top })
		and: { mousePos.y <= super.interactionRect.bottom })
		{ ^true };
		^false
	}

	prAddPoint {
		var prevPosIndex, positions, levels, times, curves;
		var timeRatio, newPrevTime;
		if(thisEnv.levels.size < thisMaxSize) {
			prevPosIndex;
			positions = this.prGetPointPositions;
			levels = thisEnv.levels.deepCopy;
			times = thisEnv.times.deepCopy;
			curves = thisEnv.curves.deepCopy;

			positions.do({ |pos, index|
				if(pos.x <= mousePos.x)
				{ prevPosIndex = index; };
			});

			if(prevPosIndex == (positions.size - 1)) {
				if(thisLastLevelIsZero) {
					levels = levels ++ [0];
				} {
					levels = levels ++ [
						(
							super.interactionRect.height
							- mousePos.y
							+ super.interactionRect.top
						).linlin(
							0, super.interactionRect.height,
							thisYMin, thisYMax
						)
					];
				};
				times = times ++ [
					(mousePos.x - positions[positions.size - 1].x).linlin(
						0, super.interactionRect.width,
						0, thisTimeScale
					);
				];
				curves = curves ++ [0];
			} {
				timeRatio = mousePos.x.linlin(
					positions[prevPosIndex].x,
					positions[prevPosIndex + 1].x,
					0, 1
				);

				newPrevTime;

				timeRatio.clip(0.0001, 0.9999);

				newPrevTime = thisEnv.times[prevPosIndex] * timeRatio;
				times = times.asList.insert(
					prevPosIndex + 1,
					thisEnv.times[prevPosIndex] - newPrevTime;
				);
				times[prevPosIndex] = newPrevTime;

				levels = levels.asList.insert(
					prevPosIndex + 1,
					(
						super.interactionRect.height
						- mousePos.y
						+ super.interactionRect.top
					).linlin(
						0, super.interactionRect.height,
						thisYMin, thisYMax
					)
				);

				curves[prevPosIndex] = 0;
				curves = curves.asList.insert(prevPosIndex + 1, 0);
			};

			thisEnv = Env(levels, times, curves);
			thisAction.value(thisEnv.deepCopy);
			this.prCheckHovering(mousePos.x, mousePos.y);
			this.refresh;
		};
	}

	prDrawHelpers {
		// FIX ME : integer round value should display integers

		var font = super.font.deepCopy.size_(thisHelpersFontSize);
		var caseSize;
		// thisXHelpers thisYHelpers
		Pen.width_(thisHelpersWidth);
		Pen.strokeColor_(super.helpersColor);

		// X Axis
		Pen.line(
			super.interactionRect.leftTop,
			super.interactionRect.leftBottom
		);
		Pen.stringAtPoint(
			"0",
			Point(
				super.interactionRect.left
				+ (thisHelpersFontSize * 2),
				super.interactionRect.top
				+ 1
			),
			font,
			super.helpersColor
		);
		Pen.line(
			super.interactionRect.rightTop,
			super.interactionRect.rightBottom
		);
		Pen.stringAtPoint(
			thisTimeScale.trunc(thisXRoundValue).asString,
			Point(
				super.interactionRect.right
				- (thisHelpersFontSize * 3),
				super.interactionRect.top
				+ 1
			),
			font,
			super.helpersColor
		);
		if(thisXHelpers > 0)
		{ caseSize = super.interactionRect.width / (thisXHelpers + 1); };
		thisXHelpers.do({ |index|
			Pen.line(
				Point(
					super.interactionRect.left
					+ (caseSize * (index + 1)),
					super.interactionRect.top
				),
				Point(
					super.interactionRect.left
					+ (caseSize * (index + 1)),
					super.interactionRect.bottom
				)
			);
			Pen.stringAtPoint(
				(index + 1)
				.linlin(
					0, (thisXHelpers + 1),
					0, thisTimeScale
				)
				.trunc(thisXRoundValue).asString,
				Point(
					super.interactionRect.left
					+ (caseSize * (index + 1))
					+ thisHelpersFontSize,
					super.interactionRect.top
					+ 1
				),
				font,
				super.helpersColor
			);
		});

		// Y Axis
		Pen.line(
			super.interactionRect.leftBottom,
			super.interactionRect.rightBottom
		);
		Pen.stringAtPoint(
			thisYMin.trunc(thisYRoundValue).asString,
			Point(
				super.interactionRect.left
				+ thisHelpersFontSize,
				super.interactionRect.bottom
				- (thisHelpersFontSize * 2)
			),
			font,
			super.helpersColor
		);
		Pen.line(
			super.interactionRect.leftTop,
			super.interactionRect.rightTop
		);
		Pen.stringAtPoint(
			thisYMax.trunc(thisYRoundValue).asString,
			Point(
				super.interactionRect.left
				+ thisHelpersFontSize,
				super.interactionRect.top
				+ 1 + thisHelpersFontSize
			),
			font,
			super.helpersColor
		);
		if(thisYHelpers > 0)
		{ caseSize = super.interactionRect.height / (thisYHelpers + 1); };
		thisYHelpers.do({ |index|
			Pen.line(
				Point(
					super.interactionRect.left,
					super.interactionRect.bottom
					- (caseSize * (index + 1))
				),
				Point(
					super.interactionRect.right,
					super.interactionRect.bottom
					- (caseSize * (index + 1))
				)
			);
			Pen.stringAtPoint(
				(index + 1)
				.linlin(
					0, (thisYHelpers + 1),
					thisYMin, thisYMax
				)
				.trunc(thisYRoundValue).asString,
				Point(
					super.interactionRect.left
					+ thisHelpersFontSize,
					super.interactionRect.bottom
					- (caseSize * (index + 1))
					- (thisHelpersFontSize * 2)
				),
				font,
				super.helpersColor
			);
		});

		Pen.stroke;
	}
}