package crawler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Function;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Point;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.JavascriptExecutor;

public class FallenCrawler {
	WebDriver driver;
	// int defaultWait = 10;
	int exceptionCount;
	int numActions; // We have to record this since it isn't updated very well

	public FallenCrawler(String username, String password) {
		driver = new FirefoxDriver();
		// driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.manage().deleteAllCookies();

		exceptionCount = 0;
		this.login(username, password);
		this.updateActions();
	}

	public Boolean login(String username, String password) {
		try {
			driver.get("http://fallenlondon.storynexus.com/");

			// Input email
			this.getElement(By.id("emailAddress")).sendKeys(username);
			;

			// Input password
			this.getElement(By.id("password")).sendKeys(password);
			;

			// Submit
			this.getElement(By.xpath("//input[@value='LOGIN']")).click();

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			if (exceptionCount < 2) {
				System.out.println("Trying login again.");
				exceptionCount++;
				return this.login(username, password);
			}

			System.out.println("Login Failed");
			return false;
		}
	}

	public void updateActions() {
		try {
			WebElement numActionsString = this.getElement(By.id("infoBarCurrentActions"), 15);
			numActions = Integer.parseInt(numActionsString.getText());
		} catch (Exception e) {
			e.printStackTrace();
			numActions = 0;
		}
	}
	
	public WebElement getChildElement(By parents, By sibling, By child) {
		WebElement parentEl = this.getElement(parents);
		List<WebElement> childEls = parentEl.findElements(By.tagName("div"));
		
		for(WebElement childEl : childEls) {
			try {
				WebElement check = childEl.findElement(sibling);
				return childEl.findElement(child);
			} catch(NoSuchElementException e) {
				//Do nothing, not found
			}
		}
		NoSuchElementException e = new NoSuchElementException("No sibling: " + sibling.toString() + " found in: " + parents.toString());
		throw e;
	}
	
	public List<WebElement> getElements(By by) {
		return this.getElements(by, 5);
	}
	
	public List<WebElement> getElements(By by, int wait) {
		driver.manage().timeouts().implicitlyWait(wait, TimeUnit.SECONDS);
		return driver.findElements(by);
	}
	
	public WebElement getElement(By by) {
		return this.getElement(by, 5);
	}

	public WebElement getElement(By by, int wait) {

		driver.manage().timeouts().implicitlyWait(wait, TimeUnit.SECONDS);
		return driver.findElement(by);
	}

	public void travel(String place) {
		driver.findElement(By.xpath("//img[@onclick=\"showMap()\"]")).click();

		WebElement placeNameEl = this.getElement(By.id("area_hdr_name"));
		String placeName = placeNameEl.getText();

		if (place.equals("flit")) {
			if (placeName.contains("Flit")) {
				return;// Already at correct place
			}

			((JavascriptExecutor) driver).executeScript("travel(11, 'flit', 'The Flit');");
			return;
		}

		if (place.equals("home") || place.equals("lodgings")) {
			if (placeName.contains("Lodgings")) {
				return;// Already at correct place
			}

			((JavascriptExecutor) driver).executeScript("lodgings(2, 'lodgings', 'your Lodgings');");
		}
	}

	public void resetFinish() {
		this.getElement(By.xpath("//input[@value='ONWARDS!']")).click();
	}

	public void resetStart() {
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

		System.out.println("Trying reset");
		// Reset previous actions first
		try {
			this.getElement(By.id("perhapsnotbtn"), 5).click();// Perhaps
			return;
		} catch (NoSuchElementException e) {
			// Do nothing, it is ok.
		}

		try {
			this.getElement(By.xpath("//input[@value=\"ONWARDS!\"]"), 5).click();// Onwards																// Button
			return;
		} catch (NoSuchElementException e) {
			// Do nothing, it is ok.
		}

		try {
			this.getElement(By.id("storyTab"), 5).click();
			return;
		} catch (NoSuchElementException e) {
			// Do nothing, it is ok.
		}
	}

	public void recordAction() {
		System.out.println("Action recorded.");
		numActions--;
	}

