package net.crusadergames.bugwars;

import net.crusadergames.bugwars.dto.request.ScriptRequest;
import net.crusadergames.bugwars.model.Script;
import net.crusadergames.bugwars.service.ScriptService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;


@SpringBootApplication
public class BugWarsApplication {

	private static final ScriptService rest = new ScriptService();

	public static void main(String[] args) {
		SpringApplication.run(BugWarsApplication.class, args);

		List<Script> listOfScripts = new ArrayList<>();
		long user = 1;
		rest.getAllScripts(user);
		if (listOfScripts != null) {
			listOfScripts.forEach(System.out::println);
		} else {
			System.out.println("Null");
		}

	}



}
