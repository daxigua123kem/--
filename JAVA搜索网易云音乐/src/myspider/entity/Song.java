package myspider.entity;

import java.util.List;

public class Song
{
	private String name;
	private List<Artist> artists;
	private String mp3Url;

	public String getMp3Url()
	{
		return mp3Url;
	}

	public void setMp3Url(String mp3Url)
	{
		this.mp3Url = mp3Url;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public List<Artist> getArtists()
	{
		return artists;
	}

	public void setArtists(List<Artist> artists)
	{
		this.artists = artists;
	}
}