	public void quit() {
		driver.get("http://fallenlondon.storynexus.com/User/LogOut");
		driver.quit();
		System.out.println("Driver Closed.");
	}

	public Map<String, Integer> getStats() {
		Map<String, Integer> stats = new HashMap();
		
		stats.put("actions", numActions);

		int wounds = 0;
		int scandal = 0;
		int suspicion = 0;
		int nightmares = 0;

		WebElement leftCol = driver.findElement(By.id("lhs_col"));
		String colText = leftCol.getText().replaceAll("(\\r|\\n)", " ");
		String[] words = colText.split(" ");

		for (int i = 0; i < words.length; i++) {
			if (words[i].equals("WOUNDS")) {

				wounds = Integer.parseInt(words[i + 1]);
				stats.put("wounds", wounds);

			} else if (words[i].equals("SCANDAL")) {

				scandal = Integer.parseInt(words[i + 1]);
				stats.put("scandal", scandal);

			} else if (words[i].equals("SUSPICION")) {

				suspicion = Integer.parseInt(words[i + 1]);
				stats.put("suspicion", suspicion);

			} else if (words[i].equals("NIGHTMARES")) {

				nightmares = Integer.parseInt(words[i + 1]);
				stats.put("nightmares", nightmares);
			}
		}

		return stats;
	}

	public int getNumActions() {
		return numActions;
	}

	/*
	 * public void clickElement(By by) { driver.findElement(by).click(); }
	 */

	public WebElement findButton(String scanText) {

		WebElement els = driver.findElement(By.tagName("body"));

		List<WebElement> elList = els.findElements(By.tagName("div"));
		return findButton(scanText, elList);
	}

	public WebElement findButton(String scanText, List<WebElement> root) {

		List<WebElement> parentList = new ArrayList<WebElement>();
		List<WebElement> parent = this.scan(root, scanText, parentList);

		for (int i = parent.size() - 1; i >= 0; i--) {
			WebElement tmp = parent.get(i);

			WebElement button = null;
			try {
				// button = tmp.findElement(By.className("standard_btn"));
				button = tmp.findElement(By.tagName("input"));
			} catch (Exception e) {
				continue;
			}

			return button;
		}

		System.out.println("Failed to find text: " + scanText);
		return null;
	}

	// Parents is a list of all parents containing the scanText public
	List<WebElement> scan(List<WebElement> els, String scanText, List<WebElement> parents) {
		System.out.println("Size: " + els.size());
		for (WebElement el : els) {
			String text = el.getText();
			if (text.contains(scanText)) {
				parents.add(el);
				return this.scan(el.findElements(By.tagName("div")), scanText, parents);
			}
		}

		return parents;
	}

	/*
	 * 
	 * AVAILABLE ACTIONS BELOW
	 */

	/*
	 * public void act() { // Reset previous actions first try { WebElement
	 * perhapsNotButton = this.getElement(By.id("perhapsnotbtn"), 5);
	 * perhapsNotButton.click();
	 * 
	 * WebElement onwardsButton =
	 * this.getElement(By.xpath("//*[@id=\"mainContentViaAjax\"]/div[4]/input"),
	 * 5); onwardsButton.click(); } catch (Exception e) { // Do nothing, it is
	 * ok. }
	 * 
	 * WebElement travelButton =
	 * this.getElement(By.xpath("//*[@id=\"mainContentViaAjax\"]/div[2]/img"));
	 * travelButton.click();
	 * 
	 * WebElement flitButton =
	 * this.getElement(By.xpath("//*[@id=\"topMap\"]/img[11]"));
	 * flitButton.click();
	 * 
	 * WebElement storyTab = this.getElement(By.id("storyTab"));
	 * storyTab.click();
	 * 
	 * WebElement makeMoneyButton = this.getElement(By.xpath(
	 * "//*[@id=\"storyletsSection\"]/div[11]/div[3]/div[1]/input"));
	 * makeMoneyButton.click();
	 * 
	 * WebElement robSeanceButton = this.getElement(By.xpath(
	 * "//*[@id=\"storylet16925\"]/form/div[3]/div[1]/input"));
	 * robSeanceButton.click();
	 * 
	 * WebElement onwardsButton =
	 * this.getElement(By.xpath("//*[@id=\"mainContentViaAjax\"]/div[6]/input"
	 * )); onwardsButton.click();
	 * 
	 * numActions--; }
	 */

