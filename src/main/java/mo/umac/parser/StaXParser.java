/**
 * 
 */
package mo.umac.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 * Parse the returned xml file.
 * 
 * @author Kate Yim
 * 
 */
public class StaXParser {
	static final String RESULT_SET = "ResultSet";
	static final String TOTAL_RESULTS_AVAILABLE = "totalResultsAvailable";
	static final String TOTAL_RESULTS_RETURNED = "totalResultsReturned";
	static final String FIRST_RESULT_POSITION = "firstResultPosition";
	static final String RESULT = "Result";
	static final String ID = "id";
	static final String TITLE = "Title";
	static final String ADDRESS = "Address";
	static final String CITY = "City";
	static final String STATE = "State";
	static final String PHONE = "Phone";
	static final String LATITUDE = "Latitude";
	static final String LONGITUDE = "Longitude";
	static final String RATING = "Rating";
	static final String AVERAGE_RATING = "AverageRating";
	static final String TOTAL_RATINGS = "TotalRatings";
	static final String TOTAL_REVIEWS = "TotalReviews";
	static final String LAST_REVIEW_DATE = "LastReviewDate";
	static final String LAST_REVIEW_INTRO = "LastReviewIntro";
	static final String DISTANCE = "Distance";
	static final String URL = "Url";
	static final String CLICK_URL = "ClickUrl";
	static final String MAP_URL = "MapUrl";
	static final String BUSINESS_URL = "BusinessUrl";
	static final String BUSINESS_CLICK_URL = "BusinessClickUrl";
	static final String CATEGORIES = "Categories";
	static final String CATEGORY = "Category";

