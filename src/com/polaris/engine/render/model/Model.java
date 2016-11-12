package com.polaris.engine.render.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.polaris.engine.App;
import com.polaris.engine.render.ITexture;
import com.polaris.engine.render.Texture;
import com.polaris.engine.render.VAO;

public abstract class Model implements ITexture
{
	
	private App application;
	private VAO modelVao;
	//private ModelBounds modelBounds;
	private Texture modelTexture;

	public Model(App app, File model)
	{
		application = app;
		try
		{
			FileInputStream stream = new FileInputStream(model);
			
			modelTexture = application.getTextureManager().genTexture("", null);
			modelVao = generateVAO(stream);
			//modelBounds = new ModelBounds(((IBO) modelVao.getDrawCall()));
			
			stream.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	protected abstract VAO generateVAO(InputStream stream) throws IOException;
	
	public void bind()
	{
		modelTexture.bind();
	}
	
	public void draw()
	{
		modelVao.draw();
	}
	
}
