package polaris.okapi.gui.content;

import polaris.okapi.options.Settings;

public abstract class GuiSlider<T, I extends Settings> extends GuiContent
{
	
	protected T minValue;
	protected T maxValue;
	protected T currentValue;
	
	public GuiSlider(double x, double y, double width, double height, T min, T max, T current)
	{
		super(x, y, width, height);
		minValue = min;
		maxValue = max;
		currentValue = current;
	}
	
	@Override
	public void render(double delta)
	{
		
	}

	@Override
	public int mouseClick(int mouseId)
	{
		return 2;
	}

}
