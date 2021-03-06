package myPLS.services;

import java.util.List;

import myPLS.beans.Course;
import myPLS.beans.CourseGroup;
import myPLS.beans.CourseGroupChat;
import myPLS.beans.CourseGroupMembers;
import spark.Request;

public interface CourseService {
	boolean addCourse(Request request);
	default boolean updateCourse(Request course) {return false;}
	Object getCourses();
	boolean deleteCourse(Request request);
	Course getCourseByCourseId(Request request);
	List<Course> getCoursesById(Request request);
	Course getCourseByCourseId(int courseId);
	boolean createACourseGroup(Request request);
	CourseGroup getCourseGroupByCourseId(int courseId);
	List<CourseGroup> getCourseGroupByUserId(int userId);
	List<CourseGroup> getCourseGroupForUserByUserId(int userId);
	List<CourseGroupChat> getChatsForCourseGroup(int courseGroupID);
	int addMessageToCourseGroup(Request request);
	List<CourseGroupMembers> getCourseGroupMemberFromCourseGroupId(int courseGroupId);
	boolean addRemoveMemberCourseGroup(int courseId, int userId, int operation);
	public boolean modifyCourse(int courseId, String operation);
}
