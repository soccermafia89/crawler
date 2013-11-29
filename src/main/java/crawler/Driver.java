package crawler;

import java.util.Map;

public class Driver {
	public Driver() {

	}

	public void drive(String[] args) {
		FallenCrawler crawler = new FallenCrawler(args[0], args[1]);
		try {
			Story story = new Story(crawler);

			Map<String, Integer> stats = crawler.getStats();
			while (this.checkStats(stats)) {
				stats = crawler.getStats();

				if (stats.get("wounds") > 4) {
					this.inviteNurse();
					story.acceptMessages();
					crawler.reload();
				} else {
					story.robSeance();
					// story.forceAction();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			crawler.quit();
		}
	}

	public Boolean checkStats(Map<String, Integer> stats) {
		int actions = stats.get("actions");
		int wounds = stats.get("wounds");
		int suspicion = stats.get("suspicion");
		int nightmares = stats.get("nightmares");
		int scandal = stats.get("scandal");

		System.out.println("actions: " + actions);
		System.out.println("wounds: " + wounds);
		System.out.println("suspicion: " + suspicion);
		System.out.println("nightmares: " + nightmares);
		System.out.println("scandal: " + scandal);

		if (actions < 3 || wounds > 6 || suspicion > 6 || nightmares > 6 || scandal > 6) {
			return false;
		}

		return true;
	}

	public void inviteNurse() {
		FallenCrawler garbage = new FallenCrawler("aethier@gmail.com", "1");
		
		try {
			Story story = new Story(garbage);
			Map<String, Integer> stats = garbage.getStats();
			if (stats.get("actions") > 0) {
				story.nurseFriend();
			}
		} finally {
			garbage.quit();
		}
	}
}
