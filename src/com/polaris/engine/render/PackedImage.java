package com.polaris.engine.render;

/*import static com.polaris.engine.util.ImageHelper.injectBufferedImage;
import static com.polaris.engine.util.ImageHelper.resize;
import static com.polaris.engine.util.MathHelper.log;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;*/

public class PackedImage 
{

	/*private int defaultWidth = 2;
	private int defaultHeight = 2;
	private boolean switchGrowth = false;

	private Map<String, Texture> textureMap = new HashMap<String, Texture>();
	private List<Row> rows = new ArrayList<Row>();
	private BufferedImage stitchedImage;

	public PackedImage(Map<String, BufferedImage> images)
	{
		int totalArea = 0;
		int maxHeight = 0;
		int maxWidth = 0;
		for(String imageName : images.keySet())
		{
			BufferedImage image = images.get(imageName);
			totalArea += image.getWidth() * image.getHeight();
			maxHeight = Math.max(image.getHeight(), maxHeight);
			maxWidth = Math.max(image.getWidth(), maxWidth);
		}
		maxWidth = (int) Math.pow(2, (int) Math.ceil(log(2, maxWidth)));
		maxHeight = (int) Math.pow(2, (int) Math.ceil(log(2, maxHeight)));
		defaultWidth = defaultHeight = (int) Math.pow(2, (int) log(2, Math.sqrt(totalArea)));
		defaultWidth = Math.max(maxWidth, defaultWidth);
		defaultHeight = Math.max(maxHeight, defaultHeight);
		switchGrowth = defaultWidth < defaultHeight;
		stitchedImage = new BufferedImage(defaultWidth, defaultHeight, BufferedImage.TYPE_INT_ARGB);
		rows.add(new Row(defaultHeight, new Cell(defaultWidth)));
		pack(images);
	}

	private void pack(Map<String, BufferedImage> images)
	{
		int moveX = 0;
		int moveY = 0;
		for(String imageName : images.keySet())
		{
			BufferedImage image = images.get(imageName);
			int rowSize = rows.size();
			int cellSize = rows.get(0).cells.size();
			int currentRow = moveY;
			int currentCell = moveX;
			boolean fitInto = false;
			while(!fitInto)
			{
				int tempCell = currentCell - 1;
				int tempRow = currentRow - 1;
				while(!fitInto && (tempCell < cellSize - 1 || tempRow < rowSize - 1))
				{
					tempCell++;
					tempRow++;
					if(tempCell < cellSize)
					{
						fitInto = checkIfFits(currentRow, tempCell, rowSize, cellSize, image.getWidth(), image.getHeight());
					}
					if(!fitInto)
					{
						if(tempRow < rowSize)
							fitInto = checkIfFits(tempRow, currentCell, rowSize, cellSize, image.getWidth(), image.getHeight());
						if(fitInto)
						{
							currentRow = tempRow;
						}
					}
					else
					{
						currentCell = tempCell;
					}
				}
				if(!fitInto)
				{
					if(rowSize == currentRow || cellSize == currentCell)
					{
						if(!switchGrowth == (defaultHeight >= defaultWidth))
						{
							for(int i = 0; i < rows.size(); i++)
							{
								rows.get(i).cells.add(new Cell(defaultWidth));
							}
							defaultWidth *= 2;
							cellSize++;
						}
						else
						{
							rows.add(new Row(defaultHeight, genCellList(rows.get(0), false)));
							defaultHeight *= 2;
							rowSize++;
						}
						stitchedImage = resize(stitchedImage, defaultWidth, defaultHeight);
						moveX = moveY = 0;
						currentCell = currentRow = 0;
					}
					else
					{
						moveX = ++moveY;
						currentRow = currentCell = moveX;
					}
				}
			}
			int x = 0;
			int y = 0;
			for(int i = 0; i < currentRow; i++)
			{
				y += rows.get(i).height;
			}
			for(int i = 0; i < currentCell; i++)
			{
				x += rows.get(currentRow).cells.get(i).width;
			}
			textureMap.put(imageName.substring(0, imageName.lastIndexOf('.')), new Texture(x, y, x + image.getWidth(), y + image.getHeight()));
			injectBufferedImage(stitchedImage, image, x, y);
		}
		for(String imageName : textureMap.keySet())
		{
			Texture texture = textureMap.get(imageName);
			texture.reduce(defaultWidth, defaultHeight);
		}
	}

	private boolean checkIfFits(int rowPosition, int cellPosition, int rowSize, int cellSize, int width, int height)
	{
		if(rowPosition == rowSize || cellPosition == cellSize)
			return false;
		Row row = rows.get(rowPosition);
		Cell cell = row.cells.get(cellPosition);
		if(cell.filled)
			return false;
		boolean flag = true;
		if(cell.width < width )
		{
			if(cellPosition + 1 == cellSize)
				return false;

			flag = checkIfFits(rowPosition, cellPosition + 1, rowSize, cellSize, width - cell.width, height);
		}
		if(row.height < height)
		{
			if(rowPosition + 1 == rowSize)
				return false;

			flag = checkIfFits(rowPosition + 1, cellPosition, rowSize, cellSize, width, height - row.height);
		}
		if(flag)
		{
			if(cell.width > width)
			{
				for(int i = 0; i < rows.size(); i++)
				{
					Cell editedCell = rows.get(i).cells.get(cellPosition);
					Cell addedCell = new Cell(cell.width - width);
					addedCell.filled = editedCell.filled;
					editedCell.width = width;
					rows.get(i).cells.add(cellPosition + 1, addedCell);
				}
			}
			if(row.height > height)
			{
				Row newRow = new Row(row.height - height, genCellList(row, true));
				row.height = height;
				rows.add(rowPosition + 1, newRow);
			}
			cell.filled = true;
		}
		return flag;
	}

	private Cell[] genCellList(Row row, boolean establish)
	{
		List<Cell> cellList = new ArrayList<Cell>();
		for(int i = 0; i < row.cells.size(); i++)
		{
			Cell iteratorCell = row.cells.get(i);
			Cell newCell = new Cell(iteratorCell.width);
			if(establish)
				newCell.filled = iteratorCell.filled;
			cellList.add(newCell);
		}
		return cellList.toArray(new Cell[cellList.size()]);
	}

	public int getWidth()
	{
		return defaultWidth;
	}

	public int getHeight()
	{
		return defaultHeight;
	}

	public Map<String, Texture> getTextureMapping()
	{
		return textureMap;
	}

	public BufferedImage getImage()
	{
		return stitchedImage;
	}

	private static class Cell
	{
		private int width;
		private boolean filled = false;

		public Cell(int width) 
		{
			this.width = width;
		}
	}

	private static class Row
	{
		private int height;
		private List<Cell> cells = new ArrayList<Cell>();

		public Row(int height, Cell ... cells)
		{
			this.height = height;
			this.cells.addAll(Arrays.asList(cells));
		}
	}*/

}
