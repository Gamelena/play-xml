package controllers;

import play.*;
import play.mvc.*;
import zwei.admin.ClassInvoker;
import zwei.admin.Xml;
import zwei.dojo.Json;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.xml.sax.SAXException;


import models.*;



public class Application extends Controller 
{
	private String style = "tundra";
	private String sitename;
	private String template = "";	
	

	
	public static void index(String style, String template)
	{
		String baseUrl = Play.configuration.getProperty("application.baseUrl");
		String baseDojoFolder = "/dojotoolkit";
		style = style != null ? style : "tundra"; 
		
		render(baseUrl, template, style);
	}
	
	
	public static void components(String p)
	{
		Map<String, String> form = params.allSimple();//All request params
		String pathComponents = Play.configuration.getProperty("application.pathComponents");
		String content = "";
		ClassInvoker componentClass = null;
		
		File file = new File(pathComponents + "/" + p + ".xml");
		Xml xml = new Xml(file);
		
		try {
			xml.parse();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			content = "<p>No se encuentra archivo <i>" + pathComponents + "/" + p + ".xml</i></p>";
			render(content);
		}
		
		Element component = xml.getComponent();
		String className = "";
		String methodName = "";
		
		try {
			className = "zwei.admin.components." + zwei.utils.StringU.toClassName(component.getAttribute("type"));//Nombre clase
			methodName = "display";//Nombre método
		} catch (NullPointerException e) {
			e.printStackTrace();
			content = "<p>Componente <i>" + pathComponents + "/" + p + ".xml</i> no tiene atributo <i>type</i></p>";
			render(content);
		}	
		
		/**
		 * Información de parametros clase
		 */
		Class[] componentParamsTypes = new Class[] { String.class, String[].class, Map.class };//Clases de parámetros del constructor
		Object[] componentParams = new Object [] { p, null, form };//Valores parámetros del constructor

		
		try {
			componentClass = new ClassInvoker(className, componentParamsTypes);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			content = "<p>No se encuentra <i>" + className + ".xml</i></p>";
			render(content);
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//se asocian parámetros a clase
		
		try {
			//componentClass.initMethod(methodName, methodArgsTypes);
			componentClass.initMethod(methodName);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//se construye clase y prepara método
		
		try {
			//content = (String) componentClass.getResult(componentParams, methodArgs);
			content = (String) componentClass.getResult(componentParams);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			content = "<p>IllegalArgumentException para <i>" + className + "." + methodName + "(), " + e.getMessage() + "</i></p>";
			render(content);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			Logger.error(e, methodName, componentParams);
			content = "<p>InvocationTargetException para <i>" + className + "." + methodName + "(), " + e.getMessage() + "</i></p>";
			render(content);

		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//se obtiene resultado del método

		render(content);
	}
	
	public static void modules()
	{
		AclModules aclModules = new AclModules();
		HashMap tree = aclModules.getTreeStruct(0);
		HashMap content = Json.toDataStore(tree); 
		renderJSON(content);
	}
	
	
    public static void test() 
    {
    	new AclModules();
    	List tasks = Task.findAll();
        render(tasks);
    }
    
    public static void changeStatus(Long id, Boolean done) 
    {
    	Task task = Task.findById(id);
    	task.done = done;
    	task.save();
    	renderJSON(task);
    }
    
	public static void createTask(String title) 
	{
		Task task = new Task(title).save();
		renderJSON(task);
	}
}