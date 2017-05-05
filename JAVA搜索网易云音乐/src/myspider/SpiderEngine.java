package myspider;

import java.util.List;
import java.util.Queue;

/**
 * 爬虫主要管理类
 * 
 * @author Administrator
 * 
 */
public class SpiderEngine
{
	private SpiderSearch spiderSearch;
	private Thread[] threads;

	/**
	 * 构造函数
	 * 
	 * @param url
	 * @param threadCount
	 * @param songFilePath
	 */
	public SpiderEngine(String url, int threadCount, String songFilePath)
	{
		// String rootPath = Common.getRootPath(url);
		spiderSearch = new SpiderSearch(songFilePath);
		spiderSearch.getAwaitVisitUrlQueue().offer(url);

		threadInit(threadCount);
	}

	/**
	 * 构造函数
	 * 
	 * @param url
	 * @param threadCount
	 * @param songFilePath
	 */
	public SpiderEngine(List<String> urls, int threadCount, String songFilePath)
	{
		// String rootPath = Common.getRootPath(urls.get(0));
		spiderSearch = new SpiderSearch(songFilePath);
		Queue<String> queue = spiderSearch.getAwaitVisitUrlQueue();
		for (String url : urls)
		{
			queue.offer(url);
		}

		threadInit(threadCount);
	}

	/**
	 * 线程初始化
	 * 
	 * @param threadCount
	 */
	private void threadInit(int threadCount)
	{
		threads = new Thread[threadCount];
		for (int i = 0; i < threads.length; i++)
		{
			threads[i] = new Thread(spiderSearch);
		}
	}

	/**
	 * 开始爬虫任务
	 */
	public void start()
	{
		for (Thread thread : threads)
		{
			thread.start();
		}
		// System.out.println("呵呵");

	}

	/**
	 * 设置过滤url的正则表达式，与正则表达式匹配的url才会放入队列等待访问
	 * 
	 * @param urlRegex
	 */
	public void setMatchRegex(String urlRegex)
	{
		spiderSearch.setMatchedRegex(urlRegex);
	}
}
