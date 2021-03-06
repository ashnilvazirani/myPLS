package myPLS.controllers;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.Version;
import myPLS.services.RegistrationService;
import spark.Request;
import spark.Response;
import spark.Spark;

/**
 * The RegistrationController class to implement registration page functionality
 * @author abriti
 *
 */
public class RegistrationController {
	private final Configuration configuration = new Configuration(new Version(2, 3, 0));
	private static RegistrationService registrationService;

	public RegistrationController() {
		setConfiguration();
		registrationService = new RegistrationService();
	}

	 // method to call registration.ftl page for new users
	public StringWriter getRegistrationPage() {
		StringWriter writer = new StringWriter();
		try {
			Template formTemplate = configuration.getTemplate("templates/registration.ftl");
			formTemplate.process(null, writer);
		} catch (Exception e) {
			Spark.halt(500);
		}

		return writer;

	}

	 // method to call registration.ftl page for new users
	public StringWriter registerUser(Request request) {
		StringWriter writer = new StringWriter();
		Map<String, Object> map = new HashMap<String, Object>();
		Template resultTemplate;
		Map<String, Object> result = registrationService.registerUser(request);
		if (result.isEmpty()) {
			try {
				resultTemplate = configuration.getTemplate("templates/authorizationMsg.ftl");
				map.put("result", result);
				resultTemplate.process(map, writer);
			} catch (Exception e) {
				Spark.halt(500);
			}
		} else {
			try {
				resultTemplate = configuration.getTemplate("templates/registration.ftl");
				resultTemplate.process(result, writer);
			} catch (Exception e) {
				Spark.halt(500);
			}
		}

		return writer;
	}

	 // method to call resetPassword.ftl page 
	public StringWriter authoriseUser(Request request) {
		registrationService.updateAuthorization(request);
		StringWriter writer = new StringWriter();
		try {
			Template formTemplate = configuration.getTemplate("templates/resetPassword.ftl");
			formTemplate.process(null, writer);
		} catch (Exception e) {
			Spark.halt(500);
		}
		return writer;
	}

	public void resetPassword(Request request, Response response) {
		Map<String, Object> map = registrationService.resetPassword(request);
		if ((boolean)map.get("updated")) {
			try {
				registrationService.updatePassword(request);
				this.getDashboard(map.get("role").toString(), response);
			} catch (Exception e) {
				Spark.halt(500);
			}
		} else {
			// render page to verify email address
			System.out.println("Please verify your email address");
		}
	}

	public void getDashboard(String role, Response response) {
		try {
			if (role.equals("admin")) {
				response.redirect("/courses");
			} else if (role.equals("student")) {
				response.redirect("/studentDashboard");
			} else {
				response.redirect("/professorDashboard");
			}
		} catch (Exception e) {
			Spark.halt(500);
		}

	}

	private void setConfiguration() {
		configuration.setClassForTemplateLoading(RegistrationController.class, "/");
	}

	 // method to call login.ftl page based on user role
	public Object logIn(Request request, Response response) {
		StringWriter writer = new StringWriter();
		Template formTemplate;
		Map<String,Object> result = registrationService.logIn(request);
		if ((boolean) result.get("validUser")) {
			request.session().attribute("userID", result.get("userID"));
			try {
				String role = registrationService.logIn(request).get("role").toString();
				request.session().attribute("role", role);
				this.getDashboard(role, response);
			} catch (Exception e) {
				Spark.halt(500);
			}
		} else {
			System.out.println("Invalid credentials");
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("status", "error");
			map.put("message", "Invalid Credentials!!");
			try {
				formTemplate = configuration.getTemplate("templates/login.ftl");
				formTemplate.process(map, writer);
			} catch (Exception e) {
				Spark.halt(500);
			}

		}
		return writer;
	}

	public StringWriter getLoginPage() {
		StringWriter writer = new StringWriter();
		try {
			Template formTemplate = configuration.getTemplate("templates/login.ftl");
			formTemplate.process(null, writer);
		} catch (Exception e) {
			Spark.halt(500);
		}

		return writer;

	}

	public StringWriter getUploadPage() {
		StringWriter writer = new StringWriter();
		try {
			Template formTemplate = configuration.getTemplate("templates/uploadFileTempForm.ftl");
			formTemplate.process(null, writer);
		} catch (Exception e) {
			Spark.halt(500);
		}
		return writer;
	}
}
