package i5.las2peer.services.researchService;

import java.net.HttpURLConnection;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Random;
import java.util.logging.Level;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import i5.las2peer.api.Context;
import i5.las2peer.api.ServiceException;
import i5.las2peer.api.logging.MonitoringEvent;
import i5.las2peer.api.persistency.Envelope;
import i5.las2peer.restMapper.RESTService;
import i5.las2peer.restMapper.annotations.ServicePath;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Contact;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;

import i5.las2peer.api.security.ServiceAgent;
import i5.las2peer.api.security.UserAgent;

@Api
@SwaggerDefinition(
		info = @Info(
				title = "las2peer Sandbox Service",
				version = "1.0.0",
				description = "A las2peer Service for research purposes.",
				termsOfService = "http://las2peer.org",
				contact = @Contact(
						name = "Michal Slupczynski",
						url = "las2peer.org",
						email = "michal.slupczynski@rwth-aachen.de")))
@ServicePath("/research")
// TODO Your own service class
public class ResearchService extends RESTService {
	
	String key = "testKey";
	
	/**
	 * This method stores an object inside the LAS2peer network storage. The type of the object is not limited, any
	 * class that implements the {@link java.io.Serializable} interface can be used.
	 * 
	 * @param identifier This identifier is used to identify the stored object inside the network.
	 * @param object The object that should actually be stored in network storage.
	 */
	public void persistObject(String identifier, Transaction object) {
		try {
			Envelope env = null;
			try {
				// fetch existing container object from network storage
				env = Context.get().requestEnvelope(identifier);
				// place the new object inside container
				env.setContent(object);
			} catch (Exception e) {
				// write info message to logfile and console
				Context.get().getLogger(this.getClass()).log(Level.INFO, "Network storage container not found. Creating new one. " + e.toString());
				// create new container object with current ServiceAgent as owner
				env = Context.get().createEnvelope(identifier);
				env.setContent(object);
			}
			// upload the updated storage container back to the network
			Context.get().storeEnvelope(env);
		} catch (Exception e) {
			// write error to logfile and console
			Context.get().getLogger(this.getClass()).log(Level.SEVERE, "Can't persist to network storage!", e);
			// create and publish a monitoring message
			Context.get().monitorEvent(this, MonitoringEvent.SERVICE_ERROR, e.toString());
		}
	}

	/**
	 * This method fetches an object from the LAS2peer network storage. The return type is not limited, any class that
	 * implements the {@link java.io.Serializable} interface can be used.
	 * 
	 * @param identifier This identifier is used to identify the storage object inside the network.
	 * @return Returns the fetched object or null if an error occurred.
	 */
	public Transaction fetchObject(String identifier) {
		try {
			// fetch existing container object from network storage
			Envelope env = Context.get().requestEnvelope(identifier);
			// deserialize content from envelope
			Transaction retrieved = (Transaction) env.getContent();
			return retrieved;
		} catch (Exception e) {
			// write error to logfile and console
			Context.get().getLogger(this.getClass()).log(Level.SEVERE, "Can't fetch from network storage!", e);
			// create and publish a monitoring message
			Context.get().monitorEvent(this, MonitoringEvent.SERVICE_ERROR, e.toString());
		}
		return null;
	}
	
	/**
	 * Template of a get function.
	 *
	 * @param randID Key of persistent storage ID
	 * @return Returns an HTTP response with the username as string content.
	 */
	@GET
	@Path("/chain_set/{id}")
	@Produces(MediaType.TEXT_PLAIN)
	@ApiOperation(
			value = "Set Persistent Object on Blockchain",
			notes = "use randID param to set key")
	@ApiResponses(
			value = { @ApiResponse(
					code = HttpURLConnection.HTTP_OK,
					message = "HTTP_OK") })
	public Response chainSetTemplate(@PathParam("id") int randID) {
		String retVal = "";
		DateTimeFormatter formatter = DateTimeFormatter
				.ofPattern("yyyy-MM-dd HH:mm:ss")
				.withLocale(Locale.getDefault())
				.withZone(ZoneId.systemDefault());
		
		Instant now = Instant.now();
		
		String formatted = formatter.format(now);
		
		Transaction t = new Transaction(formatted);
		//StorageService.persistObject(key, t);
		
		String persistKey = this.key + randID;
		
		//this.persistObject(persistKey, t);
		//var a = getRunningAtNode();
		//Application a = null;
		
		retVal = "Written  [" + "XXX" + "]: " + t.getMsg();
		
		return Response.ok().entity(retVal).build();
	}

	/**
	 * Template of a get function.
	 * 
	 * @param randID Key of persistent storage ID
	 * @return Returns an HTTP response with the username as string content.
	 */
	@GET
	@Path("/dht_set/{id}")
	@Produces(MediaType.TEXT_PLAIN)
	@ApiOperation(
			value = "Set Persistent Object",
			notes = "use randID param to set key")
	@ApiResponses(
			value = { @ApiResponse(
					code = HttpURLConnection.HTTP_OK,
					message = "HTTP_OK") })
	public Response getTemplate(@PathParam("id") int randID) {
		//UserAgent userAgent = (UserAgent) Context.getCurrent().getMainAgent();
		//String name = userAgent.getLoginName();
		//return Response.ok().entity(name).build();
		String retVal = "";
		DateTimeFormatter formatter = DateTimeFormatter
	            .ofPattern("yyyy-MM-dd HH:mm:ss")
	            .withLocale(Locale.getDefault())
	            .withZone(ZoneId.systemDefault());

	        Instant now = Instant.now();

	        String formatted = formatter.format(now);
		
		Transaction t = new Transaction(formatted);
		//StorageService.persistObject(key, t);
		
		String persistKey = this.key + randID;
		
		this.persistObject(persistKey, t);
		
		retVal = "Written  [" + persistKey + "]: " + t.getMsg();
		
		return Response.ok().entity(retVal).build();
	}
	
	/**
	 * Template of a get function.
	 * 
	 * @param randID Key of persistent storage ID
	 * @return Returns an HTTP response with the username as string content.
	 */
	@GET
	@Path("/dht_get/{id}")
	@Produces(MediaType.TEXT_PLAIN)
	@ApiOperation(
			value = "Get persistent object",
			notes = "use randID param to specify key for retrieval")
	@ApiResponses(
			value = { 
					@ApiResponse(
						code = HttpURLConnection.HTTP_OK,
						message = "HTTP_OK"), 
					@ApiResponse(
						code = HttpURLConnection.HTTP_NOT_FOUND,
						message = "HTTP_NOT_FOUND") }
			)
	public Response testAction(@PathParam("id") int randID) {
		Transaction t = (Transaction) this.fetchObject(this.key + randID);
		String retVal = "";
		if ( t == null ) {
			retVal = "Querying [" + this.key + randID + "]: NOT FOUND!";
			return Response.status(404).entity(retVal).build();
		}
		else
		{
			retVal = "Returned [" + this.key + randID + "]: " + t.toString();
			return Response.ok().entity(retVal).build();
		}
	}
}
