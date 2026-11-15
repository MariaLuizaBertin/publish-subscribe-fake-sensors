package core.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class ExampleController {

	@GetMapping("/{userId}")
	public String getUser(@PathVariable Long userId) {
		return "user " + userId;
	}


}
