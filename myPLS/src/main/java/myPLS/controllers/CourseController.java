package myPLS.controllers;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.Version;
import myPLS.beans.Course;
import myPLS.beans.CourseGroup;
import myPLS.beans.CourseGroupChat;
import myPLS.beans.Stream;
import myPLS.beans.User;
import myPLS.services.CourseService;
import myPLS.services.CourseServiceImpl;
import myPLS.services.RegistrationService;
import myPLS.services.CourseComponentServiceImpl;
import myPLS.services.StreamService;
import myPLS.services.StreamServiceImpl;
import spark.Request;
import spark.Response;
import spark.Spark;

/**
 * The CourseController class to implement courses functionality
 * @author ashnil
 *
 *
 */
public class CourseController {
	private final Configuration configuration = new Configuration(new Version(2, 3, 0));
	private static CourseService courseService;
	private static StreamService streamService;
	private static RegistrationService registrationService;
	private static CourseService preReqService;
	private ProfessorController pfController;

	public CourseController() {
		setConfiguration();
		courseService = new CourseComponentServiceImpl();
		streamService = new StreamServiceImpl();
		registrationService = new RegistrationService(); 
		preReqService = new CourseServiceImpl();
		pfController = new ProfessorController();
	}

	// method to call adminDashboard.ftl file based on admin role
	public StringWriter getCourses(Request request) {
		StringWriter writer = new StringWriter();
		Map<String, Object> map = new HashMap<String, Object>();
		Template resultTemplate;
		List<Course> courses = (List<Course>)courseService.getCourses();
		Map<Integer, List<Course>> prereqCourses = new HashMap<Integer, List<Course>>();
		for (Course c : courses) {
			request.attribute("courseId", c.getCourseId());
			prereqCourses.put(c.getCourseId(), new ArrayList<Course>());
			List<Course> preReqs = preReqService.getCoursesById(request);
			Iterator<Course> itr = preReqs.iterator();
			while (itr.hasNext()) {
				prereqCourses.get(c.getCourseId()).add(itr.next());
			}
		}
		try {
			resultTemplate = configuration.getTemplate("templates/adminDashboard.ftl");
			map.put("courses", courses);
			map.put("preReqs", prereqCourses);
			resultTemplate.process(map, writer);
		} catch (Exception e) {
			Spark.halt(500);
		}
		return writer;
	}

	// method to call viewCourseGroupChats.ftl file 
	public StringWriter getCourseChat(Request request, Response response) {
		StringWriter writer = new StringWriter();
		Map<String, Object> map = new HashMap<String, Object>();
		int courseGroupID=-1;
		if(request.queryParams("courseGroupID") != null){
			courseGroupID = Integer.parseInt(request.queryParams("courseGroupID"));
		}else{
			courseGroupID = Integer.parseInt(request.params("courseGroupID"));
		}
		// int courseGroupID = Integer.parseInt(request.queryParams("courseGroupID") != null ? request.queryParams("courseGroupID") : "-1");
		List<CourseGroupChat> groupChats = courseService.getChatsForCourseGroup(courseGroupID);
		Template resultTemplate;
		try {
			resultTemplate = configuration.getTemplate("templates/viewCourseGroupChats.ftl");
			map.put("groupChats", groupChats);
			map.put("userID", request.session().attribute("userID"));
			map.put("courseGroupID", courseGroupID);
			resultTemplate.process(map, writer);
		} catch (Exception e) {
			Spark.halt(500);
		}
		return writer;
	}
	public boolean postGroupMessage(Request request, Response response){
		int courseGroupID = courseService.addMessageToCourseGroup(request);
		if(courseGroupID!=-1){
			response.redirect("/courseGroupChat/"+courseGroupID);
			return true;
		}
		return false;
		
	}
	// method to  add or remove members from course group
	public boolean addRemoveMemberCourseGroup(Request request, Response response){
		int operation = -1;
		int courseId=Integer.parseInt(request.queryParams("courseId"));
		int userID=Integer.parseInt(request.queryParams("userID"));
		if(request.queryParams("addMember")!=null){
			operation=1;
		}else if(request.queryParams("removeMember")!=null){
			operation=2;
		}

		if(operation!=-1)
			if(courseService.addRemoveMemberCourseGroup(courseId, userID, operation))
				response.redirect("/enrolledLearners?courseId="+courseId);
		return true;
	}
	// method to modify a course
	public void modifyCourse(Request request,Response response){
		int courseId = Integer.parseInt(request.queryParams("courseId"));
		String operation="";
		if(request.queryParams("deleteCourse")!=null){
			operation="DELETE";
		}else if(request.queryParams("updateCourse")!=null){
			operation="UPDATE";
			this.getAddCoursePage(operation, courseId);
		}
		if(this.courseService.modifyCourse(courseId, operation)){
			response.redirect("/courses");
		}else{
			System.out.println("ERROR");
		}
	}
	// method to call course.ftl file 
	public StringWriter getAddCoursePage(String operation, int courseId) {
		StringWriter writer = new StringWriter();
		Map<String,Object> map = new HashMap<String, Object>();
		List<Stream> streams = streamService.getStreams();
		List<User> professors = registrationService.getUsersByRole("professor");
		if(operation.equalsIgnoreCase("UPDATE") && courseId>0){
			Course c = this.courseService.getCourseByCourseId(courseId);
			map.put("courseToUpdate", c);
		}
		map.put("streams", streams);
		map.put("professors", professors);
		try {
			Template formTemplate = configuration.getTemplate("templates/course.ftl");
			formTemplate.process(map, writer);
		} catch (Exception e) {
			Spark.halt(500);
		}

		return writer;
	}
	
	// method to call addPreReq.ftl file 
	public StringWriter getAddPreReqCoursePage(Request request, Response response) {
		StringWriter writer = new StringWriter();
		Map<String, Object> map = new HashMap<String, Object>();
		Template resultTemplate;
		List<Course> courses = (List<Course>)courseService.getCourses();
		try {
			resultTemplate = configuration.getTemplate("templates/addPreReq.ftl");
			map.put("courses", courses);
			map.put("courseId", Integer.parseInt(request.queryParams("courseId")));
			String msg = request.queryParams("errorMsg");
			map.put("errorMsg", msg);
			resultTemplate.process(map, writer);
		} catch (Exception e) {
			Spark.halt(500);
		}
		return writer;
	}
	
	public StringWriter addPreReqCourse(Request request, Response response) {
		boolean result = preReqService.addCourse(request);
		StringWriter writer = new StringWriter();
		Map<String, Object> map = new HashMap<String, Object>();
		Template resultTemplate;
		try {
			if(result) {
				return this.getCourses(request);
			} else {
				resultTemplate = configuration.getTemplate("templates/addPreReq.ftl");
				List<Course> courses = (List<Course>) courseService.getCourses();
				map.put("courses", courses);
				map.put("courseId", Integer.parseInt(request.queryParams("courseId")));
				map.put("errorMsg", "Course preReq cannot be added as it's creating cyclic dependency");
			}
			
			resultTemplate.process(map, writer);
		} catch (Exception e) {
			Spark.halt(500);
			}
			return writer;
	}

	public void addCourse(Request request, Response response) {
		courseService.addCourse(request);
	}
	
	public StringWriter deleteCourse(Request request, Response response) {
		courseService.deleteCourse(request);
		return this.getCourses(request);
	}

	private void setConfiguration() {
		configuration.setClassForTemplateLoading(CourseController.class, "/");
	}
}
