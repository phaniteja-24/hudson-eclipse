package dk.contix.eclipse.hudson;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.eclipse.core.net.proxy.IProxyData;
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
		HttpClient client = getClient(getBase());
		GetMethod method = new GetMethod(getRelativePath(getBase()) + "/api/xml");

		try {
			client.executeMethod(method);
			InputStream is = method.getResponseBodyAsStream();
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
			is.close();

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

			return res;
		} catch (SAXException e) {
			throw new RuntimeException(e);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		} finally {
			method.releaseConnection();
		}
	}

	private String getBuildNumber(String name) throws IOException, SAXException, ParserConfigurationException {
		HttpClient client = getClient(getBase());
		GetMethod method = new GetMethod(getRelativePath(getBase()) + "/job/" + encode(name) + "/api/xml");
		try {
			client.executeMethod(method);
			InputStream bodyStream = method.getResponseBodyAsStream();
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(bodyStream);
			bodyStream.close();
			NodeList els = doc.getElementsByTagName("lastBuild");
			if (els.getLength() == 1) {
				return getNodeValue((Element) els.item(0), "number");
			}
		} finally {
			method.releaseConnection();
		}
		return null;
	}

	private String encode(String url) {
		try {
			return URLEncoder.encode(url, "UTF-8").replaceAll("\\+", "%20");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public void scheduleJob(String project) throws IOException {
		HttpClient client = getClient(getBase());
		String st = prefs.getString(Activator.PREF_SECURITY_TOKEN + "_" + project);
		String token = "";
		if (st != null && st.length() > 0) {
			token = "?token=" + st;
		}

		GetMethod method = new GetMethod(getRelativePath(getBase()) + "/job/" + encode(project) + "/build" + token);

		try {
			int res = client.executeMethod(method);
			if (res == HttpStatus.SC_FORBIDDEN) {
				throw new IOException("Scheduling failed, security token required");
			}
			method.getResponseBodyAsStream().close();
		} finally {
			method.releaseConnection();
		}
	}

	public void checkValidUrl(String base) throws Exception {

		HttpClient client = getClient(base);
		GetMethod method = new GetMethod(getRelativePath(base) + "/api/xml");

		try {
			client.executeMethod(client.getHostConfiguration(), method);
			InputStream bodyStream = method.getResponseBodyAsStream();
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(bodyStream);
			bodyStream.close();

			Element root = doc.getDocumentElement();
			if (root.getChildNodes().getLength() == 0 || !root.getNodeName().equals("hudson")) {
				throw new IllegalArgumentException("URL does not point to a valid Hudson installation. /api/xml does not return correct data.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			method.releaseConnection();
		}
	}

	private String getNodeValue(Element node, String name) {
		NodeList list = node.getElementsByTagName(name);
		if (list.getLength() == 1) {
			return list.item(0).getTextContent().trim();
		}
		return null;
	}

	private HttpClient getClient(String base) {
		try {
			HttpClient client = new HttpClient();
			String type;
			URI u = new URI(base);
			if (u.getScheme().equalsIgnoreCase("https")) {
				type = IProxyData.HTTPS_PROXY_TYPE;
				client.getHostConfiguration().setHost(u.getHost(), u.getPort(), new Protocol("https", (ProtocolSocketFactory) new EasySSLProtocolSocketFactory(), 443));
			} else {
				type = IProxyData.HTTP_PROXY_TYPE;
				client.getHostConfiguration().setHost(u.getHost(), u.getPort());
			}
			IProxyData proxyData = Activator.getDefault().getProxyService().getProxyDataForHost(u.getHost(), type);
			if (proxyData != null) {
				client.getHostConfiguration().setProxy(proxyData.getHost(), proxyData.getPort());
				if (proxyData.isRequiresAuthentication()) {
					client.getState().setProxyCredentials(new AuthScope(proxyData.getHost(), proxyData.getPort()),
							new UsernamePasswordCredentials(proxyData.getUserId(), proxyData.getPassword()));
				}
			}
			client.getParams().setConnectionManagerTimeout(1000);
			client.getParams().setSoTimeout(1000);
			return client;
		} catch (URISyntaxException e1) {
			throw new RuntimeException(e1);
		}
	}

	private String getRelativePath(String url) {
		int pos = url.indexOf('/', 8);
		if (pos == -1) {
			return "";
		} else {
			return url.substring(pos + 1);
		}
	}
}