package controllers;

import play.mvc.Controller;
import play.libs.F;
import play.mvc.Result;
import models.*;
import java.util.*;
import play.data.Form;
import static play.data.Form.form;
import com.avaje.ebean.Ebean;
import play.db.ebean.Model;
import play.db.ebean.Transactional;
import play.db.ebean.Model;
import play.cache.Cached;
import play.cache.Cache;
import play.mvc.WebSocket;

public class FitnessController extends Controller {

	public static Result welcome() {
		return ok("Welcome to Borg Fitness! Time to assimilate into fitness!");
	}

	public static Result welcomeWithName(String name) {
		return ok(String.format("Welcome to Borg Fitness %s! Time to assimilate into fitness!",name));
	}

	public static Result exerciseOfTheDay() {
		return ok(views.html.exerciseoftheday.render(new Exercise("Swimming",60)));
	}

	public static Result workoutOfTheDay() {
		List<Exercise> exercises = new ArrayList<Exercise>();
		exercises.add(new Exercise("Running Sprints", 10));
		exercises.add(new Exercise("Running Light Jog", 20));
		exercises.add(new Exercise("Running Sprints", 10));
		exercises.add(new Exercise("Cool Down", 10));
		return ok(views.html.workoutoftheday.render(exercises));
		}

		public static Result initExercise() {
		Form<Exercise> exerciseForm = form(Exercise.class);
		return ok(views.html.createexercise.render(exerciseForm));
		}

		/*public static Result createExercise() {
		Form<Exercise> filledInForm = form(Exercise.class).bindFromRequest();
		if (filledInForm.hasErrors()) {
		return badRequest
		(views.html.createexercise.render(filledInForm));
		}
		return ok(
		String.format("Received exercise for %s", filledInForm.get()));
		}*/

		@Transactional
		public static Result createExercise() {
		Form<Exercise> filledInForm =form(Exercise.class).bindFromRequest();
		if (filledInForm.hasErrors()) {
		return badRequest(
		views.html.createexercise.render(filledInForm));
		}
		Exercise exercise = filledInForm.get();
		Ebean.save(exercise);
		System.out.println(Cache.get("channels"));
		for (WebSocket.Out out : (List<WebSocket.Out>) Cache.get("channels")) {
		out.write("> Added exercise! " + exercise);
		}
        Cache.remove("exercise-list");
		return getList();
		}

		@Cached(key = "exercise-list", duration = 60)
		@SuppressWarnings("unchecked")
		public static Result getList() {
		Model.Finder finder = new Model.Finder<Long, Exercise>(Long.class, Exercise.class);
		return ok(views.html.allexercises.render((List<Exercise>) finder.all()));
		}
        
        public static WebSocket<String> wsCall() {
		return new WebSocket<String>() {
		public void onReady(final WebSocket.In<String> in,
		final WebSocket.Out<String> out) {
		if (Cache.get("channels") == null) {
		List<Out> outs = new ArrayList<Out>();
		outs.add(out);
		Cache.set("channels", outs);
		} else ((List<Out>) Cache.get("channels")).add(out);
		in.onClose(new F.Callback0() {
		@Override
		public void invoke() throws Throwable {
		((List<Out>) Cache.get("channels")).remove(out);
		out.close();
		}
		});
		}
		};
		}

        /*public static WebSocket<String> wsCall() {
		return new WebSocket<String>() {
		@SuppressWarnings("unchecked")
		public void onReady(final WebSocket.In<String> in,
		final WebSocket.Out<String> out) {
		if (Cache.get("channels") == null) {
		List<Out> outs = new ArrayList<Out>();
		outs.add(out);
		Cache.set("channels", outs);
		} else ((List<Out>) Cache.get("channels")).add(out);
		in.onClose(new F.Callback0() {
		@Override
		public void invoke() throws Throwable {
		((List<Out>) Cache.get("channels")).remove(out);
		out.close();
		}
		});
		}
		};
		}*/

}
