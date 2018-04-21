package net.tetrakoopa.mdu4j.admin.service;

import net.tetrakoopa.mdu4j.admin.front.servlet.AbstractAdminServlet;
import net.tetrakoopa.mdu4j.front.servlet.helper.ServletInitParameterHelper;
import net.tetrakoopa.mdu4j.view.UI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.servlet.ServletConfig;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class AdminUtilService {

    private final static Logger LOGGER = LoggerFactory.getLogger(AdminUtilService.class);

    private final List<AbstractAdminServlet> servlets = new ArrayList<AbstractAdminServlet>();

    public void register(AbstractAdminServlet servlet) {
        synchronized (this) {
            servlets.add(servlet);
            Collections.sort(servlets, servletOrderComparator);
        }
    }

    private static final Comparator<AbstractAdminServlet> servletOrderComparator =new Comparator<AbstractAdminServlet>() {

        @Override
        public int compare(AbstractAdminServlet servlet1, AbstractAdminServlet servlet2) {
            final Integer orderFromInit1 = safeGetIntegerParameter(servlet1,AbstractAdminServlet.INIT_PARAMETER_MENU_ORDER);
            final Integer orderFromInit2 = safeGetIntegerParameter(servlet2,AbstractAdminServlet.INIT_PARAMETER_MENU_ORDER);
            final int order1;
            final int order2;
            if (orderFromInit1 != null) {
                order1 = orderFromInit1;
            } else {
                final UI.Order orderAnnotation1 = servlet1.getClass().getAnnotation(UI.Order.class);
                order1 =  orderAnnotation1 == null ? Integer.MAX_VALUE : orderAnnotation1.value();
            }
            if (orderFromInit2 != null) {
                order2 = orderFromInit2;
            } else {
                final UI.Order orderAnnotation2 = servlet1.getClass().getAnnotation(UI.Order.class);
                order2 = orderAnnotation2 == null ? Integer.MAX_VALUE : orderAnnotation2.value();
            }
            if (order1 > order2) return 1;
            if (order1 < order2) return -1;
            return 0;
        }
    };

    public List<AbstractAdminServlet> getServlets() {
        synchronized (this) {
            // FIXME retourner une copie ou une liste immutable
            return servlets;
        }
    }

    /**
     * @return la liste d'URL mappée sur cette servlet<br/>
     *          <b>peut être null</b> si une erreur est survenue en lisant le fichier 'web.xml'
     */
    public List<String> findMappedPaths(ServletConfig config) {
        final InputStream webxml = config.getServletContext().getResourceAsStream("/WEB-INF/web.xml");
        if (webxml==null) {
            return null;
        }

        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            LOGGER.error("Could not create builder for XPath", e);
            return null;
        }
        final Document document;
        try {
            document = builder.parse(webxml);
        } catch (SAXException e) {
            LOGGER.error("Malformed web.xml", e);
            return null;
        } catch (IOException e) {
            LOGGER.error("Could not read web.xml", e);
            return null;
        }
        final XPathFactory xPathfactory = XPathFactory.newInstance();
        final XPath xpath = xPathfactory.newXPath();
        final XPathExpression mappingExpression;
        final XPathExpression servletClassExpression;
        try {
            servletClassExpression = xpath.compile("//web-app/servlet/servlet-class");
            mappingExpression = xpath.compile("//web-app/servlet-mapping");
        } catch (XPathExpressionException e) {
            LOGGER.error("", e);
            return null;
        }


        final NodeList mappingNodes;
        try {
            mappingNodes = (NodeList) mappingExpression.evaluate(document, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            LOGGER.error("", e);
            return null;
        }

        final String servletName = config.getServletName();

        final List<String> urls = new ArrayList<String>();

        for (int indexMapping = 0; indexMapping < mappingNodes.getLength() ; indexMapping ++) {
            final Node node = mappingNodes.item(indexMapping);
            final NodeList children = node.getChildNodes();

            String mappedServletName = null;
            String mappedURL = null;

            for (int index = 0; index < children.getLength() ; index ++) {
                final Node child = children.item(index);
                final String childName = child.getNodeName();
                if ("url-pattern".equals(childName)) {
                    mappedURL = child.getTextContent();
                    continue;
                }
                if ("servlet-name".equals(childName)) {
                    mappedServletName = child.getTextContent();
                    continue;
                }
            }

            if (mappedServletName != null && mappedURL != null) {
                if (mappedServletName.equals(servletName)) {
                    urls.add(mappedURL);
                }
            }
        }


        return urls;
    }


    public static String getHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return null;
        }
    }
    public static String getIP() {
        final String host = getHostname();
        try {
            return host == null ? null : InetAddress.getByName(host).getHostAddress();
        } catch (UnknownHostException e) {
            return null;
        }
    }

    private final static Integer safeGetIntegerParameter(AbstractAdminServlet servlet, String name) {
        try {
            return ServletInitParameterHelper.getIntegerParameter(servlet, name);
        } catch (RuntimeException rex) {
            return null;
        }
    }


}
