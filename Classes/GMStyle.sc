GMStyle {

	classvar thisDefault;

	var listeners,

	thisMainColor,
	thisSecondColor,

	thisOutlineColor,
	thisOutlineSize = 3,

	thisBackColor,
	thisBackgroundColor,

	thisBeatColor,

	thisDisabledColor,
	thisFontColorDisabled,
	thisSelectedColor,

	thisBorderSize = 4,
	thisBorderColor,
	thisSecondBorderSize = 4,
	thisSecondBorderColor,
	thisThirdBorderSize = 8,
	thisThirdBorderColor,

	thisHelpersColor,

	thisFont,
	thisFontColor,

	thisValueFontColor,
	thisHighlightColor;

	*new {
		^super.new.init
	}

	*initClass {
		thisDefault = this.new();
	}

	*default {
		^thisDefault
	}

	*default_ { |aGMStyle|
		thisDefault = aGMStyle;
	}

	init {
		listeners = List(0);

		thisMainColor = Color(0.75, 0, 0.333);
		thisSecondColor = Color(0.5, 0, 0.111);
		thisOutlineColor = Color(1, 1, 1);

		thisBackColor = Color(1, 0, 0.25);
		thisBackgroundColor = Color(0, 0, 0);

		thisBeatColor = Color(0, 1, 1, 0.5);

		thisFontColorDisabled = Color(0.25, 0.25, 0.25);
		thisDisabledColor = Color(0.5, 0.5, 0.5);

		thisBorderColor = Color(0.666, 0, 0.125);
		thisSecondBorderColor = Color(0.333, 0, 0.0625);
		thisThirdBorderColor = Color(0, 0, 0);

		thisHelpersColor = Color(1, 1, 1, 0.25);

		thisFont = Font.default.deepCopy.size_(24);
		thisFontColor = Color(0, 0, 0);

		thisSelectedColor = Color(0.75, 0, 0.333);

		thisValueFontColor = Color(1, 1, 1);
		thisHighlightColor = Color(1, 1, 1, 0.5);
	}

	borderSize {
		^thisBorderSize
	}

	borderSize_ { |aNumber|
		thisBorderSize = aNumber;
		this.refreshListeners;
	}

	secondBorderSize {
		^thisSecondBorderSize
	}

	secondBorderSize_ { |aNumber|
		thisSecondBorderSize = aNumber;
		this.refreshListeners;
	}

	thirdBorderSize {
		^thisThirdBorderSize
	}

	thirdBorderSize_ { |aNumber|
		thisThirdBorderSize = aNumber;
		this.refreshListeners;
	}

	borderColor {
		^thisBorderColor
	}

	borderColor_ { |aColor|
		thisBorderColor = aColor;
		this.refreshListeners;
	}

	secondBorderColor {
		^thisSecondBorderColor
	}

	secondBorderColor_ { |aColor|
		thisSecondBorderColor = aColor;
		this.refreshListeners;
	}

	thirdBorderColor {
		^thisThirdBorderColor
	}

	thirdBorderColor_ { |aColor|
		thisThirdBorderColor = aColor;
		this.refreshListeners;
	}

	mainColor {
		^thisMainColor
	}

	mainColor_ { |aColor|
		thisMainColor = aColor;
		this.refreshListeners;
	}

	secondColor {
		^thisSecondColor
	}

	secondColor_ { |aColor|
		thisSecondColor = aColor;
		this.refreshListeners;
	}

	backColor {
		^thisBackColor
	}

	backColor_ { |aColor|
		thisBackColor = aColor;
		this.refreshListeners;
	}

	backgroundColor {
		^thisBackgroundColor
	}

	backgroundColor_ { |aColor|
		thisBackgroundColor = aColor;
		this.refreshListeners;
	}

	outlineSize {
		^thisOutlineSize
	}

	outlineSize_ { |aNumber|
		thisOutlineSize = aNumber;
		this.refreshListeners;
	}

	outlineColor {
		^thisOutlineColor
	}

	outlineColor_ { |aColor|
		thisOutlineColor = aColor;
		this.refreshListeners;
	}

	beatColor {
		^thisBeatColor
	}

	beatColor_ { |aColor|
		thisBeatColor = aColor;
		this.refreshListeners;
	}

	disabledColor {
		^thisDisabledColor
	}

	disabledColor_ { |aColor|
		thisDisabledColor = aColor;
		this.refreshListeners;
	}

	selectedColor {
		^thisSelectedColor
	}

	selectedColor_ { |aColor|
		thisSelectedColor = aColor;
		this.refreshListeners;
	}

	helpersColor {
		^thisHelpersColor
	}

	helpersColor_ { |aColor|
		thisHelpersColor = aColor;
		this.refreshListeners;
	}

	font {
		^thisFont
	}

	font_ { |aFont|
		thisFont = aFont;
		this.refreshListeners;
	}

	fontColor {
		^thisFontColor
	}

	fontColor_ { |aColor|
		thisFontColor = aColor;
		this.refreshListeners;
	}

	fontColorDisabled {
		^thisFontColorDisabled
	}

	fontColorDisabled_ { |aColor|
		thisFontColorDisabled = aColor;
		this.refreshListeners;
	}

	valueFontColor {
		^thisValueFontColor
	}

	valueFontColor_ { |aColor|
		thisValueFontColor = aColor;
		this.refreshListeners;
	}

	highlightColor {
		^thisHighlightColor
	}

	highlightColor_ { |aColor|
		thisHighlightColor = aColor;
		this.refreshListeners;
	}

	// Private methods, used to refresh Views automatically
	addListener { |aGMView|
		listeners.add(aGMView);
	}

	removeListener { |aGMView|
		listeners.removeAt(
			listeners.indexOf(aGMView));
	}

	refreshListeners {
		listeners.do({ |view|
			view.refresh;
		});
	}

}