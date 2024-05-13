GMCycleView : GMUserView {
	var thisCycles;
	var thisStates;

	*new {
		^super.new.init;
	}

	init {
		super.init;

		// Forbidden events
		this.setEventHandler(
			QObject.mouseUpEvent,
			\mouseDownEvent, false);
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

		thisCycles = [4, 4, 4];
		thisStates = Array.fill(thisCycles.size, { 0 });
	}

	cycles {
		^thisCycles
	}

	cycles_ { |anArray|
		thisCycles = anArray;
		this.reset;
	}

	states {
		^thisStates
	}

	states_ { |anArray|
		while
		{ anArray.size < thisCycles.size }
		{ anArray = anArray ++ [0] };
		thisStates = anArray;
		// Defer the refresh call
		// because we're probably inside a routine
		{ this.refresh; }.defer;
	}

	reset {
		thisStates = Array.fill(thisCycles.size, { 0 });
		// Defer the refresh call
		// because we're probably at the end of a routine
		{ this.refresh; }.defer;
	}

	advance {
		thisStates[0] = thisStates[0] + 1;
		(thisStates.size - 1).do({ |index|
			if(thisStates[index] == thisCycles[index]) {
				thisStates[index] = 0;
				thisStates[index + 1] = thisStates[index + 1] + 1;
			};
		});
		if(thisStates[thisStates.size - 1] == thisCycles[thisCycles.size - 1])
		{ thisStates[thisStates.size - 1] = 0 };

		// Defer the refresh call
		// because we're probably inside a routine
		{ this.refresh; }.defer;
	}

	draw {
		var caseSize = Point(
			0,
			super.interactionRect.height / thisCycles.size);

		super.drawFrame(super.backgroundColor);

		thisCycles.do({ |nTimes, cycle|
			caseSize.x = super.interactionRect.width / thisCycles[cycle];
			nTimes.do({ |time|
				var radius = 2;
				Pen.fillColor_(Color.grey);
				if(thisStates[cycle] == time)
				{
					Pen.fillColor_(super.mainColor);
					radius = min(
						caseSize.x * 0.4,
						caseSize.y * 0.4
					);
				};
				Pen.addArc(
					Point(
						super.interactionRect.left +
						(caseSize.x * (time + 0.5)),
						super.interactionRect.bottom -
						(caseSize.y * (cycle + 0.5))
					),
					radius, 0, 2pi
				);
				Pen.fill;
			});
		});
	}
}