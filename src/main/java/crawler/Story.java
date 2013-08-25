package crawler;

import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;

public class Story {
	FallenCrawler crawler;

	public Story(FallenCrawler myCrawler) {
		crawler = myCrawler;
	}

	public void robSeance() {
		// Reset previous actions first
		crawler.resetStart();

		crawler.travel("flit");

		System.out.println("Click menu 1.");
		crawler.findButton("It's all very well").click();// Menu 1

		System.out.println("Click menu 2.");
		crawler.findButton("It should be easy to do.").click();// Menu 2

		crawler.resetFinish();
		crawler.recordAction();
	}

	public void nurseFriend() {
		crawler.resetStart();
		
		// Travel Home
		crawler.travel("home");

		// Click menus
		crawler.findButton("Earn Hard-Earned Lessons").click();
		crawler.findButton("Wounds will be reduced.").click();

		// Choose?
		crawler.getElement(By.xpath("//input[@value=\"Choose!\"]")).click();

		crawler.resetFinish();
		crawler.recordAction();
	}

	public Map<String, Integer> getStats() {
		Map<String, Integer> stats = crawler.getStats();
		stats.put("actions", crawler.getNumActions());

		return stats;
	}
	
	public void acceptMessages() {
		try {
		crawler.getElement(By.id("homeTab")).click();
		crawler.getElement(By.linkText("/Me/AcceptInvitation")).click();
		} catch (NoSuchElementException e) {
			System.out.println("No accept messages found.");
		} finally {
			crawler.recordAction();//Record as action even on failure.
		}
	}
	
	public void forceAction() {
		//<form onsubmit="loadMainContentWithParams('/Storylet/ChooseBranch', {'branchid':5941,'secondChances': (this.secondChances &amp;&amp; this.secondChances.checked)? this.secondChances.value:null }); return false;">   
	}
}
