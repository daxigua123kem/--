package myspider.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.alibaba.fastjson.JSON;

import myspider.entity.SongDetail;

public class Common
{
	/**
	 * 从url中获取歌曲信息
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public static String getSongMsg(String url) throws IOException
	{
		URL urlObject = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) urlObject.openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(2000);// 时间可以设置的久一点，如果控制台经常提示read time out
		InputStream inStream = conn.getInputStream();
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inStream, "utf-8"), 5000);
		StringBuilder sb = new StringBuilder(5000);
		String content = null;
		while ((content = bufferedReader.readLine()) != null)
		{
			sb.append(content);
		}
		bufferedReader.close();
		conn.disconnect();
		/**
		 * 解析json字符串并访问对象的toString()后的字符串
		 */
		return JSON.parseObject(sb.toString(), SongDetail.class).toString();

	}

	/**
	 * 从一个url中获取根url
	 * @param url
	 * @return
	 */
	public static String getRootPath(String url)
	{
		if (url.endsWith("/"))
		{
			return url.substring(0, url.length() - 1);
		} else
		{
			if (url.split("/").length > 3)
			{
				return url.substring(0, url.lastIndexOf('/'));
			} else
			{
				return url;
			}
		}
	}
}