	@SuppressWarnings({ "unchecked", "null" })
	public ResultSet readConfig(String configFile) {
		ResultSet resultSet = null;
		List<Result> results = new ArrayList<Result>();
		try {
			// First create a new XMLInputFactory
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			// Setup a new eventReader
			InputStream in = new FileInputStream(configFile);
			XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
			// Read the XML document
			Result result = null;
			Rating rating = null;
			List<Category> categories = new ArrayList<Category>();

			while (eventReader.hasNext()) {
				XMLEvent event = eventReader.nextEvent();

				if (event.isStartElement()) {
					StartElement startElement = event.asStartElement();

					if (startElement.getName().getLocalPart() == (RESULT_SET)) {
						resultSet = new ResultSet();
						Iterator<Attribute> attributes = startElement
								.getAttributes();
						while (attributes.hasNext()) {
							Attribute attribute = attributes.next();
							if (attribute.getName().toString()
									.equals(TOTAL_RESULTS_AVAILABLE)) {
								resultSet.setTotalResultsAvailable(Integer
										.parseInt(attribute.getValue()));
							}
							if (attribute.getName().toString()
									.equals(TOTAL_RESULTS_RETURNED)) {
								resultSet.setTotalResultsReturned(Integer
										.parseInt(attribute.getValue()));
							}
							if (attribute.getName().toString()
									.equals(FIRST_RESULT_POSITION)) {
								resultSet.setFirstResultPosition(Integer
										.parseInt(attribute.getValue()));
							}
						}
						continue;
					}

					// If we have a result element we create a new result
					if (startElement.getName().getLocalPart() == (RESULT)) {
						result = new Result();
						// We read the attributes from this tag and add all
						// attribute to our object
						Iterator<Attribute> attributes = startElement
								.getAttributes();
						while (attributes.hasNext()) {
							Attribute attribute = attributes.next();
							if (attribute.getName().toString().equals(ID)) {
								result.setId(Integer.parseInt(attribute
										.getValue()));
							}

						}
					}
					if (event.asStartElement().getName().getLocalPart()
							.equals(TITLE)) {
						event = eventReader.nextEvent();
						result.setTitle(event.toString());
						continue;
					}
					if (event.asStartElement().getName().getLocalPart()
							.equals(ADDRESS)) {
						event = eventReader.nextEvent();
						result.setAddress(event.toString());
						continue;
					}
					if (event.asStartElement().getName().getLocalPart()
							.equals(CITY)) {
						event = eventReader.nextEvent();
						result.setCity(event.toString());
						continue;
					}
					if (event.asStartElement().getName().getLocalPart()
							.equals(STATE)) {
						event = eventReader.nextEvent();
						result.setState(event.toString());
						continue;
					}
					if (event.asStartElement().getName().getLocalPart()
							.equals(PHONE)) {
						event = eventReader.nextEvent();
						result.setPhone(event.toString());
						continue;
					}
					if (event.asStartElement().getName().getLocalPart()
							.equals(LATITUDE)) {
						event = eventReader.nextEvent();
						result.setLatitude(Double.parseDouble(event.toString()));
						continue;
					}
					if (event.asStartElement().getName().getLocalPart()
							.equals(LONGITUDE)) {
						event = eventReader.nextEvent();
						result.setLongitude(Double.parseDouble(event.toString()));
						continue;
					}
					//
					if (event.asStartElement().getName().getLocalPart()
							.equals(RATING)) {
						rating = new Rating();
						continue;
					}
					if (event.asStartElement().getName().getLocalPart()
							.equals(AVERAGE_RATING)) {
						event = eventReader.nextEvent();
						rating.setAverageRating(event.toString());
						continue;
					}
					if (event.asStartElement().getName().getLocalPart()
							.equals(TOTAL_RATINGS)) {
						event = eventReader.nextEvent();
						rating.setTotalRatings(Integer.parseInt(event
								.toString()));
						continue;
					}
					if (event.asStartElement().getName().getLocalPart()
							.equals(TOTAL_REVIEWS)) {
						event = eventReader.nextEvent();
						rating.setTotalReviews(Integer.parseInt(event
								.toString()));
						continue;
					}
					if (event.asStartElement().getName().getLocalPart()
							.equals(LAST_REVIEW_DATE)) {
						event = eventReader.nextEvent();
						rating.setLastReviewDate(event.toString());
						continue;
					}
					if (event.asStartElement().getName().getLocalPart()
							.equals(LAST_REVIEW_INTRO)) {
						event = eventReader.nextEvent();
						rating.setLastReviewIntro(event.toString());
						continue;
					}
					//
					if (event.asStartElement().getName().getLocalPart()
							.equals(DISTANCE)) {
						event = eventReader.nextEvent();
						result.setDistance(Double.parseDouble(event.toString()));
						continue;
					}
					if (event.asStartElement().getName().getLocalPart()
							.equals(URL)) {
						event = eventReader.nextEvent();
						result.setUrl(event.toString());
						continue;
					}
					if (event.asStartElement().getName().getLocalPart()
							.equals(CLICK_URL)) {
						event = eventReader.nextEvent();
						result.setClickUrl(event.toString());
						continue;
					}
					if (event.asStartElement().getName().getLocalPart()
							.equals(MAP_URL)) {
						event = eventReader.nextEvent();
						result.setMapUrl(event.toString());
						continue;
					}
					if (event.asStartElement().getName().getLocalPart()
							.equals(BUSINESS_URL)) {
						event = eventReader.nextEvent();
						result.setBusinessUrl(event.toString());
						continue;
					}
					if (event.asStartElement().getName().getLocalPart()
							.equals(BUSINESS_CLICK_URL)) {
						event = eventReader.nextEvent();
						result.setBusinessClickUrl(event.toString());
						continue;
					}
					//
					if (event.asStartElement().getName().getLocalPart()
							.equals(CATEGORIES)) {
						categories = new ArrayList<Category>();
						continue;
					}
					if (event.asStartElement().getName().getLocalPart()
							.equals(CATEGORY)) {
						Category category = new Category();
						Iterator<Attribute> attributes = startElement
								.getAttributes();
						while (attributes.hasNext()) {
							Attribute attribute = attributes.next();
							if (attribute.getName().toString().equals(ID)) {
								category.setId(Integer.parseInt(attribute
										.getValue()));
							}
						}
						event = eventReader.nextEvent();
						category.setName(event.toString());
						categories.add(category);
					}
					//
				}
				// If we reach the end of an item element we add it to the list
				if (event.isEndElement()) {
					EndElement endElement = event.asEndElement();
					if (endElement.getName().getLocalPart() == (RATING)) {
						result.setRating(rating);
					}
					if (endElement.getName().getLocalPart() == (CATEGORIES)) {
						result.setCategories(categories);
					}
					if (endElement.getName().getLocalPart() == (RESULT)) {
						results.add(result);
					}
				}
			}
			if(resultSet != null){
				resultSet.setResults(results);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
		return resultSet;
	}
}
