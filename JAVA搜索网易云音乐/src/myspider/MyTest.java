package myspider;

import java.util.ArrayList;
import java.util.List;

public class MyTest
{
	public static void main(String[] args)
	{
		List<String> list = new ArrayList<>();
		list.add("某云的url");
		list.add("某云的url");
		list.add("某云的url");
		list.add("某云的url");
		list.add("某云的url");
		list.add("某云的url");
		list.add("某云的url");
		list.add("某云的url");
		list.add("某云的url");
		list.add("某云的url");
		list.add("某云的url");
		list.add("某云的url");
		SpiderEngine spiderEngine = new SpiderEngine(list, 15, "h:/某音乐.txt");
		//xxxxx是网址的dns，如www.hehe.com
		spiderEngine.setMatchRegex("http://xxxxx.*");
		spiderEngine.start();

	}

}
