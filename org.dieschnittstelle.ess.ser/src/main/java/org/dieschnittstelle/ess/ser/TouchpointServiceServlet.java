package org.dieschnittstelle.ess.ser;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static org.dieschnittstelle.ess.utils.Utils.*;

import org.apache.logging.log4j.Logger;
import org.dieschnittstelle.ess.entities.crm.AbstractTouchpoint;

public class TouchpointServiceServlet extends HttpServlet {

	protected static Logger logger = org.apache.logging.log4j.LogManager
			.getLogger(TouchpointServiceServlet.class);

	public TouchpointServiceServlet() {
		show("TouchpointServiceServlet: constructor invoked\n");
	}
	
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) {

		logger.info("doGet()");

		// we assume here that GET will only be used to return the list of all
		// touchpoints

		// obtain the executor for reading out the touchpoints
		TouchpointCRUDExecutor exec = (TouchpointCRUDExecutor) getServletContext()
				.getAttribute("touchpointCRUD");
		try {
			// set the status
			response.setStatus(HttpServletResponse.SC_OK);
			// obtain the output stream from the response and write the list of
			// touchpoints into the stream
			ObjectOutputStream oos = new ObjectOutputStream(
					response.getOutputStream());
			// write the object
			oos.writeObject(exec.readAllTouchpoints());
			oos.close();
		} catch (Exception e) {
			String err = "got exception: " + e;
			logger.error(err, e);
			throw new RuntimeException(e);
		}

	}

	/*
	 * TODO: SER3 server-side implementation of createNewTouchpoint
	 */

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) {

		// assume POST will only be used for touchpoint creation, i.e. there is
		// no need to check the uri that has been used

		// obtain the executor for reading out the touchpoints from the servlet context using the touchpointCRUD attribute

		TouchpointCRUDExecutor exec = (TouchpointCRUDExecutor) getServletContext()
				.getAttribute("touchpointCRUD");

		try {
			// create an ObjectInputStream from the request's input stream

			ObjectInputStream stream = new ObjectInputStream(request.getInputStream());

			// read an AbstractTouchpoint object from the stream

			AbstractTouchpoint tp = (AbstractTouchpoint) stream.readObject();

            // call the create method on the executor and take its return value

			exec.createTouchpoint(tp);

			// set the response status as successful, using the appropriate
			// constant from HttpServletResponse

			response.setStatus(HttpServletResponse.SC_CREATED);

			// then write the object to the response's output stream, using a
			// wrapping ObjectOutputStream

			ObjectOutputStream stream1 = new ObjectOutputStream(response.getOutputStream());
			stream1.writeObject(tp);

			// ... and write the object to the stream

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	/*
	 * TODO: SER4 server-side implementation of deleteTouchpoint
	 */

	@Override
	protected void doDelete(HttpServletRequest request,
						 HttpServletResponse response) {

		String[] stringArr = request.getRequestURL().toString().split("/");

		TouchpointCRUDExecutor exec = (TouchpointCRUDExecutor) getServletContext()
				.getAttribute("touchpointCRUD");

		try {
			exec.deleteTouchpoint(Long.parseLong(stringArr[stringArr.length - 1]));
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
