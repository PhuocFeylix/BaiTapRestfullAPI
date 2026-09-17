package vn.feylix.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

	@GetMapping("/home") // Đổi từ "/" thành "/home"
	public String index() {
		return "index";
	}
}