GMGridContainer : UserView {

	var thisViews;
	var thisMargins = 4;
	var thisOrientation = \horizontal;

	*new {
		^super.new.init;
	}

	init {
		thisViews = List(0);
		this.onResize_(
			FunctionList()
			.addFunc({ this.prArrangeViews; });
		);
	}

	insertView { |view, index = nil|
		if(index.isNil)
		{ thisViews.add(view); }
		{ thisViews.insert(view, index); };
		view.setParent(this);
		this.prArrangeViews;
	}

	removeView { |view|
		thisViews.remove(view);
		this.prArrangeViews;
	}

	margins {
		^thisMargins
	}

	margins_ { |aNumber|
		thisMargins = aNumber;
		this.prArrangeViews;
	}

	orientation {
		^thisOrientation
	}

	orientation_ { |aSymbol|
		thisOrientation = aSymbol;
		this.prArrangeViews;
	}

	prArrangeViews {
		var gridOrga = this.prGetGridOrga;
		var constantSize, varSize;

		if(thisOrientation == \horizontal) {
			constantSize = this.bounds.height - (thisMargins * (gridOrga.size - 1));
			constantSize = constantSize / gridOrga.size;

			gridOrga.do({ |nItems, index1|
				nItems.do({ |index2|
					var itemIndex = 0;

					index1.do({ |i| itemIndex = itemIndex + gridOrga[i]; });
					itemIndex = itemIndex + index2;

					varSize = this.bounds.width - (thisMargins * (nItems - 1));
					varSize = varSize / nItems;

					thisViews[itemIndex].moveTo(
						(varSize + thisMargins) * index2,
						(constantSize + thisMargins) * index1
					);

					thisViews[itemIndex].resizeTo(
						varSize,
						constantSize
					);
				});
			});
		} {
			constantSize = this.bounds.width - (thisMargins * (gridOrga.size - 1));
			constantSize = constantSize / gridOrga.size;

			gridOrga.do({ |nItems, index1|
				nItems.do({ |index2|
					var itemIndex = 0;

					index1.do({ |i| itemIndex = itemIndex + gridOrga[i]; });
					itemIndex = itemIndex + index2;

					varSize = this.bounds.height - (thisMargins * (nItems - 1));
					varSize = varSize / nItems;

					thisViews[itemIndex].moveTo(
						(constantSize + thisMargins) * index1,
						(varSize + thisMargins) * index2
					);

					thisViews[itemIndex].resizeTo(
						constantSize,
						varSize
					);
				});
			});
		};
	}

	prGetGridOrga {
		var lowerSquare = thisViews.size.sqrt.trunc(1);
		var gridOrga;

		if(thisViews.size <= (lowerSquare * (lowerSquare + 1)))
		{ gridOrga = Array.fill(lowerSquare, { 0 }); }
		{ gridOrga = Array.fill(lowerSquare + 1, { 0 }); };

		thisViews.size.do({ |index|
			gridOrga[index % gridOrga.size]
			= gridOrga[index % gridOrga.size] + 1;
		});

		^gridOrga
	}
}