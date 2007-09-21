package dk.contix.eclipse.hudson;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.Preferences;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Class for accessing the Hudson server.
 * 
 * @author Joakim Recht
 *
 */
public class HudsonClient {
	
	private Preferences prefs;

	public HudsonClient() {
		prefs = Activator.getDefault().getPluginPreferences();
	}
	
	private String getBase() {
		return prefs.getString(Activator.PREF_BASE_URL);
	}

	public Job[] getJobs() throws IOException {
		try {
			URL u = new URL(getBase() + "/api/xml");
			InputStream is = u.openStream();
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
			
			Element root = doc.getDocumentElement();
			NodeList jobNodes = root.getElementsByTagName("job");
			
			Job[] res = new Job[jobNodes.getLength()];
			for (int i = 0; i < res.length; i++) {
				Element jobNode = (Element) jobNodes.item(i);
				
				String name = getNodeValue(jobNode, "name");
				String url = getNodeValue(jobNode, "url");
				String last = getNodeValue(jobNode, "lastBuild");
				if (last == null) {
					// we're probably in a newer version of hudson, so we get the build number separately
					last = getBuildNumber(name);
				}
				res[i] = new Job(name, url, getNodeValue(jobNode, "color"), last);
			}
			
			is.close();
			
			return res;
		} catch (SAXException e) {
			throw new RuntimeException(e);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
	}
	
	private String getBuildNumber(String name) throws IOException, SAXException, ParserConfigurationException {
		URL u = new URL(getBase() + "/job/" + encode(name) + "/api/xml");
		InputStream is = u.openStream();
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
			NodeList els = doc.getElementsByTagName("lastBuild");
			if (els.getLength() == 1) {
				return getNodeValue((Element) els.item(0), "number");
			}
		} finally {
			is.close();
		}
		return null;
	}
	
	private String encode(String url) {
		try {
			return URLEncoder.encode(url, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public void scheduleJob(String project) throws IOException {
		URL u = new URL(getBase() + "/job/" + encode(project) + "/build");
		HttpURLConnection connection = (HttpURLConnection) u.openConnection();
		connection.setAllowUserInteraction(false);
		connection.setConnectTimeout(1000);
		connection.setReadTimeout(1000);
		connection.setInstanceFollowRedirects(false);
		connection.getResponseCode();

		connection.disconnect();
	}
	
	public static void checkValidUrl(String base) throws Exception {
		URL u = new URL(base + "/api/xml");
		HttpURLConnection connection = (HttpURLConnection) u.openConnection();
		connection.setAllowUserInteraction(false);
		connection.setConnectTimeout(1000);
		connection.setReadTimeout(1000);
		connection.setInstanceFollowRedirects(false);
		connection.getResponseCode();

		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(connection.getInputStream());

		Element root = doc.getDocumentElement();
		if (root.getChildNodes().getLength() == 0 || !root.getNodeName().equals("hudson")) {
			throw new IllegalArgumentException("URL does not point to a valid Hudson installation. /api/xml does not return correct data.");
		}
	}

	private String getNodeValue(Element node, String name) {
		NodeList list = node.getElementsByTagName(name);
		if (list.getLength() == 1) {
			return list.item(0).getTextContent().trim();
		}
		return null;
	}
}