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


	public static void main(String[] args) {
		SpringApplication.run(BugWarsApplication.class, args);
	}



}
