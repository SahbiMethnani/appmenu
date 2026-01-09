package com.example.appmenu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.TimeZone;

@SpringBootApplication
@EnableCaching
@EnableScheduling
@EnableAsync
public class AppmenuApplication {

	public static void main(String[] args) {
		// ✅ Configure le fuseau horaire par défaut
		TimeZone.setDefault(TimeZone.getTimeZone("Africa/Tunis")); // ou "UTC"

		// ✅ Lance l'application
		ConfigurableApplicationContext context = SpringApplication.run(AppmenuApplication.class, args);

		// ✅ Affiche les informations de démarrage
		logApplicationStartup(context.getEnvironment());
	}

	/**
	 * Affiche les informations de démarrage de l'application
	 */
	private static void logApplicationStartup(Environment env) {
		String protocol = "http";
		if (env.getProperty("server.ssl.key-store") != null) {
			protocol = "https";
		}

		String serverPort = env.getProperty("server.port", "8080");
		String contextPath = env.getProperty("server.servlet.context-path", "/");
		String hostAddress = "localhost";

		try {
			hostAddress = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			System.err.println("⚠️ Impossible de déterminer l'adresse IP de l'hôte");
		}

		String activeProfiles = String.join(", ", env.getActiveProfiles());
		if (activeProfiles.isEmpty()) {
			activeProfiles = String.join(", ", env.getDefaultProfiles());
		}


	}
}