package module3;

//Java utilities libraries
import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
import java.util.List;

//Processing library
import processing.core.PApplet;

//Unfolding libraries
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.utils.MapUtils;

//Parsing library
import parsing.ParseFeed;

/** EarthquakeCityMap
 * An application with an interactive map displaying earthquake data.
 * Author: UC San Diego Intermediate Software Development MOOC team
 * @author Your name here
 * Date: July 17, 2015
 * */
public class EarthquakeCityMap extends PApplet {

	// You can ignore this.  It's to keep eclipse from generating a warning.
	private static final long serialVersionUID = 1L;

	// IF YOU ARE WORKING OFFLINE, change the value of this variable to true
	private static final boolean offline = false;
	
	// Less than this threshold is a light earthquake
	public static final float THRESHOLD_MODERATE = 5;
	// Less than this threshold is a minor earthquake
	public static final float THRESHOLD_LIGHT = 4;

	/** This is where to find the local tiles, for working without an Internet connection */
	public static String mbTilesString = "blankLight-1-3.mbtiles";
	
	// The map
	private UnfoldingMap map;
	
	//feed with magnitude 2.5+ Earthquakes
	private String earthquakesURL = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.atom";
			//"http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_week.atom";

	public void setup() {
		size(950, 600, OPENGL);

		if (offline) {
		    map = new UnfoldingMap(this, 200, 50, 700, 500, new MBTilesMapProvider(mbTilesString));
		    earthquakesURL = "2.5_week.atom"; 	// Same feed, saved Aug 7, 2015, for working offline
		}
		else {
			map = new UnfoldingMap(this, 200, 50, 700, 500, new Google.GoogleMapProvider());
			// IF YOU WANT TO TEST WITH A LOCAL FILE, uncomment the next line
			//earthquakesURL = "2.5_week.atom";
		}
		
	    map.zoomToLevel(2);
	    MapUtils.createDefaultEventDispatcher(this, map);	
			
	    // The List you will populate with new SimplePointMarkers
	    List<Marker> markers = new ArrayList<Marker>();

	    //Use provided parser to collect properties for each earthquake
	    //PointFeatures have a getLocation method
	    List<PointFeature> earthquakes = ParseFeed.parseEarthquake(this, earthquakesURL);
	    
	    if (earthquakes.size() > 0) {
	    	for (PointFeature earthquake : earthquakes) {
				Object magnitudeObj = earthquake.getProperty("magnitude");
                float magnitude = Float.parseFloat(magnitudeObj.toString());
                SimplePointMarker marker = createMarker(earthquake);
                if (magnitude >= THRESHOLD_MODERATE) {
                    marker.setRadius(20);
                    marker.setColor(color(255, 0, 0));
                } else if (magnitude <= THRESHOLD_LIGHT) {
                    marker.setRadius(5);
                    marker.setColor(color(0, 0, 255));
                } else if (magnitude > THRESHOLD_LIGHT && magnitude < THRESHOLD_MODERATE) {
                    marker.setRadius(10);
                    marker.setColor(color(255, 255, 0));
                }
                markers.add(marker);
			}
	    }
        map.addMarkers(markers);
	}
		
	// A suggested helper method that takes in an earthquake feature and 
	// returns a SimplePointMarker for that earthquake
	private SimplePointMarker createMarker(PointFeature feature)
	{
		// finish implementing and use this method, if it helps.
		return new SimplePointMarker(feature.getLocation());
	}
	
	public void draw() {
	    background(10);
	    map.draw();
	    addKey();
	}


	// helper method to draw key in GUI
	private void addKey()
	{
        fill(230,230,250);
        rect(15,50,170,300,5);
        textSize(20);
        fill(20, 20, 20);
        textAlign(CENTER);
        text("Earthquake Key", 100, 70);
        fill(255,0,0);
        ellipse(30, 120, 20, 20);
        textSize(15);
        textAlign(LEFT);
        text("5.0+ Magnitude", 50, 125);
        fill(255,255,0);
        ellipse(30, 170, 10, 10);
        text("4.0+ Magnitude", 50, 175);
        fill(0,0,255);
        ellipse(30, 220, 5, 5);
        text("Below 4.0", 50, 225);
	}
}
