package controllers;

import play.*;
import play.mvc.*;

import java.util.*;
import models.*;



public class Components extends Controller 
{
    public static void index() {
    	new AclModules();
    	List tasks = Task.findAll();
        render(tasks);
    }
    
    public static void changeStatus(Long id, Boolean done) {
    	Task task = Task.findById(id);
    	task.done = done;
    	task.save();
    	renderJSON(task);
    }
    
	public static void createTask(String title) {
		Task task = new Task(title).save();
		renderJSON(task);
		
	}
}