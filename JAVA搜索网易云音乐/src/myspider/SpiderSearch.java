package myspider;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayDeque;

import java.util.HashSet;

import java.util.Queue;
import java.util.Set;

import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import myspider.utils.Common;

public class SpiderSearch implements Runnable
{
	/**
	 *  用于存储待访问的url
	 *  由于线程安全的队列时利用链表实现的，效率低。所以使用数组双端队列，并使用同步代码
	 */
	private Queue<String> awaitVisitUrlQueue = new ArrayDeque<>(1000);
	/**
	 * 存储awaitVisitUrlQueue里的url的hashcode值
	 * 用来判断保证awaitVisitUrlQueue里面的url不会重复
	 */
	private Set<Integer> awaitVisitUrlSet = new HashSet<>(1000);
	
	/**
	 * 存储已经被访问过的url
	 */
	private Set<Integer> alreadyVisitUrlSet = new HashSet<>(1000);
	// private String rootPath;
	/**
	 * 将歌曲的信息写入文件
	 */
	private BufferedWriter bWriter;
	/**
	 * 过滤url的正则表达式
	 * 与正则表达式匹配的url才会放入队列等待访问
	 */
	private String matchedRegex;

	/**
	 * 用于将抓取的歌曲写入文件，当count为10，就刷新一次写入流
	 */
	private int count = 0;

	public Queue<String> getAwaitVisitUrlQueue()
	{
		return awaitVisitUrlQueue;
	}

	public void setMatchedRegex(String matchedRegex)
	{
		this.matchedRegex = matchedRegex;
	}

	/**
	 * 爬虫构造函数
	 * @param songFilePath
	 */
	public SpiderSearch(String songFilePath)
	{
		// this.rootPath = rootPath;
		this.matchedRegex = ".*";// rootPath+".*";
		try
		{
			bWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(songFilePath, true), "utf-8"),
					1000);
		} catch (Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException();
		}
	}

	/**
	 * 线程运行的任务
	 */
	@Override
	public void run()
	{
		String url = null;

		while ((url = getAwaitUrl()) != null)
		{
			addUrlToAlreadyVisitSet(url);
			if (Pattern.matches("http://music.163.com/song\\?id=([0-9]+)", url))
			{
				addSong(url);
			}

			visit(url);

//			if (alreadyVisitUrlSet.contains(url.hashCode()))
//			{
//				System.out.println(Thread.currentThread().getName() + ":"+alreadyVisitUrlSet.size()+"  "+url);
//			} else
//			{
//				System.out.println("已经访问    "+Thread.currentThread().getName() + ":"+alreadyVisitUrlSet.size()+"  " + url);
//			}
		}

	}

	/**
	 * 从待访问队列中获取待访问url
	 * @return
	 */
	private String getAwaitUrl()
	{
		String url = null;
		// synchronized (awaitVisitUrlQueue)
		{
			url = awaitVisitUrlQueue.poll();
			if (url != null)
			{
				awaitVisitUrlSet.remove(url.hashCode());
			}
		}
		return url;
	}

	/**
	 * 将已经访问过的url放入已访问集合中
	 * @param url
	 */
	private void addUrlToAlreadyVisitSet(String url)
	{
		// synchronized (alreadyVisitUrlSet)
		{
			alreadyVisitUrlSet.add(url.hashCode());
		}
	}

	/**
	 * 将从url连接获取的数据中的子url放入待访问队列
	 * @param urlSet
	 */
	private void addUrlsToAwaitVisitQueue(Set<String> urlSet)
	{
		// synchronized (awaitVisitUrlQueue)
		{
			// synchronized (alreadyVisitUrlSet)
			{
				for (String url : urlSet)
				{
					if (!alreadyVisitUrlSet.contains(url.hashCode()) && !awaitVisitUrlSet.contains(url.hashCode()))
					{
						awaitVisitUrlSet.add(url.hashCode());
						awaitVisitUrlQueue.offer(url);

					}

				}
			}

		}

	}

	/**
	 * 将歌曲写入文件
	 * @param url
	 */
	private void addSong(String url)
	{
		try
		{
			//将url转换成可以获取歌曲json数据的url地址
			String id = url.substring(url.lastIndexOf('?') + 4);
			//这里的xxxx是通过id获取歌曲json格式的网址，我就不给了，怕某云告，你可以自己网上查，或者利用抓包工具获取
			String urlInfo = "xxxx"+id + "&ids=%5B" + id + "%5D";
			String songMsg = Common.getSongMsg(urlInfo + "\r\n");
			bWriter.write(songMsg+"\r\n");
			System.out.println("歌曲：" + songMsg);
			++count;
			if (count >= 10)
			{
				bWriter.flush();
				count = 0;
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 访问url
	 * @param url
	 */
	private void visit(String url)
	{
		Document doc = null;
		try
		{
			doc = Jsoup.connect(url).get();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		if (doc != null)
		{
			Elements elements = doc.select("a");
			String childUrl = null;
			Set<String> set = new HashSet<>(20);
			if (elements != null)
			{
				for (Element element : elements)
				{
					// childUrl = getValidUrl(element.attr("abs:href"), url);
					childUrl = element.attr("abs:href");
					if (childUrl != null)
					{
						if (Pattern.matches(matchedRegex, childUrl))
						{
							set.add(childUrl);
						}
					}
				}
			}

			addUrlsToAwaitVisitQueue(set);
		}
	}

	// private String getValidUrl(String childUrl, String url)
	// {
	//
	// if (childUrl != null)
	// {
	//
	// if (childUrl.endsWith(";"))
	// {
	// throw new RuntimeException("找到错误");
	// // return null;
	// }
	// // else if (childUrl.startsWith("/"))
	// // {
	// // childUrl = rootPath + childUrl;
	// // } else if (!childUrl.startsWith("http://") &&
	// // !childUrl.startsWith("https://"))
	// // {
	// // childUrl = url.substring(0, url.lastIndexOf("/")) + childUrl;
	// // }
	// return childUrl;
	// }
	//
	// return childUrl;
	// }

}
