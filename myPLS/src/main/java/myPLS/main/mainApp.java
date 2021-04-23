package myPLS.main;

import static spark.Spark.get;
import static spark.Spark.post;

import java.io.IOException;

import freemarker.core.ParseException;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;
import myPLS.controllers.AdminController;
import myPLS.controllers.CourseController;
import myPLS.controllers.FeedbackController;
import myPLS.controllers.GroupDiscussionChatController;
import myPLS.controllers.LearnerController;
import myPLS.controllers.ProfessorController;
import myPLS.controllers.RegistrationController;

public class mainApp {
	
	private final static RegistrationController registraionController = new RegistrationController();
	private final static CourseController courseController = new CourseController();
	private final static LearnerController learnerController = new LearnerController();
	private final static AdminController adminController = new AdminController();
	private final static GroupDiscussionChatController groupDiscussionChatController = new GroupDiscussionChatController();
	private static final ProfessorController professorController = new ProfessorController();
	private static final FeedbackController feedbackController = new FeedbackController();
	public static int fileCount=0;
	public static void main(String[] args) throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException, TemplateException, Exception {
		// get("/*", (request, response) -> {
		// 	if(request.session().attribute("userID")==null){
		// 		request.session().attribute("userID", -1);
        //     	return registraionController.getLoginPage();
		// 	}
		// 	return null;
        // });
		
		get("/registerUser", (request, response) -> {
            return registraionController.getRegistrationPage();
        });
		
		post("/registerUser", (request, response) -> {
        	return registraionController.registerUser(request);
        });
		
		post("/mailVerified",(request,response) -> {
			return registraionController.authoriseUser(request);
		});
		
		post("/resetPassword",(request,response) -> {
			registraionController.resetPassword(request, response);
			return 0;
		});
		
		get("/loginPage", (request, response) -> {
            return registraionController.getLoginPage();
        });
        get("/index", (request, response) -> {
			response.redirect("/loginPage");
			return 0;
        });
        get("/", (request, response) -> {
			response.redirect("/loginPage");
			return 0;
        });
        post("/logIn",(request,response) -> {
            return registraionController.logIn(request, response);
        });
		post("/inviteMembers",(request,response) -> {
            return adminController.inviteMembers(request, response);
        });
		post("/postChat",(request,response) -> {
            return groupDiscussionChatController.postMessage(request, response);
        });
		post("/addMemberToGroup",(request,response) -> {
            return adminController.addMemberToGroup(request, response);
        });
		get("/viewGroupChats/:groupDiscussionID",(request,response) -> {
            return adminController.viewGroupChats(request, response);
        });
        get("/addCourse", (request, response) -> {
        	return courseController.getAddCoursePage("", -1);
        });
        get("/createGroup", (request, response) -> {
        	return adminController.getGroupDiscussionPage(request, response);
        });
		get("/viewGroups",(request,response) -> {
        	return adminController.viewAllGroupDiscussion(request, response);
        });
		post("/viewMembersInGroup",(request,response) -> {
        	return adminController.viewMembersInGroupDiscussion(request, response);
        });
		// -----------------QUESTION MODULE---------------------
		post("/question",(request,response) -> {
        	return professorController.getQuestionPage(request, response);
        });
		get("/question",(request,response) -> {
        	return professorController.getQuestionPage(request, response);
        });
		get("/addQuestion",(request,response) -> {
        	return professorController.addQuestionPage(request, response);
        });
		post("/addQuestion",(request,response) -> {
        	return professorController.addQuestion(request, response);
        });
		post("/addQuestion",(request,response) -> {
        	return professorController.addQuestion(request, response);
        });
		post("/publishQuiz",(request,response) -> {
        	return professorController.publishQuiz(request, response);
        });
		post("/addQuestionToList",(request,response) -> {
        	return professorController.addQuestionToList(request, response);
        });
		get("/createQuiz/:courseID", (request, response) -> {
			return professorController.getQuestionPage(request, response);
        });
		get("/viewQuiz/:courseID", (request, response) -> {
			return professorController.getQuizListPage(request, response);
        });
		
		get("/createQuiz", (request, response) -> {
			return professorController.getQuestionPage(request, response);
        });
		get("/quizAnswerKey/:quizID", (request, response) -> {
			return professorController.getAnswerKey(request, response);
        });
		// -----------------QUESTION MODULE---------------------
        get("/courses",(request,response) -> {
        	return courseController.getCourses();
        });
		post("/modifyCourse",(request,response) -> {
        	courseController.modifyCourse(request, response);
			return 0;
        });
		
        
        get("/studentDashboard",(request,response) -> {
        	return learnerController.getLearnerDashboard(request);
        });
		get("/enrollForCourses",(request,response) -> {
        	return learnerController.getCourseListForLearners(request, response);
        });
		get("/enroll/:userId/:courseId",(request,response) -> {
        	return learnerController.enrollLearnerForCourse(request, response);
        });
        
        get("/professorDashboard",(request,response) -> {
        	return professorController.getProfessorDashboard(request);
        });
        
        post("/addCourse", (request, response) -> {
			courseController.addCourse(request,response);
			response.redirect("/courses");
			return 0;
        });	
		post("/courseGroupChat", (request, response) -> {
			return courseController.getCourseChat(request,response);
        });
		get("/courseGroupChat/:courseGroupID", (request, response) -> {
			return courseController.getCourseChat(request,response);
        });
		post("/postMessageForCourseGroup", (request, response) -> {
			courseController.postGroupMessage(request,response);
			return null;
        });
        post("/createACourseGroup", (request, response) -> {
			if(professorController.createACourseGroup(request,response))
				response.redirect("/professorDashboard");
			return 0;
        });
        post("/addRemoveMemberCourseGroup",(request,response) -> {
        	courseController.addRemoveMemberCourseGroup(request, response);
			return true;
        });
		get("/enrollCourse",(request,response) -> {
			return learnerController.getAllCourses(request);
		});
		
		post("/enrollCourse",(request,response) -> {
			return learnerController.enrollCourse(request,response);
		});
		
		get("/enrolledLearners",(request,response) -> {
			return learnerController.getLearnersEnrolledList(request);
		});
		
		post("/learnerFeedback",(request,response) -> {
			return feedbackController.getStudentFeedbackPage(request);
		});

		post("/addStudentFeedback",(request,response) -> {
			return feedbackController.addStudentFeedback(request,response);
		});
		
		post("/preReqCourse", (request, response) -> {
				return courseController.getAddPreReqCoursePage(request,response);
	    });
		
		post("/addPreReqCourse", (request, response) -> {
			return courseController.addPreReqCourse(request,response);
		});
        
		post("/addLecture", (request, response) -> {
			return professorController.addLecture(request,response);
		});
		
		get("/addLecture",(request,response) -> {
			return professorController.getAddLecturePage(request);
		});
		
		get("/getLectures",(request,response) -> {
			return professorController.getLectures(request);
		});
		
		get("/getUploadPdfPage",(request,response) -> {
			return professorController.getUploadPage(request);
		});
		
		post("/uploadPdf", (request, response) -> {
			return professorController.uploadPdf(request,response);
		});
		
		post("/downloadPdf",(request,response) -> {
			return professorController.downloadPdfLecture(request,response);
		});
		
		post("/getPdfs",(request,response) -> {
			return professorController.getPdfLectures(request,response);
		});
		
		post("/editLecture",(request,response) -> {
			return professorController.getEditLecturePage(request,response);
		});
		
		post("/updateLecture",(request,response) -> {
			return professorController.updateLecture(request,response);
		});
		
		post("/deleteLecture",(request,response) -> {
			return professorController.deleteLecture(request,response);
		});
	}
}
