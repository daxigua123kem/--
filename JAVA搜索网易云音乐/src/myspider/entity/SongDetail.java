package myspider.entity;

import java.util.List;

/**
 * 实体字段对应于歌曲信息的json数据
 * 用于将json字符串解析成对象
 * @author Administrator
 *
 */
public class SongDetail
{
	List<Song> songs;

	public List<Song> getSongs()
	{
		return songs;
	}

	public void setSongs(List<Song> songs)
	{
		this.songs = songs;
	}

	@Override
	public String toString()
	{
		String string=null;
		if(songs.size()!= 0)
		{
			Song song = songs.get(0);
			string =song.getName()+"  "+song.getArtists().get(0).getName()+"  "+song.getMp3Url();
		}
		return string;
	}
}