	// public void robSeance() throws InterruptedException {
	// // Reset previous actions first
	// try {
	// WebElement perhapsNotButton = this.getElement(By.id("perhapsnotbtn"), 5);
	// perhapsNotButton.click();
	//
	// WebElement onwardsButton =
	// this.getElement(By.xpath("//*[@id=\"mainContentViaAjax\"]/div[4]/input"),
	// 5);
	// onwardsButton.click();
	// } catch (Exception e) {
	// // Do nothing, it is ok.
	// }
	//
	// WebElement travelButton =
	// this.getElement(By.xpath("//*[@id=\"mainContentViaAjax\"]/div[2]/img"));
	// travelButton.click();
	//
	// Thread.sleep(5000);
	//
	// System.out.println("?");
	// Actions action = new Actions(driver);
	// WebElement el1 = this.getElement(By.xpath("//*[@id=\"topMap\"]/img[8]"));
	// WebElement el2 = this.getElement(By.xpath("//*[@id=\"topMap\"]/a[8]"));
	// action.moveToElement(el1).moveToElement(el2).click();
	// System.out.println("??");
	// // WebElement flitButton =
	// // this.getElement(By.xpath("//*[@id=\"overlay11\"]"));
	// // flitButton.click();
	// // this.getElement(By.xpath("//*[@id=\"overlay11\"]")).click();
	//
	// this.doAction("It's all very well");
	// this.doAction("It should be easy to do.");
	//
	// WebElement storyTab = this.getElement(By.id("storyTab"));
	// storyTab.click();
	//
	// numActions--;
	// }
	//
	// public void acceptMessages() throws InterruptedException {
	// WebElement homeTab = this.getElement(By.id("homeTab"));
	// homeTab.click();
	//
	// /*
	// * WebElement messages =
	// * this.getElement(By.id("FeedMessagesWithInvitations"));
	// * List<WebElement> messagesEl =
	// * messages.findElements(By.className("buttons")); for(WebElement el :
	// * messagesEl) { try { WebElement proceed = this.getElement(el,
	// * By.className("GO")); proceed.click(); } catch (Exception e) { //It's
	// * ok, do nothing. } }
	// */
	//
	// // this.doAction("Accept",
	// // driver.findElements(By.id("FeedMessagesWithInvitations")));
	// WebElement acceptButton =
	// this.getElement(By.linkText("/Me/AcceptInvitation"));
	// acceptButton.click();
	// numActions--;
	// }
	//
	// public void nurseFriend() throws InterruptedException {
	// // Reset previous actions first
	// try {
	// WebElement perhapsNotButton = this.getElement(By.id("perhapsnotbtn"), 5);
	// perhapsNotButton.click();
	//
	// WebElement onwardsButton =
	// this.getElement(By.xpath("//*[@id=\"mainContentViaAjax\"]/div[4]/input"),
	// 5);
	// onwardsButton.click();
	// } catch (Exception e) {
	// // Do nothing, it is ok.
	// }
	//
	// WebElement travelButton =
	// this.getElement(By.xpath("//*[@id=\"mainContentViaAjax\"]/div[2]/img"));
	// travelButton.click();
	//
	// WebElement homeButton =
	// this.getElement(By.xpath("//*[@id=\"overlay2\"]"));
	// homeButton.click();
	//
	// this.doAction("Earn Hard-Earned Lessons");
	// this.doAction("Wounds will be reduced.");
	//
	// WebElement chooseButton =
	// this.getElement(By.xpath("//*[@id=\"mainContentViaAjax\"]/div/div[7]/form/input[2]"));
	// chooseButton.click();
	//
	// WebElement storyTab = this.getElement(By.id("storyTab"));
	// storyTab.click();
	//
	// numActions--;
	// }
	//
	// /***********
	// *
	// *
	// * Internal methods below
	// *
	// * @throws InterruptedException
	// *
	// */
	// public void doAction(String scanText, List<WebElement> rootChildren) {
	// List<WebElement> parentList = new ArrayList();
	// List<WebElement> parent = this.scan(rootChildren, scanText, parentList);
	//
	// for (int i = parent.size() - 1; i >= 0; i--) {
	// WebElement tmp = parent.get(i);
	//
	// WebElement button = null;
	// try {
	// button = this.getElement(tmp, By.className("standard_btn"), 0);
	// } catch (Exception e) {
	// continue;
	// }
	//
	// button.click();
	// System.out.println("Action taken: " + scanText);
	// return;
	// }
	//
	// System.out.println("Failed to find text: " + scanText);
	// }
	//
	//
	//
	// public Boolean checkValid() {
	// int wounds = 0;
	// int scandal = 0;
	// int suspicion = 0;
	// int nightmares = 0;
	//
	// WebElement leftCol = this.getElement(By.id("lhs_col"));
	// String colText = leftCol.getText().replaceAll("(\\r|\\n)", " ");
	// String[] words = colText.split(" ");
	// for (int i = 0; i < words.length; i++) {
	// if (words[i].equals("WOUNDS")) {
	// wounds = Integer.parseInt(words[i + 1]);
	// attributes.put("wounds", wounds);
	// } else if (words[i].equals("SCANDAL")) {
	// scandal = Integer.parseInt(words[i + 1]);
	// attributes.put("scandal", scandal);
	// } else if (words[i].equals("SUSPICION")) {
	// suspicion = Integer.parseInt(words[i + 1]);
	// attributes.put("suspicion", suspicion);
	// } else if (words[i].equals("NIGHTMARES")) {
	// nightmares = Integer.parseInt(words[i + 1]);
	// attributes.put("nightmares", nightmares);
	// }
	// }
	//
	// if (numActions > 7 && wounds < 5 && scandal < 5 && suspicion < 5 &&
	// nightmares < 5) {
	// return true;
	// }
	//
	// return false;
	// }
	//

	//
	// /*
	// * public WebElement getElement(final By type) { return
	// * this.getElement(type, defaultWait); }
	// *
	// * public WebElement getElement(final By type, int timeout) {
	// *
	// * if (timeout > 0) { Wait<WebDriver> wait = new
	// * FluentWait<WebDriver>(driver).withTimeout(timeout,
	// * TimeUnit.SECONDS).pollingEvery(1,
	// * TimeUnit.SECONDS).ignoring(NoSuchElementException.class);
	// *
	// * WebElement foo = wait.until(new Function<WebDriver, WebElement>() {
	// * public WebElement apply(WebDriver driver) { return
	// * driver.findElement(type); } }); return foo; } else { return
	// * driver.findElement(type); } }
	// */
	// public WebElement getElement(final WebElement root, final By type) {
	// return this.getElement(root, type, defaultWait);
	// }
	//
	// public WebElement getElement(final WebElement root, final By type, int
	// timeout) {
	//
	// if (timeout > 0) {
	// Wait<WebDriver> wait = new
	// FluentWait<WebDriver>(driver).withTimeout(timeout,
	// TimeUnit.SECONDS).pollingEvery(1,
	// TimeUnit.SECONDS).ignoring(NoSuchElementException.class);
	//
	// WebElement foo = wait.until(new Function<WebDriver, WebElement>() {
	// public WebElement apply(WebDriver driver) {
	// return root.findElement(type);
	// }
	// });
	// return foo;
	// } else {
	// return root.findElement(type);
	// }
	// }
	//
	// public int getWounds() {
	// return attributes.get("wounds").intValue();
	// }
	//
	// public int getScandal() {
	// return attributes.get("scandal").intValue();
	// }
	//
	// public int getNightmares() {
	// return attributes.get("nightmares").intValue();
	// }
	//
	// public int getSuspicion() {
	// return attributes.get("suspicion").intValue();
	// }
	//
	// public int getActions() {
	// return numActions;
	// }
}
